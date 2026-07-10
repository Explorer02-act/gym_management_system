package com.example.GymManagementSystem.repository;

import com.example.GymManagementSystem.model.GymSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GymSettingsRepository extends JpaRepository<GymSettings, Long> {
}
