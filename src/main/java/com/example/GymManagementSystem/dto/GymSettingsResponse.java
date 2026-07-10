package com.example.GymManagementSystem.dto;

import com.example.GymManagementSystem.model.GymSettings;

public class GymSettingsResponse {

    private Long id;
    private String gymName;
    private String gymPhone;
    private String gpayNumber;
    private String qrImageUrl;

    public static GymSettingsResponse from(GymSettings settings) {
        GymSettingsResponse response = new GymSettingsResponse();
        response.setId(settings.getId());
        response.setGymName(settings.getGymName());
        response.setGymPhone(settings.getGymPhone());
        response.setGpayNumber(settings.getGpayNumber());
        response.setQrImageUrl(settings.getQrImageUrl());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGymName() {
        return gymName;
    }

    public void setGymName(String gymName) {
        this.gymName = gymName;
    }

    public String getGymPhone() {
        return gymPhone;
    }

    public void setGymPhone(String gymPhone) {
        this.gymPhone = gymPhone;
    }

    public String getGpayNumber() {
        return gpayNumber;
    }

    public void setGpayNumber(String gpayNumber) {
        this.gpayNumber = gpayNumber;
    }

    public String getQrImageUrl() {
        return qrImageUrl;
    }

    public void setQrImageUrl(String qrImageUrl) {
        this.qrImageUrl = qrImageUrl;
    }
}
