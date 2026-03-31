package com.caseyquinn.personal_website.service;

import static com.caseyquinn.personal_website.constants.EmailConstants.*;

import com.caseyquinn.personal_website.entity.ContactSubmission;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Service for sending email notifications via Resend.
 * Uses Thymeleaf templates for HTML email rendering.
 * Emails are sent asynchronously to avoid blocking the main request.
 */
@Service
@Slf4j
public class EmailService {

    private final Resend resend;
    private final TemplateEngine emailTemplateEngine;

    @Value("${resend.from-email}")
    private String fromEmail;

    @Value("${resend.owner-email}")
    private String ownerEmail;

    /**
     * Constructs the EmailService with Resend client and email-specific template engine.
     *
     * @param resend the Resend email client
     * @param emailTemplateEngine the Thymeleaf template engine for email rendering
     */
    public EmailService(Resend resend, @Qualifier("emailTemplateEngine") TemplateEngine emailTemplateEngine) {
        this.resend = resend;
        this.emailTemplateEngine = emailTemplateEngine;
    }

    /**
     * Sends a confirmation email to the visitor who submitted the contact form.
     *
     * @param submission the contact submission
     */
    @Async
    public void sendContactConfirmation(ContactSubmission submission) {
        log.info("Sending contact confirmation email to: {}", submission.getEmail());

        try {
            Context context = new Context();
            context.setVariable(VAR_NAME, submission.getName());
            context.setVariable(VAR_MESSAGE, submission.getMessage());

            String html = emailTemplateEngine.process(TEMPLATE_CONTACT_CONFIRMATION, context);

            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(submission.getEmail())
                    .subject(SUBJECT_CONTACT_CONFIRMATION)
                    .html(html)
                    .build();

            resend.emails().send(params);
            log.info("Contact confirmation email sent successfully to: {}", submission.getEmail());

        } catch (ResendException e) {
            log.error("Failed to send contact confirmation email to: {}", submission.getEmail(), e);
        }
    }

    /**
     * Sends a notification email to the site owner about a new contact submission.
     *
     * @param submission the contact submission
     */
    @Async
    public void sendNewContactNotification(ContactSubmission submission) {
        log.info("Sending new contact notification to owner");

        try {
            Context context = new Context();
            context.setVariable(VAR_NAME, submission.getName());
            context.setVariable(VAR_EMAIL, submission.getEmail());
            context.setVariable(VAR_INQUIRY_TYPE, submission.getInquiryType().name());
            context.setVariable(VAR_SUBJECT, submission.getSubject());
            context.setVariable(VAR_MESSAGE, submission.getMessage());
            context.setVariable(VAR_IP_ADDRESS, submission.getIpAddress());
            context.setVariable(VAR_SUBMITTED_AT, submission.getCreatedAt());

            String html = emailTemplateEngine.process(TEMPLATE_CONTACT_NOTIFICATION, context);

            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(ownerEmail)
                    .subject(SUBJECT_NEW_CONTACT_PREFIX + submission.getInquiryType())
                    .html(html)
                    .build();

            resend.emails().send(params);
            log.info("New contact notification sent successfully to owner");

        } catch (ResendException e) {
            log.error("Failed to send new contact notification to owner", e);
        }
    }
}
