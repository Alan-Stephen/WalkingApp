package com.example.gpscw2;

import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {

    enum MainFragments {
        STATS,
        MAIN,
        MAPS
    }
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
    public MainActivityViewModel() {
        setLat(0.0);
        setLon(0.0);
        setCurrFragment(MainFragments.MAIN);
    }
}
