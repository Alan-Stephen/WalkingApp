package com.example.gpscw2;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Upsert;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TransferQueue;

@Dao
public interface TravelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insert(TravelEntity entity);

    @Update
    public void update(TravelEntity entity);

    @Delete
    public void delete(TravelEntity entity);

    @Query("SELECT * FROM travel WHERE movementType = 'TRAVEL' ORDER BY date DESC")
    LiveData<List<TravelEntity>> getTravelLiveData();
    @Query("SELECT * FROM travel WHERE movementType = 'RUN' ORDER BY date DESC")
    LiveData<List<TravelEntity>> getRunsLiveData();
    @Query("SELECT * FROM travel WHERE movementType = 'WALK' ORDER BY date DESC")
    LiveData<List<TravelEntity>>getWalksLiveData();
    @Query("SELECT * FROM travel WHERE movementType = 'CYCLE' ORDER BY date DESC")
    LiveData<List<TravelEntity>> getCyclesLiveData();

    @Upsert
    public void upsertTravel(TravelEntity entity);
    @Query("SELECT * FROM travel WHERE movementType = 'TRAVEL' ORDER BY date DESC")
    List<TravelEntity> getTravels();

    @Query("SELECT * FROM travel WHERE movementType = 'RUN' ORDER BY date DESC")
    List<TravelEntity> getRuns();

    @Query("SELECT * FROM travel WHERE movementType = 'WALK' ORDER BY date DESC")
    List<TravelEntity> getWalks();

    @Query("SELECT * FROM travel WHERE movementType = 'CYCLE' ORDER BY date DESC")
    List<TravelEntity> getCycles();

    @Query("SELECT * FROM travel WHERE movementType = :movementType AND date = :specifiedDate LIMIT 1")
    TravelEntity getEntityByDate(long specifiedDate, String movementType);
    @Query("SELECT :columns FROM travel WHERE :options")
    Cursor getDataCustom(String columns, String options);
    @Query("DELETE FROM travel WHERE id = :entityId")
    void deleteById(int entityId);


    @Query("SELECT * FROM travel")
    LiveData<List<TravelEntity>> getAllTravelEntitiesLiveData();
}
