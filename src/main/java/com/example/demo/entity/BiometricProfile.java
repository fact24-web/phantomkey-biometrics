package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "biometric_profiles")
public class BiometricProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Lob
    @Column(nullable = false)
    private String vectorJson;

    public BiometricProfile() {}

    public BiometricProfile(String username, String vectorJson) {
        this.username = username;
        this.vectorJson = vectorJson;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getVectorJson() { return vectorJson; }
    public void setVectorJson(String vectorJson) { this.vectorJson = vectorJson; }
}