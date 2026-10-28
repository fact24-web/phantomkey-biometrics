package com.example.demo.dto;

import java.util.List;

public class AuthPayload {
    private String username;
    private List<KeystrokePoint> cadenceVector;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public List<KeystrokePoint> getCadenceVector() { return cadenceVector; }
    public void setCadenceVector(List<KeystrokePoint> cadenceVector) { this.cadenceVector = cadenceVector; }
}