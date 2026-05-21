package com.example.GymManagementSystem.repository;

import com.example.GymManagementSystem.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

}