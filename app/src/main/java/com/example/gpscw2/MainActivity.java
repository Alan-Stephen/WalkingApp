package com.example.gpscw2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "COMP3018";
    private MainActivityViewModel viewModel;
    private TextView currLocation;
    LocationService.LocationServiceBinder locationBinder;

    GoogleMap map;
    FragmentManager fragmentManager;

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

        setContentView(R.layout.activity_main);

        currLocation = findViewById(R.id.textView);

        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        Intent serviceIntent = new Intent(this, LocationService.class);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        fragmentManager = getSupportFragmentManager();

        ImageView globeIcon = findViewById(R.id.globeIcon);
        globeIcon.setOnClickListener(v -> {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentHolder,MapsFragment.class,null)
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
    }
}