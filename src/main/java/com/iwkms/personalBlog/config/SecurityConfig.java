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
    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_USER = "ROLE_USER";

    // URL patterns grouped by access level
    private static final String[] PUBLIC_URLS = {"/", "/post/*", "/user/login", "/user/register"};
    private static final String[] PUBLIC_RESOURCE_URLS = {"/css/**", "/js/**"};
    private static final String[] AUTHENTICATED_URLS = {"/post/new"};
    private static final String[] USER_CONTENT_URLS = {"/post/{id}/edit", "/post/{id}/delete"};

    // Authentication configuration constants
    private static final String LOGIN_PAGE_URL = "/user/login";
    private static final String DEFAULT_SUCCESS_URL = "/";
    private static final String LOGOUT_URL = "/user/logout";
    private static final String LOGOUT_SUCCESS_URL = "/";

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        configureRequestAuthorization(http);
        configureLoginForm(http);
        configureLogout(http);
        http.userDetailsService(userDetailsService);

        return http.build();
    }

    private void configureRequestAuthorization(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(PUBLIC_URLS).permitAll()
                .requestMatchers(PUBLIC_RESOURCE_URLS).permitAll()
                .requestMatchers(AUTHENTICATED_URLS).authenticated()
                .requestMatchers(USER_CONTENT_URLS).authenticated()
                .anyRequest().authenticated()
        );
    }

    private void configureLoginForm(HttpSecurity http) throws Exception {
        http.formLogin(form -> form
                .loginPage(LOGIN_PAGE_URL)
                .defaultSuccessUrl(DEFAULT_SUCCESS_URL, true)
                .permitAll()
        );
    }

    private void configureLogout(HttpSecurity http) throws Exception {
        http.logout(logout -> logout
                .logoutUrl(LOGOUT_URL)
                .logoutSuccessUrl(LOGOUT_SUCCESS_URL)
                .permitAll()
        );
    }
}