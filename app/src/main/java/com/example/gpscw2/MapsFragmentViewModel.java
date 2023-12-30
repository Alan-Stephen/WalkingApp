package com.example.gpscw2;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class MapsFragmentViewModel extends AndroidViewModel {
    private LocationNotificationRepo repo;

    public LiveData<List<LocationNotificationEntity>> getAllNotifications() {
        return allNotifications;
    }

    private LiveData<List<LocationNotificationEntity>> allNotifications;

    public MapsFragmentViewModel(@NonNull Application application) {
        super(application);
        repo = new LocationNotificationRepo(application);
        allNotifications = repo.getAllNotifications();
        if(allNotifications == null) {
            Log.d("VIEWMODEL","in view  model");
        }
    }

    public void insert(LocationNotificationEntity notification) {
        repo.insert(notification);
    }

    public void update(LocationNotificationEntity notification) {
        repo.update(notification);
    }
    public void delete(LocationNotificationEntity notification) {
        repo.delete(notification);
    }

}
