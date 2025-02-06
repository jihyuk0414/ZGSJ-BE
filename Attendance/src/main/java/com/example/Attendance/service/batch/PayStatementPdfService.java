package com.example.Attendance.service.batch;

import com.example.Attendance.dto.batch.pdf.PdfInputData;
import com.example.Attendance.service.batch.PdfService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PayStatementPdfService extends PdfService {

    public byte[] generateIncomeStatementPdf(PdfInputData  pid) {
        // 손익 계산하기
        // 게산한 손익관련 자료로 HTML 생성
        String html = generateHtml(pid);
        log.info("Generated HTML: {}", html);
        // HTML을 PDF로 변환
        return convertHtmlToPdf(html);
    }

    private String generateHtml(PdfInputData  pid) {
        StringBuilder html = new StringBuilder();
        html.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                .append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3" +
                        ".org/TR/xhtml1/DTD/xhtml1-strict.dtd\">")
                .append("<html xmlns=\"http://www.w3.org/1999/xhtml\">")
                .append("<head>")
                .append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>")
                .append("<style type=\"text/css\">")
                .append("@font-face {")
                .append("    font-family: 'NanumGothic';")
                .append("    src: url('classpath:/fonts/NanumGothic-Regular.ttf') format('truetype');")
                .append("}")
                .append("body { font-family: 'NanumGothic', Arial, sans-serif; margin: 40px; }")
                .append(".title { text-align: center; font-size: 24px; margin-bottom: 30px; font-weight: bold; }")
                .append(".date { text-align: center; font-size: 16px; margin-bottom: 40px; }")
                .append(".section { margin: 20px 0; }")
                .append(".table { width: 100%; border-collapse: collapse; margin: 15px 0; }")
                .append(".table th, .table td { border: 1px solid #000; padding: 8px; text-align: left; }")
                .append(".table th { background-color: #f5f5f5; width: 150px; }")
                .append(".total { font-weight: bold; }")
                .append(".footer { margin-top: 50px; text-align: right; font-size: 14px; }")
                .append("</style>")
                .append("</head><body>")

                // 제목
                .append("<div class=\"title\">급여지급명세서</div>")
                .append("<div class=\"date\">").append(pid.getIssuanceDate().getYear()).append("년 ")
                .append(pid.getIssuanceDate().getMonthValue()).append("월</div>")

                // 기본 정보
                .append("<div class=\"section\">")
                .append("<table class=\"table\">")
                .append("<tr>")
                .append("<th colspan=\"4\">기본 정보</th>")
                .append("</tr>")
                .append("<tr>")
                .append("<th>이름</th>")
                .append("<td>").append(pid.getName()).append("</td>")
                .append("<th>이메일</th>")
                .append("<td>").append(pid.getEmail()).append("</td>")
                .append("</tr>")
                .append("<tr>")
                .append("<th>전화번호</th>")
                .append("<td>").append(pid.getPhoneNumber()).append("</td>")
                .append("<th>생년월일</th>")
                .append("<td>").append(pid.getBirthDate()).append("</td>")
                .append("</tr>")
                .append("</table>")
                .append("</div>")

                // 지급 내역
                .append("<div class=\"section\">")
                .append("<h3>지급 내역</h3>")
                .append("<table class=\"table\">")
                .append("<tr>")
                .append("<th colspan=\"2\">지급항목</th>")
                .append("</tr>")
                .append("<tr>")
                .append("<th>기본급여(a)</th>")
                .append("<td>").append(String.format("%,d원", pid.getSalary())).append("</td>")
                .append("</tr>")
                .append("<tr>")
                .append("<th>주휴 수당(b)</th>")
                .append("<td>").append(String.format("%,d원", pid.getAllowance())).append("</td>")
                .append("</tr>")
                .append("<tr>")
                .append("<th>국민연금(c)</th>")
                .append("<td>").append(String.format("%,d원", pid.getNationalCharge())).append("</td>")
                .append("</tr>")
                .append("<tr>")
                .append("<th>건보료 + 장기요양보험료(d)</th>")
                .append("<td>").append(String.format("%,d원", pid.getInsuranceCharge())).append("</td>")
                .append("</tr>")
                .append("<tr>")
                .append("<th>고용 보험료(e)</th>")
                .append("<td>").append(String.format("%,d원", pid.getEmploymentCharge())).append("</td>")
                .append("</tr>")
                .append("<tr>")
                .append("<th>소득세(f)</th>")
                .append("<td>").append(String.format("%,d원", pid.getIncomeTax())).append("</td>")
                .append("</tr>")
                .append("<tr>")
                .append("<th>지급액(a+b-c-d-e-f)</th>")
                .append("<td>").append(String.format("%,d원",
                        pid.getSalary() + pid.getAllowance() -
                                pid.getNationalCharge() - pid.getInsuranceCharge() -
                                pid.getEmploymentCharge() - pid.getIncomeTax())).append("</td>")
                .append("</tr>")
                .append("</table>")
                .append("</div>")

                // 발행일자
                .append("<div class=\"footer\">")
                .append("발행일자: ").append(pid.getIssuanceDate())
                .append("</div>")

                .append("</body></html>");
        return html.toString();
    }
}