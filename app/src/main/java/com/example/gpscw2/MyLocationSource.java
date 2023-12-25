package com.example.gpscw2;

import android.location.Location;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.LocationSource;

public class MyLocationSource implements LocationSource {

    OnLocationChangedListener listener;
    @Override
    public void activate(@NonNull OnLocationChangedListener onLocationChangedListener) {
       listener = onLocationChangedListener;
    }

    public void alert(double lat, double lon) {
        if(listener == null) {
            return;
        }
        Location location = new Location("myProvider");
        location.setLongitude(lon);
        location.setLatitude(lat);

        listener.onLocationChanged(location);
    }

    @Override
    public void deactivate() {
    }
}
