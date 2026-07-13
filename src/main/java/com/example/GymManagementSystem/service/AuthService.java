package com.example.GymManagementSystem.service;

import com.example.GymManagementSystem.dto.LoginRequest;
import com.example.GymManagementSystem.dto.LoginResponse;
import com.example.GymManagementSystem.exception.BadRequestException;
import com.example.GymManagementSystem.model.Admin;
import com.example.GymManagementSystem.repository.AdminRepository;
import com.example.GymManagementSystem.security.JwtService;
import java.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_MINUTES = 15;

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuditLogService auditLogService;

    public AuthService(AdminRepository adminRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuditLogService auditLogService) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        Admin admin = adminRepository.findByUsernameIgnoreCase(request.getUsername())
                .orElseThrow(() -> new BadRequestException("Invalid username or password"));
        if (!admin.isEnabled()) {
            throw new BadRequestException("Account is disabled");
        }
        unlockIfExpired(admin);
        if (admin.getLockTime() != null) {
            throw new BadRequestException("Account is temporarily locked. Try again later.");
        }
        if (!passwordEncoder.matches(request.getPassword(), admin.getPasswordHash())) {
            registerFailedAttempt(admin);
            auditLogService.record(admin.getUsername(), "FAILED_LOGIN");
            throw new BadRequestException("Invalid username or password");
        }
        admin.setFailedAttempts(0);
        admin.setLockTime(null);
        adminRepository.save(admin);
        auditLogService.record(admin.getUsername(), "LOGIN");
        String token = jwtService.generateToken(admin.getUsername(), admin.getRole());
        return new LoginResponse(token, jwtService.getExpirationSeconds(), admin.getUsername(), admin.getRole());
    }

    private void registerFailedAttempt(Admin admin) {
        int attempts = admin.getFailedAttempts() + 1;
        admin.setFailedAttempts(attempts);
        if (attempts >= MAX_FAILED_ATTEMPTS) {
            admin.setLockTime(LocalDateTime.now());
        }
        adminRepository.save(admin);
    }

    private void unlockIfExpired(Admin admin) {
        if (admin.getLockTime() != null && admin.getLockTime().plusMinutes(LOCK_MINUTES).isBefore(LocalDateTime.now())) {
            admin.setFailedAttempts(0);
            admin.setLockTime(null);
            adminRepository.save(admin);
        }
    }
}
