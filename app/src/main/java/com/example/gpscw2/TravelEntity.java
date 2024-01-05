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
    private int distance;

    public TravelEntity( String movementType, long date, int distance) {
        this.movementType = movementType;
        this.date = date;
        this.distance = distance;
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
}
