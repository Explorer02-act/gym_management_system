package com.example.GymManagementSystem.repository;

import com.example.GymManagementSystem.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    boolean existsByMemberIdAndAttendanceDate(Long memberId, LocalDate attendanceDate);

    List<Attendance> findByAttendanceDateOrderByCheckInTimeAsc(LocalDate attendanceDate);

    List<Attendance> findByMemberIdOrderByAttendanceDateDescCheckInTimeDesc(Long memberId);

    void deleteByMemberId(Long memberId);

    List<Attendance> findTop10ByOrderByAttendanceDateDescCheckInTimeDesc();

    long countByAttendanceDate(LocalDate attendanceDate);

    long countByMemberId(Long memberId);

    @Query("select a.member.id as memberId, a.member.name as memberName, a.member.memberCode as memberCode, count(a) as visits " +
            "from Attendance a group by a.member.id, a.member.name, a.member.memberCode order by count(a) desc")
    List<AttendanceLeaderboardRow> findAttendanceLeaderboard();
}
