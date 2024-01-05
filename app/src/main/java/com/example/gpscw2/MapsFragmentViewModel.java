package com.example.gpscw2;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class MapsFragmentViewModel extends AndroidViewModel {
    private LocationRepo repo;
    private MutableLiveData<MapButtonState> buttonState;

    public LiveData<List<LocationNotificationEntity>> getAllNotifications() {
        return allNotifications;
    }

    public LiveData<MapButtonState> getButtonState() {
        return buttonState;
    }

    public void setButtonState(MapButtonState buttonState) {
        this.buttonState.setValue(buttonState);
    }

    private LiveData<List<LocationNotificationEntity>> allNotifications;

    public MapsFragmentViewModel(@NonNull Application application) {
        super(application);
        repo = new LocationRepo(application);
        allNotifications = repo.getAllNotifications();

        Log.d(this.getClass().getSimpleName(), "APPLICATION HASHCODE: " + application.hashCode());
        if(allNotifications.getValue() == null) {
            Log.d("VIEWMODEL","in view  model");
        }
        buttonState = new MutableLiveData<>();
        buttonState.setValue(MapButtonState.NONE);
    }

    public void insert(LocationNotificationEntity notification) {
        repo.insertLocation(notification);
    }
    public void deleteById(int id) {repo.deleteById(id);}

    public void update(LocationNotificationEntity notification) {
        repo.updateNotification(notification);
    }
    public void delete(LocationNotificationEntity notification) {
        repo.deleteNotification(notification);
    }

}
