package com.conclave.checkin.controller;

import com.conclave.checkin.model.Attendee;
import com.conclave.checkin.repository.AttendeeRepository;
import com.conclave.checkin.service.AttendeeService;
import com.conclave.checkin.service.EmailService;
import com.conclave.checkin.service.PassService;
import com.conclave.checkin.service.QrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class AttendeeController {

    @Autowired
    private EmailService emailService;

    @Autowired
    public AttendeeService attendeeService;

    @Autowired
    private QrService qrService;

    @Autowired
    private PassService passService;

    private final AttendeeRepository repository;

    public AttendeeController(AttendeeRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/attendees")
    public List<Attendee> getAllAttendees() {
        return repository.findAll();
    }

    @GetMapping("/attendees/{uid}")
    public Attendee getAttendeeByUid(@PathVariable UUID uid) {

        return repository.findById(uid)
                .orElseThrow(() ->
                        new RuntimeException("Attendee not found"));
    }

    @PutMapping("/attendees/{uid}/checkin")
    public Attendee checkIn(@PathVariable UUID uid) {

        Attendee attendee = repository.findById(uid)
                .orElseThrow(() ->
                        new RuntimeException("Attendee not found"));

        if (Boolean.TRUE.equals(attendee.getCheckedIn())) {
            throw new RuntimeException("Attendee already checked in");
        }

        attendee.setCheckedIn(true);
        attendee.setCheckinTime(LocalDateTime.now());

        return repository.save(attendee);
    }

    @GetMapping("/attendees/search")
    public List<Attendee> searchAttendees(@RequestParam String q) {

        return repository
                .findByFullNameContainingIgnoreCaseOrEmailIdContainingIgnoreCaseOrMobileNumberContaining(
                        q,
                        q,
                        q
                );
    }

    @GetMapping("/testmail")
    public String testMail() {

        emailService.sendEmail(
                "kanagasamuel@gmail.com",
                "Conclave Test",
                "Email service is working."
        );

        return "Mail Sent";
    }

    @GetMapping("/testqr/{uid}")
    public String testQr(
            @PathVariable String uid
    ) throws Exception {

        qrService.generateQr(uid);

        return "QR Generated";
    }

    @GetMapping("/test-pass/{uid}")
    public String testPass(
            @PathVariable UUID uid
    ) throws Exception {

        Attendee attendee =
                repository.findById(uid)
                        .orElseThrow();

        File qr =
                qrService.generateQr(
                        attendee.getUid().toString()
                );

        File pass =
                passService.generatePass(
                        attendee,
                        qr
                );

        return pass.getAbsolutePath();
    }

    @PostMapping("/attendees/send-pending-passes")
    public String sendPendingPasses() throws Exception {

        attendeeService.sendPendingPasses();

        return "Passes Sent Successfully";
    }

    @PostMapping("/test-mail/{uid}")
    public String testMail(
            @PathVariable UUID uid
    ) throws Exception {

        Attendee attendee =
                repository.findById(uid)
                        .orElseThrow();

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

        return "Mail Sent";
    }

    @GetMapping("/dashboard/stats")
    public Map<String, Long> dashboardStats() {

        Map<String, Long> stats =
                new HashMap<>();

        stats.put(
                "totalRegistered",
                repository.count()
        );

        stats.put(
                "checkedIn",
                repository.countByCheckedInTrue()
        );

        stats.put(
                "pendingCheckIn",
                repository.count() -
                        repository.countByCheckedInTrue()
        );

        stats.put(
                "passesSent",
                repository.countByMailSentTrue()
        );

        stats.put(
                "passesPending",
                repository.count() -
                        repository.countByMailSentTrue()
        );

        return stats;
    }

    @GetMapping("/attendees/checked-in")
    public List<Attendee> checkedIn() {

        return repository.findByCheckedInTrue();
    }

    @GetMapping("/attendees/pending")
    public List<Attendee> pending() {

        return repository.findByCheckedInFalse();
    }
}