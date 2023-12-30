package com.example.gpscw2;

import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {

    enum MainFragments {
        STATS,
        MAIN,
        MAPS
    }
    private boolean gotLocationPermissions = false;
    private boolean locationServiceActive = false;
    private double lat;
    private double lon;
    private MainFragments currFragment;
    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public MainFragments getCurrFragment() {
        return currFragment;
    }

    public void setCurrFragment(MainFragments currFragment) {
        this.currFragment = currFragment;
    }

    public boolean isGotLocationPermissions() {
        return gotLocationPermissions;
    }

    public void setGotLocationPermissions(boolean gotLocationPermissions) {
        this.gotLocationPermissions = gotLocationPermissions;
    }

    public boolean isLocationServiceActive() {
        return locationServiceActive;
    }

    public MainActivityViewModel() {
        setGotLocationPermissions(false);
        setLocationServiceActive(false);
        setLat(0.0);
        setLon(0.0);
        setCurrFragment(MainFragments.MAIN);
    }

    public void setLocationServiceActive(boolean locationServiceActive) {
        this.locationServiceActive = locationServiceActive;
    }
}
