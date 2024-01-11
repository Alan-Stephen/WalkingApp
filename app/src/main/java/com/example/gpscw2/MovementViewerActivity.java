package com.example.gpscw2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Spinner;

import java.lang.reflect.Array;
import java.util.concurrent.Executors;

public class MovementViewerActivity extends AppCompatActivity {
    MovementViewViewModel viewModel;
    public static final String TODAY = "Today";
    public static final String LAST_WEEK = "Last Week";
    public static final String LAST_MONTH = "Last Month";
    public static final String ANY = "ANY";
    public static final String POSITIVE = "Positive";
    public static final String NEGATIVE = "Negative";
    public static final String RAIN = "Rain";
    public static final String SUN = "Sun";
    public static final String SNOW = "SNOW";
    RecyclerView recyclerView;
    Movement.MovementType type;
    MovementViewerActivity movementViewerActivity = this;
    Spinner filterTime;
    Spinner filterWeather;
    Spinner filterMovement;
    public void setUpRecycleView() {
        // to prevent blocking ui thread when calling load data
        // recyclerView needs access to data in viewModel which is populated with .loadData
        Executors.newSingleThreadExecutor().execute(() -> {
            viewModel.setType(type);
            viewModel.loadData();

            runOnUiThread(this::setUp);
        });
    }

    public void setUp() {
        recyclerView.setLayoutManager(new LinearLayoutManager(movementViewerActivity));
        MovementCardViewAdapter cardViewAdapter = new MovementCardViewAdapter(movementViewerActivity,
                movementViewerActivity.getLayoutInflater(),
                viewModel);
        recyclerView.setAdapter(cardViewAdapter);

        String[] timeOptions = {TODAY, LAST_WEEK, LAST_MONTH,ANY};
        String[] weatherOptions = {RAIN, SUN, SNOW,ANY};
        String[] movementOptions = {POSITIVE,NEGATIVE,ANY};

        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                timeOptions);
        ArrayAdapter<String> weatherAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                weatherOptions);
        ArrayAdapter<String> movementAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                movementOptions);

        filterTime.setAdapter(timeAdapter);
        filterWeather.setAdapter(weatherAdapter);
        filterMovement.setAdapter(movementAdapter);

        filterTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

                viewModel.setTimeFilter(filterOption);
                cardViewAdapter.notifyDataSetChanged();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                viewModel.setTimeFilter(TimeFilterOptions.ALL_TIME);
                cardViewAdapter.notifyDataSetChanged();
            }
        });
        filterWeather.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Weather weatherOption = null;
                if(weatherOptions[position].equals(RAIN)) {
                    weatherOption = Weather.RAIN;
                } else if(weatherOptions[position].equals(SUN)) {
                   weatherOption = Weather.SUN;
                } else if(weatherOptions[position].equals(SNOW)) {
                    weatherOption = Weather.SNOW;
                }

                viewModel.setWeatherFilter(weatherOption);
                cardViewAdapter.notifyDataSetChanged();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                viewModel.setWeatherFilter(null);
                cardViewAdapter.notifyDataSetChanged();
            }
        });
        filterMovement.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(movementOptions[position].equals(POSITIVE)) {
                    viewModel.setPositiveFilter(true);
                } else if (movementOptions[position].equals(NEGATIVE)) {
                    viewModel.setPositiveFilter(false);
                } else {
                    viewModel.setPositiveFilter(null);
                }
                cardViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                viewModel.setPositiveFilter(null);
                cardViewAdapter.notifyDataSetChanged();
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movement_viewer);

        type = Movement.MovementType.values()[getIntent().getIntExtra("movementType",0)];

        viewModel = new ViewModelProvider(this).get(MovementViewViewModel.class);
        recyclerView = findViewById(R.id.recyclerView);
        filterTime = findViewById(R.id.filterTime);
        filterWeather = findViewById(R.id.filterWeather);
        filterMovement = findViewById(R.id.filterMovement);

        setUpRecycleView();
    }
}