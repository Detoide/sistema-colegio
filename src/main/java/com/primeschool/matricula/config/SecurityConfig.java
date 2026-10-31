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

/**
 * Configuración central de seguridad de la aplicación.
 *
 * <p>Define la autenticación basada en formulario, la autorización por roles,
 * la protección de rutas, el cifrado de contraseñas y la gestión de sesiones.</p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * Crea el codificador de contraseñas BCrypt utilizado por Spring Security.
     *
     * @return codificador BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Construye la cadena de filtros de seguridad con las reglas de acceso,
     * inicio y cierre de sesión y recordatorio de sesión.
     *
     * @param http configuración HTTP de Spring Security
     * @param usuarioDetailsService servicio que carga los usuarios autenticables
     * @return cadena de filtros configurada
     * @throws Exception si ocurre un error al configurar la seguridad
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   UsuarioDetailsService usuarioDetailsService) throws Exception {
        http
                .userDetailsService(usuarioDetailsService)
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/login", "/error").permitAll()
                        .requestMatchers("/admin/usuarios", "/admin/usuarios/**")
                        .hasRole("ADMINISTRADOR")
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
