package br.com.zupacademy.sergio.ecommerce.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
  private final JwtAuthenticationService jwtAuthenticationService;

  protected JwtAuthenticationFilter(
    String url,
    AuthenticationManager authenticationManager,
    JwtAuthenticationService jwtAuthenticationService
  ) {
    super(new AntPathRequestMatcher(url));
    this.setAuthenticationManager(authenticationManager);
    this.jwtAuthenticationService = jwtAuthenticationService;
  }

  private static AccountCredentials accountCredentialsFromHttpServletRequest(
    HttpServletRequest httpServletRequest
  ) throws IOException {
    return new ObjectMapper().readValue(
      httpServletRequest.getInputStream(), AccountCredentials.class
    );
  }

  @Override
  public Authentication attemptAuthentication(
    HttpServletRequest httpServletRequest,
    HttpServletResponse httpServletResponse
  ) throws IOException {
    return this.authenticationFromAccountCredentials(
      accountCredentialsFromHttpServletRequest(httpServletRequest)
    );
  }

  @Override
  protected void successfulAuthentication(
    HttpServletRequest httpServletRequest,
    HttpServletResponse httpServletResponse,
    FilterChain filterChain,
    Authentication authentication
  ) {
    this.jwtAuthenticationService.addAuthentication(
      httpServletResponse, authentication.getName()
    );
  }

  private Authentication authenticationFromAccountCredentials(
    AccountCredentials accountCredentials
  ) {
    return this.getAuthenticationManager().authenticate(
      new UsernamePasswordAuthenticationToken(
        accountCredentials.getUsername(),
        accountCredentials.getPassword(),
        Collections.emptyList()
      )
    );
  }
}
