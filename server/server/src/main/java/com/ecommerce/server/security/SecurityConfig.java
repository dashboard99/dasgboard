package com.ecommerce.server.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // we need to intecept the token betwwen filiter and controller
    private CustomUserDetailsService customUserDetailsService;
    private JwtAuthEntryPoint jwtAuthEntryPoint;
    @Autowired


    public SecurityConfig(CustomUserDetailsService customUserDetailsService,JwtAuthEntryPoint jwtAuthEntryPoint) {
        this.customUserDetailsService = customUserDetailsService;

        this.jwtAuthEntryPoint = jwtAuthEntryPoint;
    }


// add auth entry point for exception handling in spring security
// since all  auth is happening before the servlet, so the exception should be handled before reach servelt
    // we need to  register this runtime exceptional handling into the security filiter below
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .csrf(csrf->csrf.disable())
                .authorizeHttpRequests((authz)-> authz
                    .requestMatchers("/api/auth/**").permitAll()
                            .requestMatchers("/api/user").hasAuthority("ADMIN")
                            .anyRequest().authenticated()



                )
                .exceptionHandling((exception)->exception.authenticationEntryPoint(jwtAuthEntryPoint))
                .headers(headers->headers
                        .httpStrictTransportSecurity(Customizer.withDefaults())
                        .xssProtection(Customizer.withDefaults())
                        .contentSecurityPolicy(csp->csp.policyDirectives("default-src 'self"))
                ).sessionManagement(sess->sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//before get to the servet implement the security filter
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();


    }


    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

   @Bean
    public JWTAuthenticationFilter jwtAuthenticationFilter(){
        return new JWTAuthenticationFilter();
   }

}
