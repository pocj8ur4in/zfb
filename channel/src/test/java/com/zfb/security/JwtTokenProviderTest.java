package com.zfb.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtTokenProviderTest {

  private JwtTokenProvider jwtTokenProvider;

  @BeforeEach
  void setUp() {
    jwtTokenProvider = new JwtTokenProvider();
    ReflectionTestUtils.setField(
        jwtTokenProvider, "jwtSecret", "test-secret-key-which-must-be-at-least-256-bits-long");
    ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenValidity", 3600000L);
  }

  @Test
  @DisplayName("generate token success")
  void generateToken_Success() {
    // given
    String userId = "user123";
    String email = "test@example.com";
    List<String> roles = Arrays.asList("ROLE_USER", "ROLE_ADMIN");

    // when
    String token = jwtTokenProvider.generateToken(userId, email, roles);

    // then
    assertThat(token).isNotNull().isNotEmpty();
  }

  @Test
  @DisplayName("get user id from token success")
  void getUserIdFromToken_Success() {
    // given
    String userId = "user123";
    String email = "test@example.com";
    List<String> roles = Arrays.asList("ROLE_USER");
    String token = jwtTokenProvider.generateToken(userId, email, roles);

    // when
    String extractedUserId = jwtTokenProvider.getUserIdFromToken(token);

    // then
    assertThat(extractedUserId).isEqualTo(userId);
  }

  @Test
  @DisplayName("get email from token success")
  void getEmailFromToken_Success() {
    // given
    String userId = "user123";
    String email = "test@example.com";
    List<String> roles = Arrays.asList("ROLE_USER");
    String token = jwtTokenProvider.generateToken(userId, email, roles);

    // when
    String extractedEmail = jwtTokenProvider.getEmailFromToken(token);

    // then
    assertThat(extractedEmail).isEqualTo(email);
  }

  @Test
  @DisplayName("get roles from token success")
  void getRolesFromToken_Success() {
    // given
    String userId = "user123";
    String email = "test@example.com";
    List<String> roles = Arrays.asList("ROLE_USER", "ROLE_ADMIN");
    String token = jwtTokenProvider.generateToken(userId, email, roles);

    // when
    List<String> extractedRoles = jwtTokenProvider.getRolesFromToken(token);

    // then
    assertThat(extractedRoles).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
  }

  @Test
  @DisplayName("validate token success")
  void validateToken_ValidToken_ReturnsTrue() {
    // given
    String userId = "user123";
    String email = "test@example.com";
    List<String> roles = Arrays.asList("ROLE_USER");
    String token = jwtTokenProvider.generateToken(userId, email, roles);

    // when
    boolean isValid = jwtTokenProvider.validateToken(token);

    // then
    assertThat(isValid).isTrue();
  }

  @Test
  @DisplayName("validate token invalid token returns false")
  void validateToken_InvalidToken_ReturnsFalse() {
    // given
    String invalidToken = "invalid.jwt.token";

    // when
    boolean isValid = jwtTokenProvider.validateToken(invalidToken);

    // then
    assertThat(isValid).isFalse();
  }

  @Test
  @DisplayName("validate token expired token returns false")
  void validateToken_ExpiredToken_ReturnsFalse() {
    // given
    JwtTokenProvider expiredTokenProvider = new JwtTokenProvider();
    ReflectionTestUtils.setField(
        expiredTokenProvider, "jwtSecret", "test-secret-key-which-must-be-at-least-256-bits-long");
    ReflectionTestUtils.setField(expiredTokenProvider, "accessTokenValidity", -1L); // 즉시 만료

    String token =
        expiredTokenProvider.generateToken("user123", "test@example.com", List.of("ROLE_USER"));

    // when
    boolean isValid = jwtTokenProvider.validateToken(token);

    // then
    assertThat(isValid).isFalse();
  }
}
