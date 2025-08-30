package com.caseyquinn.personal_website.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    
    @Value("${app.version:1.0.0}")
    private String version;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Personal Website API")
                        .version(version)
                        .description("REST API for Casey Quinn's personal website")
                        .contact(new Contact()
                                .name("Casey Quinn")
                                .email("caseythecoder90@gmail.com")
                                .url("https://caseyquinn.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}