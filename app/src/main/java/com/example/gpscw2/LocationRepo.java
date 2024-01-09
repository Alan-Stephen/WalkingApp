package com.example.gpscw2;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LocationRepo {

    private LocationNotificationDao notificationDao;
    private TravelDao travelDao;

    private Executor executor;
    private LiveData<List<LocationNotificationEntity>> allNotifications;
    private LiveData<List<TravelEntity>> allTravels;
    private LiveData<List<TravelEntity>> allRuns;
    private LiveData<List<TravelEntity>> allWalks;
    private LiveData<List<TravelEntity>> allCycles;
    private LiveData<TravelEntity> latestEntity;

    public LocationRepo(Application app) {
        LocationDatabase db = LocationDatabase.getInstance(app);
        notificationDao = db.locationNotificationDao();
        travelDao = db.travelDao();
        allNotifications = notificationDao.getAllNotifications();
        allTravels = travelDao.getTravels();
        allRuns = travelDao.getRuns();
        allWalks = travelDao.getWalks();
        allCycles = travelDao.getCycles();

        executor = Executors.newSingleThreadExecutor();
    }

    public String convertMovementTypeToString(Movement.MovementType type) {
        String movementType;
        switch (type) {
            case RUN:
                movementType = "RUN";
                break;
            case WALK:
                movementType = "WALK";
                break;
            case CYCLE:
                movementType = "CYCLE";
                break;
            default:
                movementType = "TRAVEL";
                break;
        }

        return movementType;
    }
    public void addDistanceToCurrentDate(int distance, Movement.MovementType type)  {
        executor.execute(() -> {
            TravelEntity entity = getLatestEntity(type);

            entity.setDistance(entity.getDistance() + distance);
            travelDao.update(entity);
        });
    }

    private TravelEntity getLatestEntity(Movement.MovementType type) {
        long date = LocalDate.now().toEpochDay();
        TravelEntity entity = travelDao.getEntityByDate(date,convertMovementTypeToString(type));

        if(entity == null) {
            TravelEntity newEntity = new TravelEntity(convertMovementTypeToString(type),date,0,
                    true,"",Weather.SUN,0);
            travelDao.insert(newEntity);
            entity = travelDao.getEntityByDate(date,convertMovementTypeToString(type));
        }

        return entity;
    };

    public void upsertTravel(TravelEntity entity) {
        travelDao.upsertTravel(entity);
    }
    public LiveData<List<TravelEntity>> getAllTravels() {
        return allTravels;
    }

    public LiveData<List<TravelEntity>> getAllRuns() {
        return allRuns;
    }

    public LiveData<List<TravelEntity>> getAllWalks() {
        return allWalks;
    }

    public LiveData<List<TravelEntity>> getAllCycles() {
        return allCycles;
    }

    public void insertTravel(TravelEntity entity) {
        executor.execute(() -> travelDao.insert(entity));
    }
    public void insertLocation(LocationNotificationEntity notification) {
        executor.execute(() -> notificationDao.insert(notification));
    }
    public void updateNotification(LocationNotificationEntity notification) {
        executor.execute(() -> notificationDao.update(notification));
    }
    public void deleteNotification(LocationNotificationEntity notification) {
        executor.execute(() -> notificationDao.delete(notification));
    }

    public void deleteById(int entityId) {
        executor.execute(() -> notificationDao.deleteById(entityId));
    }

    public LiveData<List<LocationNotificationEntity>> getAllNotifications() {
        return allNotifications;
    }
}
