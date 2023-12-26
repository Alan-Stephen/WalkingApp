package com.example.gpscw2;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.android.gms.maps.LocationSource;

import java.util.List;

public class MyLocationListener implements LocationListener {
    private MutableLiveData<Double> lat;
    private MutableLiveData<Double> lon;
    private int intervalSeconds;

    MyLocationSource locationSource;
    private boolean starting;

    private Location lastLocation;
    Movement currentMovement;
    MyLocationListener(int intervalSeconds, double lastLat, double lastLon) {
       super();
       currentMovement = null;
       lat = new MutableLiveData<>();
       lon = new MutableLiveData<>();

       lastLocation = new Location("gps");
       lastLocation.setLatitude(lastLat);
       lastLocation.setLongitude(lastLon);

       Log.d("comp3018", lastLocation.toString());
       this.intervalSeconds = intervalSeconds;

       starting = false;
       lon.setValue(lastLon);
       lat.setValue(lastLat);

       locationSource = new MyLocationSource();
       locationSource.alert(lastLat,lastLon);
    }

    public void setIntervalSeconds(int seconds) {
        intervalSeconds = seconds;
    }
    public MutableLiveData<Double> getLat() {
        return lat;
    }

    public MutableLiveData<Double> getLon() {
        return lon;
    }

    public void setStarting(boolean starting) {
        this.starting = starting;
    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        lon.setValue(location.getLongitude());
        lat.setValue(location.getLatitude());

        locationSource.alert(lat.getValue(), lon.getValue());
        if(!starting) {
            Log.d("comp3018", " Distance travellled : " + location.distanceTo(lastLocation) +
                    " from:" + lastLocation.toString() + " to :" + location.toString());

        }
        starting = false;
        lastLocation.setLongitude(location.getLongitude());
        lastLocation.setLatitude(location.getLatitude());
        Log.d("comp3018", location.getLatitude() + " " + location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("comp3018", "onStatusChanged: " + provider + " " + status);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        Log.d("comp3018", "onProviderEnabled: " + provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Log.d("comp3018", "onProviderDisabled: " + provider);
    }
}
