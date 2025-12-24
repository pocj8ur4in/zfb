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
    jwtTokenProvider.init();
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
    ReflectionTestUtils.setField(expiredTokenProvider, "accessTokenValidity", -1L);
    expiredTokenProvider.init();

    String token =
        expiredTokenProvider.generateToken("user123", "test@example.com", List.of("ROLE_USER"));

    // when
    boolean isValid = jwtTokenProvider.validateToken(token);

    // then
    assertThat(isValid).isFalse();
  }

  @Test
  @DisplayName("get all claims from token success")
  void getAllClaimsFromToken_Success() {
    // given
    String userId = "user123";
    String email = "test@example.com";
    List<String> roles = Arrays.asList("ROLE_USER", "ROLE_ADMIN");
    String token = jwtTokenProvider.generateToken(userId, email, roles);

    // when
    JwtTokenClaims claims = jwtTokenProvider.getAllClaimsFromToken(token);

    // then
    assertThat(claims).isNotNull();
    assertThat(claims.getUserId()).isEqualTo(userId);
    assertThat(claims.getEmail()).isEqualTo(email);
    assertThat(claims.getRoles()).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
  }

  @Test
  @DisplayName("get all claims from token invalid token returns null")
  void getAllClaimsFromToken_InvalidToken_ReturnsNull() {
    // given
    String invalidToken = "invalid.jwt.token";

    // when
    JwtTokenClaims claims = jwtTokenProvider.getAllClaimsFromToken(invalidToken);

    // then
    assertThat(claims).isNull();
  }

  @Test
  @DisplayName("get all claims from token expired token returns null")
  void getAllClaimsFromToken_ExpiredToken_ReturnsNull() {
    // given
    JwtTokenProvider expiredTokenProvider = new JwtTokenProvider();
    ReflectionTestUtils.setField(
        expiredTokenProvider, "jwtSecret", "test-secret-key-which-must-be-at-least-256-bits-long");
    ReflectionTestUtils.setField(expiredTokenProvider, "accessTokenValidity", -1L);
    expiredTokenProvider.init();

    String token =
        expiredTokenProvider.generateToken("user123", "test@example.com", List.of("ROLE_USER"));

    // when
    JwtTokenClaims claims = jwtTokenProvider.getAllClaimsFromToken(token);

    // then
    assertThat(claims).isNull();
  }
}
