package com.example.gpscw2;


import android.Manifest;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.LocationSource;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private final static int LOCATION_PERMISSION = 1022;

    private static final String TAG = "comp3018:HomeFragment";
    private static final int BACKGROUND_PERMISSION = 3232;
    // TODO: Rename and change types of parameters
    private HomeFragmentViewModel viewModel;
    private Button pauseAndPlayLocation;
    private Button travelled;
    private Button run;
    private Button walk;
    private Button cycle;
    LocationService.LocationServiceBinder locationBinder;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public int getDistanceToday(List<TravelEntity> entities) {
        int res = 0;

        if(entities == null || entities.size() == 0) {
            return 0;
        }

        for(TravelEntity entity: entities) {
            if(entity.getDate() == LocalDate.now().toEpochDay())
                res += entity.getDistance();
        }

        return res;
    }
    public void startMovementViewerActivity(Movement.MovementType type) {
        Intent intent = new Intent(getActivity(),MovementViewerActivity.class);

        intent.putExtra("movementType",type.ordinal());
        startActivity(intent);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        viewModel = new ViewModelProvider(this).get(HomeFragmentViewModel.class);


        pauseAndPlayLocation = view.findViewById(R.id.pauseAndPlay);
        travelled = view.findViewById(R.id.travel);
        run = view.findViewById(R.id.run);
        walk = view.findViewById(R.id.walk);
        cycle = view.findViewById(R.id.cycle);

        travelled.setText(requireActivity().getString(R.string.travelledText,0));

        travelled.setOnClickListener(v -> startMovementViewerActivity(Movement.MovementType.TRAVEL));
        run.setOnClickListener(v -> startMovementViewerActivity(Movement.MovementType.RUN));
        walk.setOnClickListener(v -> startMovementViewerActivity(Movement.MovementType.WALK));
        cycle.setOnClickListener(v -> startMovementViewerActivity(Movement.MovementType.CYCLE));


        viewModel.getTravelEntities().observe(getActivity(), travelEntities -> {
            if(travelEntities == null || getContext() == null || travelEntities.size() == 0) {
                return;
            }

            TravelEntity entity = travelEntities.get(0);
            travelled.setText(requireActivity().getString(R.string.travelledText,entity.getDistance()));
        });

        viewModel.getWalkEntities().observe(getActivity(),
                entities -> {
            if (getContext() == null)
                return;
            walk.setText(getString(R.string.walkText,getDistanceToday(entities)));
        });

        viewModel.getRunEntities().observe(getActivity(),
                entities -> {
                    if (getContext() == null)
                        return;
                    run.setText(getString(R.string.runText,getDistanceToday(entities)));
                });

        viewModel.getCycleEntities().observe(getActivity(),
                entities -> {
                    if (getContext() == null)
                        return;
                    cycle.setText(getString(R.string.cycleText,getDistanceToday(entities)));
                });

        if (isMyServiceRunning(LocationService.class)) {
            pauseAndPlayLocation.setText(R.string.pauseAndPlayAlt);
        } else {
            pauseAndPlayLocation.setText(R.string.pauseAndPlayInitial);
        }

        pauseAndPlayLocation.setOnClickListener(v -> {
            if(!isMyServiceRunning(LocationService.class)) {

                Log.d(TAG,"TRYING TO START SERVICE FROM FRAGMENT");
                getLocationPermssionsAndStartService();
            } else {
                Log.d(TAG,"TRYING TO STOP SERVICE FROM FRAGMENT");
                Intent intent = new Intent(requireActivity(), LocationService.class);

                if(getActivity() != null) {
                    ((MainActivity) getActivity()).setLocationSource(null);
                }
                getActivity().unbindService(serviceConnection);
                getActivity().stopService(intent);
                pauseAndPlayLocation.setText(R.string.pauseAndPlayInitial);
            }
        });

        if(isMyServiceRunning(LocationService.class)) {
            startLocationService();
        }
        return view;
    }

    private void getLocationPermssionsAndStartService() {
        Log.d(TAG,"GETTING LOCATION PERMISSIONS");
        if(!checkLocationPermissions()) {
            requestLocationPermissions();
            return;
        }
        Log.d(TAG,"HAVE ALREAD LOCATION PERMISSIONS");
        startLocationService();
    }

    private void requestLocationPermissions() {
        Log.d(TAG,"REQUESTING LOCATION PERMISSIONS");
        this.requestPermissions(
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,}
                ,LOCATION_PERMISSION);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            locationBinder = (LocationService.LocationServiceBinder) service;
            if(getActivity() != null) {
                MainActivity activity = (MainActivity) getActivity();
                activity.setLocationSource(locationBinder.getLocationSource());
            }
            Log.d(TAG, "Location Serivce Bound to Main");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            locationBinder = null;
            if(getActivity() != null) {
                MainActivity activity = (MainActivity) getActivity();
                activity.setLocationSource(null);
            }
            Log.d(TAG, "Location Serivce Disconnected to Main");
        }
    };

    private boolean checkLocationPermissions() {
        Log.d(TAG,"CHECKING LOCATION PERMISSIONS");
        boolean coarse = getActivity().checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean fine = getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        return coarse && fine;
    }

    private void startLocationService() {
        Intent serviceIntent = new Intent(getActivity(), LocationService.class);

        pauseAndPlayLocation.setText(R.string.pauseAndPlayAlt);
        if(isMyServiceRunning(LocationService.class)) {
            getActivity().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            Log.d(TAG,"Just Binding service");
            return;
        }

        getActivity().startService(serviceIntent);
        getActivity().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG,"starting and binding service");

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void getBackgroundPermissions(){
        boolean background = getActivity().checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if(background) {
            startLocationService();
            return;
        }

        Log.d(TAG,"REQUESTING BACKGROUND PERMISSINOS");
        this.requestPermissions(
                new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION,}
                ,BACKGROUND_PERMISSION);
        startLocationService();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "FOREGROUND PERMISSIONS ATTAINED");
                getBackgroundPermissions();
            } else {


                Log.d(TAG, "LOCATION REQUESTS DENIED");
                Toast.makeText(getActivity(), "All Location permissions are needed for this feature," +
                        " enable them in settings.", Toast.LENGTH_SHORT).show();
            }
        }

        if(requestCode == BACKGROUND_PERMISSION){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "BACKGROUND PERMISSIONS ATTAINED");
                startLocationService();
            } else {
                Log.d(TAG, "LOCATION REQUESTS DENIED");
                Toast.makeText(getActivity(), "All Location permissions are needed for this feature," +
                        " enable them in settings.", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(isMyServiceRunning(LocationService.class)) {
           getActivity().unbindService(serviceConnection);
        }
    }
}