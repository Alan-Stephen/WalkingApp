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

    public LocationNotificationEntity(double lat, double lon, int distanceMetres) {
        this.lat = lat;
        this.lon = lon;
        this.distanceMetres = distanceMetres;
    }

    public int getDistanceMetres() {
        return distanceMetres;
    }
}
