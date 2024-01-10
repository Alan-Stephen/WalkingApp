package com.example.gpscw2;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class FinishMovementActivity extends AppCompatActivity {

    private EditText title;
    private final String TAG = this.getClass().getSimpleName();
    private EditText description;
    private ImageView goodMovement;
    private ImageView badMovement;
    private ImageView sunWeather;
    private ImageView rainWeather;
    private ImageView snowWeather;
    private Button delete;
    private Button record;
    private Button exit;
    FinishMovementViewModel viewModel;

    LocationService.LocationServiceBinder binder;
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG,"BINDER CONNECTED");
            binder = (LocationService.LocationServiceBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            binder = null;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_movement);

        viewModel = new ViewModelProvider(this).get(FinishMovementViewModel.class);

        Intent intent = new Intent(FinishMovementActivity.this,LocationService.class);
        bindService(intent,connection,BIND_AUTO_CREATE);

        title = findViewById(R.id.titleInput);
        description = findViewById(R.id.descriptionInput);
        goodMovement = findViewById(R.id.goodMovement);
        badMovement = findViewById(R.id.badMovement);
        sunWeather = findViewById(R.id.sunWeather);
        rainWeather = findViewById(R.id.rainWeather);
        snowWeather = findViewById(R.id.snowWeather);

        delete = findViewById(R.id.deleteMovement);
        record = findViewById(R.id.recordMovement);
        exit = findViewById(R.id.exitMovementFinish);


        delete.setOnClickListener(v -> {
            if(binder == null) {
                return;
            }
            Intent resultIntent = new Intent();

            resultIntent.putExtra("status",FinishMovementResultStatus.DISCARD.ordinal());
            setResult(RESULT_OK,resultIntent);

            binder.stopCurrentMovement();
            finish();
        });

        record.setOnClickListener(v -> {
            if(binder == null) {
                return;
            }

            Intent resultIntent = new Intent();

            resultIntent.putExtra("status",FinishMovementResultStatus.SAVE.ordinal());
            setResult(RESULT_OK,resultIntent);

            binder.stopAndSaveMovement(
                    viewModel.getTitle(),
                    viewModel.getDescription(),
                    viewModel.getPositive().getValue(),
                    viewModel.getWeather().getValue()
            );

            finish();
        });

        exit.setOnClickListener(v -> {
            Intent resultIntent = new Intent();

            resultIntent.putExtra("status",FinishMovementResultStatus.CANCEL.ordinal());
            setResult(RESULT_OK,resultIntent);
            finish();
        });

        sunWeather.setOnClickListener(v -> viewModel.setWeather(Weather.SUN));
        rainWeather.setOnClickListener(v -> viewModel.setWeather(Weather.RAIN));
        snowWeather.setOnClickListener(v -> viewModel.setWeather(Weather.SNOW));

        goodMovement.setOnClickListener(v -> viewModel.setPositive(true));
        badMovement.setOnClickListener(v -> viewModel.setPositive(false));

        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setTitle(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setDescription(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        viewModel.getPositive().observe(this, positive -> {
            if(positive) {
                badMovement.setColorFilter(Color.argb(150,0,0,0));
                goodMovement.setColorFilter(Color.argb(0,0,0,0));
            } else {
                badMovement.setColorFilter(Color.argb(0,0,0,0));
                goodMovement.setColorFilter(Color.argb(150,0,0,0));
            }
        });

        viewModel.getWeather().observe(this, weather -> {
            Log.d(TAG,"CHANGING WEATHER");
            switch(weather) {
                case SUN:
                    sunWeather.setColorFilter(Color.argb(0,0,0,0));
                    snowWeather.setColorFilter(Color.argb(155,0,0,0));
                    rainWeather.setColorFilter(Color.argb(155,0,0,0));
                    break;
                case RAIN:
                    sunWeather.setColorFilter(Color.argb(155,0,0,0));
                    snowWeather.setColorFilter(Color.argb(155,0,0,0));
                    rainWeather.setColorFilter(Color.argb(0,0,0,0));
                    break;
                case SNOW:
                    sunWeather.setColorFilter(Color.argb(155,0,0,0));
                    snowWeather.setColorFilter(Color.argb(0,0,0,0));
                    rainWeather.setColorFilter(Color.argb(155,0,0,0));
                    break;
            }
        });
    }
}