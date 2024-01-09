package com.example.gpscw2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class StartMovementActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private TextView distance;
    private TextView duration;
    private Button run;
    private Button walk;
    private Button cycle;

    StartActivityViewModel viewModel;

    public void startStopMovement(Movement.MovementType type) {
        if(viewModel.getMovementType().getValue() != null) {
            if(viewModel.getMovementType().getValue() != type) {
                Toast.makeText(this,
                        "You have to stop a movement before starting a new one",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            binder.stopCurrentMovement();

            viewModel.setMovementType(null);
            viewModel.setCurrMovement(null);
            handler.removeCallbacks(updateTimeRunnable);
            return;
        }

        if(binder == null) {
            return;
        }

        viewModel.setMovementType(type);
        binder.startMovement(type);

        viewModel.setCurrMovement(binder.getCurrMovement());
        viewModel.getCurrMovement().getTravelledMetres().observe(this, distanceMetres -> {
            distance.setText(getString(R.string.distanceLabel, distanceMetres));
        });

        handler.post(updateTimeRunnable);
    }

    public void updateEnables(Movement.MovementType type) {
        if(type == null) {
            run.setEnabled(true);
            walk.setEnabled(true);
            cycle.setEnabled(true);
            run.setText(getString(R.string.runButton));
            walk.setText(getString(R.string.walkButton));
            cycle.setText(getString(R.string.cycleButton));
            return;
        }

        switch(type) {
            case RUN:
                run.setEnabled(true);
                walk.setEnabled(false);
                cycle.setEnabled(false);
                run.setText(getString(R.string.stopRun));
                walk.setText(getString(R.string.walkButton));
                cycle.setText(getString(R.string.cycleButton));
                break;
            case CYCLE:
                run.setEnabled(false);
                walk.setEnabled(false);
                cycle.setEnabled(true);
                cycle.setText(getString(R.string.stop_cycling));
                run.setText(getString(R.string.runButton));
                walk.setText(getString(R.string.walkButton));
                break;
            case WALK:
                run.setEnabled(false);
                walk.setEnabled(true);
                cycle.setEnabled(false);
                walk.setText(getString(R.string.stop_walk));
                run.setText(getString(R.string.runButton));
                cycle.setText(getString(R.string.cycleButton));
                break;
        }
    }

    public void handleMovementAlreadyStarted() {
        viewModel.setCurrMovement(binder.getCurrMovement());
        viewModel.getCurrMovement().getTravelledMetres().observe(this, distanceMetres -> {
            distance.setText(getString(R.string.distanceLabel, distanceMetres));
        });

        viewModel.setMovementType(viewModel.getCurrMovement().getMovementType());
        handler.post(updateTimeRunnable);
        distance.setText(getString(R.string.distanceLabel,
                viewModel.getCurrMovement().getTravelledMetres().getValue()));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_movement);

        viewModel = new ViewModelProvider(this).get(StartActivityViewModel.class);


        duration = findViewById(R.id.durationTextView);
        duration.setText(getString(R.string.durationLabel,0,0,0));
        distance = findViewById(R.id.distanceTravelled);
        distance.setText(getString(R.string.distanceLabel,0));

        run = findViewById(R.id.startRun);
        walk = findViewById(R.id.startWalk);
        cycle = findViewById(R.id.startCycle);

        if(!isMyServiceRunning(LocationService.class)) {
            Log.d(TAG, "ERROR SERVICE IS NOT RUNNING");
            finish();
        }

        bindLocationService();

        run.setOnClickListener(v -> startStopMovement(Movement.MovementType.RUN));
        walk.setOnClickListener(v -> startStopMovement(Movement.MovementType.WALK));
        cycle.setOnClickListener(v -> startStopMovement(Movement.MovementType.CYCLE));

        viewModel.getMovementType().observe(this, movementType -> {
            updateEnables(movementType);
        });

        if(viewModel.getMovementType() != null) {
            // update to movement type in view model
            viewModel.setMovementType(viewModel.getMovementType().getValue());
        }
    }

    LocationService.LocationServiceBinder binder;

    private Handler handler = new Handler();
    private Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
           Log.d(TAG,"UPDATING TIME");
           updateTime();

           if(viewModel.getMovementType().getValue() != null) {
               handler.postDelayed(this, 1000);
           }
        }
    };

    private void updateTime() {
        if(viewModel.getCurrMovement() == null) {
            return;
        }

        LocalTime startTime = viewModel.getCurrMovement().getTimeStarted();
        LocalTime endTime = LocalTime.now();

        long difference = ChronoUnit.SECONDS.between(startTime,endTime);
        long hours = difference / 3600;
        long minutes = (difference / 3600) / 60;
        long seconds = difference % 60;
        duration.setText(getString(R.string.durationLabel,hours,minutes,seconds));
    }

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (LocationService.LocationServiceBinder) service;
            if(binder.getCurrMovement() != null) {
                Log.d(TAG,"movement already exists");
                handleMovementAlreadyStarted();
            }
            Log.d(TAG,"Service bound");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            binder = null;
            Log.d(TAG,"Service disconnected");
        }
    };

    private void bindLocationService() {
        Log.d(TAG,"ATTEMPTING SERVICE BINDING");
        bindService(new Intent(StartMovementActivity.this,LocationService.class),connection,Context.BIND_AUTO_CREATE);
    }



    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(connection != null) {
            Log.d(TAG,"UNBINDING SERVICE");
            unbindService(connection);
        }

        handler.removeCallbacks(updateTimeRunnable);

        Log.d(TAG,"DESTROYING" + this.getClass().getSimpleName());
    }
}