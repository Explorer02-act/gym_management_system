package com.example.GymManagementSystem.controller;

import com.example.GymManagementSystem.dto.AttendanceCheckInRequest;
import com.example.GymManagementSystem.dto.AttendanceLookupCheckInRequest;
import com.example.GymManagementSystem.dto.AttendanceResponse;
import com.example.GymManagementSystem.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/checkin")
    public AttendanceResponse checkIn(@Valid @RequestBody AttendanceCheckInRequest request) {
        return attendanceService.checkIn(request);
    }

    @PostMapping("/checkin-lookup")
    public AttendanceResponse checkInByCodeOrPhone(@Valid @RequestBody AttendanceLookupCheckInRequest request) {
        return attendanceService.checkInByCodeOrPhone(request);
    }

    @GetMapping("/today")
    public List<AttendanceResponse> getTodayAttendance() {
        return attendanceService.getTodayAttendance();
    }

    @GetMapping("/member/{memberId}")
    public List<AttendanceResponse> getMemberAttendance(@PathVariable Long memberId) {
        return attendanceService.getMemberAttendance(memberId);
    }
}
