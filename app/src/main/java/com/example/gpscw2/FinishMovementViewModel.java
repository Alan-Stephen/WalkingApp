package com.example.gpscw2;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FinishMovementViewModel extends ViewModel {
    private String title;
    private String description;
    private MutableLiveData<Boolean> positive;
    private MutableLiveData<Weather> weather;

    FinishMovementViewModel() {
        title = "";
        description = "";
        positive = new MutableLiveData<>(true);
        weather = new MutableLiveData<>(Weather.SUN);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MutableLiveData<Boolean> getPositive() {
        return positive;
    }

    public void setPositive(Boolean positive) {
        this.positive.setValue(positive);
    }

    public MutableLiveData<Weather> getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather.setValue(weather);
    }
}