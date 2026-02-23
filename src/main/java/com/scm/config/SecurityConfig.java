package com.scm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

import com.scm.services.impl.SecurityCustomUserDetailService;

@Configuration
public class SecurityConfig {

    @Autowired
    private SecurityCustomUserDetailService userDetailsService;

    @Autowired
    private OAuthenticationSuccessHandler handler;

    @Autowired
    private AuthFailureHandler authFailureHandler;

    // ✅ Password Encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ✅ Authentication Provider (FIXED)
    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    // ✅ Authentication Manager (IMPORTANT)
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {

        return config.getAuthenticationManager();
    }

    // ✅ Security Filter Chain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http

        // Disable CSRF (for simplicity)
        .csrf(AbstractHttpConfigurer::disable)

        // Authorize requests
        .authorizeHttpRequests(auth -> auth

                .requestMatchers(
                        "/",
                        "/home",
                        "/login",
                        "/register",
                        "/do-register",

                        // forgot password URLs
                        "/forgot-password",
                        "/reset-password",

                        // static files
                        "/css/**",
                        "/js/**",
                        "/images/**"

                ).permitAll()

                .requestMatchers("/user/**").authenticated()

                .anyRequest().permitAll()
        )

        // Form Login
        .formLogin(form -> form

                .loginPage("/login")

                .loginProcessingUrl("/authenticate")

                .usernameParameter("email")

                .passwordParameter("password")

                .defaultSuccessUrl("/user/dashboard", true)

                .failureHandler(authFailureHandler)

                .permitAll()
        )

        // Logout
        .logout(logout -> logout

                .logoutUrl("/do-logout")

                .logoutSuccessUrl("/login?logout=true")

                .permitAll()
        )

        // Remember Me
        .rememberMe(remember -> remember

                .key("scm-secret-key")

                .rememberMeParameter("remember-me")

                .tokenValiditySeconds(7 * 24 * 60 * 60) // 7 days
        )

        // OAuth Login
        .oauth2Login(oauth -> oauth

                .loginPage("/login")

                .successHandler(handler)
        )

        // Authentication provider
        .authenticationProvider(authenticationProvider());

        return http.build();
    }
}