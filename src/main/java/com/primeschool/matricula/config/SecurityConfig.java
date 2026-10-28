package com.primeschool.matricula.config;

import com.primeschool.matricula.security.UsuarioDetailsService;
import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   UsuarioDetailsService usuarioDetailsService) throws Exception {
        http
                .userDetailsService(usuarioDetailsService)
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/login", "/error").permitAll()
                        .requestMatchers("/admin/**", "/apoderados/**", "/estudiantes/**", "/vacantes")
                        .hasAnyRole("ADMINISTRADOR", "DIRECTIVO")
                        .requestMatchers("/matriculas/nueva", "/matriculas/*/confirmar", "/matriculas/*/anular")
                        .hasAnyRole("ADMINISTRADOR", "DIRECTIVO")
                        .requestMatchers(HttpMethod.POST, "/matriculas")
                        .hasAnyRole("ADMINISTRADOR", "DIRECTIVO")
                        .requestMatchers("/notas", "/notas/**", "/asistencia", "/asistencia/**",
                                "/registro-academico")
                        .hasAnyRole("DOCENTE", "ADMINISTRADOR", "DIRECTIVO")
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error")
                        .permitAll())
                .rememberMe(remember -> remember
                        .rememberMeParameter("remember-me")
                        .tokenValiditySeconds(60 * 60 * 24 * 14))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll());
        return http.build();
    }
}
