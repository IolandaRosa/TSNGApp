package com.example.tsngapp.model;

import com.example.tsngapp.api.SMARTAAL;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DashboardData implements Serializable {
    private Boolean isAwake;
    private Boolean isInside;
    private Boolean isGasEmissionNormal;
    private String weatherCondition;
    private Integer temperature;
    private List<SMARTAAL.CurrentLastValues.Data> currentValues;
    private List<SMARTAAL.InternalTempLastValues.Data> internalTempValues;

    public DashboardData() {
        this.currentValues = new ArrayList<>();
        this.internalTempValues = new ArrayList<>();
    }

    public DashboardData(Boolean isAwake, Boolean isInside, Boolean isGasEmissionNormal,
                         String weatherCondition, Integer temperature,
                         List<SMARTAAL.CurrentLastValues.Data> currentValues,
                         List<SMARTAAL.InternalTempLastValues.Data> internalTempValues) {
        this.isAwake = isAwake;
        this.isInside = isInside;
        this.isGasEmissionNormal = isGasEmissionNormal;
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

    public Boolean getGasEmissionNormal() {
        return isGasEmissionNormal;
    }

    public void setGasEmissionNormal(Boolean gasEmissionNormal) {
        isGasEmissionNormal = gasEmissionNormal;
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

    public void addCurrentValue(SMARTAAL.CurrentLastValues.Data value) {
        this.currentValues.add(value);
    }

    public void addCurrentValues(List<SMARTAAL.CurrentLastValues.Data> values) {
        this.currentValues.addAll(values);
    }

    public List<SMARTAAL.InternalTempLastValues.Data> getInternalTempValues() {
        return internalTempValues;
    }

    public void addInternalTempValue(SMARTAAL.InternalTempLastValues.Data value) {
        this.internalTempValues.add(value);
    }

    public void addInternalTempValues(List<SMARTAAL.InternalTempLastValues.Data> values) {
        this.internalTempValues.addAll(values);
    }

    public void setInternalTempValues(List<SMARTAAL.InternalTempLastValues.Data> internalTempValues) {
        this.internalTempValues = internalTempValues;
    }
}