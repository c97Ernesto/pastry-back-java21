package com.malva_pastry_shop.backend.security;

import com.malva_pastry_shop.backend.domain.publicuser.PublicUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JwtTokenProvider Tests")
class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;
    private PublicUser testUser;

    // Base64-encoded 64-byte key para HS512
    private static final String TEST_SECRET = Base64.getEncoder().encodeToString(
            "this-is-a-very-long-secret-key-for-HS512-that-needs-to-be-at-least-64-bytes!".getBytes());
    private static final long TEST_EXPIRATION_MS = 86400000L; // 24 horas

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(tokenProvider, "jwtSecret", TEST_SECRET);
        ReflectionTestUtils.setField(tokenProvider, "jwtExpirationMs", TEST_EXPIRATION_MS);
        tokenProvider.init();

        testUser = new PublicUser("google-123", "test@gmail.com", "Test User", "https://avatar.url/photo.jpg");
        testUser.setId(1L);
    }

    @Nested
    @DisplayName("generateToken Tests")
    class GenerateTokenTests {

        @Test
        @DisplayName("Debe generar un token JWT no nulo y no vacio")
        void generateToken_ReturnsNonEmptyToken() {
            String token = tokenProvider.generateToken(testUser);

            assertThat(token).isNotNull().isNotBlank();
        }

        @Test
        @DisplayName("Debe generar un token con 3 partes separadas por punto")
        void generateToken_ReturnsTokenWithThreeParts() {
            String token = tokenProvider.generateToken(testUser);
            String[] parts = token.split("\\.");

            assertThat(parts).hasSize(3);
        }

        @Test
        @DisplayName("Debe generar tokens diferentes para distintos usuarios")
        void generateToken_GeneratesDifferentTokensForDifferentUsers() {
            PublicUser otherUser = new PublicUser("google-456", "other@gmail.com", "Other User", null);
            otherUser.setId(2L);

            String token1 = tokenProvider.generateToken(testUser);
            String token2 = tokenProvider.generateToken(otherUser);

            assertThat(token1).isNotEqualTo(token2);
        }
    }

    @Nested
    @DisplayName("getPublicUserIdFromToken Tests")
    class GetPublicUserIdTests {

        @Test
        @DisplayName("Debe extraer el ID del usuario del token")
        void getPublicUserIdFromToken_ReturnsCorrectId() {
            String token = tokenProvider.generateToken(testUser);

            Long extractedId = tokenProvider.getPublicUserIdFromToken(token);

            assertThat(extractedId).isEqualTo(1L);
        }

        @Test
        @DisplayName("Debe extraer IDs distintos para usuarios distintos")
        void getPublicUserIdFromToken_ReturnsDifferentIdsForDifferentUsers() {
            PublicUser otherUser = new PublicUser("google-456", "other@gmail.com", "Other User", null);
            otherUser.setId(99L);

            String token1 = tokenProvider.generateToken(testUser);
            String token2 = tokenProvider.generateToken(otherUser);

            assertThat(tokenProvider.getPublicUserIdFromToken(token1)).isEqualTo(1L);
            assertThat(tokenProvider.getPublicUserIdFromToken(token2)).isEqualTo(99L);
        }
    }

    @Nested
    @DisplayName("validateToken Tests")
    class ValidateTokenTests {

        @Test
        @DisplayName("Debe retornar true para un token valido")
        void validateToken_WithValidToken_ReturnsTrue() {
            String token = tokenProvider.generateToken(testUser);

            boolean isValid = tokenProvider.validateToken(token);

            assertThat(isValid).isTrue();
        }

        @Test
        @DisplayName("Debe retornar false para un token nulo")
        void validateToken_WithNullToken_ReturnsFalse() {
            boolean isValid = tokenProvider.validateToken(null);

            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("Debe retornar false para un token vacio")
        void validateToken_WithEmptyToken_ReturnsFalse() {
            boolean isValid = tokenProvider.validateToken("");

            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("Debe retornar false para un token con formato invalido")
        void validateToken_WithMalformedToken_ReturnsFalse() {
            boolean isValid = tokenProvider.validateToken("not.a.valid.jwt.token");

            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("Debe retornar false para un token manipulado")
        void validateToken_WithTamperedToken_ReturnsFalse() {
            String token = tokenProvider.generateToken(testUser);
            // Alterar el payload del token
            String tamperedToken = token.substring(0, token.lastIndexOf('.')) + ".tampered-signature";

            boolean isValid = tokenProvider.validateToken(tamperedToken);

            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("Debe retornar false para un token firmado con otra clave")
        void validateToken_WithDifferentSecret_ReturnsFalse() {
            String token = tokenProvider.generateToken(testUser);

            // Crear otro provider con clave distinta
            JwtTokenProvider otherProvider = new JwtTokenProvider();
            String otherSecret = Base64.getEncoder().encodeToString(
                    "another-completely-different-secret-key-that-is-also-at-least-64-bytes-long!!".getBytes());
            ReflectionTestUtils.setField(otherProvider, "jwtSecret", otherSecret);
            ReflectionTestUtils.setField(otherProvider, "jwtExpirationMs", TEST_EXPIRATION_MS);
            otherProvider.init();

            boolean isValid = otherProvider.validateToken(token);

            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("Debe retornar false para un token expirado")
        void validateToken_WithExpiredToken_ReturnsFalse() {
            // Crear provider con expiracion de 0ms (token expira inmediatamente)
            JwtTokenProvider expiredProvider = new JwtTokenProvider();
            ReflectionTestUtils.setField(expiredProvider, "jwtSecret", TEST_SECRET);
            ReflectionTestUtils.setField(expiredProvider, "jwtExpirationMs", 0L);
            expiredProvider.init();

            String token = expiredProvider.generateToken(testUser);

            boolean isValid = expiredProvider.validateToken(token);

            assertThat(isValid).isFalse();
        }
    }

    @Nested
    @DisplayName("Flujo completo: generate -> validate -> extract")
    class FullFlowTests {

        @Test
        @DisplayName("Debe generar un token, validarlo y extraer el ID correctamente")
        void fullFlow_GenerateValidateExtract_WorksCorrectly() {
            // 1. Generar
            String token = tokenProvider.generateToken(testUser);
            assertThat(token).isNotBlank();

            // 2. Validar
            assertThat(tokenProvider.validateToken(token)).isTrue();

            // 3. Extraer ID
            Long extractedId = tokenProvider.getPublicUserIdFromToken(token);
            assertThat(extractedId).isEqualTo(testUser.getId());
        }
    }
}
