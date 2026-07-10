package com.example.GymManagementSystem.dto;

import com.example.GymManagementSystem.model.Member;

public class MemberResponse {

    private Long id;
    private String memberCode;
    private String name;
    private String phone;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String photoUrl;

    public static MemberResponse from(Member member) {
        MemberResponse response = new MemberResponse();
        response.setId(member.getId());
        response.setMemberCode(member.getMemberCode());
        response.setName(member.getName());
        response.setPhone(member.getPhone());
        response.setEmergencyContactName(member.getEmergencyContactName());
        response.setEmergencyContactPhone(member.getEmergencyContactPhone());
        response.setPhotoUrl(member.getPhotoUrl());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMemberCode() {
        return memberCode;
    }

    public void setMemberCode(String memberCode) {
        this.memberCode = memberCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }

    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }

    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
