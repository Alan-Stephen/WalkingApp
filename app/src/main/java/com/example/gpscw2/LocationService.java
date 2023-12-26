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
    }

    private void setCurrLocationAccuracy(LocationAccuracy accuracy) {
        locationManager.removeUpdates(locationListener);

        locationManager.removeUpdates(locationListener);
        currLocationAccuracy = accuracy;
        try {
            if(currLocationAccuracy == LocationAccuracy.LOW_ACCURACY) {
                Log.d(TAG,"SETTING LOCATION ACCURACY TO LOW ACCRAUCY");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000,
                        25,locationListener);
                locationListener.setIntervalSeconds(20000);
            } else if (currLocationAccuracy == LocationAccuracy.HIGH_ACCURACY){
                Log.d(TAG,"SETTING LOCATION ACCURACY TO HIGH ACCURACY");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000,
                        5,locationListener);
                locationListener.setIntervalSeconds(2000);
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
            locationListener = new MyLocationListener(20000,currLat.get(),currLon.get());
        currLocationAccuracy = LocationAccuracy.HIGH_ACCURACY;

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
        wakeLock.release();
        Log.d(TAG,"Wake Lock Released");
        Log.d(TAG, "Service destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

}
