package com.caseyquinn.personal_website.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Jasypt encryption used in application properties.
 */
@Configuration
public class JasyptConfig {

    @Value("${jasypt.encryptor.password:default-dev-password}")
    private String encryptorPassword;

    /**
     * Creates the Jasypt string encryptor bean.
     *
     * @return configured StringEncryptor
     */
    @Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setPoolSize(1);
        encryptor.setPassword(encryptorPassword);
        encryptor.setAlgorithm("PBEWithHMACSHA512AndAES_256");
        encryptor.setIvGenerator(new RandomIvGenerator());
        return encryptor;
    }
}
