package com.example.demo.repository;

import com.example.demo.entity.BiometricProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BiometricProfileRepository extends JpaRepository<BiometricProfile, Long> {
    Optional<BiometricProfile> findByUsername(String username);
}