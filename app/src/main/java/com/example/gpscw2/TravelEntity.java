package com.example.gpscw2;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.util.Date;

@Entity(tableName="travel")
public class TravelEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String movementType;
    private long date;
    private long lengthSeconds;
    private int distance;
    private boolean positive;
    private String description;
    private Weather weather;
    private String title;

    public TravelEntity(String movementType, long date, int distance, boolean positive,
                        String description, Weather weather, long lengthSeconds, String title) {
        this.movementType = movementType;
        this.date = date;
        this.distance = distance;
        this.positive = positive;
        this.description = description;
        this.weather = weather;
        this.lengthSeconds = lengthSeconds;
        this.title = title;
    }

    public long getLengthSeconds() {
        return lengthSeconds;
    }

    public void setLengthSeconds(long lengthSeconds) {
        this.lengthSeconds = lengthSeconds;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isPositive() {
        return positive;
    }

    public void setPositive(boolean positive) {
        this.positive = positive;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public int getId() {
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getMovementType() {
        return movementType;
    }

    public void setMovementType(String movementType) {
        this.movementType = movementType;
    }

    public long getDate() {
        return date;
    }

    public void LocalDate(long date) {
        this.date = date;
    }
    public int getDistance() {
        return distance;
    }
    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

