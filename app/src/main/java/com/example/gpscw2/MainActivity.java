package com.example.gpscw2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

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
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {


    public static final int LOCATION_PERMISSION = 1922;
    private static final String TAG = "COMP3018";

    private MainActivityViewModel viewModel;
    private TextView currLocation;
    LocationService.LocationServiceBinder locationBinder;

    GoogleMap map;

    private void bindCurrLocation(MutableLiveData<Double> lat, MutableLiveData<Double> lon) {
        Log.d(TAG, "binding location to textview");
        currLocation.setText("hello");
        lat.observe(MainActivity.this, aDouble -> {
            viewModel.setLat(aDouble);

            Log.d(TAG, "changing lat");
            currLocation.setText(getString(R.string.currLocationResource, Double.toString(viewModel.getLat()), Double.toString(viewModel.getLat())));
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        setContentView(R.layout.activity_main);

        currLocation = findViewById(R.id.textView);

        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        //Intent serviceIntent = new Intent(this, LocationService.class);
        //bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION);
            return;
        }
        map.setMyLocationEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                map.setMyLocationEnabled(true);
            } else {
                Log.d(TAG,"Do something else");
            }
        }
    }
}