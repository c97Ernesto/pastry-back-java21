package com.malva_pastry_shop.backend.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class GoogleTokenVerifier {

    private static final Logger log = LoggerFactory.getLogger(GoogleTokenVerifier.class);

    @Value("${app.google.client-id}")
    private String googleClientId;

    private GoogleIdTokenVerifier verifier;

    @PostConstruct
    public void init() {
        verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(googleClientId))
                .build();
    }

    /**
     * Verifica el ID token de Google y extrae el payload.
     * Retorna null si la verificación falla.
     */
    public GoogleIdToken.Payload verify(String idTokenString) {
        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                return idToken.getPayload();
            }
            log.warn("Token de Google inválido o expirado");
        } catch (Exception e) {
            log.error("Error verificando token de Google: {}", e.getMessage());
        }
        return null;
    }
}
