package com.example.gpscw2;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao public interface LocationNotificationDao {

    @Insert
    public void insert(LocationNotificationEntity notification);
    @Update
    public void update(LocationNotificationEntity notification);
    @Delete
    public void delete(LocationNotificationEntity notification);
    @Query("SELECT * FROM location_notifications")
    LiveData<List<LocationNotificationEntity>> getAllNotifications();
    @Query("DELETE FROM location_notifications WHERE id = :entityId")
    void deleteById(int entityId);
}
