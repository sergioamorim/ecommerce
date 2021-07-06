package br.com.zupacademy.sergio.ecommerce.security;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

  private final JwtAuthenticationService jwtAuthenticationService;
  private final DataSource dataSource;
  private final PasswordEncoding passwordEncoding;

  public WebSecurityConfiguration(
    JwtAuthenticationService jwtAuthenticationService,
    DataSource dataSource,
    PasswordEncoding passwordEncoding
  ) {
    this.jwtAuthenticationService = jwtAuthenticationService;
    this.dataSource = dataSource;
    this.passwordEncoding = passwordEncoding;
  }


  @Override
  protected void configure(HttpSecurity httpSecurity) throws Exception {
    httpSecurity.csrf().disable().authorizeRequests()
      .antMatchers(HttpMethod.POST, "/auth").permitAll()
      .antMatchers(HttpMethod.POST, "/users").permitAll()
      .anyRequest().authenticated()
      .and()
      .addFilterBefore(
        new JwtAuthenticationFilter(
          "/auth", this.authenticationManager(), this.jwtAuthenticationService
        ),
        UsernamePasswordAuthenticationFilter.class
      )
      .addFilterBefore(
        new JwtAuthorizationFilter(this.jwtAuthenticationService),
        UsernamePasswordAuthenticationFilter.class
      );
  }

  @Override
  protected void configure(
    AuthenticationManagerBuilder authenticationManagerBuilder
  ) throws Exception {
    authenticationManagerBuilder.jdbcAuthentication()
      .dataSource(this.dataSource)
      .passwordEncoder(this.passwordEncoding.passwordEncoder())
      .usersByUsernameQuery("SELECT email, password, 'TRUE' FROM user WHERE email=?")
      .authoritiesByUsernameQuery("SELECT email, 'USER' FROM user WHERE email=?");
  }
}
