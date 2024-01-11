package com.example.gpscw2;

import android.app.Application;
import android.location.Location;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class MovementViewViewModel extends AndroidViewModel {

    private final String TAG = this.getClass().getSimpleName();
    private HashMap<Integer,TravelEntity> data;
    private ArrayList<TravelEntity> viewedData;
    private LocationRepo repo;
    private Movement.MovementType type;

    public MovementViewViewModel(@NonNull Application application) {
        super(application);
        repo = new LocationRepo(application);

        type = Movement.MovementType.RUN;
        data = new HashMap<>();
        viewedData = new ArrayList<>();
    }

    public void setType(Movement.MovementType type) {
        this.type = type;
    }
    public void loadData() {
        List<TravelEntity> entities = repo.getTravelEntities(type);

        if(entities == null) {
            Log.d(TAG,"ENTITIES DO NOT CONTAIN ANYTHING");
            return;
        }

        viewedData = new ArrayList<>(entities);
        entities.forEach(entity -> {
            data.put(entity.getId(),entity);
        });
    }

    public List<TravelEntity> getData() {
        return viewedData;
    }

    public Movement.MovementType getType() {
        return type;
    }

    public void saveChanges(int id, boolean positive, Weather weather, String title, String description) {
        TravelEntity entity = data.get(id);

        if(entity == null) {
            Log.d(TAG,"ENTITY FOR ID " + id  + " IS NULL");
            return;
        }

        if (entity.getId() != id) {
            Log.d(TAG,"MIS MATCH IN IDs");
        }

        Log.d(TAG,"saving entity with " + positive + " " + weather + " ");
        entity.setPositive(positive);
        entity.setWeather(weather);
        entity.setTitle(title);
        entity.setDescription(description);

        repo.insertTravel(entity);
    }

    public void deleteById(int id) {
        TravelEntity entity = data.remove(id);
        viewedData.remove(entity);
        repo.deleteById(id);
    }
}