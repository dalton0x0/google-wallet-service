package com.cheridanh.googlewalletservice.config;


import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@Validated
@Slf4j
public class AppConfigValidation {

    @Value("${google.wallet.issuer.id}")
    @NotBlank(message = "Google Wallet Issuer ID must be configured")
    private String issuerId;

    @Value("${google.wallet.key.file}")
    @NotBlank(message = "Google Wallet key file must be configured")
    private String keyFile;

    @PostConstruct
    public void validateConfig() {
        log.info("Configuration validated successfully");
        log.info("Issuer ID: {}", issuerId);
        log.info("Key File: {}", keyFile);
    }
}
