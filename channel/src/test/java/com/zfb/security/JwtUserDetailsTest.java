package com.zfb.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

class JwtUserDetailsTest {

  @Test
  @DisplayName("create jwt user details success")
  void createJwtUserDetails_Success() {
    // given
    String userId = "user123";
    String email = "test@example.com";
    List<String> roles = Arrays.asList("ROLE_USER", "ROLE_ADMIN");

    // when
    JwtUserDetails userDetails = new JwtUserDetails(userId, email, roles);

    // then
    assertThat(userDetails.getUserId()).isEqualTo(userId);
    assertThat(userDetails.getEmail()).isEqualTo(email);
    assertThat(userDetails.getRoles()).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    assertThat(userDetails.getUsername()).isEqualTo(userId);
  }

  @Test
  @DisplayName("get authorities success")
  void getAuthorities_Success() {
    // given
    String userId = "user123";
    String email = "test@example.com";
    List<String> roles = Arrays.asList("ROLE_USER", "ROLE_ADMIN");
    JwtUserDetails userDetails = new JwtUserDetails(userId, email, roles);

    // when
    var authorities = userDetails.getAuthorities();

    // then
    assertThat(authorities)
        .hasSize(2)
        .extracting(GrantedAuthority::getAuthority)
        .containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
  }

  @Test
  @DisplayName("account status all enabled")
  void accountStatus_AllEnabled() {
    // given
    JwtUserDetails userDetails =
        new JwtUserDetails("user123", "test@example.com", List.of("ROLE_USER"));

    // then
    assertThat(userDetails.isAccountNonExpired()).isTrue();
    assertThat(userDetails.isAccountNonLocked()).isTrue();
    assertThat(userDetails.isCredentialsNonExpired()).isTrue();
    assertThat(userDetails.isEnabled()).isTrue();
  }

  @Test
  @DisplayName("get password returns null")
  void getPassword_ReturnsNull() {
    // given
    JwtUserDetails userDetails =
        new JwtUserDetails("user123", "test@example.com", List.of("ROLE_USER"));

    // when
    String password = userDetails.getPassword();

    // then
    assertThat(password).isNull();
  }
}
