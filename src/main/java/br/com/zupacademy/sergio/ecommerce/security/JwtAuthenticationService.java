package br.com.zupacademy.sergio.ecommerce.security;

import br.com.zupacademy.sergio.ecommerce.repository.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Date;

@Service
public class JwtAuthenticationService {
  private final JwtConfiguration jwtConfiguration;
  private final UserRepository userRepository;

  @Autowired
  public JwtAuthenticationService(
    JwtConfiguration jwtConfiguration,
    UserRepository userRepository
  ) {
    this.jwtConfiguration = jwtConfiguration;
    this.userRepository = userRepository;
  }

  private static String userNameFromTokenWithVerifier(
    String token, JWTVerifier jwtVerifier
  ) {
    try {
      return jwtVerifier.verify(token).getSubject();
    } catch (SignatureVerificationException signatureVerificationException) {
      return null;
    }
  }

  void addAuthentication(
    HttpServletResponse httpServletResponse, String username
  ) {
    httpServletResponse.addHeader(
      this.jwtConfiguration.getHeader(),
      this.tokenFromUsernameWithPrefix(username)
    );
  }

  Authentication getAuthentication(HttpServletRequest request) {
    return authenticationForUsername(verifiedUsernameFromRequest(request));
  }

  private String verifiedUsernameFromRequest(HttpServletRequest request) {
    return verifiedUsernameFromToken(tokenFromRequest(request));
  }

  private Authentication authenticationForUsername(String username) {
    if (username != null) {
      return new UsernamePasswordAuthenticationToken(
        this.userRepository.findByEmail(username).orElse(null),
        null,
        Collections.emptyList()
      );
    }
    return null;
  }

  private String verifiedUsernameFromToken(String token) {
    if (null != token) {
      return userNameFromTokenWithVerifier(
        token, JWT.require(this.generatedSignAlgorithm()).build()
      );
    }
    return null;
  }

  private String tokenFromRequest(HttpServletRequest request) {
    return this.tokenWithoutPrefix(this.tokenFromHeader(request));
  }

  private String tokenFromHeader(HttpServletRequest request) {
    return request.getHeader(this.jwtConfiguration.getHeader());
  }

  private String tokenWithoutPrefix(String tokenWithPrefix) {
    if (tokenWithPrefix != null) {
      return tokenWithPrefix.replace(
        this.jwtConfiguration.getTokenPrefix(), ""
      ).strip(); // this strip is important as it removes preceding space left after the prefix deletion
    }
    return null;
  }

  private String tokenFromUsernameWithPrefix(String username) {
    return tokenWithPrefix(this.generatedTokenForUsername(username));
  }

  private String generatedTokenForUsername(String username) {
    return JWT.create()
      .withSubject(username)
      .withIssuedAt(this.issueDate())
      .withExpiresAt(this.generatedExpirationDate())
      .sign(this.generatedSignAlgorithm());
  }

  Algorithm generatedSignAlgorithm() {
    return Algorithm.HMAC512(this.jwtConfiguration.getSecret());
  }

  private Date generatedExpirationDate() {
    return Date.from(
      LocalDate.now()
        .plusDays(this.jwtConfiguration.getDaysToExpire())
        .atStartOfDay()
        .toInstant(this.configuredZoneOffset())
    );
  }

  private Date issueDate() {
    return Date.from(
      LocalDate.now().atStartOfDay().toInstant(this.configuredZoneOffset())
    );
  }

  private ZoneOffset configuredZoneOffset() {
    return ZoneOffset.of(this.jwtConfiguration.getZoneOffset());
  }

  private String tokenWithPrefix(String token) {
    return this.jwtConfiguration.getTokenPrefix() + " " + token;
  }
}
