package com.example.gpscw2;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao public interface LocationNotificationDao {

    @Insert
    public long insert(LocationNotificationEntity notification);
    @Update
    public void update(LocationNotificationEntity notification);
    @Delete
    public void delete(LocationNotificationEntity notification);
    @Query("SELECT * FROM location_notification")
    LiveData<List<LocationNotificationEntity>> getAllNotifications();
    @Query("DELETE FROM location_notification WHERE id = :entityId")
    void deleteById(int entityId);

    @Query("SELECT :columns FROM location_notification WHERE :options")
    Cursor getDataCustom(String columns, String options);

}
