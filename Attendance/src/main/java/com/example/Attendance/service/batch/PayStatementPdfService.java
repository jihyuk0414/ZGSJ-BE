package com.example.Attendance.service.batch;


import com.example.Attendance.dto.batch.pdf.PdfInputData;
import com.example.Attendance.error.CustomException;
import com.example.Attendance.error.ErrorCode;
import com.itextpdf.text.pdf.BaseFont;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class PayStatementPdfService extends PdfService {
    private static final String HTML_TEMPLATE = """
            <?xml version="1.0" encoding="UTF-8"?>
            <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
            <html xmlns="http://www.w3.org/1999/xhtml">
            <head>
                <style type="text/css">
                    body { font-family: 'NanumGothic', Arial, sans-serif; margin: 40px; }
                    .title { text-align: center; font-size: 24px; margin-bottom: 30px; font-weight: bold; }
                    .date { text-align: center; font-size: 16px; margin-bottom: 40px; }
                    .section { margin: 20px 0; }
                    .table { width: 100%; border-collapse: collapse; margin: 15px 0; }
                    .table th, .table td { border: 1px solid #000; padding: 8px; text-align: left; }
                    .table th { background-color: #f5f5f5; width: 150px; }
                    .total { font-weight: bold; }
                    .footer { margin-top: 50px; text-align: right; font-size: 14px; }
                </style>
            </head>
            <body>
                <div class="title">급여지급명세서</div>
                <div class="date">${year}년 ${month}월</div>
                ${basicInfo}
                ${paymentDetails}
                <div class="footer">발행일자: ${issuanceDate}</div>
            </body>
            </html>""";

    private String generateBasicInfoSection(PdfInputData pid) {
        return String.format("""
                <div class="section">
                    <table class="table">
                        <tr><th colspan="4">기본 정보</th></tr>
                        <tr>
                            <th>이름</th><td>%s</td>
                            <th>이메일</th><td>%s</td>
                        </tr>
                        <tr>
                            <th>전화번호</th><td>%s</td>
                            <th>생년월일</th><td>%s</td>
                        </tr>
                    </table>
                </div>""",
                escapeXml(pid.getName()),
                escapeXml(pid.getEmail()),
                escapeXml(pid.getPhoneNumber()),
                escapeXml(pid.getBirthDate().toString()));
    }



    private String generatePaymentSection(PdfInputData pid) {
        int totalPayment = (int) (pid.getSalary() + pid.getAllowance() -
                        pid.getNationalCharge() - pid.getInsuranceCharge() -
                        pid.getEmploymentCharge() - pid.getIncomeTax());

        return String.format("""
                <div class="section">
                    <h3>지급 내역</h3>
                    <table class="table">
                        <tr><th colspan="2">지급항목</th></tr>
                        <tr><th>기본급여(a)</th><td>%,d원</td></tr>
                        <tr><th>주휴 수당(b)</th><td>%,d원</td></tr>
                        <tr><th>국민연금(c)</th><td>%,d원</td></tr>
                        <tr><th>건보료 + 장기요양보험료(d)</th><td>%,d원</td></tr>
                        <tr><th>고용 보험료(e)</th><td>%,d원</td></tr>
                        <tr><th>소득세(f)</th><td>%,d원</td></tr>
                        <tr><th>지급액(a+b-c-d-e-f)</th><td>%,d원</td></tr>
                    </table>
                </div>""",
                pid.getSalary(), pid.getAllowance(),
                pid.getNationalCharge(), pid.getInsuranceCharge(),
                pid.getEmploymentCharge(), pid.getIncomeTax(),
                totalPayment);
    }

    // XML 특수문자 이스케이프 처리
    private String escapeXml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    @Override
    public byte[] convertHtmlToPdf(String html) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("PDF 생성 중 오류 발생", e);
            throw new CustomException(ErrorCode.PDF_CREATE_ERROR);
        }
    }
    public byte[] generateIncomeStatementPdf(PdfInputData pid) {
        try {
            String html = HTML_TEMPLATE
                    .replace("${year}", String.valueOf(pid.getIssuanceDate().getYear()))
                    .replace("${month}", String.valueOf(pid.getIssuanceDate().getMonthValue()))
                    .replace("${basicInfo}", generateBasicInfoSection(pid))
                    .replace("${paymentDetails}", generatePaymentSection(pid))
                    .replace("${issuanceDate}", pid.getIssuanceDate().toString());

            return convertHtmlToPdf(html);
        } catch (Exception e) {
            log.error("급여명세서 PDF 생성 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.PDF_CREATE_ERROR);
        }
    }

}