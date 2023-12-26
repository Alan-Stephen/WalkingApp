package com.example.gpscw2;

public class Movement {
    private double kmTravelled;
    private long secondsTravelled;

    private MovementType movementType;

    enum MovementType {
        RUN,
        WALK,
        CYCLE
    }

    Movement(MovementType type) {
        this.movementType = type;
        kmTravelled = 0.0;
        secondsTravelled = 0;
    }

    public double getKmTravelled() {
        return kmTravelled;
    }

    public void setKmTravelled(double kmTravelled) {
        this.kmTravelled = kmTravelled;
    }

    public long getSecondsTravelled() {
        return secondsTravelled;
    }

    public void setSecondsTravelled(long secondsTravelled) {
        this.secondsTravelled = secondsTravelled;
    }
}
