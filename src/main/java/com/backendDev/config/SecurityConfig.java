package com.backendDev.config;

import com.backendDev.security.JwtAuthEntryPoint;
import com.backendDev.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(jwtAuthEntryPoint)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()

                        // ── WebSocket SockJS handshake — MUST be public ──
                        .requestMatchers("/ws/**").permitAll()

                        // ── Auth ──
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/business/setup").permitAll()

                        // ── Customer menu (QR scan) ──
                        .requestMatchers("/api/menu/**").permitAll()

                        // ── Business ──
                        .requestMatchers("/api/business-information/**").authenticated()
                        .requestMatchers("/api/categories/**").authenticated()
                        .requestMatchers("/api/products/**").authenticated()
                        .requestMatchers("/api/images/**").authenticated()

                        // ── Pay at counter public check ──
                        .requestMatchers(HttpMethod.GET, "/api/payment/pay-at-counter/status").permitAll()

                        // ── Payment & QR ──
                        .requestMatchers("/api/payment/**").authenticated()
                        .requestMatchers("/api/qr/**").authenticated()
                        .requestMatchers("/api/suggestions/**").authenticated()

                        // ── Forgot password ──
                        .requestMatchers("/api/auth/forgot-password").permitAll()
                        .requestMatchers("/api/auth/validate-reset-token").permitAll()
                        .requestMatchers("/api/auth/reset-password").permitAll()

                        // ── Customer ordering flow (public — no JWT) ──
                        .requestMatchers("/api/customer/session").permitAll()
                        .requestMatchers("/api/customer/order").permitAll()
                        .requestMatchers("/api/customer/payment/initiate").permitAll()
                        .requestMatchers("/api/customer/payment/confirm").permitAll()
                        .requestMatchers("/api/customer/order/*/status").permitAll()

                        // ── Invoice (Task 1) — public, customer-facing, no JWT ──
                        .requestMatchers(HttpMethod.GET, "/api/customer/order/*/invoice").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/customer/order/*/invoice/send-email").permitAll()

                        // ── Customer → Business reviews (Task 2) — public, no JWT ──
                        .requestMatchers(HttpMethod.POST, "/api/reviews/business").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reviews/business/*").permitAll()

                        // ── Admin orders (JWT protected) ──
                        .requestMatchers("/api/admin/orders/**").authenticated()

                        // ── Admin → Application reviews (JWT protected) ──
                        .requestMatchers("/api/admin/reviews/**").authenticated()

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}