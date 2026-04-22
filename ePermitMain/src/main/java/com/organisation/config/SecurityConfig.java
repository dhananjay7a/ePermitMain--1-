package com.organisation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Password Encoder Bean
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF disabled for REST
                .authorizeHttpRequests(auth -> auth
                      //  .requestMatchers("/new/login").permitAll()   
                        .requestMatchers("/**").permitAll()// allow login
                        .requestMatchers("/error").permitAll()      // optional
                        .anyRequest().authenticated()               // secure others
                )
                 // disable old basic login
                .formLogin(form -> form.disable())  // disable UI login
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // JWT friendly
                );

        return http.build();
    }
    
   
   
}
