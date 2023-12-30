package com.example.gpscw2;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class MakeMarkerActivityViewModel extends AndroidViewModel {

    LocationNotificationRepo repo;

    public LiveData<List<LocationNotificationEntity>> getAllNotifications() {
        return allNotifications;
    }

    private LiveData<List<LocationNotificationEntity>> allNotifications;
    public MakeMarkerActivityViewModel(@NonNull Application application) {
        super(application);
        repo = new LocationNotificationRepo(application);
        allNotifications = repo.getAllNotifications();
    }

    public void insert(LocationNotificationEntity notification) {
        repo.insert(notification);
    }
}
