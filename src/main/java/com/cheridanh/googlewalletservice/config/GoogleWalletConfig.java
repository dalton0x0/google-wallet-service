package com.cheridanh.googlewalletservice.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.walletobjects.Walletobjects;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Configuration
public class GoogleWalletConfig {

    @Value("${google.wallet.key.file}")
    private Resource keyFile;

    @Getter
    @Value("${google.wallet.issuer.id}")
    private String issuerId;

    @Value("${google.wallet.application.name:TechSchool Student Card}")
    private String applicationName;

    @Bean
    public GoogleCredentials googleCredentials() throws IOException {
        return GoogleCredentials.fromStream(keyFile.getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/wallet_object.issuer"));
    }

    @Bean
    public Walletobjects walletObjectsService(GoogleCredentials credentials)
            throws GeneralSecurityException, IOException {
        return new Walletobjects.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName(applicationName)
                .build();
    }
}
