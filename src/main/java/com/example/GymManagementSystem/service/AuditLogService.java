package com.example.GymManagementSystem.service;

import com.example.GymManagementSystem.model.AuditLog;
import com.example.GymManagementSystem.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class AuditLogService {
    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) { this.auditLogRepository = auditLogRepository; }

    public void record(String action) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null && authentication.isAuthenticated() ? authentication.getName() : "system";
        record(username, action);
    }

    public void record(String username, String action) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUsername(username == null || username.isBlank() ? "anonymous" : username);
        auditLog.setAction(action);
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setIpAddress(resolveIpAddress());
        auditLogRepository.save(auditLog);
    }

    private String resolveIpAddress() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) { return "unknown"; }
        HttpServletRequest request = attributes.getRequest();
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) { return forwardedFor.split(",")[0].trim(); }
        return request.getRemoteAddr();
    }
}
