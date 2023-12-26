package com.example.gpscw2;

import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.LocationSource;

public class MyLocationSource implements LocationSource {

    OnLocationChangedListener listener;

    double lastLat = 0;
    double lastLon = 0;
    @Override
    public void activate(@NonNull OnLocationChangedListener onLocationChangedListener) {
       listener = onLocationChangedListener;
    }

    public void alert(double lat, double lon) {
        lastLat = lat;
        lastLon = lon;
        if(listener == null) {
            return;
        }
        Location location = new Location("myProvider");
        location.setLongitude(lon);
        location.setLatitude(lat);

        listener.onLocationChanged(location);
    }

    public void relalert() {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLongitude(lastLon);
        location.setLatitude(lastLat);

        Log.d("LOCATION SOURCE", location.toString());

        listener.onLocationChanged(location);
    }

    @Override
    public void deactivate() {
    }
}
