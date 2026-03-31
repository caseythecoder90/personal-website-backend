package com.caseyquinn.personal_website.constants;

/**
 * Constants for email templates, variable keys, and subjects.
 * Used by EmailService for Thymeleaf template rendering via Resend.
 */
public final class EmailConstants {

    private EmailConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // ── Template Names ───────────────────────────────────────────────────

    public static final String TEMPLATE_CONTACT_CONFIRMATION = "contact-confirmation";
    public static final String TEMPLATE_CONTACT_NOTIFICATION = "contact-notification";

    // ── Template Variable Keys ───────────────────────────────────────────

    public static final String VAR_NAME = "name";
    public static final String VAR_EMAIL = "email";
    public static final String VAR_MESSAGE = "message";
    public static final String VAR_SUBJECT = "subject";
    public static final String VAR_INQUIRY_TYPE = "inquiryType";
    public static final String VAR_IP_ADDRESS = "ipAddress";
    public static final String VAR_SUBMITTED_AT = "submittedAt";

    // ── Email Subjects ───────────────────────────────────────────────────

    public static final String SUBJECT_CONTACT_CONFIRMATION = "Thank you for reaching out!";
    public static final String SUBJECT_NEW_CONTACT_PREFIX = "New Contact Submission: ";
}
