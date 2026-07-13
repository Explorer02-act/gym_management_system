package com.example.GymManagementSystem.service;

import com.example.GymManagementSystem.exception.ResourceNotFoundException;
import com.example.GymManagementSystem.model.GymSettings;
import com.example.GymManagementSystem.model.Payment;
import com.example.GymManagementSystem.repository.GymSettingsRepository;
import com.example.GymManagementSystem.repository.PaymentRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class ReceiptService {

    private static final float PAGE_WIDTH = 595;
    private static final float PAGE_HEIGHT = 842;
    private static final float MARGIN = 44;
    private static final Color INK = new Color(37, 57, 115);
    private static final Color LIGHT = new Color(248, 250, 255);

    private final PaymentRepository paymentRepository;
    private final GymSettingsRepository gymSettingsRepository;

    public ReceiptService(PaymentRepository paymentRepository, GymSettingsRepository gymSettingsRepository) {
        this.paymentRepository = paymentRepository;
        this.gymSettingsRepository = gymSettingsRepository;
    }

    public byte[] generateReceipt(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id " + paymentId));

        GymSettings settings = gymSettingsRepository.findAll().stream().findFirst().orElse(null);
        String gymName = isBlank(settings != null ? settings.getGymName() : null)
                ? "MUSCLE MONSTERZ"
                : settings.getGymName();

        return buildReceiptPdf(payment, gymName);
    }

    public String receiptFileName(Long paymentId) {
        return "receipt-MMR-" + String.format("%05d", paymentId) + ".pdf";
    }

    private byte[] buildReceiptPdf(Payment payment, String gymName) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            PDImageXObject logo = loadLogo(document);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                drawOuterFrame(content);
                drawReceiptHeader(content, logo, gymName, payment);
                drawReceiptBody(content, payment);
                drawPaymentFooter(content, payment);
            }

            document.save(outputStream);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to generate receipt PDF", ex);
        }

        return outputStream.toByteArray();
    }

    private void drawOuterFrame(PDPageContentStream content) throws IOException {
        content.setStrokingColor(INK);
        content.setLineWidth(2f);
        content.addRect(MARGIN, MARGIN, PAGE_WIDTH - (MARGIN * 2), PAGE_HEIGHT - (MARGIN * 2));
        content.stroke();

        content.setLineWidth(0.8f);
        content.addRect(MARGIN + 7, MARGIN + 7, PAGE_WIDTH - (MARGIN * 2) - 14, PAGE_HEIGHT - (MARGIN * 2) - 14);
        content.stroke();
    }

    private void drawReceiptHeader(PDPageContentStream content,
                                   PDImageXObject logo,
                                   String gymName,
                                   Payment payment) throws IOException {
        if (logo != null) {
            drawLineBox(content, MARGIN + 18, PAGE_HEIGHT - 244, 112, 190);
            drawImageContained(content, logo, MARGIN + 22, PAGE_HEIGHT - 240, 104, 182);
        }

        drawCentered(content, gymName.toUpperCase(), 22, PAGE_HEIGHT - 82, PDType1Font.HELVETICA_BOLD);
        drawCentered(content, "Unisex Fitness Centre", 14, PAGE_HEIGHT - 104, PDType1Font.HELVETICA_BOLD);

        content.setStrokingColor(INK);
        content.setLineWidth(1.4f);
        content.moveTo(MARGIN + 8, PAGE_HEIGHT - 260);
        content.lineTo(PAGE_WIDTH - MARGIN - 8, PAGE_HEIGHT - 260);
        content.stroke();

        drawText(content, "No.", MARGIN + 18, PAGE_HEIGHT - 288, 12, PDType1Font.HELVETICA_BOLD);
        drawText(content, "MMR-" + String.format("%05d", paymentIdOf(payment)), MARGIN + 55, PAGE_HEIGHT - 288, 12, PDType1Font.HELVETICA);
        drawCentered(content, "INVOICE", 14, PAGE_HEIGHT - 288, PDType1Font.HELVETICA_BOLD);
        drawText(content, "Date :", PAGE_WIDTH - 178, PAGE_HEIGHT - 288, 12, PDType1Font.HELVETICA_BOLD);
        drawText(content, paymentDateOf(payment).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), PAGE_WIDTH - 125, PAGE_HEIGHT - 288, 12, PDType1Font.HELVETICA);
    }

    private void drawReceiptBody(PDPageContentStream content, Payment payment) throws IOException {
        float x = MARGIN + 16;
        float width = PAGE_WIDTH - (MARGIN * 2) - 32;
        float y = PAGE_HEIGHT - 330;

        drawLineBox(content, x, y, width, 34);
        drawText(content, "Received with thanks from", x + 8, y + 12, 11, PDType1Font.HELVETICA_BOLD);
        drawText(content, memberNameOf(payment), x + 178, y + 12, 11, PDType1Font.HELVETICA);

        y -= 48;
        drawLineBox(content, x, y, width, 34);
        drawText(content, "Member Code", x + 8, y + 12, 11, PDType1Font.HELVETICA_BOLD);
        drawText(content, memberCodeOf(payment), x + 110, y + 12, 11, PDType1Font.HELVETICA);
        drawText(content, "Plan", x + 270, y + 12, 11, PDType1Font.HELVETICA_BOLD);
        drawText(content, planNameOf(payment), x + 318, y + 12, 11, PDType1Font.HELVETICA);

        y -= 48;
        drawLineBox(content, x, y, width, 34);
        drawText(content, "the sum of", x + 8, y + 12, 11, PDType1Font.HELVETICA_BOLD);
        drawText(content, amountInWords(payment.getAmount()), x + 90, y + 12, 10, PDType1Font.HELVETICA);
        drawText(content, "Rupees", x + width - 56, y + 12, 11, PDType1Font.HELVETICA_BOLD);

        y -= 48;
        drawLineBox(content, x, y, width, 34);
        drawText(content, "Only", x + 8, y + 12, 11, PDType1Font.HELVETICA_BOLD);
        drawText(content, "By " + value(payment.getPaymentMode()), x + width - 94, y + 12, 11, PDType1Font.HELVETICA_BOLD);

        y -= 62;
        drawLineBox(content, x, y, 250, 42);
        drawText(content, "Transaction ID", x + 8, y + 16, 11, PDType1Font.HELVETICA_BOLD);
        drawText(content, value(payment.getTransactionId()), x + 122, y + 16, 11, PDType1Font.HELVETICA);

        drawLineBox(content, x + 270, y, width - 270, 42);
        drawText(content, "Balance", x + 280, y + 16, 11, PDType1Font.HELVETICA_BOLD);
        drawText(content, money(balanceAmountOf(payment)), x + 360, y + 16, 11, PDType1Font.HELVETICA);

        y -= 82;
        content.setNonStrokingColor(LIGHT);
        content.addRect(x, y, 180, 58);
        content.fill();
        drawLineBox(content, x, y, 180, 58);
        drawText(content, "Rs.", x + 12, y + 20, 28, PDType1Font.HELVETICA_BOLD);
        drawText(content, money(payment.getAmount()).replace("Rs ", ""), x + 68, y + 24, 18, PDType1Font.HELVETICA_BOLD);

        drawLineBox(content, x + 194, y, 138, 58);
        drawText(content, "Mode Of", x + 204, y + 34, 11, PDType1Font.HELVETICA_BOLD);
        drawText(content, "Payment", x + 204, y + 18, 11, PDType1Font.HELVETICA_BOLD);
        drawText(content, value(payment.getPaymentMode()), x + 270, y + 24, 10, PDType1Font.HELVETICA);

        drawText(content, "for", x + 358, y + 34, 10, PDType1Font.HELVETICA);
        drawRightAligned(content, "MUSCLE MONSTERZ", PAGE_WIDTH - MARGIN - 28, y + 18, 12, PDType1Font.HELVETICA_BOLD);
    }

    private void drawPaymentFooter(PDPageContentStream content, Payment payment) throws IOException {
        float x = MARGIN + 16;
        float y = 122;

        drawText(content, "* Payment Non Refundable", x, y + 48, 8, PDType1Font.HELVETICA);
        drawText(content, "* Admission Fees Valid For 1 year.", x, y + 34, 8, PDType1Font.HELVETICA);
        drawText(content, "* Term Fees Valid For That Particular Term Only.", x, y + 20, 8, PDType1Font.HELVETICA);

        drawText(content, "Customer Signature", PAGE_WIDTH - 220, y + 20, 12, PDType1Font.HELVETICA_BOLD);
        content.setStrokingColor(INK);
        content.setLineWidth(0.8f);
        content.moveTo(PAGE_WIDTH - 230, y + 12);
        content.lineTo(PAGE_WIDTH - 76, y + 12);
        content.stroke();
    }

    private PDImageXObject loadLogo(PDDocument document) {
        try {
            ClassPathResource resource = new ClassPathResource("static/assets/muscle-monsterz-gym.jpeg");
            if (!resource.exists()) {
                return null;
            }
            try (InputStream inputStream = resource.getInputStream()) {
                return PDImageXObject.createFromByteArray(document, inputStream.readAllBytes(), "gym-logo");
            }
        } catch (IOException ex) {
            return null;
        }
    }

    private void drawLineBox(PDPageContentStream content, float x, float y, float width, float height) throws IOException {
        content.setStrokingColor(INK);
        content.setLineWidth(1f);
        content.addRect(x, y, width, height);
        content.stroke();
    }

    private void drawCentered(PDPageContentStream content, String text, int size, float y, PDType1Font font) throws IOException {
        String safeText = safePdfText(text);
        float width = font.getStringWidth(safeText) / 1000 * size;
        drawText(content, safeText, (PAGE_WIDTH - width) / 2, y, size, font);
    }

    private void drawRightAligned(PDPageContentStream content, String text, float rightX, float y, int size, PDType1Font font) throws IOException {
        String safeText = safePdfText(text);
        float width = font.getStringWidth(safeText) / 1000 * size;
        drawText(content, safeText, rightX - width, y, size, font);
    }
    private void drawText(PDPageContentStream content, String text, float x, float y, int size, PDType1Font font) throws IOException {
        content.setNonStrokingColor(INK);
        content.setFont(font, size);
        content.beginText();
        content.newLineAtOffset(x, y);
        content.showText(safePdfText(text));
        content.endText();
    }

    private void drawImageContained(PDPageContentStream content,
                                    PDImageXObject image,
                                    float x,
                                    float y,
                                    float maxWidth,
                                    float maxHeight) throws IOException {
        float imageRatio = (float) image.getWidth() / image.getHeight();
        float boxRatio = maxWidth / maxHeight;
        float drawWidth = maxWidth;
        float drawHeight = maxHeight;

        if (imageRatio > boxRatio) {
            drawHeight = maxWidth / imageRatio;
        } else {
            drawWidth = maxHeight * imageRatio;
        }

        float drawX = x + ((maxWidth - drawWidth) / 2);
        float drawY = y + ((maxHeight - drawHeight) / 2);
        content.drawImage(image, drawX, drawY, drawWidth, drawHeight);
    }

    private void drawImageCover(PDPageContentStream content,
                                PDImageXObject image,
                                float x,
                                float y,
                                float width,
                                float height) throws IOException {
        float scale = Math.max(width / image.getWidth(), height / image.getHeight());
        float drawWidth = image.getWidth() * scale;
        float drawHeight = image.getHeight() * scale;
        float drawX = x + ((width - drawWidth) / 2);
        float drawY = y + ((height - drawHeight) / 2);

        content.saveGraphicsState();
        content.addRect(x, y, width, height);
        content.clip();
        content.drawImage(image, drawX, drawY, drawWidth, drawHeight);
        content.restoreGraphicsState();

        content.setStrokingColor(INK);
        content.setLineWidth(0.8f);
        content.addRect(x, y, width, height);
        content.stroke();
    }
    private String money(BigDecimal value) {
        if (value == null) {
            value = BigDecimal.ZERO;
        }
        return "Rs " + value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String amountInWords(BigDecimal value) {
        if (value == null) {
            value = BigDecimal.ZERO;
        }
        long rupees = value.setScale(0, RoundingMode.DOWN).longValue();
        return convertToWords(rupees) + " ONLY";
    }

    private String convertToWords(long number) {
        if (number == 0) {
            return "ZERO";
        }

        String[] units = {"", "ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE"};
        String[] teens = {"TEN", "ELEVEN", "TWELVE", "THIRTEEN", "FOURTEEN", "FIFTEEN", "SIXTEEN", "SEVENTEEN", "EIGHTEEN", "NINETEEN"};
        String[] tens = {"", "", "TWENTY", "THIRTY", "FORTY", "FIFTY", "SIXTY", "SEVENTY", "EIGHTY", "NINETY"};

        StringBuilder result = new StringBuilder();
        long crore = number / 10000000;
        long lakh = (number % 10000000) / 100000;
        long thousand = (number % 100000) / 1000;
        long hundred = (number % 1000) / 100;
        long remainder = number % 100;

        if (crore > 0) {
            result.append(convertToWords(crore)).append(" CRORE ");
        }
        if (lakh > 0) {
            result.append(convertToWords(lakh)).append(" LAKH ");
        }
        if (thousand > 0) {
            result.append(convertToWords(thousand)).append(" THOUSAND ");
        }
        if (hundred > 0) {
            result.append(units[(int) hundred]).append(" HUNDRED ");
        }
        if (remainder > 0) {
            if (remainder < 10) {
                result.append(units[(int) remainder]);
            } else if (remainder < 20) {
                result.append(teens[(int) remainder - 10]);
            } else {
                long tensValue = remainder / 10;
                long unitValue = remainder % 10;
                result.append(tens[(int) tensValue]);
                if (unitValue > 0) {
                    result.append(" ").append(units[(int) unitValue]);
                }
            }
        }
        return result.toString().trim();
    }

    private Long paymentIdOf(Payment payment) {
        return payment.getId() == null ? 0L : payment.getId();
    }

    private LocalDate paymentDateOf(Payment payment) {
        return payment.getPaymentDate() == null ? LocalDate.now() : payment.getPaymentDate();
    }

    private String memberNameOf(Payment payment) {
        return payment.getMember() != null && payment.getMember().getName() != null ? payment.getMember().getName() : "-";
    }

    private String memberCodeOf(Payment payment) {
        return payment.getMember() != null && payment.getMember().getMemberCode() != null ? payment.getMember().getMemberCode() : "-";
    }

    private String planNameOf(Payment payment) {
        return payment.getMembership() != null && payment.getMembership().getPlanType() != null ? payment.getMembership().getPlanType() : "-";
    }

    private BigDecimal balanceAmountOf(Payment payment) {
        if (payment.getMembership() == null) {
            return BigDecimal.ZERO;
        }
        return payment.getMembership().getBalanceAmount() != null ? payment.getMembership().getBalanceAmount() : BigDecimal.ZERO;
    }

    private String value(String value) {
        return isBlank(value) ? "-" : value;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String safePdfText(String value) {
        return value == null ? "-" : value.replace("ÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¢ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬Ãƒâ€¦Ã‚Â¡ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¹", "Rs").replace("ÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¢ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡Ãƒâ€šÃ‚Â¬ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¢", "-").replace("ÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¢ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬Ãƒâ€šÃ‚Â ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬ÃƒÂ¢Ã¢â‚¬Å¾Ã‚Â¢", "->");
    }
}
