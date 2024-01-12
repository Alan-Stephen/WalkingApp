package com.example.gpscw2;

import androidx.lifecycle.MutableLiveData;

public class Stats {
    private long averageDuration;
    private int averageDistance;
    private double averageSpeed;
    private double distanceImprovement;
    private double durationImprovement;
    private double speedImprovement;
    private int distanceTravelled;
    private int travelDistanceImproved;

    public long getAverageDuration() {
        return averageDuration;
    }

    public void setAverageDuration(Long averageDuration) {
        this.averageDuration = averageDuration;
    }

    public int getAverageDistance() {
        return averageDistance;
    }

    public void setAverageDistance(Integer averageDistance) {
        this.averageDistance = averageDistance;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(Double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public double getDistanceImprovement() {
        return distanceImprovement;
    }

    public void setDistanceImprovement(Double distanceImprovement) {
        this.distanceImprovement = distanceImprovement;
    }

    public double getDurationImprovement() {
        return durationImprovement;
    }

    public void setDurationImprovement(Double durationImprovement) {
        this.durationImprovement = durationImprovement;
    }

    public double getSpeedImprovement() {
        return speedImprovement;
    }

    public void setSpeedImprovement(Double speedImprovement) {
        this.speedImprovement = speedImprovement;
    }

    public int getDistanceTravelled() {
        return distanceTravelled;
    }

    public void setDistanceTravelled(Integer distanceTravelled) {
        this.distanceTravelled = distanceTravelled;
    }

    public int getTravelDistanceImproved() {
        return travelDistanceImproved;
    }

    public void setTravelDistanceImproved(Integer travelDistanceImproved) {
        this.travelDistanceImproved = travelDistanceImproved;
    }
}
