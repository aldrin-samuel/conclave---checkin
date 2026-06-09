package com.conclave.checkin.repository;

import com.conclave.checkin.model.Attendee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AttendeeRepository extends JpaRepository<Attendee, UUID> {

    List<Attendee> findByFullNameContainingIgnoreCase(String fullName);

    List<Attendee> findByEmailIdContainingIgnoreCase(String emailId);

    List<Attendee> findByMobileNumberContaining(String mobileNumber);

    List<Attendee> findByMailSentFalse();

    List<Attendee> findByFullNameContainingIgnoreCaseOrEmailIdContainingIgnoreCaseOrMobileNumberContaining(
            String fullName,
            String emailId,
            String mobileNumber
    );

    Long countByCheckedInTrue();

    Long countByMailSentTrue();

    List<Attendee> findByCheckedInTrue();

    List<Attendee> findByCheckedInFalse();
}