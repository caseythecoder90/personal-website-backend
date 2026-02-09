package com.caseyquinn.personal_website.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for rate limiting.
 * Configurable per-tier rate limits via application.yml.
 */
@Component
@ConfigurationProperties(prefix = "rate-limiting")
@Getter
@Setter
public class RateLimitProperties {

    private boolean enabled = true;
    private Tier publicApi = new Tier(60, 1);
    private Tier login = new Tier(5, 1);
    private Tier adminApi = new Tier(30, 1);

    /**
     * Rate limit tier with configurable request count and duration.
     */
    @Getter
    @Setter
    public static class Tier {
        private int requests;
        private int durationMinutes;

        public Tier() {
        }

        /**
         * Constructs a rate limit tier with the specified limits.
         *
         * @param requests maximum number of requests allowed
         * @param durationMinutes duration window in minutes
         */
        public Tier(int requests, int durationMinutes) {
            this.requests = requests;
            this.durationMinutes = durationMinutes;
        }
    }
}
