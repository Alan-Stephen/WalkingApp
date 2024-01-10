package com.example.gpscw2;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {LocationNotificationEntity.class,TravelEntity.class}, version = 7)
public abstract class LocationDatabase extends RoomDatabase {
    private static LocationDatabase instance;

    public abstract LocationNotificationDao locationNotificationDao();
    public abstract TravelDao travelDao();

    public static synchronized LocationDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            LocationDatabase.class,
                            "notification_database").fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
