package com.example.GymManagementSystem.service;

import com.example.GymManagementSystem.model.Member;
import com.example.GymManagementSystem.model.Membership;
import com.example.GymManagementSystem.repository.MemberRepository;
import com.example.GymManagementSystem.repository.MembershipRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final MembershipRepository membershipRepository;

    public MemberService(MemberRepository memberRepository,
                         MembershipRepository membershipRepository) {
        this.memberRepository = memberRepository;
        this.membershipRepository = membershipRepository;
    }

    public Member addMember(Member member) {
        return memberRepository.save(member);
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }

    public Member updateMember(Long id, Member updatedMember) {

        Member member = memberRepository.findById(id).orElse(null);

        if (member != null) {

            member.setName(updatedMember.getName());
            member.setPhone(updatedMember.getPhone());

            return memberRepository.save(member);
        }
        return null;
    }

    public Membership renewMembership(Long id, String planType) {

        Member member = memberRepository.findById(id).orElse(null);

        if (member != null) {
            LocalDate joinDate = LocalDate.now();
            LocalDate expiryDate = calculateExpiryDate(joinDate, planType);

            Membership membership = new Membership(
                    planType,
                    joinDate,
                    expiryDate,
                    "ACTIVE",
                    member
            );

            return membershipRepository.save(membership);
        }

        return null;
    }

    private LocalDate calculateExpiryDate(LocalDate joinDate, String planType) {
        switch (planType) {
            case "1 Month":
                return joinDate.plusMonths(1);
            case "3 Month":
                return joinDate.plusMonths(3);
            case "6 Month":
                return joinDate.plusMonths(6);
            case "12 Month":
                return joinDate.plusMonths(12);
            default:
                return joinDate;
        }
    }
}
