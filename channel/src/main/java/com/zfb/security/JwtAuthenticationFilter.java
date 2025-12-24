package com.zfb.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

  @Autowired private JwtTokenProvider tokenProvider;

  /**
   * filter to authenticate the request
   *
   * @param request
   * @param response
   * @param filterChain
   * @throws ServletException
   * @throws IOException
   */
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    try {
      String jwt = getJwtFromRequest(request);

      if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
        String userId = tokenProvider.getUserIdFromToken(jwt);
        String email = tokenProvider.getEmailFromToken(jwt);
        List<String> roles = tokenProvider.getRolesFromToken(jwt);

        JwtUserDetails userDetails = new JwtUserDetails(userId, email, roles);
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        response.setHeader(SecurityConstants.HEADER_USER_ID, userId);
        response.setHeader(SecurityConstants.HEADER_USER_EMAIL, email);
        response.setHeader(SecurityConstants.HEADER_USER_ROLES, String.join(",", roles));
      }
    } catch (Exception ex) {
      logger.error("Could not set user authentication in security context", ex);
    }

    filterChain.doFilter(request, response);
  }

  /**
   * get jwt from request
   *
   * @param request
   * @return jwt
   */
  private String getJwtFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader(SecurityConstants.HEADER_STRING);
    if (StringUtils.hasText(bearerToken)
        && bearerToken.startsWith(SecurityConstants.TOKEN_PREFIX)) {
      return bearerToken.substring(SecurityConstants.TOKEN_PREFIX.length());
    }
    return null;
  }
}
