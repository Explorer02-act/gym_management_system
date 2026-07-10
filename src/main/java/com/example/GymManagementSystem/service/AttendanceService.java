package com.example.GymManagementSystem.service;

import com.example.GymManagementSystem.dto.AttendanceCheckInRequest;
import com.example.GymManagementSystem.dto.AttendanceLookupCheckInRequest;
import com.example.GymManagementSystem.dto.AttendanceResponse;
import com.example.GymManagementSystem.exception.BadRequestException;
import com.example.GymManagementSystem.exception.DuplicateResourceException;
import com.example.GymManagementSystem.exception.ResourceNotFoundException;
import com.example.GymManagementSystem.model.Attendance;
import com.example.GymManagementSystem.model.Member;
import com.example.GymManagementSystem.repository.AttendanceRepository;
import com.example.GymManagementSystem.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final MemberRepository memberRepository;

    public AttendanceService(AttendanceRepository attendanceRepository,
                             MemberRepository memberRepository) {
        this.attendanceRepository = attendanceRepository;
        this.memberRepository = memberRepository;
    }

    public AttendanceResponse checkIn(AttendanceCheckInRequest request) {
        LocalDate today = LocalDate.now();
        Long memberId = requireId(request.getMemberId(), "Member id is required");
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id " + memberId));

        return saveCheckIn(member, today);
    }

    public AttendanceResponse checkInByCodeOrPhone(AttendanceLookupCheckInRequest request) {
        String lookup = request.getMemberCodeOrPhone().trim();
        Member member = memberRepository.findByPhone(lookup)
                .or(() -> memberRepository.findByMemberCodeIgnoreCase(lookup))
                .orElseThrow(() -> new ResourceNotFoundException("Member not found for code or phone " + lookup));

        return saveCheckIn(member, LocalDate.now());
    }

    private AttendanceResponse saveCheckIn(Member member, LocalDate today) {
        if (attendanceRepository.existsByMemberIdAndAttendanceDate(requireId(member.getId(), "Member id is required"), today)) {
            throw new DuplicateResourceException("Member has already checked in today");
        }

        Attendance attendance = new Attendance(member, today, LocalTime.now());
        return AttendanceResponse.from(attendanceRepository.save(attendance));
    }

    public List<AttendanceResponse> getTodayAttendance() {
        return attendanceRepository.findByAttendanceDateOrderByCheckInTimeAsc(LocalDate.now())
                .stream()
                .map(AttendanceResponse::from)
                .toList();
    }

    public List<AttendanceResponse> getMemberAttendance(Long memberId) {
        Long id = requireId(memberId, "Member id is required");

        if (!memberRepository.existsById(id)) {
            throw new ResourceNotFoundException("Member not found with id " + id);
        }

        return attendanceRepository.findByMemberIdOrderByAttendanceDateDescCheckInTimeDesc(id)
                .stream()
                .map(AttendanceResponse::from)
                .toList();
    }

    private Long requireId(Long id, String message) {
        if (id == null) {
            throw new BadRequestException(message);
        }
        return id;
    }
}
