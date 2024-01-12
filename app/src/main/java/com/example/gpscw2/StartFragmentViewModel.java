package com.example.gpscw2;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class StartFragmentViewModel extends AndroidViewModel {

    private final String TAG = this.getClass().getSimpleName();
    private Movement.MovementType movementOption;
    private TimeFilterOptions timeOption;
    private TimeFilterOptions timeOptionTravels;
    private LocationRepo repo;
    private LiveData<List<TravelEntity>> data;
    private MutableLiveData<Stats> stats;
    private List<TravelEntity> currentEntities;
    private List<TravelEntity> averageEntities;
    private List<TravelEntity> travelEntities;

    public StartFragmentViewModel(@NonNull Application application) {
        super(application);
        repo = new LocationRepo(application);

        movementOption = Movement.MovementType.RUN;
        timeOption =  TimeFilterOptions.TODAY;
        timeOptionTravels = TimeFilterOptions.TODAY;
        data = repo.getAllTravelEntities();
        averageEntities = new ArrayList<>();
        travelEntities = new ArrayList<>();
        currentEntities = new ArrayList<>();
        stats = new MutableLiveData<>();
        stats.setValue(new Stats());
    }

    public void setMovementOption(Movement.MovementType movementOption) {
        this.movementOption = movementOption;
        refreshStats();
    }

    public void setTimeOption(TimeFilterOptions timeOption) {
        this.timeOption = timeOption;
        refreshStats();
    }

    public void setTimeOptionTravels(TimeFilterOptions timeOptionTravels) {
        this.timeOptionTravels = timeOptionTravels;
        refreshStats();
    }

    public void startObserving(LifecycleOwner context) {
        data.observe(context, entities -> {
            if(entities == null) {
                return;
            }

            currentEntities = entities;


            refreshStats();
        });
    }

    public void updateMovementAndTravelLists(){
        averageEntities.clear();
        travelEntities.clear();
        currentEntities.forEach(new Consumer<TravelEntity>() {
            public boolean averageTest(TravelEntity entity) {
                if (parseMovementFromString(entity.getMovementType()) != movementOption) {
                    return false;
                }
                return true;
            }

            public boolean travelTest(TravelEntity entity) {
                return (parseMovementFromString(entity.getMovementType()) == Movement.MovementType.TRAVEL);
            }
            @Override
            public void accept(TravelEntity entity) {
                Log.d(TAG,"entity movement type " + entity.getMovementType());
                if(averageTest(entity)) {
                    averageEntities.add(entity);
                }

                if(travelTest(entity)) {
                    Log.d(TAG,"ADDING TO TRAVEL");
                    travelEntities.add(entity);
                }
            }
        });
    }

    private void refreshStats() {
        updateMovementAndTravelLists();
        int thisTimeCount = 0;
        double thisTimeTotalDistance = 0;
        double lastTimeTotalDistance = 0;
        long thisTimeTotalDuration = 0;
        long lastTimeTotalDuration = 0;
        double thisTimeSpeed = 0;
        double lastTimeSpeed = 0;
        int timeFrame = convertTimeToDays(timeOption);
        int lastTimeCount = 0;

        Log.d(TAG," REFRESHING WITH averageEntities = " + averageEntities.size());
        Log.d(TAG," REFRESHING WITH travelEntities = " + travelEntities.size());
        Log.d(TAG," WITH TIME FRAME " + timeFrame);
        for(TravelEntity entity: averageEntities) {
            Log.d(TAG,"calculating");
            long difference = LocalDate.now().toEpochDay() - entity.getDate();
            if(difference > 2L *timeFrame) {
                Log.d(TAG,"skipping");
                continue;
            }

            if(difference < timeFrame) {
                thisTimeCount += 1;
                thisTimeTotalDistance += entity.getDistance();
                thisTimeTotalDuration += entity.getLengthSeconds();
                if(entity.getLengthSeconds() == 0) {
                    continue;
                }
                thisTimeSpeed += (double) entity.getDistance() / entity.getLengthSeconds();
                continue;
            }

            lastTimeCount += 1;
            lastTimeTotalDistance += entity.getDistance();
            lastTimeTotalDuration += entity.getLengthSeconds();
            if(entity.getLengthSeconds() == 0) {
                continue;
            }
            lastTimeSpeed += (double) entity.getDistance() / entity.getLengthSeconds();
        }

        double thisTimeAverageDistance = thisTimeTotalDistance / thisTimeCount;
        double thisTimeAverageDuration = (double) thisTimeTotalDuration / thisTimeCount;
        double lastTimeAverageDistance = lastTimeTotalDistance / lastTimeCount;
        double lastTimeAverageDuration = (double) lastTimeTotalDuration / lastTimeCount;
        double lastTimeSpeedAverage = (double) lastTimeSpeed / lastTimeCount;
        double thisTimeSpeedAverage = (double) thisTimeSpeed / lastTimeCount;
        double speedImprovement = (lastTimeSpeedAverage - thisTimeSpeedAverage) / lastTimeSpeedAverage;
        double distanceImprovement = (lastTimeAverageDistance - thisTimeAverageDistance) / lastTimeAverageDistance;
        double durationImprovement = (lastTimeAverageDuration - thisTimeAverageDuration) / lastTimeAverageDuration;

        Stats updatedStats = new Stats();
        updatedStats.setAverageDistance((int) thisTimeAverageDistance);
        updatedStats.setAverageDuration((long) thisTimeAverageDuration);

        if(!Double.isInfinite(thisTimeSpeedAverage)) {
            updatedStats.setAverageSpeed(thisTimeSpeedAverage);
        }

        updatedStats.setDurationImprovement(durationImprovement);
        updatedStats.setDistanceImprovement(distanceImprovement);
        updatedStats.setDistanceImprovement(speedImprovement);

        int thisDistance = 0;
        int thisCount = 0;
        int lastDistance = 0;
        int lastCount = 0;
        timeFrame = convertTimeToDays(timeOptionTravels);
        for (TravelEntity entity: travelEntities) {
            long difference = LocalDate.now().toEpochDay() - entity.getDate();
            if(difference > 2L *Integer.max(timeFrame,1)) {
                continue;
            }

            if(difference < timeFrame) {
                thisDistance += entity.getDistance();
                thisCount += 1;
                continue;
            }

            lastCount += 1;
            lastDistance += entity.getDistance();
        }

        double lastAverage = (double) lastDistance / lastCount;
        double thisAverage = (double) thisDistance / thisCount;
        updatedStats.setTravelDistanceImproved((int) ((int) (lastAverage - thisAverage) / lastAverage));
        updatedStats.setDistanceTravelled(thisDistance);

        stats.setValue(updatedStats);
    }

    private Movement.MovementType parseMovementFromString(String movementType) {
        switch (movementType) {
            case "RUN":
                return Movement.MovementType.RUN;
            case "WALK":
                return Movement.MovementType.WALK;
            case "CYCLE":
                return Movement.MovementType.CYCLE;
            default:
                return Movement.MovementType.TRAVEL;
        }
    }

    private int convertTimeToDays(TimeFilterOptions time) {
        switch (time) {
            case ALL_TIME:
                return Integer.MAX_VALUE;
            case TODAY:
                return 1;
            case LAST_WEEK:
                return 7;
            case LAST_MONTH:
                return 30;
        }
        return 0;
    }

    public MutableLiveData<Stats> getStats() {
        return stats;
    }
}
