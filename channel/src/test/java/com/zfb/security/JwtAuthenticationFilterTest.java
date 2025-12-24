package com.zfb.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

  @Mock private JwtTokenProvider tokenProvider;

  @InjectMocks private JwtAuthenticationFilter jwtAuthenticationFilter;

  @BeforeEach
  void setUp() {
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("do filter internal valid token sets authentication")
  void doFilterInternal_ValidToken_SetsAuthentication() throws Exception {
    // given
    String token = "valid.jwt.token";
    String userId = "user123";
    String email = "test@example.com";
    List<String> roles = Arrays.asList("ROLE_USER");

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    FilterChain filterChain = mock(FilterChain.class);

    JwtTokenClaims claims = new JwtTokenClaims(userId, email, roles);

    when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
    when(tokenProvider.getAllClaimsFromToken(token)).thenReturn(claims);

    // when
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // then
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    assertThat(authentication).isNotNull();
    assertThat(authentication.getPrincipal()).isInstanceOf(JwtUserDetails.class);

    JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
    assertThat(userDetails.getUserId()).isEqualTo(userId);
    assertThat(userDetails.getEmail()).isEqualTo(email);
    assertThat(userDetails.getRoles()).containsExactly("ROLE_USER");
  }

  @Test
  @DisplayName("do filter internal no token no authentication")
  void doFilterInternal_NoToken_NoAuthentication() throws Exception {
    // given
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    FilterChain filterChain = mock(FilterChain.class);

    when(request.getHeader("Authorization")).thenReturn(null);

    // when
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // then
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    assertThat(authentication).isNull();
  }

  @Test
  @DisplayName("do filter internal invalid token no authentication")
  void doFilterInternal_InvalidToken_NoAuthentication() throws Exception {
    // given
    String token = "invalid.jwt.token";

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    FilterChain filterChain = mock(FilterChain.class);

    when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
    when(tokenProvider.getAllClaimsFromToken(token)).thenReturn(null);

    // when
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // then
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    assertThat(authentication).isNull();
  }

  @Test
  @DisplayName("do filter internal no bearer prefix no authentication")
  void doFilterInternal_NoBearerPrefix_NoAuthentication() throws Exception {
    // given
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    FilterChain filterChain = mock(FilterChain.class);

    when(request.getHeader("Authorization")).thenReturn("invalid.jwt.token");

    // when
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // then
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    assertThat(authentication).isNull();
  }
}
