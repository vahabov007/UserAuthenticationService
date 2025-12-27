package com.vahabvahabov.LoginDemo.service.imp;

import com.vahabvahabov.LoginDemo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;


    @Override
    @Async
    public void sendPinToEmail(String toEmail, String pin) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("vahabovcompany@gmail.com");
        mailMessage.setTo(toEmail);
        mailMessage.setSubject("Verification For Your Account");
        mailMessage.setText("Dear user,\n\nTo complete your account verification, please use the following PIN code : " + pin + "\n\nFor your security, please do not share this code with anyone.");
        javaMailSender.send(mailMessage);
    }
}