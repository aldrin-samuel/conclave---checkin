package com.conclave.checkin.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.io.File;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(
            String to,
            String subject,
            String body
    ) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(to);

        message.setSubject(subject);

        message.setText(body);

        mailSender.send(message);
    }

    public void sendPass(
            String toEmail,
            File passFile
    ) throws Exception {

        MimeMessage message =
                mailSender.createMimeMessage();

        MimeMessageHelper helper =
                new MimeMessageHelper(
                        message,
                        true
                );

        helper.setTo(toEmail);

        helper.setSubject(
                "Your Delegate Pass"
        );

        helper.setText(
                """
                Dear Delegate,
    
                Your delegate pass is attached to this email.
    
                Please carry it during the event.
    
                Regards,
                Event Registration Team
                """
        );

        helper.addAttachment(
                "Delegate-Pass.png",
                new FileSystemResource(passFile)
        );

        mailSender.send(message);
    }

}