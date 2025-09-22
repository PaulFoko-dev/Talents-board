package com.talentsboard.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration de Spring Security avec filtre Firebase.
 * Compatible Spring Security 6.1+ :
 * - utilise la nouvelle syntaxe pour désactiver CSRF.
 * - autorise les endpoints /api/auth/** sans authentification.
 * - protège toutes les autres routes avec Firebase token.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Swagger & OpenAPI doivent être accessibles sans authentification
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**"
                        ).permitAll()

                        // Endpoints publics
                        .requestMatchers("/api/auth/**").permitAll()

                        // Le reste nécessite un token Firebase
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new FirebaseAuthenticationFilter(),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
