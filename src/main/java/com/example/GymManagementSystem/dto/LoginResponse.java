package com.example.GymManagementSystem.dto;

public class LoginResponse {
    private String token;
    private String tokenType = "Bearer";
    private long expiresInSeconds;
    private String username;
    private String role;

    public LoginResponse(String token, long expiresInSeconds, String username, String role) {
        this.token = token;
        this.expiresInSeconds = expiresInSeconds;
        this.username = username;
        this.role = role;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    public long getExpiresInSeconds() { return expiresInSeconds; }
    public void setExpiresInSeconds(long expiresInSeconds) { this.expiresInSeconds = expiresInSeconds; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
