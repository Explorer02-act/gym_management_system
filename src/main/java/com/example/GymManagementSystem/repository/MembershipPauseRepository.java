package com.example.GymManagementSystem.repository;

import com.example.GymManagementSystem.model.MembershipPause;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MembershipPauseRepository extends JpaRepository<MembershipPause, Long> {

    List<MembershipPause> findByMembershipIdOrderByPauseStartDateDescIdDesc(Long membershipId);

    void deleteByMembershipMemberId(Long memberId);

    List<MembershipPause> findByPauseStartDateLessThanEqualAndPauseEndDateGreaterThanEqual(LocalDate pauseStartDate,
                                                                                         LocalDate pauseEndDate);

    @Query("select case when count(mp) > 0 then true else false end from MembershipPause mp " +
            "where mp.membership.id = :membershipId " +
            "and mp.pauseStartDate <= :pauseEndDate " +
            "and mp.pauseEndDate >= :pauseStartDate")
    boolean existsOverlap(@Param("membershipId") Long membershipId,
                          @Param("pauseStartDate") LocalDate pauseStartDate,
                          @Param("pauseEndDate") LocalDate pauseEndDate);
}

