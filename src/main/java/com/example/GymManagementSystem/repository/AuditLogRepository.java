package com.example.GymManagementSystem.repository;

import com.example.GymManagementSystem.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
