package com.example.gpscw2;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName="location_notifications")
public class LocationNotificationEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private double lat;
    private double lon;
    private int distanceMetres;

    private String title;
    private String description;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }


    public int getId() {
        return id;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocationNotificationEntity(double lat, double lon, int distanceMetres, String title,
                                      String description) {
        this.lat = lat;
        this.lon = lon;
        this.distanceMetres = distanceMetres;
        this.title = title;
        this.description = description;
    }

    public int getDistanceMetres() {
        return distanceMetres;
    }
}
