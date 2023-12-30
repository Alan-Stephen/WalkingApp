package com.example.gpscw2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MapsFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String TAG = "COMP3018_MAPS_FRAGMENT";
    private static final int MAKE_MARKER_RESULT_CODE = 109;
    MyLocationSource locationSource;

    private boolean mapReady = false;
    private boolean markerSelected = false;

    private MapsFragmentViewModel viewModel;
    private Button confirmMarker;
    private Button discardMarker;

    ArrayList<Marker> markers;

    Marker currMarker = null;

    public static MapsFragment newInstance(MyLocationSource source) {
        

        MapsFragment fragment = new MapsFragment();
        fragment.locationSource = source;

        return fragment;
    }

    public static final int LOCATION_PERMISSION = 1922;
    GoogleMap map;
    MapsFragment mapsFragment = this;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            Log.d(TAG,"MAP READY");
            map = googleMap;
            map.setLocationSource(locationSource);
            if (ActivityCompat.checkSelfPermission(mapsFragment.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(mapsFragment.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG,"ERROR LOCATION PERMISSIONS NOT ENABLED IN MAPS");
                return;
            }
            map.setMyLocationEnabled(true);
            locationSource.relalert();
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locationSource.lastLat,locationSource.lastLon),15.0F));
            mapReady = true;

            bindMapMarking();
            createInitialMarkers();
        }
    };

    private void createInitialMarkers() {
        markers = new ArrayList<>();
        refreshMarkers();
    }

    private void bindMapMarking() {
        map.setOnMapClickListener(latLng -> {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title("Here's where your reminder will be set!");
            if(currMarker != null) {
                currMarker.remove();
            }
           currMarker = map.addMarker(options);
           markerSelected = true;
           switchVisiblities();
        });


    }

    private void switchVisiblities() {
        if(markerSelected) {
            confirmMarker.setVisibility(View.VISIBLE);
            discardMarker.setVisibility(View.VISIBLE);
        } else {
            confirmMarker.setVisibility(View.INVISIBLE);
            discardMarker.setVisibility(View.INVISIBLE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_maps, container, false);


        confirmMarker = view.findViewById(R.id.confirmButton);
        discardMarker = view.findViewById(R.id.discardButton);

        confirmMarker.setOnClickListener(v -> {
            markerSelected = false;
            Intent intent = new Intent(getActivity(),MakeMarkerActivity.class);
            intent.putExtra("lat",currMarker.getPosition().latitude);
            intent.putExtra("lon",currMarker.getPosition().longitude);

            startActivity(intent);
            switchVisiblities();
            refreshMarkers();
        });
        discardMarker.setOnClickListener(v -> {
            markerSelected = false;
            currMarker.remove();
            currMarker = null;
            switchVisiblities();
        });

        return view;
    }

    private void refreshMarkers() {
        if(markers == null) {
            return;
        }
        markers.forEach(Marker::remove);

        markers.clear();
        
        if(viewModel.getAllNotifications().getValue() == null) {
            Log.d(TAG,"it null");
            return;
        }
        viewModel.getAllNotifications().getValue().forEach(notification -> {
            MarkerOptions options = new MarkerOptions()
                    .position(new LatLng(notification.getLat(),notification.getLon()));

            Marker marker = map.addMarker(options);

            markers.add(marker);
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(MapsFragmentViewModel.class);

        viewModel.getAllNotifications().observe(getActivity(), locationNotificationEntities -> {
            refreshMarkers();
        });
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}