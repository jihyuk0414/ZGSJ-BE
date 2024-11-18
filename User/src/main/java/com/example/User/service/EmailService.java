package com.example.User.service;


import com.example.User.error.CustomException;
import com.example.User.error.ErrorCode;
import com.example.User.model.President;
import com.example.User.repository.PresidentRepository;
import com.example.User.util.QRCodeUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private PresidentRepository presidentRepository;

    private final QRCodeUtil qrCodeUtil;
    private final BCryptPasswordEncoder encoder;

    private static final String CHAR_SET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int PASSWORD_LENGTH = 10;

    public byte[] sendQRToEmail(String email, Integer storeId) {  // 리턴 타입을 void로 변경
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            byte[] qrImageData = qrCodeUtil.generateQRCodeImage(storeId);

            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("[집계사장]직원관리 QR");

            // QR 코드를 첨부 파일로도 추가
            helper.addAttachment("QRCode.png", new ByteArrayDataSource(qrImageData, "image/png"));

            InputStreamSource imageSource = new ByteArrayResource(qrImageData);
            helper.addInline("qrImage", imageSource, "image/png");  // ContentType 명시적 지정

            helper.setText(createHTML(), true);

            mailSender.send(mimeMessage);
            log.info("QR 코드 이메일 전송 완료: {}", email);
            return qrImageData;
        } catch (MessagingException e) {
            log.error("이메일 전송 실패: {}", e.getMessage());
            throw new RuntimeException("메일 발송에 실패했습니다.", e);
        }
    }

    // 임의의 비밀번호 생성
    public String makeRandomPassword() {
        SecureRandom rand = new SecureRandom();
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        for(int i = 0; i < PASSWORD_LENGTH; i++) {
            int randIdx = rand.nextInt(CHAR_SET.length());
            password.append(CHAR_SET.charAt(randIdx));
        }
        return password.toString();
    }

    // 이메일과 이름 확인
    public boolean validateEmailAndName(String email, String name) {
        Optional<President> president = presidentRepository.findByEmail(email);
        return president.isPresent() && president.get().getName().equals(name);
    }

    // mail 양식 설정
    public String joinEmail(String email) {
        String authPassword = makeRandomPassword();
        String setForm = "${EMAIL_USERNAME}@gmail.com"; // email-config에 설정한 내 이메일 주소
        String toMail = email;
        String title = "[집계사장] 임시 비밀번호를 보내드립니다."; // 이메일 제목
        String content =
                "집계사장을 사용해주셔서 감사합니다. 🦀🍔🍟" +
                        "<br><br> " +
                        "임시 비밀번호는 " + authPassword + "입니다." +
                        "<br> " +
                        "보안을 위해 로그인 후에는 꼭 비밀번호를 변경해주세요!"; // 이메일 내용
        mailSend(setForm, toMail, title, content);
        return authPassword;
    }

    private void mailSend(String setForm, String toMail, String title, String content) {
        MimeMessage message = mailSender.createMimeMessage(); // MimeMessage 객체 생성
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(setForm); // 이메일 발신자 주소 설정
            helper.setTo(toMail); // 이메일 수신자 주소 설정
            helper.setSubject(title); // 이메일 주소 설정
            helper.setText(content, true); // 이메일의 내용
            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("이메일 전송 실패: {}", e.getMessage());
            throw new RuntimeException("메일 발송에 실패했습니다.", e);
        }
    }

    public void updatePassword(String str, String email) {
        String encodedPassword = encoder.encode(str); // 패스워드 암호화
        President existingPresident = presidentRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.PRESIDENT_NOT_FOUND));
        existingPresident.setPassword(encodedPassword);
        presidentRepository.save(existingPresident);
    }

    private String createHTML() {
        return """
                <div style='max-width: 600px; margin: 0 auto; padding: 20px; font-family: Arial, sans-serif;'>
                    <div style='background-color: #0068FF; padding: 20px; text-align: center;'>
                        <h1 style='color: white; margin: 0;'>집계사장</h1>
                    </div>
                    <div style='background-color: #ffffff; padding: 40px 20px; text-align: center; border: 1px solid #e9e9e9;'>
                        <h2 style='color: #333333; margin-bottom: 30px;'>직원 QR 이미지</h2>
                        <p style='color: #666666; font-size: 16px; line-height: 24px;'>
                            안녕하세요.<br>직원 관리를 위한 QR코드를 보내드립니다.
                        </p>
                        <div style='background-color: #f8f9fa; padding: 15px; margin: 30px auto; max-width: 300px; border-radius: 4px;'>
                            <img src='cid:qrImage' alt='QR Code' style='max-width: 200px; max-height: 200px;'/>
                        </div>
                    </div>
                    <div style='text-align: center; padding: 20px; color: #999999; font-size: 12px;'>
                        <p>본 메일은 발신전용 메일입니다.</p>
                        <p>&copy; 2024 CoreBank. All rights reserved.</p>
                    </div>
                </div>
                """;
    }
}