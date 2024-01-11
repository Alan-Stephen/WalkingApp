package com.example.gpscw2;


import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "comp3018:HomeFragment";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private HomeFragmentViewModel viewModel;
    private MutableLiveData<Integer> distanceTravelled;
    private Button pauseAndPlayLocation;
    private Button travelled;
    private Button run;
    private Button walk;
    private Button cycle;

    public HomeFragment() {
        // Required empty public constructor
    }


    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) requireActivity().getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }


    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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

        if (isServiceRunning(LocationService.class)) {
            pauseAndPlayLocation.setText(R.string.pauseAndPlayAlt);
        } else {
            pauseAndPlayLocation.setText(R.string.pauseAndPlayInitial);
        }

        pauseAndPlayLocation.setOnClickListener(v -> {
            if(!isServiceRunning(LocationService.class)) {
                Log.d(TAG,"TRYING TO START SERVICE FROM FRAGMENT");
                Intent intent = new Intent(requireActivity(), LocationService.class);
                requireActivity().startService(intent);
                pauseAndPlayLocation.setText(R.string.pauseAndPlayAlt);
            } else {
                Log.d(TAG,"TRYING TO STOP SERVICE FROM FRAGMENT");
                Intent intent = new Intent(requireActivity(), LocationService.class);
                requireActivity().stopService(intent);
                pauseAndPlayLocation.setText(R.string.pauseAndPlayInitial);
            }
        });

        return view;
    }
}