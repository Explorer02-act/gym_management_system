package com.example.GymManagementSystem.dto;

public class EnrollmentResponse {

    private MemberResponse member;
    private MembershipResponse membership;
    private PaymentResponse payment;
    private String receiptNumber;

    public MemberResponse getMember() {
        return member;
    }

    public void setMember(MemberResponse member) {
        this.member = member;
    }

    public MembershipResponse getMembership() {
        return membership;
    }

    public void setMembership(MembershipResponse membership) {
        this.membership = membership;
    }

    public PaymentResponse getPayment() {
        return payment;
    }

    public void setPayment(PaymentResponse payment) {
        this.payment = payment;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }
}
