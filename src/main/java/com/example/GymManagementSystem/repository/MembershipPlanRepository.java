package com.example.GymManagementSystem.repository;

import com.example.GymManagementSystem.model.MembershipPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, Long> {

    List<MembershipPlan> findByActiveTrueOrderByDurationMonthsAsc();

    Optional<MembershipPlan> findByNameIgnoreCase(String name);
}
