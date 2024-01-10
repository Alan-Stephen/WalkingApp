package com.example.gpscw2;

import android.net.http.UrlRequest;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StartActivityViewModel extends ViewModel {

    public static class CollectedData {
        public CollectedData(boolean positive, String title, String description,
                             Weather weather, FinishMovementResultStatus status) {
            this.positive = positive;
            this.title = title;
            this.description = description;
            this.weather = weather;
            this.status = status;
        }

        boolean positive = false;
        String title = "";
        String description = "";
        Weather weather = Weather.SUN;
        FinishMovementResultStatus status;
    }

    private CollectedData collectedData;
    private Movement currMovement;
    private MutableLiveData<Movement.MovementType> movementType;

    StartActivityViewModel()  {
        currMovement = null;

        movementType = new MutableLiveData<>(null);
        collectedData = null;
    }

    public CollectedData getCollectedData() {
        return collectedData;
    }

    public void setCollectedData(CollectedData collectedData) {
        this.collectedData = collectedData;
    }

    public Movement getCurrMovement() {
        return currMovement;
    }

    public void setCurrMovement(Movement currMovement) {
        this.currMovement = currMovement;
    }

    public MutableLiveData<Movement.MovementType> getMovementType() {
        return movementType;
    }

    public void setMovementType(Movement.MovementType movementType) {
        this.movementType.setValue(movementType);
    }
}
