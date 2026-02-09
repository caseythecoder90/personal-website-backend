package com.caseyquinn.personal_website.repository;

import com.caseyquinn.personal_website.entity.ContactSubmission;
import com.caseyquinn.personal_website.entity.enums.InquiryType;
import com.caseyquinn.personal_website.entity.enums.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for ContactSubmission entities.
 */
@Repository
public interface ContactSubmissionRepository extends JpaRepository<ContactSubmission, Long> {

    List<ContactSubmission> findByStatusOrderByCreatedAtDesc(SubmissionStatus status);

    List<ContactSubmission> findByInquiryTypeOrderByCreatedAtDesc(InquiryType inquiryType);

    List<ContactSubmission> findByEmailOrderByCreatedAtDesc(String email);

    List<ContactSubmission> findAllByOrderByCreatedAtDesc();
}
