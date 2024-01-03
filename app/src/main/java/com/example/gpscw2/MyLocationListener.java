package com.example.gpscw2;

import android.app.Application;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import kotlin.Triple;

public class MyLocationListener implements LocationListener {
    private MutableLiveData<Double> lat;
    private MutableLiveData<Double> lon;
    private int intervalSeconds;

    /**
     * Helper class for use in this function only.
     * */
    ArrayList<LocationNotificationEntity> notifications;
    MyLocationSource locationSource;
    private boolean starting;
    private LocationService service;
    private Location lastLocation;
    LiveData<List<LocationNotificationEntity>> liveData;
    Observer<List<LocationNotificationEntity>> notificationObserver;
    Movement currentMovement;
    MyLocationListener(int intervalSeconds, double lastLat, double lastLon, LocationService service) {
       super();
       currentMovement = null;
       lat = new MutableLiveData<>();
       lon = new MutableLiveData<>();
       this.service = service;
       notifications = new ArrayList<>();

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


       notificationObserver = locationNotificationEntities -> {
           if(locationNotificationEntities == null) {
               return;
           }

           notifications.clear();
           notifications.addAll(locationNotificationEntities);
       };

       liveData = new LocationNotificationRepo(service.getApplication()).getAllNotifications();
       liveData.observeForever(notificationObserver);

       Log.d(this.getClass().getSimpleName(), "APPLICATION HASHCODE: " + service.getApplication().hashCode());
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


        notifications.forEach(notification -> {
            Location notificationLocation = new Location(LocationManager.GPS_PROVIDER);

            notificationLocation.setLatitude(notification.getLat());
            notificationLocation.setLongitude(notification.getLon());

            Log.d("comp3018","" + location.distanceTo(notificationLocation) + " " + notification.getTitle() + " dist" + notification.getDistanceMetres());
            if(location.distanceTo(notificationLocation) >= notification.getDistanceMetres()) {
                service.activeLocationNotification(notification.getId(),notification.getTitle(),notification.getDescription());
            }
        });
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

    public void removeObserver( ) {
        liveData.removeObserver(notificationObserver);
    }
}
