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

import static com.iwkms.personalBlog.config.AppConstants.Urls.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

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
        configureExceptionHandling(http);
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

    private void configureExceptionHandling(HttpSecurity http) throws Exception {
        http.exceptionHandling(exceptions -> exceptions
                .accessDeniedHandler((_, response, _) -> response.sendRedirect("/?error=accessDenied"))
                .authenticationEntryPoint((_, response, _) -> response.sendRedirect(LOGIN_PAGE_URL))
        );
    }
}