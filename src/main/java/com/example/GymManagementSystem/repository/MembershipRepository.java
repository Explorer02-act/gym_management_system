package com.example.GymManagementSystem.repository;

import com.example.GymManagementSystem.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipRepository extends JpaRepository<Membership, Long> {
}
