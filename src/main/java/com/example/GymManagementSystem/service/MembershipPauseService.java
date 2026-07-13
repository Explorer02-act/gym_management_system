package com.example.GymManagementSystem.service;

import com.example.GymManagementSystem.dto.MembershipPauseRequest;
import com.example.GymManagementSystem.dto.MembershipPauseResponse;
import com.example.GymManagementSystem.exception.BadRequestException;
import com.example.GymManagementSystem.exception.ResourceNotFoundException;
import com.example.GymManagementSystem.model.Membership;
import com.example.GymManagementSystem.model.MembershipPause;
import com.example.GymManagementSystem.repository.MembershipPauseRepository;
import com.example.GymManagementSystem.repository.MembershipRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class MembershipPauseService {

    private final MembershipRepository membershipRepository;
    private final MembershipPauseRepository membershipPauseRepository;

    public MembershipPauseService(MembershipRepository membershipRepository,
                                  MembershipPauseRepository membershipPauseRepository) {
        this.membershipRepository = membershipRepository;
        this.membershipPauseRepository = membershipPauseRepository;
    }

    @Transactional
    public MembershipPauseResponse pauseMembership(MembershipPauseRequest request) {
        Membership membership = membershipRepository.findById(request.getMembershipId())
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found with id " + request.getMembershipId()));

        LocalDate pauseStartDate = request.getPauseStartDate();
        LocalDate pauseEndDate = request.getPauseEndDate();

        if (pauseStartDate == null || pauseEndDate == null) {
            throw new BadRequestException("Pause start and end dates are required");
        }
        if (pauseEndDate.isBefore(pauseStartDate)) {
            throw new BadRequestException("Pause end date must be on or after pause start date");
        }

        long pauseDays = ChronoUnit.DAYS.between(pauseStartDate, pauseEndDate);
        if (pauseDays < 1) {
            throw new BadRequestException("Pause duration must be at least one day");
        }

        boolean overlaps = membershipPauseRepository.existsOverlap(
                membership.getId(),
                pauseStartDate,
                pauseEndDate
        );
        if (overlaps) {
            throw new BadRequestException("The requested pause overlaps with an existing pause for this membership");
        }

        MembershipPause pause = new MembershipPause();
        pause.setMembership(membership);
        pause.setPauseStartDate(pauseStartDate);
        pause.setPauseEndDate(pauseEndDate);
        pause.setPauseDays(Math.toIntExact(pauseDays));
        pause.setReason(request.getReason());

        MembershipPause savedPause = membershipPauseRepository.save(pause);

        membership.setExpiryDate(membership.getExpiryDate().plusDays(pauseDays));
        membershipRepository.save(membership);

        return MembershipPauseResponse.from(savedPause, membership.getExpiryDate());
    }

    public List<MembershipPauseResponse> getPausedMemberships() {
        LocalDate today = LocalDate.now();
        return membershipPauseRepository.findByPauseStartDateLessThanEqualAndPauseEndDateGreaterThanEqual(today, today)
                .stream()
                .map(pause -> MembershipPauseResponse.from(pause, pause.getMembership().getExpiryDate()))
                .toList();
    }

    public List<MembershipPauseResponse> getPauseHistory(Long membershipId) {
        if (!membershipRepository.existsById(membershipId)) {
            throw new ResourceNotFoundException("Membership not found with id " + membershipId);
        }

        return membershipPauseRepository.findByMembershipIdOrderByPauseStartDateDescIdDesc(membershipId)
                .stream()
                .map(pause -> MembershipPauseResponse.from(pause, pause.getMembership().getExpiryDate()))
                .toList();
    }
}
