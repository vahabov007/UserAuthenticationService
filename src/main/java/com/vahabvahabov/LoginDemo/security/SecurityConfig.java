package com.vahabvahabov.LoginDemo.security;

import com.vahabvahabov.LoginDemo.repository.UserRepository;
import com.vahabvahabov.LoginDemo.security.jwt.JwtRequestFilter;
import com.vahabvahabov.LoginDemo.security.jwt.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final DataSource dataSource;
    private final UserRepository userRepository;

    public SecurityConfig(DataSource dataSource, UserRepository userRepository) {
        this.dataSource = dataSource;
        this.userRepository = userRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return usernameOrMail -> {
            return userRepository.findByMail(usernameOrMail)
                    .or(() -> userRepository.findByUsername(usernameOrMail))
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + usernameOrMail));
        };
    }

    @Bean
    public JwtRequestFilter jwtRequestFilter(UserDetailsService userDetailsService,
                                             JwtUtil jwtUtil
                                             ) {
        JwtRequestFilter jwtRequestFilter = new JwtRequestFilter();
        jwtRequestFilter.setUserDetailsService(userDetailsService);
        jwtRequestFilter.setJwtUtil(jwtUtil);
        return jwtRequestFilter;
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtRequestFilter jwtRequestFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/style.css",
                                "/script.js",
                                "/forgot-password.js",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/webjars/**",
                                "/my-login",
                                "/register",
                                "/forgot-password",
                                "/api/register/**",
                                "/api/forgot-password/**",
                                "/favicon.ico",
                                "/api/auth/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
}