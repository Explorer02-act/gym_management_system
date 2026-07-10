package com.example.GymManagementSystem.dto;

public class AttendanceLeaderboardResponse {

    private Long memberId;
    private String memberName;
    private String memberCode;
    private long visits;

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    public String getMemberCode() { return memberCode; }
    public void setMemberCode(String memberCode) { this.memberCode = memberCode; }
    public long getVisits() { return visits; }
    public void setVisits(long visits) { this.visits = visits; }
}
