package com.example.gpscw2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "COMP3018";
    private MainActivityViewModel viewModel;
    private TextView currLocation;
    LocationService.LocationServiceBinder locationBinder;
    FragmentManager fragmentManager;
    MyLocationSource locationSource;
    private final static int LOCATION_PERMISSION = 1022;

    boolean hasLocationPermissions;


    private void bindCurrLocation(MutableLiveData<Double> lat, MutableLiveData<Double> lon) {
        Log.d(TAG, "binding location to textview");
        currLocation.setText("hello");
        lat.observe(MainActivity.this, aDouble -> {

            locationSource.alert(lat.getValue(),lon.getValue());
            Log.d(TAG, "changing lat");
            currLocation.setText(getString(R.string.currLocationResource, Double.toString(lat.getValue()), Double.toString(lon.getValue())));
        });
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            locationBinder = (LocationService.LocationServiceBinder) service;
            bindCurrLocation(locationBinder.getLat(), locationBinder.getLon());
            Log.d(TAG, "Location Serivce Bound to Main");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            locationBinder = null;
            Log.d(TAG, "Location Serivce Disconnected to Main");
        }
    };
    
   private void getLocationPermssions() {
       Log.d(TAG,"GETTING LOCATION PERMISSIONS");
       if(!checkLocationPermissions()) {
           requestLocationPermissions();
           return;
       }
       Log.d(TAG,"HAVE ALREAD LOCATION PERMISSIONS");
   }

    private void requestLocationPermissions() {
        Log.d(TAG,"REQUESTING LOCATION PERMISSIONS");
        this.requestPermissions(
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION},LOCATION_PERMISSION);
        return;
    }

    private boolean checkLocationPermissions() {
        Log.d(TAG,"CHECKING LOCATION PERMISSIONS");
        boolean coarse = this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean fine = this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        return coarse && fine;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                viewModel.setGotLocationPermissions(true);
                Log.d(TAG,"ALL PERMISSIONS ATTAINED");

                Intent serviceIntent = new Intent(this, LocationService.class);
                bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

                startService(serviceIntent);
            }
            } else {
                viewModel.setGotLocationPermissions(false);
                Toast.makeText(this, "Location permissions are needed for this feature", Toast.LENGTH_SHORT).show();
            }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        currLocation = findViewById(R.id.textView);
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        if(!viewModel.isGotLocationPermissions())
            getLocationPermssions();

        locationSource = new MyLocationSource();


        fragmentManager = getSupportFragmentManager();

        ImageView globeIcon = findViewById(R.id.globeIcon);
        globeIcon.setOnClickListener(v -> {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentHolder,MapsFragment.newInstance(locationSource),null)
                    .setReorderingAllowed(true)
                    .commit();
        });

        ImageView homeIcon = findViewById(R.id.homeIcon);
        homeIcon.setOnClickListener(v -> {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentHolder,HomeFragment.class,null)
                    .setReorderingAllowed(true)
                    .commit();
        });

        ImageView statsIcon = findViewById(R.id.statsIcon);
        statsIcon.setOnClickListener(v -> {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentHolder,StatFragment.class,null)
                    .setReorderingAllowed(true)
                    .commit();
        });

        ImageView movementIcon = findViewById(R.id.movementIcon);
        movementIcon.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,StartMovementActivity.class);
            startActivity(intent);
        });
    }
}