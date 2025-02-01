package com.example.Bank_App.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;
    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // definisco i miei endpoints pubblici
        List<String> publicEndpoints = List.of(
                "/app/v1/auth/register",
                "/app/v1/auth/login"
        );
        // passo la lista al jwtAuthFilter
        jwtAuthFilter.setPublicEndpoints(publicEndpoints);
        // creo la catena di filtri
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> req
                        .requestMatchers(publicEndpoints.toArray(new String[0])) // per gli endpoint pubblici
                        .permitAll() // permetti sempre l'accesso
                        .anyRequest() // per ogni altra richiesta
                        .authenticated() // richiedi l'autenticazione
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}