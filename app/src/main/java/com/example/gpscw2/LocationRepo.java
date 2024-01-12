package com.example.gpscw2;

import android.content.Context;
import android.database.Cursor;

import androidx.lifecycle.LiveData;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LocationRepo {

    private LocationNotificationDao notificationDao;
    private TravelDao travelDao;
    private LiveData<List<TravelEntity>> travelEntitiesLiveData;
    private LiveData<List<TravelEntity>> runEntitiesLiveData;
    private LiveData<List<TravelEntity>> walkEntitiesLiveData;
    private LiveData<List<TravelEntity>> cycleEntitiesLiveData;
    private Executor executor;
    private LiveData<List<LocationNotificationEntity>> allNotifications;
    private LiveData<List<TravelEntity>> allTravelEntities;

    public LocationRepo(Context app) {
        LocationDatabase db = LocationDatabase.getInstance(app);
        notificationDao = db.locationNotificationDao();
        travelDao = db.travelDao();
        allNotifications = notificationDao.getAllNotifications();
        travelEntitiesLiveData = travelDao.getTravelLiveData();
        runEntitiesLiveData = travelDao.getRunsLiveData();
        walkEntitiesLiveData = travelDao.getWalksLiveData();
        cycleEntitiesLiveData = travelDao.getCyclesLiveData();
        allTravelEntities = travelDao.getAllTravelEntitiesLiveData();

        executor = Executors.newSingleThreadExecutor();
    }


    public Cursor getLocationCustom(String columns, String options) {
        return notificationDao.getDataCustom(columns,options);
    }

    public Cursor getTravelCustom(String columns, String options) {
        return travelDao.getDataCustom(columns,options);
    }
    public LiveData<List<TravelEntity>> getRunEntitiesLiveData() {
        return runEntitiesLiveData;
    }

    public LiveData<List<TravelEntity>> getWalkEntitiesLiveData() {
        return walkEntitiesLiveData;
    }

    public LiveData<List<TravelEntity>> getCycleEntitiesLiveData() {
        return cycleEntitiesLiveData;
    }

    public LiveData<List<TravelEntity>> getTravelEntitiesLiveData() {
        return travelEntitiesLiveData;
    }
    public List<TravelEntity> getTravelEntities(Movement.MovementType type) {
        List<TravelEntity> res;

        switch (type){
            case WALK:
                res = travelDao.getWalks();
                break;
            case RUN:
                res = travelDao.getRuns();
                break;
            case CYCLE:
                res = travelDao.getCycles();
                break;
            default:
                res = travelDao.getTravels();
                break;
        }

        return res;
    };

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
                    true,"",Weather.SUN,0,"");
            travelDao.insert(newEntity);
            entity = travelDao.getEntityByDate(date,convertMovementTypeToString(type));
        }

        return entity;
    };

    public void upsertTravel(TravelEntity entity) {
        travelDao.upsertTravel(entity);
    }
    public void insertTravel(TravelEntity entity) {
        executor.execute(() -> travelDao.insert(entity));
    }

    public int insertTravelAndReturnId(TravelEntity entity) {
        return (int) travelDao.insert(entity);
    }

    public int insertLocationAndReturnId(LocationNotificationEntity entity) {
        return (int) notificationDao.insert(entity);
    }
    public void insertLocation(LocationNotificationEntity notification) {
        executor.execute(() -> notificationDao.insert(notification));
    }
    public void updateNotification(LocationNotificationEntity notification) {
        executor.execute(() -> notificationDao.update(notification));
    }
    public void updateTravelEntity(TravelEntity entity){
        executor.execute(() -> travelDao.update(entity));
    }
    public void deleteNotification(LocationNotificationEntity notification) {
        executor.execute(() -> notificationDao.delete(notification));
    }
    public void deleteNotificationById(int entityId) {
        executor.execute(() -> notificationDao.deleteById(entityId));
    }
    public void deleteTravelById(int entityId) {
        executor.execute(() -> travelDao.deleteById(entityId));
    }
    public LiveData<List<LocationNotificationEntity>> getAllNotifications() {
        return allNotifications;
    }

    public LiveData<List<TravelEntity>> getAllTravelEntities(){
        return allTravelEntities;
    }
}
