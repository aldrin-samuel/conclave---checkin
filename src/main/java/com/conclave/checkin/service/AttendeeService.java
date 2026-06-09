package com.conclave.checkin.service;

import com.conclave.checkin.model.Attendee;
import com.conclave.checkin.repository.AttendeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AttendeeService {

    @Autowired
    private AttendeeRepository repository;

    @Autowired
    private QrService qrService;

    @Autowired
    private PassService passService;

    @Autowired
    private EmailService emailService;

    public void sendPendingPasses() throws Exception {

        List<Attendee> attendees =
                repository.findByMailSentFalse();

        for (Attendee attendee : attendees) {

            try {

                File qr =
                        qrService.generateQr(
                                attendee.getUid().toString()
                        );

                File pass =
                        passService.generatePass(
                                attendee,
                                qr
                        );

                emailService.sendPass(
                        attendee.getEmailId(),
                        pass
                );

                attendee.setMailSent(true);

                attendee.setMailSentTime(
                        LocalDateTime.now()
                );

                repository.save(attendee);

                qr.delete();
                pass.delete();

                System.out.println(
                        "Pass sent to: " +
                                attendee.getEmailId()
                );

            } catch (Exception e) {

                System.out.println(
                        "Failed for: " +
                                attendee.getEmailId()
                );

                e.printStackTrace();
            }
        }
    }
}