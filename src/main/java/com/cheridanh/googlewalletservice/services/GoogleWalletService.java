package com.cheridanh.googlewalletservice.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cheridanh.googlewalletservice.config.GoogleWalletConfig;
import com.cheridanh.googlewalletservice.dtos.StudentCardRequestDto;
import com.google.auth.oauth2.ServiceAccountCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.interfaces.RSAPrivateKey;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GoogleWalletService {

    private final GoogleWalletConfig config;
    private final ServiceAccountCredentials credentials;

    // URL de hero image
    //private static final String HERO_IMAGE_URL = "https://i.ibb.co/qYyrNCZ7/Logo-tech-school-black.png";
    private static final String HERO_IMAGE_URL = "https://i.ibb.co/tpGvG6Pm/logo-tech-school-1.jpg";

    // URL logo Tech School
    private static final String LOGO_URL = "https://storage.googleapis.com/wallet-lab-tools-codelab-artifacts-public/pass_google_logo.jpg";

    // Email Tech School
    private static final String TECH_SCHOOL_EMAIL = "rp-info@iticparis.com";

    // Site web Tech School
    private static final String TECH_SCHOOL_WEB_SITE = "https://www.iticparis.com/tech-school";

    public GoogleWalletService(GoogleWalletConfig config) throws IOException {
        this.config = config;
        this.credentials = (ServiceAccountCredentials) config.googleCredentials();
    }

    // Objet générique perso
    public String createOrUpdateGenericClass() throws IOException {
        String classSuffix = "student_card_class";
        String classId = String.format("%s.%s", config.getIssuerId(), classSuffix);

        Map<String, Object> classMap = new HashMap<>();
        classMap.put("id", classId);
        classMap.put("multipleDevicesAndHoldersAllowedStatus", "MULTIPLE_HOLDERS");

        // Card title de la classe
        Map<String, Object> cardTitle = new HashMap<>();
        Map<String, Object> defaultValue = new HashMap<>();
        defaultValue.put("language", "fr-FR");
        defaultValue.put("value", "CARTE D'ÉTUDIANT");
        cardTitle.put("defaultValue", defaultValue);
        classMap.put("cardTitle", cardTitle);

        // Template 2 lignes
        Map<String, Object> classTemplateInfo = new HashMap<>();
        Map<String, Object> cardTemplateOverride = new HashMap<>();
        List<Map<String, Object>> cardRowTemplateInfos = new ArrayList<>();

        // Ligne 1 : Niveau + Année académique
        Map<String, Object> row1 = new HashMap<>();
        Map<String, Object> twoItems1 = new HashMap<>();

        Map<String, Object> startItem1 = new HashMap<>();
        Map<String, Object> startFirstValue1 = new HashMap<>();
        List<Map<String, String>> startFields1 = new ArrayList<>();
        Map<String, String> startField1 = new HashMap<>();
        startField1.put("fieldPath", "object.textModulesData['niveau']");
        startFields1.add(startField1);
        startFirstValue1.put("fields", startFields1);
        startItem1.put("firstValue", startFirstValue1);
        twoItems1.put("startItem", startItem1);

        Map<String, Object> endItem1 = new HashMap<>();
        Map<String, Object> endFirstValue1 = new HashMap<>();
        List<Map<String, String>> endFields1 = new ArrayList<>();
        Map<String, String> endField1 = new HashMap<>();
        endField1.put("fieldPath", "object.textModulesData['annee_academique']");
        endFields1.add(endField1);
        endFirstValue1.put("fields", endFields1);
        endItem1.put("firstValue", endFirstValue1);
        twoItems1.put("endItem", endItem1);

        row1.put("twoItems", twoItems1);
        cardRowTemplateInfos.add(row1);

        // Ligne 2 : Formation + N° Étudiant
        Map<String, Object> row2 = new HashMap<>();
        Map<String, Object> twoItems2 = new HashMap<>();

        Map<String, Object> startItem2 = new HashMap<>();
        Map<String, Object> startFirstValue2 = new HashMap<>();
        List<Map<String, String>> startFields2 = new ArrayList<>();
        Map<String, String> startField2 = new HashMap<>();
        startField2.put("fieldPath", "object.textModulesData['formation']");
        startFields2.add(startField2);
        startFirstValue2.put("fields", startFields2);
        startItem2.put("firstValue", startFirstValue2);
        twoItems2.put("startItem", startItem2);

        Map<String, Object> endItem2 = new HashMap<>();
        Map<String, Object> endFirstValue2 = new HashMap<>();
        List<Map<String, String>> endFields2 = new ArrayList<>();
        Map<String, String> endField2 = new HashMap<>();
        endField2.put("fieldPath", "object.textModulesData['student_id']");
        endFields2.add(endField2);
        endFirstValue2.put("fields", endFields2);
        endItem2.put("firstValue", endFirstValue2);
        twoItems2.put("endItem", endItem2);

        row2.put("twoItems", twoItems2);
        cardRowTemplateInfos.add(row2);

        cardTemplateOverride.put("cardRowTemplateInfos", cardRowTemplateInfos);
        classTemplateInfo.put("cardTemplateOverride", cardTemplateOverride);
        classMap.put("classTemplateInfo", classTemplateInfo);

        // TextModules pour la classe
        List<Map<String, Object>> textModules = new ArrayList<>();
        Map<String, Object> universityModule = new HashMap<>();
        universityModule.put("id", "university_info");
        universityModule.put("header", "ÉCOLE");
        universityModule.put("body", "Tech School - ITIC Paris");
        textModules.add(universityModule);
        classMap.put("textModulesData", textModules);

        // Liens
        Map<String, Object> linksModule = new HashMap<>();
        List<Map<String, Object>> uris = new ArrayList<>();
        Map<String, Object> uri = new HashMap<>();
        uri.put("id", "university_website");
        uri.put("uri", TECH_SCHOOL_WEB_SITE);
        uri.put("description", "Site web de la Tech School");
        uris.add(uri);
        linksModule.put("uris", uris);
        classMap.put("linksModuleData", linksModule);

        try {
            String response = makeRestApiCall(
                    "POST",
                    "https://walletobjects.googleapis.com/walletobjects/v1/genericClass",
                    classMap
            );
            log.info("Class created : {}", classId);
            return classId;
        } catch (Exception ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("409")) {
                log.info("Class already exists, updating : {}", classId);
                String response = makeRestApiCall(
                        "PUT",
                        "https://walletobjects.googleapis.com/walletobjects/v1/genericClass/" + classId,
                        classMap
                );
                log.info("Class updated : {}", classId);
                return classId;
            } else {
                log.error("Error while creating class : {}", classId);
                log.error("Error : {}", ex.getMessage(), ex);
                throw new IOException("Error while creating class : " + ex.getMessage(), ex);
            }
        }
    }

    // Carte
    public String createStudentCard(StudentCardRequestDto request) throws IOException {
        String classSuffix = "student_card_class";
        String objectSuffix = request.getStudentId().toLowerCase().replaceAll("[^a-z0-9]", "_");
        String classId = String.format("%s.%s", config.getIssuerId(), classSuffix);
        String objectId = String.format("%s.%s", config.getIssuerId(), objectSuffix);

        try {
            makeRestApiCall("GET",
                    "https://walletobjects.googleapis.com/walletobjects/v1/genericClass/" + classId,
                    null);
        } catch (Exception ex) {
            log.info("Class not found, creating new class : {}", classId);
            createOrUpdateGenericClass();
        }

        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("id", objectId);
        objectMap.put("classId", classId);
        objectMap.put("state", "ACTIVE");

        // Logo en haut à gauche
        Map<String, Object> logo = new HashMap<>();
        Map<String, Object> logoSourceUri = new HashMap<>();
        logoSourceUri.put("uri", LOGO_URL);
        logo.put("sourceUri", logoSourceUri);

        Map<String, Object> logoContentDescription = new HashMap<>();
        Map<String, Object> logoDescValue = new HashMap<>();
        logoDescValue.put("language", "fr-FR");
        logoDescValue.put("value", "Logo Tech School");
        logoContentDescription.put("defaultValue", logoDescValue);
        logo.put("contentDescription", logoContentDescription);
        objectMap.put("logo", logo);

        // Card title
        Map<String, Object> objectCardTitle = new HashMap<>();
        Map<String, Object> objectCardTitleValue = new HashMap<>();
        objectCardTitleValue.put("language", "fr-FR");
        objectCardTitleValue.put("value", "CARTE D'ÉTUDIANT");
        objectCardTitle.put("defaultValue", objectCardTitleValue);
        objectMap.put("cardTitle", objectCardTitle);

        // Header : Nom complet de l'étudiant
        Map<String, Object> header = new HashMap<>();
        Map<String, Object> headerValue = new HashMap<>();
        headerValue.put("language", "fr-FR");
        headerValue.put("value", request.getFirstName() + " " + request.getLastName());
        header.put("defaultValue", headerValue);
        objectMap.put("header", header);

        // Subheader : Email de l'étudiant
        Map<String, Object> subheader = new HashMap<>();
        Map<String, Object> subheaderValue = new HashMap<>();
        subheaderValue.put("language", "fr-FR");
        subheaderValue.put("value", request.getEmail());
        subheader.put("defaultValue", subheaderValue);
        objectMap.put("subheader", subheader);

        // Bg
        objectMap.put("hexBackgroundColor", "#232d62");

        // TextModules pour le template à 2 lignes
        List<Map<String, Object>> textModules = new ArrayList<>();

        // Module "niveau" : Colonne gauche la ligne 1
        Map<String, Object> niveauModule = new HashMap<>();
        niveauModule.put("id", "niveau");
        niveauModule.put("header", "NIVEAU");
        niveauModule.put("body", request.getLevel());
        textModules.add(niveauModule);

        // Module "annee_academique" : Colonne droite la ligne 1
        Map<String, Object> anneeModule = new HashMap<>();
        anneeModule.put("id", "annee_academique");
        anneeModule.put("header", "ANNÉE");
        anneeModule.put("body", determineAcademicYear(request.getExpirationDate()));
        textModules.add(anneeModule);

        // Module "formation" : Colonne gauche la ligne 2
        Map<String, Object> formationModule = new HashMap<>();
        formationModule.put("id", "formation");
        formationModule.put("header", "FORMATION");
        formationModule.put("body", request.getFormation());
        textModules.add(formationModule);

        // Module "student_id" : Colonne droite la ligne 2
        Map<String, Object> studentIdModule = new HashMap<>();
        studentIdModule.put("id", "student_id");
        studentIdModule.put("header", "N° ÉTUDIANT");
        studentIdModule.put("body", request.getStudentId());
        textModules.add(studentIdModule);

        // Module date d'expiration
        if (request.getExpirationDate() != null) {
            Map<String, Object> expirationModule = new HashMap<>();
            expirationModule.put("id", "expiration");
            expirationModule.put("header", "VALIDE JUSQU'AU");
            expirationModule.put("body", request.getExpirationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            textModules.add(expirationModule);
        }

        objectMap.put("textModulesData", textModules);

        String heroImageUrl = HERO_IMAGE_URL;

        // Image module : Logo de l'école avec description
        List<Map<String, Object>> imageModules = new ArrayList<>();

        Map<String, Object> schoolImageModule = new HashMap<>();
        schoolImageModule.put("id", "school_logo");

        // Image principale
        Map<String, Object> mainImage = new HashMap<>();
        Map<String, Object> mainImageSourceUri = new HashMap<>();
        mainImageSourceUri.put("uri", HERO_IMAGE_URL);
        mainImage.put("sourceUri", mainImageSourceUri);

        Map<String, Object> mainImageDescription = new HashMap<>();
        Map<String, Object> mainImageDescValue = new HashMap<>();
        mainImageDescValue.put("language", "fr-FR");
        mainImageDescValue.put("value", "Logo Tech School - ITIC Paris");
        mainImageDescription.put("defaultValue", mainImageDescValue);
        mainImage.put("contentDescription", mainImageDescription);

        schoolImageModule.put("mainImage", mainImage);
        imageModules.add(schoolImageModule);

        objectMap.put("imageModulesData", imageModules);

        // Barcode QR Code avec le numéro étudiant
        Map<String, Object> barcode = new HashMap<>();
        barcode.put("type", "QR_CODE");
        barcode.put("value", "https://www.cheridanh.cg");
        barcode.put("alternateText", "");
        objectMap.put("barcode", barcode);

        // Hero image
        Map<String, Object> heroImage = new HashMap<>();
        Map<String, Object> heroSourceUri = new HashMap<>();

        heroSourceUri.put("uri", heroImageUrl);
        heroImage.put("sourceUri", heroSourceUri);

        Map<String, Object> heroContentDescription = new HashMap<>();
        Map<String, Object> heroDescValue = new HashMap<>();
        heroDescValue.put("language", "fr-FR");
        heroDescValue.put("value", "Bannière Tech School");
        heroContentDescription.put("defaultValue", heroDescValue);
        heroImage.put("contentDescription", heroContentDescription);

        objectMap.put("heroImage", heroImage);

        // Liens
        Map<String, Object> linksModule = new HashMap<>();
        List<Map<String, Object>> uris = new ArrayList<>();

        Map<String, Object> contactUri = new HashMap<>();
        contactUri.put("id", "contact");
        contactUri.put("uri", "mailto:" + TECH_SCHOOL_EMAIL);
        contactUri.put("description", "Contactez la Tech School");
        uris.add(contactUri);

        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            Map<String, Object> emailUri = new HashMap<>();
            emailUri.put("id", "email");
            emailUri.put("uri", "mailto:" + request.getEmail());
            emailUri.put("description", "Envoyer un email");
            uris.add(emailUri);
        }

        linksModule.put("uris", uris);
        objectMap.put("linksModuleData", linksModule);

        try {
            String response = makeRestApiCall(
                    "POST",
                    "https://walletobjects.googleapis.com/walletobjects/v1/genericObject",
                    objectMap
            );
            log.info("Student card created : {}", objectId);
        } catch (Exception ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("409")) {
                log.info("Student card already exists, updating");
                makeRestApiCall(
                        "PUT",
                        "https://walletobjects.googleapis.com/walletobjects/v1/genericObject/" + objectId,
                        objectMap
                );
                log.info("Student card updated : {}", objectId);
            } else {
                log.error("Error while creating student card : {}", ex.getMessage());
                throw new IOException("Error while student creating card : " + ex.getMessage(), ex);
            }
        }

        return generateJWT(objectId);
    }

    // Année
    private String determineAcademicYear(LocalDate expirationDate) {
        if (expirationDate == null) {
            return LocalDate.now().getYear() + " - " + (LocalDate.now().getYear() + 1);
        }
        int year = expirationDate.getYear();
        return (year - 1) + " - " + year;
    }

    // Appel à l'API REST Google Wallet
    private String makeRestApiCall(String method, String url, Map<String, Object> body) throws IOException {
        try {
            credentials.refreshIfExpired();
            String accessToken = credentials.getAccessToken().getTokenValue();

            URL apiUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);
            connection.setRequestProperty("Content-Type", "application/json");

            if (body != null && !method.equals("GET")) {
                connection.setDoOutput(true);
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = new com.google.gson.Gson().toJson(body).getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
            }

            int responseCode = connection.getResponseCode();

            InputStream inputStream;
            if (responseCode >= 200 && responseCode < 300) {
                inputStream = connection.getInputStream();
            } else {
                inputStream = connection.getErrorStream();
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "utf-8"))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }

            if (responseCode >= 400) {
                log.error("API HHTP error : {}, {}", responseCode, response);
                throw new IOException("HTTP error " + responseCode + ": " + response);
            }

            return response.toString();
        } catch (Exception e) {
            log.error("Error while calling API : {}", e.getMessage(), e);
            throw new IOException("Error while calling API : " + e.getMessage(), e);
        }
    }

    // JWT
    private String generateJWT(String objectId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("iss", credentials.getClientEmail());
        claims.put("aud", "google");
        claims.put("origins", List.of("localhost:8080"));
        claims.put("typ", "savetowallet");

        Map<String, Object> payload = new HashMap<>();
        List<Map<String, String>> genericObjects = new ArrayList<>();
        Map<String, String> objectRef = new HashMap<>();
        objectRef.put("id", objectId);
        genericObjects.add(objectRef);
        payload.put("genericObjects", genericObjects);

        claims.put("payload", payload);

        Algorithm algorithm = Algorithm.RSA256(null, (RSAPrivateKey) credentials.getPrivateKey());
        String token = JWT.create().withPayload(claims).sign(algorithm);

        String saveUrl = String.format("https://pay.google.com/gp/v/save/%s", token);
        log.info("JWT generated : {}", token);
        log.info("JWT size : {}", token.length());
        log.info("URL : {}", saveUrl);

        return saveUrl;
    }
}
