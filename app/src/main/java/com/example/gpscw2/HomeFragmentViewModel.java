package com.example.gpscw2;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executor;

public class HomeFragmentViewModel extends AndroidViewModel {
    private LocationRepo repo;
    private LiveData<List<TravelEntity>> travelEntities;

    public HomeFragmentViewModel(@NonNull Application application) {
        super(application);

        repo = new LocationRepo(application);
        travelEntities = repo.getAllTravels();
    }

    public LiveData<List<TravelEntity>> getTravelEntities() {
        return travelEntities;
    }
}
