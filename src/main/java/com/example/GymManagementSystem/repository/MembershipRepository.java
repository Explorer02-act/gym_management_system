package com.example.GymManagementSystem.repository;

import com.example.GymManagementSystem.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MembershipRepository extends JpaRepository<Membership, Long> {

    List<Membership> findByMemberIdAndStatus(Long memberId,
                                         String status);

    List<Membership> findByStatus(String status);
    
    List<Membership> findByMemberId(Long memberId);

    List<Membership> findByMemberIdOrderByJoinDateDescIdDesc(Long memberId);

    Optional<Membership> findFirstByMemberIdOrderByJoinDateDescIdDesc(Long memberId);

    @Query("select count(m) from Membership m where m.status = 'ACTIVE' and m.expiryDate >= :today")
    long countCurrentActiveMemberships(@Param("today") LocalDate today);

    @Query("select count(m) from Membership m where m.status = 'EXPIRED'")
    long countExplicitExpiredMemberships();
}
