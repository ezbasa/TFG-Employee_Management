package com.availability_manager.config;

import com.availability_manager.security.filters.JwtAuthenticationFilter;
import com.availability_manager.security.filters.OwnershipValidationFilter;
import com.availability_manager.security.request_wrapper.CachedBodyFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OwnershipValidationFilter ownershipValidationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/teamwork", "/employee").hasAuthority("PROJECT_MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/teamwork", "/employee").hasAuthority("PROJECT_MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/teamwork", "/employee").hasAuthority("PROJECT_MANAGER")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(ownershipValidationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new CachedBodyFilter(), OwnershipValidationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, CachedBodyFilter.class)

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*")); // Permitir cualquier origen
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(List.of("*")); // Permitir cualquier header
        config.setAllowCredentials(true); // Permitir cookies (si lo necesitas)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

}