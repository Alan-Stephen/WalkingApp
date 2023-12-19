package com.example.gpscw2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "COMP3018";

    private MainActivityViewModel viewModel;
    private TextView currLocation;
    LocationService.LocationServiceBinder locationBinder;

    private void bindCurrLocation(MutableLiveData<Double> lat,MutableLiveData<Double> lon) {
        Log.d(TAG,"binding location to textview");
        currLocation.setText("hello");
        lat.observe(MainActivity.this, aDouble -> {
            viewModel.setLat(aDouble);

            Log.d(TAG,"changing lat");
            currLocation.setText(getString(R.string.currLocationResource,Double.toString(viewModel.getLat()),Double.toString(viewModel.getLat())));
        });
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            locationBinder = (LocationService.LocationServiceBinder) service;
            bindCurrLocation(locationBinder.getLat(),locationBinder.getLon());
            Log.d(TAG,"Location Serivce Bound to Main");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            locationBinder = null;
            Log.d(TAG,"Location Serivce Disconnected to Main");
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocationManager locationManager =
                (LocationManager)getSystemService(Context.LOCATION_SERVICE);


        setContentView(R.layout.activity_main);

        currLocation = findViewById(R.id.textView);

        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        Intent serviceIntent = new Intent(this, LocationService.class);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
};