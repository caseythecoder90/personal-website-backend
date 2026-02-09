package com.caseyquinn.personal_website.config;

import com.resend.Resend;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the Resend email client.
 */
@Configuration
public class ResendConfig {

    @Value("${resend.api-key}")
    private String apiKey;

    /**
     * Creates a Resend client bean for sending emails.
     *
     * @return configured Resend client
     */
    @Bean
    public Resend resend() {
        return new Resend(apiKey);
    }
}
