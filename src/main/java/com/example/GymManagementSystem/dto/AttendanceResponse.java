package com.example.GymManagementSystem.dto;

import com.example.GymManagementSystem.model.Attendance;

import java.time.LocalDate;
import java.time.LocalTime;

public class AttendanceResponse {

    private Long id;
    private MemberResponse member;
    private LocalDate attendanceDate;
    private LocalTime checkInTime;

    public static AttendanceResponse from(Attendance attendance) {
        AttendanceResponse response = new AttendanceResponse();
        response.setId(attendance.getId());
        response.setMember(MemberResponse.from(attendance.getMember()));
        response.setAttendanceDate(attendance.getAttendanceDate());
        response.setCheckInTime(attendance.getCheckInTime());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MemberResponse getMember() {
        return member;
    }

    public void setMember(MemberResponse member) {
        this.member = member;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public LocalTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalTime checkInTime) {
        this.checkInTime = checkInTime;
    }
}
