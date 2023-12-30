package com.example.gpscw2;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LocationNotificationRepo {

    private LocationNotificationDao dao;

    private Executor executor;
    private LiveData<List<LocationNotificationEntity>> allNotifications;

    public LocationNotificationRepo(Application app) {
        LocationNotificationDatabase db = LocationNotificationDatabase.getInstance(app);
        dao = db.locationNotificationDao();
        allNotifications = dao.getAllNotifications();
        executor = Executors.newSingleThreadExecutor();
    }

    public void insert(LocationNotificationEntity notification) {
        executor.execute(() -> dao.insert(notification));
    }
    public void update(LocationNotificationEntity notification) {
        executor.execute(() -> dao.update(notification));
    }
    public void delete(LocationNotificationEntity notification) {
        executor.execute(() -> dao.delete(notification));
    }

    public LiveData<List<LocationNotificationEntity>> getAllNotifications() {
        if(allNotifications.getValue() == null) {
            Log.d("comp3018", "it null in repo");
        }
        return allNotifications;
    }
}
