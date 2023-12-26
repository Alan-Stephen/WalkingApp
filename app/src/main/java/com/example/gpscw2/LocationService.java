package com.example.gpscw2;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public class LocationService extends Service {
    private static final String TAG = "COMP3018";
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "LocationServiceID";
    private static final String CHANNEL_NAME = "Location Notifications";

    enum LocationAccuracy {
        HIGH_ACCURACY,
        LOW_ACCURACY
    }

    private LocationAccuracy currLocationAccuracy;
    MyLocationListener locationListener;

    private final IBinder binder = new LocationServiceBinder();

    public class LocationServiceBinder extends Binder {
        public MutableLiveData<Double> getLon() {
            return locationListener.getLon();
        }

        public MutableLiveData<Double> getLat() {
            return locationListener.getLat();
        }
    }

    public void onCreate() {
        super.onCreate();

        if(locationListener == null)
            locationListener = new MyLocationListener();

        currLocationAccuracy = LocationAccuracy.LOW_ACCURACY;
        Log.d(TAG, "Service created");
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");

        if(locationListener == null)
            locationListener = new MyLocationListener();
        currLocationAccuracy = LocationAccuracy.HIGH_ACCURACY;
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        try {
            if(currLocationAccuracy == LocationAccuracy.LOW_ACCURACY) {
                Log.d(TAG,"SETTING LOCATION ACCURACY TO LOW ACCRAUCY");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000,
                        25,locationListener);
            } else {
                Log.d(TAG,"SETTING LOCATION ACCURACY TO HIGH ACCURACY");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000,
                        5,locationListener);
            }
            Location intial = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            locationListener.setInitialLocation(intial);
        } catch (SecurityException e) {
            Log.d(TAG,e.toString());
        }

        startForeground(NOTIFICATION_ID, buildNotification());

        return START_STICKY;
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
        Log.d(TAG, "Service destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

}
