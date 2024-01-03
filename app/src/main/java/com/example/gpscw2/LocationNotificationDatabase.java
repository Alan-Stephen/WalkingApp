package com.example.gpscw2;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = LocationNotificationEntity.class, version = 3)
public abstract class LocationNotificationDatabase extends RoomDatabase {
    private static LocationNotificationDatabase instance;

    public abstract LocationNotificationDao locationNotificationDao();

    public static synchronized LocationNotificationDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            LocationNotificationDatabase.class,
                            "notification_database").fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
