package com.example.gpscw2;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MovementViewViewModel extends AndroidViewModel {

    private final String TAG = this.getClass().getSimpleName();
    private HashMap<Integer,TravelEntity> data;
    private ArrayList<TravelEntity> viewedData;
    private LocationRepo repo;
    private Movement.MovementType type;
    TimeFilterOptions timeFilterOption;
    Weather weatherFilterOption;
    Boolean positiveFitlerOption;

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
        repo.deleteNotificationById(id);
    }

    public void setTimeFilter(TimeFilterOptions filter) {
        this.timeFilterOption = filter;
        resetViewedEntities();
    }

    public void setWeatherFilter(Weather weather) {
        this.weatherFilterOption = weather;
        resetViewedEntities();
    }

    public void setPositiveFilter(Boolean filter) {
        this.positiveFitlerOption = filter;
        resetViewedEntities();
    }

    private void resetViewedEntities() {
        viewedData.clear();
        Log.d(TAG,"Changing data");
        for(TravelEntity entity: data.values()) {
            Log.d(TAG,"SCANNING");
            switch(timeFilterOption) {
                case TODAY:
                    if(LocalDate.now().toEpochDay() - entity.getDate() != 0) {
                        continue;
                    }
                case LAST_MONTH:
                    if (LocalDate.now().toEpochDay() - entity.getDate() > 30) {
                        continue;
                    }
                case LAST_WEEK:
                    if (LocalDate.now().toEpochDay() - entity.getDate() > 7) {
                        continue;
                    }
            }

            if(weatherFilterOption != entity.getWeather() && weatherFilterOption != null) {
                continue;
            }

            if(positiveFitlerOption != null && positiveFitlerOption == entity.isPositive()) {
                continue;
            }

            viewedData.add(entity);
            Log.d(TAG,"ENTITIES WHICH SHOULD BE VIEWED " + viewedData.size());
        }
    }
}