package com.example.gpscw2;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyLocationListener implements LocationListener {
    private final String TAG = this.getClass().getSimpleName();
    private MutableLiveData<Double> lat;
    private MutableLiveData<Double> lon;
    ArrayList<LocationNotificationEntity> notifications;
    MyLocationSource locationSource;
    private boolean starting;
    private LocationService service;
    private LocationRepo repo;
    private Location lastLocation;
    private HashMap<Integer,Long> notificationTimeouts;
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
       notificationTimeouts = new HashMap<>();

       lastLocation = new Location("gps");
       lastLocation.setLatitude(lastLat);
       lastLocation.setLongitude(lastLon);

       Log.d("comp3018", lastLocation.toString());

       starting = true;
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

       repo = new LocationRepo(service.getApplication());

       liveData = repo.getAllNotifications();
       liveData.observeForever(notificationObserver);


       Log.d(this.getClass().getSimpleName(), "APPLICATION HASHCODE: " + service.getApplication().hashCode());
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

        // first step can produce invalid distances, so we ignore distance travelled.
        if(!starting) {
            int travelled = (int) location.distanceTo(lastLocation);

            Log.d(TAG,"travelled : " + travelled);
            if(currentMovement != null) {
                currentMovement.addToKmTravelled(travelled);
            }

            repo.addDistanceToCurrentDate(travelled, Movement.MovementType.TRAVEL);
        } else {
            repo.addDistanceToCurrentDate(0, Movement.MovementType.TRAVEL);
        }

        starting = false;
        lastLocation.setLongitude(location.getLongitude());
        lastLocation.setLatitude(location.getLatitude());
        Log.d(this.getClass().getSimpleName(), location.getLatitude() + " " + location.getLongitude());


        notifications.forEach(notification -> {
            Location notificationLocation = new Location(LocationManager.GPS_PROVIDER);

            notificationLocation.setLatitude(notification.getLat());
            notificationLocation.setLongitude(notification.getLon());

            Log.d("comp3018","" + location.distanceTo(notificationLocation) + " " + notification.getTitle() + " dist" + notification.getDistanceMetres());
            if(location.distanceTo(notificationLocation) <= notification.getDistanceMetres()) {
                if(notification.isRemoveAfterNotify()) {
                    repo.deleteById(notification.getId());
                } else {
                    if(notificationTimeouts.containsKey(notification.getId())) {
                        long timeElapsedSeconds = -1 * (notificationTimeouts.get(notification.getId()) - System.currentTimeMillis()) / 1000;
                        Log.d("comp3018","elapsed " + timeElapsedSeconds + " for " + notification.getTimeoutTimeSeconds());
                        if(timeElapsedSeconds < notification.getTimeoutTimeSeconds()) {
                            return;
                        }
                    }
                    notificationTimeouts.put(notification.getId(), System.currentTimeMillis());
                }
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

    public void startMovement(Movement.MovementType type) {
        currentMovement = new Movement(type);
    }

    public Movement getCurrMovement() {
        return currentMovement;
    }

    public Movement stopMovement() {
        Movement temp = currentMovement;
        currentMovement = null;
        return temp;
    }

    public void stopAndSaveMovement(String title, String description, boolean positive,
                                    Weather weather) {
        long difference = ChronoUnit.SECONDS.between(currentMovement.getTimeStarted(), LocalTime.now());
        repo.insertTravel(new TravelEntity(
                repo.convertMovementTypeToString(currentMovement.getMovementType()),
                LocalDate.now().toEpochDay(),
                currentMovement.getTravelledMetres().getValue(),
                positive,
                description,
                weather,
                difference,
                title
                ));
        stopMovement();
    }
}
