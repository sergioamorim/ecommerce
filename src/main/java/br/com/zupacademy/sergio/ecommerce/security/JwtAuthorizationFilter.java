package br.com.zupacademy.sergio.ecommerce.security;

import com.auth0.jwt.exceptions.TokenExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class JwtAuthorizationFilter extends GenericFilterBean {
  private final JwtAuthenticationService jwtAuthenticationService;

  public JwtAuthorizationFilter(JwtAuthenticationService jwtAuthenticationService) {
    this.jwtAuthenticationService = jwtAuthenticationService;
  }

  private static void doFilterChain(
    ServletRequest servletRequest,
    ServletResponse servletResponse,
    FilterChain filterChain
  ) throws ServletException, IOException {
    filterChain.doFilter(servletRequest, servletResponse);
  }

  @Override
  public void doFilter(
    ServletRequest servletRequest,
    ServletResponse servletResponse,
    FilterChain filterChain
  ) throws ServletException, IOException {
    SecurityContextHolder.getContext().setAuthentication(
      this.authenticationFromServletRequest(servletRequest)
    );
    doFilterChain(servletRequest, servletResponse, filterChain);
  }

  private Authentication authenticationFromServletRequest(
    ServletRequest servletRequest
  ) {
    try {
      return this.jwtAuthenticationService.getAuthentication(
        this.httpServletRequestFromServletRequest(servletRequest)
      );
    } catch (TokenExpiredException tokenExpiredException) {
      return null;
    }
  }

  private HttpServletRequest httpServletRequestFromServletRequest(
    ServletRequest servletRequest
  ) {
    return (HttpServletRequest) servletRequest;
  }
}
