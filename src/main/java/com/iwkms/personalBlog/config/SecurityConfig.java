package com.iwkms.personalBlog.config;

import com.iwkms.personalBlog.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String ADMIN_ROLE = "ADMIN";
    private static final String USER_ROLE = "USER";
    private static final String[] PUBLIC_URLS = {"/", "/post/*/view", "/login", "/css/**", "/js/**", "/register"};
    private static final String LOGIN_PAGE_URL = "/login";
    private static final String DEFAULT_SUCCESS_URL = "/";
    private static final String LOGOUT_URL = "/logout";
    private static final String LOGOUT_SUCCESS_URL = "/";

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(PUBLIC_URLS).permitAll()
                        .requestMatchers("/post/new").authenticated()
                        .requestMatchers("/post/{id}/edit", "/post/{id}/delete").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage(LOGIN_PAGE_URL)
                        .defaultSuccessUrl(DEFAULT_SUCCESS_URL, true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl(LOGOUT_URL)
                        .logoutSuccessUrl(LOGOUT_SUCCESS_URL)
                        .permitAll()
                )
                .userDetailsService(userDetailsService);
        return http.build();
    }
}