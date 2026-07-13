package com.example.GymManagementSystem.service;

import com.example.GymManagementSystem.dto.AdminCreateRequest;
import com.example.GymManagementSystem.dto.AdminResponse;
import com.example.GymManagementSystem.exception.DuplicateResourceException;
import com.example.GymManagementSystem.model.Admin;
import com.example.GymManagementSystem.repository.AdminRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    public AdminService(AdminRepository adminRepository, PasswordEncoder passwordEncoder, AuditLogService auditLogService) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public AdminResponse createAdmin(AdminCreateRequest request) {
        if (adminRepository.existsByUsernameIgnoreCase(request.getUsername())) {
            throw new DuplicateResourceException("Admin username already exists");
        }
        Admin admin = new Admin();
        admin.setName(request.getName());
        admin.setUsername(request.getUsername().trim().toLowerCase());
        admin.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        admin.setRole(request.getRole());
        admin.setEnabled(true);
        Admin saved = adminRepository.save(admin);
        auditLogService.record("ADMIN_CREATED:" + saved.getUsername());
        return AdminResponse.from(saved);
    }

    public List<AdminResponse> getAdmins() {
        return adminRepository.findAll().stream().map(AdminResponse::from).toList();
    }
}
