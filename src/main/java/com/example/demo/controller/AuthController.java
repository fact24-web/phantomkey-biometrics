package com.example.demo.controller;

import com.example.demo.dto.AuthPayload;
import com.example.demo.dto.KeystrokePoint;
import com.example.demo.entity.BiometricProfile;
import com.example.demo.repository.BiometricProfileRepository;
import com.example.demo.service.CadenceVerificationService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final CadenceVerificationService verificationService;
    private final BiometricProfileRepository repository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuthController(CadenceVerificationService verificationService, 
                          BiometricProfileRepository repository) {
        this.verificationService = verificationService;
        this.repository = repository;
    }

    @PostMapping("/enroll")
    public ResponseEntity<Map<String, Object>> enrollPattern(@RequestBody AuthPayload payload) {
        if (payload.getCadenceVector() == null || payload.getCadenceVector().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("status", "FAILED", "message", "Vector payload empty"));
        }

        try {
            String vectorJson = objectMapper.writeValueAsString(payload.getCadenceVector());

            BiometricProfile profile = repository.findByUsername(payload.getUsername())
                    .orElse(new BiometricProfile(payload.getUsername(), vectorJson));
            profile.setVectorJson(vectorJson);
            repository.save(profile);

            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "message", "Biometric template persisted in DB for user: " + payload.getUsername()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "ERROR", "message", "Serialization error"));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyAttempt(@RequestBody AuthPayload payload) {
        Optional<BiometricProfile> profileOpt = repository.findByUsername(payload.getUsername());

        if (profileOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "ERROR",
                    "message", "User profile not enrolled in database"
            ));
        }

        try {
            List<KeystrokePoint> enrolled = objectMapper.readValue(
                    profileOpt.get().getVectorJson(), 
                    new TypeReference<List<KeystrokePoint>>() {}
            );

            boolean isValid = verificationService.verifyCadence(enrolled, payload.getCadenceVector());

            if (isValid) {
                return ResponseEntity.ok(Map.of(
                        "status", "AUTHENTICATED",
                        "token", UUID.randomUUID().toString()
                ));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                        "status", "DENIED",
                        "message", "Cadence vector deviation exceeded threshold"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "ERROR", "message", "Deserialization failure"));
        }
    }
}