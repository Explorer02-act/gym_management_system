package com.example.GymManagementSystem.service;

import com.example.GymManagementSystem.model.Admin;
import com.example.GymManagementSystem.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrap implements CommandLineRunner {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final boolean enabled;
    private final String username;
    private final String password;

    public AdminBootstrap(AdminRepository adminRepository, PasswordEncoder passwordEncoder,
                          @Value("${app.bootstrap-owner.enabled:true}") boolean enabled,
                          @Value("${app.bootstrap-owner.username:owner}") String username,
                          @Value("${app.bootstrap-owner.password:password123}") String password) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.enabled = enabled;
        this.username = username;
        this.password = password;
    }

    @Override
    public void run(String... args) {
        if (!enabled || adminRepository.existsByUsernameIgnoreCase(username)) { return; }
        Admin admin = new Admin();
        admin.setName("Gym Owner");
        admin.setUsername(username.toLowerCase());
        admin.setPasswordHash(passwordEncoder.encode(password));
        admin.setRole("ADMIN");
        admin.setEnabled(true);
        adminRepository.save(admin);
    }
}
