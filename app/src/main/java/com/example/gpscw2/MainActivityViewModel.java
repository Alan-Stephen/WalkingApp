package com.example.gpscw2;

import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {
    private boolean gotLocationPermissions = false;
    private boolean locationServiceActive = false;

    public boolean isGotLocationPermissions() {
        return gotLocationPermissions;
    }

    public void setGotLocationPermissions(boolean gotLocationPermissions) {
        this.gotLocationPermissions = gotLocationPermissions;
    }

    public boolean isLocationServiceActive() {
        return locationServiceActive;
    }

    public void setLocationServiceActive(boolean locationServiceActive) {
        this.locationServiceActive = locationServiceActive;
    }
}
