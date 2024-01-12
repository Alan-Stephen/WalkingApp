package com.example.gpscw2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.ActivityManager;
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

public class MainActivity extends AppCompatActivity implements CanSetLocationSource {
    MyLocationSource locationSource;
    private static final String TAG = "COMP3018";
    private MainActivityViewModel viewModel;
    FragmentManager fragmentManager;
    HomeFragment homeFragment;
    StatFragment statFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        fragmentManager = getSupportFragmentManager();

        homeFragment = new HomeFragment();
        statFragment = new StatFragment();

        ImageView globeIcon = findViewById(R.id.globeIcon);
        globeIcon.setOnClickListener(v -> {
            viewModel.setCurrFragment(MainActivityViewModel.MainFragments.MAPS);
            if (locationSource == null) {
                Toast.makeText(this, "Location Tracking is Required for this feature, " +
                        "Please enable them in home!", Toast.LENGTH_SHORT).show();
                return;
            }

            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentHolder, MapsFragment.newInstance(locationSource), null)
                    .setReorderingAllowed(true)
                    .commit();
        });

        ImageView homeIcon = findViewById(R.id.homeIcon);
        homeIcon.setOnClickListener(v -> {

            viewModel.setCurrFragment(MainActivityViewModel.MainFragments.MAIN);
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentHolder, homeFragment, null)
                    .setReorderingAllowed(true)
                    .commit();
        });

        ImageView statsIcon = findViewById(R.id.statsIcon);
        statsIcon.setOnClickListener(v -> {
            viewModel.setCurrFragment(MainActivityViewModel.MainFragments.STATS);
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentHolder, statFragment, null)
                    .setReorderingAllowed(true)
                    .commit();
        });

        ImageView movementIcon = findViewById(R.id.movementIcon);
        movementIcon.setOnClickListener(v -> {

            if (locationSource == null) {
                Toast.makeText(this, "Location Tracking is Required for this feature, " +
                        "please enable them in home", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(MainActivity.this, StartMovementActivity.class);
            startActivity(intent);
        });

        fragmentManager.beginTransaction()
                .replace(R.id.fragmentHolder, homeFragment, null)
                .setReorderingAllowed(true)
                .commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.d(TAG, "SAVING INSTANCE");

        outState.putDouble("lat", viewModel.getLat());
        outState.putDouble("lon", viewModel.getLon());

        outState.putInt("currentFragment", viewModel.getCurrFragment().ordinal());
    }

    @Override
    public void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);

        Log.d(TAG, "RESTORING INSTANCE");

        viewModel.setLat(inState.getDouble("lat"));
        viewModel.setLat(inState.getDouble("lon"));

        viewModel.setCurrFragment(MainActivityViewModel.MainFragments.values()[inState.getInt("currentFragment")]);

        fragmentManager = getSupportFragmentManager();

        if (viewModel.getCurrFragment() == MainActivityViewModel.MainFragments.MAPS) {
            if (locationSource == null) {
                return;
            }
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentHolder, MapsFragment.newInstance(locationSource), null)
                    .setReorderingAllowed(true)
                    .commit();
        }
    }

    @Override
    public void setLocationSource(MyLocationSource source) {
        locationSource = source;
    }
}