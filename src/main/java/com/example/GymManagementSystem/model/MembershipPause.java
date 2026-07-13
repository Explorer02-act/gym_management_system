package com.example.GymManagementSystem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDate;

@Entity
public class MembershipPause extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "membership_id", nullable = false)
    private Membership membership;

    private LocalDate pauseStartDate;

    private LocalDate pauseEndDate;

    private int pauseDays;

    private String reason;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Membership getMembership() {
        return membership;
    }

    public void setMembership(Membership membership) {
        this.membership = membership;
    }

    public LocalDate getPauseStartDate() {
        return pauseStartDate;
    }

    public void setPauseStartDate(LocalDate pauseStartDate) {
        this.pauseStartDate = pauseStartDate;
    }

    public LocalDate getPauseEndDate() {
        return pauseEndDate;
    }

    public void setPauseEndDate(LocalDate pauseEndDate) {
        this.pauseEndDate = pauseEndDate;
    }

    public int getPauseDays() {
        return pauseDays;
    }

    public void setPauseDays(int pauseDays) {
        this.pauseDays = pauseDays;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
