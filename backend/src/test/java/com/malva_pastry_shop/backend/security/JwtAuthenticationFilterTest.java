package com.malva_pastry_shop.backend.security;

import com.malva_pastry_shop.backend.domain.publicuser.PublicUser;
import com.malva_pastry_shop.backend.repository.PublicUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationFilter Tests")
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private PublicUserRepository publicUserRepository;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private PublicUser testUser;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        request = new MockHttpServletRequest();
        request.setServletPath("/api/v1/me");
        response = new MockHttpServletResponse();

        testUser = new PublicUser("google-123", "test@gmail.com", "Test User", "https://avatar.url/photo.jpg");
        testUser.setId(1L);
        testUser.setEnabled(true);
    }

    @Nested
    @DisplayName("shouldNotFilter Tests")
    class ShouldNotFilterTests {

        @Test
        @DisplayName("No debe filtrar rutas que no son /api/")
        void shouldNotFilter_NonApiPath_ReturnsTrue() {
            request.setServletPath("/login");

            boolean result = jwtAuthenticationFilter.shouldNotFilter(request);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("No debe filtrar rutas del panel admin")
        void shouldNotFilter_AdminPath_ReturnsTrue() {
            request.setServletPath("/dashboard");

            boolean result = jwtAuthenticationFilter.shouldNotFilter(request);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Debe filtrar rutas /api/**")
        void shouldNotFilter_ApiPath_ReturnsFalse() {
            request.setServletPath("/api/v1/products");

            boolean result = jwtAuthenticationFilter.shouldNotFilter(request);

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("doFilterInternal Tests - Token valido")
    class ValidTokenTests {

        @Test
        @DisplayName("Debe autenticar al usuario cuando el token JWT es valido")
        void doFilter_WithValidToken_SetsAuthentication() throws ServletException, IOException {
            String token = "valid.jwt.token";
            request.addHeader("Authorization", "Bearer " + token);

            when(tokenProvider.validateToken(token)).thenReturn(true);
            when(tokenProvider.getPublicUserIdFromToken(token)).thenReturn(1L);
            when(publicUserRepository.findById(1L)).thenReturn(Optional.of(testUser));

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertThat(auth).isNotNull();
            assertThat(auth.getPrincipal()).isInstanceOf(PublicUserPrincipal.class);

            PublicUserPrincipal principal = (PublicUserPrincipal) auth.getPrincipal();
            assertThat(principal.id()).isEqualTo(1L);
            assertThat(principal.email()).isEqualTo("test@gmail.com");
            assertThat(principal.displayName()).isEqualTo("Test User");

            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Debe asignar la authority ROLE_PUBLIC_USER al autenticar")
        void doFilter_WithValidToken_HasPublicUserRole() throws ServletException, IOException {
            String token = "valid.jwt.token";
            request.addHeader("Authorization", "Bearer " + token);

            when(tokenProvider.validateToken(token)).thenReturn(true);
            when(tokenProvider.getPublicUserIdFromToken(token)).thenReturn(1L);
            when(publicUserRepository.findById(1L)).thenReturn(Optional.of(testUser));

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertThat(auth.getAuthorities())
                    .extracting("authority")
                    .containsExactly("ROLE_PUBLIC_USER");
        }
    }

    @Nested
    @DisplayName("doFilterInternal Tests - Sin token")
    class NoTokenTests {

        @Test
        @DisplayName("No debe autenticar cuando no hay header Authorization")
        void doFilter_WithoutAuthHeader_NoAuthentication() throws ServletException, IOException {
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertThat(auth).isNull();
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("No debe autenticar cuando el header no empieza con Bearer")
        void doFilter_WithNonBearerHeader_NoAuthentication() throws ServletException, IOException {
            request.addHeader("Authorization", "Basic some-credentials");

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertThat(auth).isNull();
            verify(filterChain).doFilter(request, response);
        }
    }

    @Nested
    @DisplayName("doFilterInternal Tests - Token invalido")
    class InvalidTokenTests {

        @Test
        @DisplayName("No debe autenticar cuando el token es invalido")
        void doFilter_WithInvalidToken_NoAuthentication() throws ServletException, IOException {
            String token = "invalid.jwt.token";
            request.addHeader("Authorization", "Bearer " + token);

            when(tokenProvider.validateToken(token)).thenReturn(false);

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertThat(auth).isNull();
            verify(publicUserRepository, never()).findById(anyLong());
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("No debe autenticar cuando el usuario no existe en BD")
        void doFilter_WithValidTokenButUserNotFound_NoAuthentication() throws ServletException, IOException {
            String token = "valid.jwt.token";
            request.addHeader("Authorization", "Bearer " + token);

            when(tokenProvider.validateToken(token)).thenReturn(true);
            when(tokenProvider.getPublicUserIdFromToken(token)).thenReturn(999L);
            when(publicUserRepository.findById(999L)).thenReturn(Optional.empty());

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertThat(auth).isNull();
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("No debe autenticar cuando el usuario esta deshabilitado")
        void doFilter_WithDisabledUser_NoAuthentication() throws ServletException, IOException {
            String token = "valid.jwt.token";
            request.addHeader("Authorization", "Bearer " + token);
            testUser.setEnabled(false);

            when(tokenProvider.validateToken(token)).thenReturn(true);
            when(tokenProvider.getPublicUserIdFromToken(token)).thenReturn(1L);
            when(publicUserRepository.findById(1L)).thenReturn(Optional.of(testUser));

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertThat(auth).isNull();
            verify(filterChain).doFilter(request, response);
        }
    }

    @Nested
    @DisplayName("doFilterInternal Tests - Siempre continua la cadena")
    class FilterChainContinuationTests {

        @Test
        @DisplayName("Debe continuar la cadena de filtros incluso con token valido")
        void doFilter_WithValidToken_ContinuesFilterChain() throws ServletException, IOException {
            String token = "valid.jwt.token";
            request.addHeader("Authorization", "Bearer " + token);

            when(tokenProvider.validateToken(token)).thenReturn(true);
            when(tokenProvider.getPublicUserIdFromToken(token)).thenReturn(1L);
            when(publicUserRepository.findById(1L)).thenReturn(Optional.of(testUser));

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Debe continuar la cadena de filtros con token invalido")
        void doFilter_WithInvalidToken_ContinuesFilterChain() throws ServletException, IOException {
            request.addHeader("Authorization", "Bearer invalid-token");
            when(tokenProvider.validateToken("invalid-token")).thenReturn(false);

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Debe continuar la cadena de filtros sin header")
        void doFilter_WithoutHeader_ContinuesFilterChain() throws ServletException, IOException {
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
        }
    }
}
