package com.example.gpscw2;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class LocationService extends Service {
    private static final String TAG = "COMP3018";
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "LocationServiceID";
    private static final String CHANNEL_NAME = "Location Notifications";
    private static final int MOVEMENT_NOTIFICATION_ID = 2;


    enum LocationAccuracy {
        HIGH_ACCURACY,
        LOW_ACCURACY,
        NO_UPDATES
    }

    private LocationAccuracy currLocationAccuracy;
    private LocationManager locationManager;
    MyLocationListener locationListener;
    private PowerManager.WakeLock wakeLock;


    private final IBinder binder = new LocationServiceBinder();

    public class LocationServiceBinder extends Binder {
        public MutableLiveData<Double> getLon() {
            return locationListener.getLon();
        }

        public MutableLiveData<Double> getLat() {
            return locationListener.getLat();
        }

        public void setAccuracy(LocationAccuracy accuracy) {
            setCurrLocationAccuracy(accuracy);
        }

        public MyLocationSource getLocationSource() {
            return locationListener.locationSource;
        }

        public Movement getCurrMovement() {
            return locationListener.getCurrMovement();
        }

        public Movement stopCurrentMovement() {
            // to save power
            setCurrLocationAccuracy(LocationAccuracy.LOW_ACCURACY);
            stopMovementNotification();
            return locationListener.stopMovement();
        }

        public void stopAndSaveMovement(String title, String description, boolean positive,
                                        Weather weather) {

            // save power
            setCurrLocationAccuracy(LocationAccuracy.LOW_ACCURACY);
            locationListener.stopAndSaveMovement(title,description, positive,weather);
        }

        public void startMovement(Movement.MovementType type) {
            setCurrLocationAccuracy(LocationAccuracy.HIGH_ACCURACY);
            createMovementNotification(type);
            locationListener.startMovement(type);
        }
    }

    private void stopMovementNotification() {
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.cancel(MOVEMENT_NOTIFICATION_ID);
    }

    private void createMovementNotification(Movement.MovementType type) {

        String title = "";
        switch(type) {
            case WALK:
                title = "We're tracking your walk!";
                break;
            case CYCLE:
                title = "We're tracking your cycle!";
                break;
            case RUN:
                title = "We're tracking your run!";
                break;
        }

        Intent intent  = new Intent(this, StartMovementActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent, PendingIntent.FLAG_IMMUTABLE);
        Notification notification = new Notification.Builder(this,CHANNEL_ID)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.globe_icon)
                .setContentIntent(pendingIntent)
                .build();

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.notify(MOVEMENT_NOTIFICATION_ID,notification);
    }

    private void setCurrLocationAccuracy(LocationAccuracy accuracy) {

        locationManager.removeUpdates(locationListener);
        currLocationAccuracy = accuracy;
        try {
            if(currLocationAccuracy == LocationAccuracy.LOW_ACCURACY) {
                Log.d(TAG,"SETTING LOCATION ACCURACY TO LOW ACCRAUCY");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000,
                        25,locationListener);
            } else if (currLocationAccuracy == LocationAccuracy.HIGH_ACCURACY){
                Log.d(TAG,"SETTING LOCATION ACCURACY TO HIGH ACCURACY");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000,
                        5,locationListener);
            }
        } catch (SecurityException e) {
            Log.d(TAG,e.toString());
        }
    }

    public void onCreate() {
        super.onCreate();

        currLocationAccuracy = LocationAccuracy.LOW_ACCURACY;
        Log.d(TAG, "Service created");
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        acquireWakeLock();
        Log.d(TAG, "Service started");

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        AtomicReference<Double> currLat = new AtomicReference<>((double) 0);
        AtomicReference<Double> currLon = new AtomicReference<>((double) 0);
        try {
            locationManager.getCurrentLocation(LocationManager.GPS_PROVIDER, null,
                    Executors.newSingleThreadExecutor(), location -> {
                currLon.set(location.getLongitude());
                currLat.set(location.getLatitude());
                    });
        } catch(SecurityException e) {
            Log.d(TAG,e.toString());
        }
        if(locationListener == null)
            locationListener = new MyLocationListener(20000,currLat.get(),currLon.get(),this);
        currLocationAccuracy = LocationAccuracy.LOW_ACCURACY;

        setCurrLocationAccuracy(currLocationAccuracy);

        startForeground(NOTIFICATION_ID, buildNotification());

        return START_STICKY;
    }

    private void acquireWakeLock() {
        PowerManager manager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = manager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "MDPCW2:MOVEMENT_TRACKING_WAKE_LOCK"
        );
        Log.d(TAG,"WAKE LOCK ACQUIRED");

        wakeLock.acquire();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification buildNotification() {
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                new Intent(this, MainActivity.class),
                PendingIntent.FLAG_IMMUTABLE
        );

        // Build the notification
        Notification notification = new Notification.Builder(this,CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText("Service is running in the foreground")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build();

        return notification;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationListener != null) {
            locationListener.removeObserver();
        }
        wakeLock.release();
        locationManager.removeUpdates(locationListener);
        Log.d(TAG,"Wake Lock Released");
        Log.d(TAG, "Service destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void activeLocationNotification(int id,String title, String description) {
        Intent intent = new Intent(this,MainActivity.class);

        intent.putExtra("fragmentToDisplay", MainActivityViewModel.MainFragments.MAPS);
        intent.putExtra("notificationID",id);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,
                intent,
                PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new Notification.Builder(this,CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(description)
                .setSmallIcon(R.drawable.globe_icon)
                .setContentIntent(pendingIntent)
                .build();
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.notify((int) System.currentTimeMillis(),notification);
    }
}
