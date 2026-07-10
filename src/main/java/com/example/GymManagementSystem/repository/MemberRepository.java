package com.example.GymManagementSystem.repository;

import com.example.GymManagementSystem.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByPhone(String phone);

    boolean existsByPhoneAndIdNot(String phone, Long id);

    Optional<Member> findByPhone(String phone);

    Optional<Member> findByMemberCodeIgnoreCase(String memberCode);

    List<Member> findByNameContainingIgnoreCase(String name);
}
