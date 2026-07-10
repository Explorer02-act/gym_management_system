package com.example.GymManagementSystem.dto;

public class GymSettingsRequest {

    private String gymName;
    private String gymPhone;
    private String gpayNumber;
    private String qrImageUrl;

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
