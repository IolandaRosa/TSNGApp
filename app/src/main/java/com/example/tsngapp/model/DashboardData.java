package com.example.tsngapp.model;

import com.example.tsngapp.api.SMARTAAL;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DashboardData implements Serializable {
    private Boolean isAwake;
    private Boolean isInside;
    private String weatherCondition;
    private Integer temperature;
    private List<SMARTAAL.CurrentLastValues.Data> currentValues;
    private List<SMARTAAL.InternalTempLastValues.Data> internalTempValues;

    public DashboardData() {}

    public DashboardData(Boolean isAwake, Boolean isInside, String weatherCondition, Integer temperature,
                         List<SMARTAAL.CurrentLastValues.Data> currentValues,
                         List<SMARTAAL.InternalTempLastValues.Data> internalTempValues) {
        this.isAwake = isAwake;
        this.isInside = isInside;
        this.weatherCondition = weatherCondition;
        this.temperature = temperature;
        this.currentValues = currentValues;
        this.internalTempValues = internalTempValues;
    }

    public Boolean getAwake() {
        return isAwake;
    }

    public void setAwake(Boolean awake) {
        isAwake = awake;
    }

    public Boolean getInside() {
        return isInside;
    }

    public void setInside(Boolean inside) {
        isInside = inside;
    }

    public String getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    public Integer getTemperature() {
        return temperature;
    }

    public void setTemperature(Integer temperature) {
        this.temperature = temperature;
    }

    public List<SMARTAAL.CurrentLastValues.Data> getCurrentValues() {
        return currentValues;
    }

    public void setCurrentValues(List<SMARTAAL.CurrentLastValues.Data> currentValues) {
        this.currentValues = currentValues;
    }

    public List<SMARTAAL.InternalTempLastValues.Data> getInternalTempValues() {
        return internalTempValues;
    }

    public void setInternalTempValues(List<SMARTAAL.InternalTempLastValues.Data> internalTempValues) {
        this.internalTempValues = internalTempValues;
    }
}