package com.example.GymManagementSystem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class GymSettings extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String gymName;

    private String gymPhone;

    private String gpayNumber;

    private String qrImageUrl;

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
