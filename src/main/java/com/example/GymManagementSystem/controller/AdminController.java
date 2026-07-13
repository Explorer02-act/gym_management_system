package com.example.GymManagementSystem.controller;

import com.example.GymManagementSystem.dto.AdminCreateRequest;
import com.example.GymManagementSystem.dto.AdminResponse;
import com.example.GymManagementSystem.service.AdminService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admins")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) { this.adminService = adminService; }

    @PostMapping
    public AdminResponse createAdmin(@Valid @RequestBody AdminCreateRequest request) {
        return adminService.createAdmin(request);
    }

    @GetMapping
    public List<AdminResponse> getAdmins() { return adminService.getAdmins(); }
}
