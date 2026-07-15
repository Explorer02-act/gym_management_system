package com.example.GymManagementSystem.service;

import com.example.GymManagementSystem.dto.MemberRequest;
import com.example.GymManagementSystem.dto.MemberResponse;
import com.example.GymManagementSystem.dto.MemberStatusResponse;
import com.example.GymManagementSystem.exception.BadRequestException;
import com.example.GymManagementSystem.exception.DuplicateResourceException;
import com.example.GymManagementSystem.exception.ResourceNotFoundException;
import com.example.GymManagementSystem.model.Member;
import com.example.GymManagementSystem.model.Membership;
import com.example.GymManagementSystem.repository.AttendanceRepository;
import com.example.GymManagementSystem.repository.MemberRepository;
import com.example.GymManagementSystem.repository.MembershipRepository;
import com.example.GymManagementSystem.repository.MembershipPauseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final MembershipRepository membershipRepository;
    private final AttendanceRepository attendanceRepository;
    private final MembershipPauseRepository membershipPauseRepository;

    public MemberService(MemberRepository memberRepository,
                         MembershipRepository membershipRepository,
                         AttendanceRepository attendanceRepository,
                         MembershipPauseRepository membershipPauseRepository) {
        this.memberRepository = memberRepository;
        this.membershipRepository = membershipRepository;
        this.attendanceRepository = attendanceRepository;
        this.membershipPauseRepository = membershipPauseRepository;
    }

    @Transactional
    public MemberResponse addMember(MemberRequest request) {
        if (memberRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("Member with phone number " + request.getPhone() + " already exists");
        }

        Member member = new Member();
        updateMemberFields(member, request);
        member.setMemberCode("PENDING-" + UUID.randomUUID());

        Member savedMember = memberRepository.save(member);
        savedMember.setMemberCode(String.format("GYM%03d", savedMember.getId()));

        return MemberResponse.from(memberRepository.save(savedMember));
    }

    public List<MemberResponse> getAllMembers() {
        return memberRepository.findAll()
                .stream()
                .map(MemberResponse::from)
                .toList();
    }

    @Transactional
    public void deleteMember(Long id) {
        Long memberId = requireId(id, "Member id is required");

        if (!memberRepository.existsById(memberId)) {
            throw new ResourceNotFoundException("Member not found with id " + memberId);
        }
        // Keep payments for revenue tracking - only delete member data
        membershipPauseRepository.deleteByMembershipMemberId(memberId);
        attendanceRepository.deleteByMemberId(memberId);
        membershipRepository.deleteByMemberId(memberId);
        memberRepository.deleteById(memberId);
    }

    public MemberResponse updateMember(Long id, MemberRequest updatedMember) {
        Long memberId = requireId(id, "Member id is required");
        Member member = findMember(memberId);

        if (memberRepository.existsByPhoneAndIdNot(updatedMember.getPhone(), memberId)) {
            throw new DuplicateResourceException("Member with phone number " + updatedMember.getPhone() + " already exists");
        }

        updateMemberFields(member, updatedMember);
        return MemberResponse.from(memberRepository.save(member));
    }

    public List<MemberStatusResponse> getActiveMembers() {
        return memberRepository.findAll()
                .stream()
                .map(this::toMemberStatus)
                .filter(status -> "ACTIVE".equals(status.getStatus()))
                .toList();
    }

    public List<MemberStatusResponse> getExpiredMembers() {
        return memberRepository.findAll()
                .stream()
                .map(this::toMemberStatus)
                .filter(status -> "EXPIRED".equals(status.getStatus()))
                .toList();
    }

    public List<MemberStatusResponse> getExpiringSoonMembers() {
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysFromNow = today.plusDays(7);

        return memberRepository.findAll()
                .stream()
                .flatMap(member -> latestMembership(requireId(member.getId(), "Member id is required")).stream())
                .filter(membership -> !membership.getExpiryDate().isBefore(today))
                .filter(membership -> !membership.getExpiryDate().isAfter(sevenDaysFromNow))
                .map(membership -> MemberStatusResponse.of(
                        membership.getMember(),
                        resolveStatus(membership),
                        membership.getExpiryDate(),
                        ChronoUnit.DAYS.between(today, membership.getExpiryDate())
                ))
                .toList();
    }

    public List<MemberResponse> searchMembers(String query) {
        String search = query == null ? "" : query.trim();

        if (search.isEmpty()) {
            return getAllMembers();
        }

        Optional<Member> memberByCode = memberRepository.findByMemberCodeIgnoreCase(search);
        if (memberByCode.isPresent()) {
            return List.of(MemberResponse.from(memberByCode.get()));
        }

        Optional<Member> memberByPhone = memberRepository.findByPhone(search);
        if (memberByPhone.isPresent()) {
            return List.of(MemberResponse.from(memberByPhone.get()));
        }

        return memberRepository.findByNameContainingIgnoreCase(search)
                .stream()
                .map(MemberResponse::from)
                .toList();
    }

    long countActiveMembers() {
        return membershipRepository.countCurrentActiveMemberships(LocalDate.now());
    }

    long countExpiredMembers() {
        return membershipRepository.countExplicitExpiredMemberships();
    }

    long countExpiringSoonMembers() {
        return getExpiringSoonMembers().size();
    }

    private Member findMember(Long id) {
        Long memberId = requireId(id, "Member id is required");
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id " + memberId));
    }

    private void updateMemberFields(Member member, MemberRequest request) {
        member.setName(request.getName());
        member.setPhone(request.getPhone());
        member.setEmergencyContactName(request.getEmergencyContactName());
        member.setEmergencyContactPhone(request.getEmergencyContactPhone());
    }

    private MemberStatusResponse toMemberStatus(Member member) {
        Long memberId = requireId(member.getId(), "Member id is required");
        return latestMembership(memberId)
                .map(membership -> MemberStatusResponse.of(
                        member,
                        resolveStatus(membership),
                        membership.getExpiryDate(),
                        ChronoUnit.DAYS.between(LocalDate.now(), membership.getExpiryDate())
                ))
                .orElseGet(() -> MemberStatusResponse.of(member, "EXPIRED", null, null));
    }

    private Optional<Membership> latestMembership(Long memberId) {
        return membershipRepository.findFirstByMemberIdOrderByJoinDateDescIdDesc(requireId(memberId, "Member id is required"));
    }

    private String resolveStatus(Membership membership) {
        if (membership.getExpiryDate().isBefore(LocalDate.now())) {
            return "EXPIRED";
        }
        return membership.getStatus();
    }

    private Long requireId(Long id, String message) {
        if (id == null) {
            throw new BadRequestException(message);
        }
        return id;
    }
}

