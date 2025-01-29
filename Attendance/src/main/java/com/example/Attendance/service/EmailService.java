package com.example.Attendance.service;

import com.example.Attendance.error.CustomException;
import com.example.Attendance.error.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void sendChargeFail(String email,Long salary){
        long charge= (long) (salary*0.001);
        String title = "[집계사장]자동 이체 수수료 실패";
        String content = new StringBuilder()
                .append("<style>")
                .append(".highlight { font-weight: bold; color: #0066cc; }")
                .append("</style>")
                .append("집계사장을 사용해주셔서 감사합니다. 🦀🍔🍟")
                .append("<br><br> ")
                .append("자동 이체 수수료 이체가 실패했습니다. +")
                .append(charge)
                .append("의 수수료를 사이트에 입금해 주세요 ")
                .append("예금자: <span class='highlight'>집계사장</span> , ")
                .append("계좌번호: <span class='highlight'>98765432112</span> , ")
                .append("은행: <span class='highlight'>우리은행</span>")
                .append("<br> ")
                .toString();
        mailSend(email, title, content);
    }

    public void sendPayStatement(String email, String url) {
        String title = "[집계사장]직원 급여명세서";
        String content = new StringBuilder()
                .append("집계사장을 사용해주셔서 감사합니다. 🦀🍔🍟")
                .append("<br><br> ")
                .append("급여 명세서 url은 ")
                .append(url)
                .append("입니다.")
                .append("<br> ")
                .toString();
        mailSend(email, title, content);
    }

    public void sendBankFail(String email, String name , LocalDate date,String message){
        String title = "[집계사장]직원 자동 이체 실패";
        String content = new StringBuilder()
                .append("집계사장을 사용해주셔서 감사합니다. 🦀🍔🍟")
                .append("<br><br> ")
                .append("자동이체는 ")
                .append(message)
                .append("로 인해 ")
                .append(name)
                .append("의 ")
                .append(date)
                .append("날짜의 자동이체가 실패했습니다.")
                .append("<br> ")
                .toString();
        mailSend(email, title, content);
    }

    public void sendPdfFail(String email, String name , LocalDate date){
        String title = "[집계사장]직원 급여 명세서 발급 실패";
        String content = new StringBuilder()
                .append("집계사장을 사용해주셔서 감사합니다. 🦀🍔🍟")
                .append("<br><br> ")
                .append("급여명세서는 ")
                .append(name)
                .append("의 ")
                .append(date)
                .append("날짜의 급여명세서 발급에 실패했습니다.")
                .append("<br> ")
                .toString();
        mailSend(email, title, content);
    }

    public void sendBankFailToAdmin(String email, String presidentaccount, String employeeacount, LocalDate date,String message){
        String title = "[집계사장 관리자 용 ] 직원 급여 이체 실패";
        String content = new StringBuilder()
                .append("관리자용 발송 메일입니다.")
                .append("<br><br> ")
                .append(message)
                .append("로 인해 사장님 계좌 : ")
                .append(presidentaccount)
                .append("의 직원 계좌 ")
                .append(employeeacount)
                .append("로의 \n")
                .append(date)
                .append("날짜의 자동이체가 실패했습니다.")
                .append("<br> ")
                .toString();
        mailSend(email, title, content);
    }


    private void mailSend(String toMail, String title, String content) {
        MimeMessage message = javaMailSender.createMimeMessage(); // MimeMessage 객체 생성
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setTo(toMail); // 이메일 수신자 주소 설정
            helper.setSubject(title); // 이메일 주소 설정
            helper.setText(content, true); // 이메일의 내용
            javaMailSender.send(message);
            log.info(" {}에게 급여명세서: {} 전송에 성공했습니다:", toMail, content);
        } catch (MailSendException sme)
        {
            String errorMessage = sme.getMessage();
            log.error("에러 상세: {}", errorMessage);
            throw new CustomException(ErrorCode.NOT_EXISTS_EMAIL);
        }  catch (MessagingException e) {
            log.error("이메일 전송 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }
}