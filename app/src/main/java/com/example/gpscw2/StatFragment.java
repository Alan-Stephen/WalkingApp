package com.example.gpscw2;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class StatFragment extends Fragment {

    public final String TAG = this.getClass().getSimpleName();
    public static final String TODAY = "Today";
    public static final String LAST_WEEK = "Last Week";
    public static final String LAST_MONTH = "Last Month";
    public static final String ANY = "ANY";
    public static final String RUN = "RUN";
    public static final String WALK = "WALK";
    public static final String CYCLE = "CYCLE";
    private Spinner movementAverage;
    private Spinner timeAverage;
    private Spinner travelTimeOption;
    private TextView averageDuration;
    private TextView averageDistance;
    private TextView durationImprovement;
    private TextView distanceImprovement;
    private TextView totalDistanceTravelled;
    private TextView travelDistanceImproved;
    private StartFragmentViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void loadAdapters() {
        String[] timeOptions = {TODAY, LAST_WEEK, LAST_MONTH,ANY};
        String[] movementOptions = {RUN,WALK,CYCLE};

        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item,
                timeOptions);
        ArrayAdapter<String> movementAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item,
                movementOptions);

        travelTimeOption.setAdapter(timeAdapter);
        timeAverage.setAdapter(timeAdapter);
        movementAverage.setAdapter(movementAdapter);

        travelTimeOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TimeFilterOptions filterOption;
                if(timeOptions[position].equals(TODAY)) {
                    filterOption = TimeFilterOptions.TODAY;
                } else if (timeOptions[position].equals(LAST_WEEK)) {
                    filterOption = TimeFilterOptions.LAST_WEEK;
                } else if(timeOptions[position].equals(LAST_MONTH)) {
                    filterOption = TimeFilterOptions.LAST_MONTH;
                } else {
                    filterOption = TimeFilterOptions.ALL_TIME;
                }

                viewModel.setTimeOptionTravels(filterOption);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                viewModel.setTimeOptionTravels(TimeFilterOptions.ALL_TIME);
            }
        });

        timeAverage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TimeFilterOptions filterOption;
                if(timeOptions[position].equals(TODAY)) {
                    filterOption = TimeFilterOptions.TODAY;
                } else if (timeOptions[position].equals(LAST_WEEK)) {
                    filterOption = TimeFilterOptions.LAST_WEEK;
                } else if(timeOptions[position].equals(LAST_MONTH)) {
                    filterOption = TimeFilterOptions.LAST_MONTH;
                } else {
                    filterOption = TimeFilterOptions.ALL_TIME;
                }

                viewModel.setTimeOption(filterOption);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                viewModel.setTimeOption(TimeFilterOptions.ALL_TIME);
            }
        });

        movementAverage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Movement.MovementType filterOption;
                switch(movementOptions[position]) {
                    case RUN:
                       filterOption = Movement.MovementType.RUN;
                       break;
                    case WALK:
                        filterOption = Movement.MovementType.WALK;
                        break;
                    default:
                        filterOption = Movement.MovementType.CYCLE;
                }

                viewModel.setMovementOption(filterOption);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                viewModel.setMovementOption(Movement.MovementType.RUN);
            }
        });
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stat, container, false);
        movementAverage = view.findViewById(R.id.movementAverage);
        timeAverage = view.findViewById(R.id.timeAverage);
        distanceImprovement = view.findViewById(R.id.distanceImprove);
        durationImprovement = view.findViewById(R.id.durationImprove);
        averageDistance = view.findViewById(R.id.averageDistance);
        averageDuration = view.findViewById(R.id.averageDuration);
        totalDistanceTravelled = view.findViewById(R.id.totalDistanceTravelled);
        travelDistanceImproved = view.findViewById(R.id.travelImprovement);
        travelTimeOption = view.findViewById(R.id.travelTime);

        viewModel = new ViewModelProvider(this).get(StartFragmentViewModel.class);
        viewModel.startObserving(getViewLifecycleOwner());

        loadAdapters();
        viewModel.getStats().observe(getViewLifecycleOwner(), stats -> {
            if(getActivity() == null ||  stats == null) {
                Log.d(TAG,"Context or Data is null");
                return;
            }

            Log.d(TAG,"altering view to stats change");
            distanceImprovement.setText(getActivity().getString(R.string.distance_improvement,
                    (int) stats.getDistanceImprovement()));
            averageDistance.setText(getActivity().getString(R.string.average_distance,
                    stats.getAverageDistance()));

            int totalSeconds = stats.getAverageDistance();
            int hours = totalSeconds / 3600;
            int minutes = (totalSeconds % 3600) / 60;
            int seconds = totalSeconds % 60;

            averageDuration.setText(getActivity().getString(R.string.average_duration,
                    hours,minutes,seconds));
            durationImprovement.setText(getActivity().getString(R.string.duration_imrpovement,
                    (int) stats.getSpeedImprovement()));
            travelDistanceImproved.setText(getActivity().getString(R.string.travel_distance_improvement,
                    stats.getTravelDistanceImproved()));
            totalDistanceTravelled.setText(getActivity().getString(R.string.total_distance_travelled,
                    stats.getDistanceTravelled()));
        });

        return view;
    }
}