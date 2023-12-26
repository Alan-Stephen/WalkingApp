package com.example.gpscw2;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.List;

public class MyLocationListener implements LocationListener {
    private MutableLiveData<Double> lat;
    private MutableLiveData<Double> lon;

    MyLocationListener() {
       super();
       lat = new MutableLiveData<>();
       lon = new MutableLiveData<>();

       lat.setValue(0.0);
       lon.setValue(0.0);
    }

    public MutableLiveData<Double> getLat() {
        return lat;
    }

    public MutableLiveData<Double> getLon() {
        return lon;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        lat.setValue(location.getLatitude());
        lon.setValue(location.getLongitude());
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

    public void setInitialLocation(Location intial) {
        if(intial != null) {
            lat.setValue(intial.getLatitude());
            lon.setValue(intial.getLongitude());
        } else {
            lat.setValue(0.0);
            lon.setValue(0.0);
        }
    }


}
