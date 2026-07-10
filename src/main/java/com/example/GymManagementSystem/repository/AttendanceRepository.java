package com.example.GymManagementSystem.repository;

import com.example.GymManagementSystem.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    boolean existsByMemberIdAndAttendanceDate(Long memberId, LocalDate attendanceDate);

    List<Attendance> findByAttendanceDateOrderByCheckInTimeAsc(LocalDate attendanceDate);

    List<Attendance> findByMemberIdOrderByAttendanceDateDescCheckInTimeDesc(Long memberId);

    long countByAttendanceDate(LocalDate attendanceDate);
}
