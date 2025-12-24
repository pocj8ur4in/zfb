package com.zfb.security;

public class SecurityConstants {

  public static final String[] PUBLIC_PATHS = {
    "/actuator/**",
    "/swagger-ui/**",
    "/swagger-ui.html",
    "/v3/api-docs/**",
    "/api-docs/**",
    "/webjars/**",
    "/error"
  };

  public static final String[] AUTH_PUBLIC_PATHS = {"/api/auth/**", "/api/public/**"};

  public static final String TOKEN_PREFIX = "Bearer ";
  public static final String HEADER_STRING = "Authorization";

  public static final String HEADER_USER_ID = "X-User-Id";
  public static final String HEADER_USER_EMAIL = "X-User-Email";
  public static final String HEADER_USER_ROLES = "X-User-Roles";

  private SecurityConstants() {}
}
