package com.example.demo.dto;

public class KeystrokePoint {
    private String key;
    private double dwellTime;   // Key hold duration (ms)
    private double flightTime;  // Flight time from previous key release (ms)

    public KeystrokePoint() {}

    public KeystrokePoint(String key, double dwellTime, double flightTime) {
        this.key = key;
        this.dwellTime = dwellTime;
        this.flightTime = flightTime;
    }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public double getDwellTime() { return dwellTime; }
    public void setDwellTime(double dwellTime) { this.dwellTime = dwellTime; }

    public double getFlightTime() { return flightTime; }
    public void setFlightTime(double flightTime) { this.flightTime = flightTime; }
}