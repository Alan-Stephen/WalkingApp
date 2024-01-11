package com.example.gpscw2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.concurrent.Executors;

public class MovementViewerActivity extends AppCompatActivity {

    MovementViewViewModel viewModel;
    RecyclerView recyclerView;
    Movement.MovementType type;
    MovementViewerActivity movementViewerActivity = this;
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
        recyclerView.setAdapter(new MovementCardViewAdapter(movementViewerActivity,
                movementViewerActivity.getLayoutInflater(),
                viewModel));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movement_viewer);


        type = Movement.MovementType.values()[getIntent().getIntExtra("movementType",0)];

        viewModel = new ViewModelProvider(this).get(MovementViewViewModel.class);
        recyclerView = findViewById(R.id.recyclerView);
        setUpRecycleView();
    }
}