package com.example.GymManagementSystem.controller;

import com.example.GymManagementSystem.dto.AttendanceLeaderboardResponse;
import com.example.GymManagementSystem.dto.AttendanceResponse;
import com.example.GymManagementSystem.service.AttendanceInsightService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/attendance")
public class AttendanceInsightController {

    private final AttendanceInsightService attendanceInsightService;

    public AttendanceInsightController(AttendanceInsightService attendanceInsightService) {
        this.attendanceInsightService = attendanceInsightService;
    }

    @GetMapping("/recent")
    public List<AttendanceResponse> recentCheckIns() {
        return attendanceInsightService.recentCheckIns();
    }

    @GetMapping("/leaderboard")
    public List<AttendanceLeaderboardResponse> leaderboard() {
        return attendanceInsightService.leaderboard();
    }
}
