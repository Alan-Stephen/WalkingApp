package com.example.gpscw2;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Upsert;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Dao
public interface TravelDao {
    @Insert
    public void insert(TravelEntity entity);

    @Update
    public void update(TravelEntity entity);

    @Delete
    public void delete(TravelEntity entity);


    @Upsert
    public void upsertTravel(TravelEntity entity);
    @Query("SELECT * FROM travel WHERE movementType = 'TRAVEL' ORDER BY date DESC")
    LiveData<List<TravelEntity>> getTravels();

    @Query("SELECT * FROM travel WHERE movementType = 'RUN' ORDER BY date DESC")
    LiveData<List<TravelEntity>> getRuns();

    @Query("SELECT * FROM travel WHERE movementType = 'WALK' ORDER BY date DESC")
    LiveData<List<TravelEntity>> getWalks();

    @Query("SELECT * FROM travel WHERE movementType = 'CYCLE' ORDER BY date DESC")
    LiveData<List<TravelEntity>> getCycles();

    @Query("SELECT * FROM travel WHERE movementType = :movementType AND date = :specifiedDate LIMIT 1")
    TravelEntity getEntityByDate(long specifiedDate, String movementType);

    @Query("DELETE FROM travel WHERE id = :entityId")
    void deleteById(int entityId);
}
