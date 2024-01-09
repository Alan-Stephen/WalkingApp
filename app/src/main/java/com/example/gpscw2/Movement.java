package com.example.gpscw2;

import androidx.lifecycle.MutableLiveData;

import java.time.LocalTime;

public class Movement {
    private MutableLiveData<Integer> travelledMetres;
    private LocalTime timeStarted;
    private MovementType movementType;

    enum MovementType {
        RUN,
        WALK,
        CYCLE,
        TRAVEL,
    }
    Movement(MovementType type) {
        this.movementType = type;
        travelledMetres = new MutableLiveData<>(0);
        timeStarted  = LocalTime.now();

    }

    public MutableLiveData<Integer> getTravelledMetres() {
        return travelledMetres;
    }
    public void setTravelledMetres(int travelledMetres) {
        this.travelledMetres.setValue(travelledMetres);
    }
    public LocalTime getTimeStarted() {
        return timeStarted;
    }
    public void setSecondsTravelled(LocalTime timeStarted) {
        this.timeStarted = timeStarted;
    }

    public MovementType getMovementType() {
        return movementType;
    }

    public void addToKmTravelled(int distMetres) {
        travelledMetres.setValue(travelledMetres.getValue() + distMetres);
    }
}
