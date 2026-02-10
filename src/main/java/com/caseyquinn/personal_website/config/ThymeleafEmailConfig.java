package com.caseyquinn.personal_website.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/**
 * Thymeleaf configuration for email template rendering.
 * Provides a dedicated template engine for processing HTML email templates.
 */
@Configuration
public class ThymeleafEmailConfig {

    /**
     * Creates a template resolver for email templates located in the classpath.
     *
     * @return configured template resolver for email templates
     */
    @Bean
    public ClassLoaderTemplateResolver emailTemplateResolver() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/email/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(true);
        resolver.setOrder(1);
        return resolver;
    }

    /**
     * Creates a dedicated template engine for email rendering.
     *
     * @param emailTemplateResolver the email template resolver
     * @return configured template engine for email processing
     */
    @Bean(name = "emailTemplateEngine")
    public TemplateEngine emailTemplateEngine(ClassLoaderTemplateResolver emailTemplateResolver) {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(emailTemplateResolver);
        return engine;
    }
}
