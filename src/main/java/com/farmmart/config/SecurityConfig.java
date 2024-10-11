package com.farmmart.config;

import com.farmmart.filter.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.List;

/**
 * replaces:
 *   - app.use(cors({...}))              in index.js
 *   - app.use(helmet())                 in index.js
 *   - middleware/Admin.js               (role-based access per route)
 *   - auth middleware on protected routes
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CORS — replaces: app.use(cors({ credentials: true, origin: process.env.FRONTEND_URL }))
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // Disable CSRF — safe for REST APIs with JWT
            .csrf(csrf -> csrf.disable())

            // Stateless sessions — no server-side session storage, JWT handles auth
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Route-level access control
            .authorizeHttpRequests(auth -> auth

                // ── PUBLIC ROUTES (no login needed) ──────────────────────────────
                .requestMatchers(
                    "/api/user/register",
                    "/api/user/login",
                    "/api/user/forgot-password",
                    "/api/user/verify-forgot-password-otp",
                    "/api/user/reset-password",
                    "/api/user/verify-email"
                ).permitAll()

                // Public product browsing
                .requestMatchers(
                    "/api/category/get",
                    "/api/subcategory/get",
                    "/api/product/get",
                    "/api/product/get-product-by-category",
                    "/api/product/get-pruduct-by-category-and-subcategory",
                    "/api/product/get-product-details",
                    "/api/product/search-product"
                ).permitAll()

                // ── EVERYTHING ELSE requires a valid JWT token ─────────────────
                .anyRequest().authenticated()
            )

            // Plug in our JWT filter BEFORE Spring's default auth filter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * replaces: app.use(cors({ credentials: true, origin: process.env.FRONTEND_URL }))
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(frontendUrl));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);   // allows cookies to be sent cross-origin

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * replaces: bcryptjs (the password hashing library)
     * BCryptPasswordEncoder(10) = same as bcryptjs.genSalt(10)
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
