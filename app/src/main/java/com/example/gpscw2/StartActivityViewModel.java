package com.example.gpscw2;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StartActivityViewModel extends ViewModel {
    private Movement currMovement;
    private MutableLiveData<Movement.MovementType> movementType;

    StartActivityViewModel()  {
        currMovement = null;

        movementType = new MutableLiveData<>(null);
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
