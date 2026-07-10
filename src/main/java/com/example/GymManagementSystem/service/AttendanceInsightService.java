package com.example.GymManagementSystem.service;

import com.example.GymManagementSystem.dto.AttendanceLeaderboardResponse;
import com.example.GymManagementSystem.dto.AttendanceResponse;
import com.example.GymManagementSystem.repository.AttendanceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttendanceInsightService {

    private final AttendanceRepository attendanceRepository;

    public AttendanceInsightService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    public List<AttendanceResponse> recentCheckIns() {
        return attendanceRepository.findTop10ByOrderByAttendanceDateDescCheckInTimeDesc()
                .stream()
                .map(AttendanceResponse::from)
                .toList();
    }

    public List<AttendanceLeaderboardResponse> leaderboard() {
        return attendanceRepository.findAttendanceLeaderboard()
                .stream()
                .map(row -> {
                    AttendanceLeaderboardResponse response = new AttendanceLeaderboardResponse();
                    response.setMemberId(row.getMemberId());
                    response.setMemberName(row.getMemberName());
                    response.setMemberCode(row.getMemberCode());
                    response.setVisits(row.getVisits() == null ? 0 : row.getVisits());
                    return response;
                })
                .toList();
    }
}
