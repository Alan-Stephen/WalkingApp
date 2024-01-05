package com.example.gpscw2;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class MakeMarkerActivityViewModel extends AndroidViewModel {

    private MutableLiveData<Integer> notificationRange;
    private MutableLiveData<Integer> notificationConsumptionTime;
    private LocationRepo repo;
    private MutableLiveData<Boolean> removeAfterNotify;
    private LiveData<List<LocationNotificationEntity>> allNotifications;

    private String title;
    private String descrpition;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescrpition() {
        return descrpition;
    }

    public void setDescrpition(String descrpition) {
        this.descrpition = descrpition;
    }

    public MutableLiveData<Integer> getNotificationRange() {
        return notificationRange;
    }

    public void setNotificationRange(int notificationRange) {
        this.notificationRange.setValue(notificationRange);
    }

    public MutableLiveData<Integer> getNotificationConsumptionTime() {
        return notificationConsumptionTime;
    }

    public void setNotificationConsumptionTime(int notificationConsumptionTime) {
        this.notificationConsumptionTime.setValue(notificationConsumptionTime);
    }

    public MutableLiveData<Boolean> getRemoveAfterNotify() {
        return removeAfterNotify;
    }

    public void setRemoveAfterNotify(boolean removeAfterNotify) {
        this.removeAfterNotify.setValue(removeAfterNotify);
    }

    public MakeMarkerActivityViewModel(@NonNull Application application) {
        super(application);
        repo = new LocationRepo(application);
        allNotifications = repo.getAllNotifications();

        notificationRange = new MutableLiveData<>();
        notificationConsumptionTime = new MutableLiveData<>();
        removeAfterNotify = new MutableLiveData<>();

        setRemoveAfterNotify(true);
        setNotificationRange(200);
        setNotificationConsumptionTime(120);
        title = "";
        descrpition = "";
    }

    public void createLocationNotification(double lat, double lon) {
       LocationNotificationEntity notification = new LocationNotificationEntity(
              lat, lon, getNotificationRange().getValue(), getTitle(), getDescrpition(),
               removeAfterNotify.getValue(), notificationConsumptionTime.getValue()
       );

       repo.insertLocation(notification);
    }
}
