package com.example.GymManagementSystem.service;

import com.example.GymManagementSystem.exception.ResourceNotFoundException;
import com.example.GymManagementSystem.model.GymSettings;
import com.example.GymManagementSystem.model.Payment;
import com.example.GymManagementSystem.repository.GymSettingsRepository;
import com.example.GymManagementSystem.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReceiptService {

    private final PaymentRepository paymentRepository;
    private final GymSettingsRepository gymSettingsRepository;

    public ReceiptService(PaymentRepository paymentRepository, GymSettingsRepository gymSettingsRepository) {
        this.paymentRepository = paymentRepository;
        this.gymSettingsRepository = gymSettingsRepository;
    }

    public byte[] generateReceipt(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id " + paymentId));
        GymSettings settings = gymSettingsRepository.findAll()
                .stream()
                .findFirst()
                .orElse(null);

        String gymName = settings != null && settings.getGymName() != null ? settings.getGymName() : "MUSCLE MONSTERZ";
        List<String> lines = new ArrayList<>();
        lines.add(gymName + " RECEIPT");
        lines.add("Receipt No: MMR-" + String.format("%05d", payment.getId()));
        lines.add("Member: " + value(payment.getMember() != null ? payment.getMember().getName() : null));
        lines.add("Member Code: " + value(payment.getMember() != null ? payment.getMember().getMemberCode() : null));
        lines.add("Plan: " + value(payment.getMembership() != null ? payment.getMembership().getPlanType() : null));
        lines.add("Amount Paid: Rs " + payment.getAmount());
        lines.add("Payment Date: " + payment.getPaymentDate().format(DateTimeFormatter.ISO_DATE));
        lines.add("Payment Mode: " + value(payment.getPaymentMode()));
        lines.add("Transaction ID: " + value(payment.getTransactionId()));
        lines.add("Thank you for training with " + gymName + ".");

        return buildPdf(lines);
    }

    public String receiptFileName(Long paymentId) {
        return "receipt-MMR-" + String.format("%05d", paymentId) + ".pdf";
    }

    private byte[] buildPdf(List<String> lines) {
        StringBuilder text = new StringBuilder();
        text.append("BT\n/F1 18 Tf\n50 780 Td\n");
        boolean first = true;
        for (String line : lines) {
            if (!first) {
                text.append("0 -28 Td\n");
            }
            text.append("(").append(escape(line)).append(") Tj\n");
            first = false;
        }
        text.append("ET");

        List<String> objects = List.of(
                "<< /Type /Catalog /Pages 2 0 R >>",
                "<< /Type /Pages /Kids [3 0 R] /Count 1 >>",
                "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>",
                "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>",
                "<< /Length " + text.length() + " >>\nstream\n" + text + "\nendstream"
        );

        StringBuilder pdf = new StringBuilder("%PDF-1.4\n");
        List<Integer> offsets = new ArrayList<>();
        for (int i = 0; i < objects.size(); i++) {
            offsets.add(pdf.toString().getBytes(StandardCharsets.US_ASCII).length);
            pdf.append(i + 1).append(" 0 obj\n")
                    .append(objects.get(i)).append("\n")
                    .append("endobj\n");
        }

        int xrefOffset = pdf.toString().getBytes(StandardCharsets.US_ASCII).length;
        pdf.append("xref\n0 ").append(objects.size() + 1).append("\n");
        pdf.append("0000000000 65535 f \n");
        for (Integer offset : offsets) {
            pdf.append(String.format("%010d 00000 n \n", offset));
        }
        pdf.append("trailer\n<< /Size ").append(objects.size() + 1).append(" /Root 1 0 R >>\n");
        pdf.append("startxref\n").append(xrefOffset).append("\n%%EOF");
        return pdf.toString().getBytes(StandardCharsets.US_ASCII);
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }

    private String value(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
