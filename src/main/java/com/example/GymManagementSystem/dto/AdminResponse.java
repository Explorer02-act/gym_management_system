package com.example.GymManagementSystem.dto;

import com.example.GymManagementSystem.model.Admin;

public class AdminResponse {
    private Long id;
    private String name;
    private String username;
    private String role;
    private boolean enabled;

    public static AdminResponse from(Admin admin) {
        AdminResponse response = new AdminResponse();
        response.setId(admin.getId());
        response.setName(admin.getName());
        response.setUsername(admin.getUsername());
        response.setRole(admin.getRole());
        response.setEnabled(admin.isEnabled());
        return response;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
