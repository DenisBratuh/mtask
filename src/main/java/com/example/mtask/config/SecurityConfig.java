package com.example.mtask.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

/**
 * Configuration class for Spring Security settings.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * Configures the security filter chain for HTTP requests.
     * <p>
     * - Disables CSRF protection.<br>
     * - Allows unrestricted access to Swagger and API documentation endpoints.<br>
     * - Requires authentication for all other endpoints.<br>
     * - Enables HTTP Basic authentication.
     * </p>
     *
     * @param http the {@link HttpSecurity} to configure
     * @return a {@link SecurityFilterChain} instance
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    /**
     * Provides a password encoder bean for encoding and validating user passwords.
     *
     * @return a {@link PasswordEncoder} instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures an in-memory user details service with predefined users.
     * <p>
     * - Defines two users: `regularUser` with role `USER` and `editorUser` with role `EDITOR`.<br>
     * - Passwords are hashed using the {@link PasswordEncoder}.
     * </p>
     *
     * @return a {@link UserDetailsService} instance containing the configured users
     */
    @Bean
    public UserDetailsService userDetailsService() {
        var regularUser =
                User.builder()
                        .username("regularUser")
                        .password(passwordEncoder().encode("password"))
                        .roles("USER")
                        .build();
        var editorUser =
                User.builder()
                        .username("editorUser")
                        .password(passwordEncoder().encode("password"))
                        .roles("EDITOR")
                        .build();
        return new InMemoryUserDetailsManager(List.of(regularUser, editorUser));
    }
}
