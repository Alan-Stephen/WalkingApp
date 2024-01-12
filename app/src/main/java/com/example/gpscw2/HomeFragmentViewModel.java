package com.example.gpscw2;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.Executor;

public class HomeFragmentViewModel extends AndroidViewModel {
    private LocationRepo repo;
    private LiveData<List<TravelEntity>> travelEntities;
    private LiveData<List<TravelEntity>> walkEntities;
    private LiveData<List<TravelEntity>> runEntities;
    private LiveData<List<TravelEntity>> cycleEntities;

    public HomeFragmentViewModel(@NonNull Application application) {
        super(application);

        repo = new LocationRepo(application);
        travelEntities = repo.getTravelEntitiesLiveData();
        walkEntities = repo.getWalkEntitiesLiveData();
        runEntities = repo.getRunEntitiesLiveData();
        cycleEntities = repo.getCycleEntitiesLiveData();
    }


    public LiveData<List<TravelEntity>> getWalkEntities() {
        return walkEntities;
    }

    public LiveData<List<TravelEntity>> getRunEntities() {
        return runEntities;
    }

    public LiveData<List<TravelEntity>> getCycleEntities() {
        return cycleEntities;
    }

    public LiveData<List<TravelEntity>> getTravelEntities() {
        return travelEntities;
    }
}
