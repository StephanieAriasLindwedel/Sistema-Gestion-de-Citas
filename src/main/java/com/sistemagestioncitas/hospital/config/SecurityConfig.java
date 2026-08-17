package com.sistemagestioncitas.hospital.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.sistemagestioncitas.hospital.security.LoginSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        private final LoginSuccessHandler loginSuccessHandler;

        public SecurityConfig(LoginSuccessHandler loginSuccessHandler) {
                this.loginSuccessHandler = loginSuccessHandler;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(auth -> auth
                                                // Rutas públicas (sin autenticación)
                                                .requestMatchers("/login", "/registro", "/registrar", "/recuperar",
                                                                "/css/**", "/js/**", "/h2-console/**")
                                                .permitAll()
                                                // Rutas para USUARIO y ADMIN (Gestión básica y citas propias)
                                                .requestMatchers("/usuario/perfil", "/usuario/guardar",
                                                                "/medico/", "/medico/*/espacios",
                                                                "/cita/mis-citas", "/cita/nueva", "/cita/espacios/**",
                                                                "/cita/guardar", "/cita/cancelar/**")
                                                .hasAnyRole("USUARIO", "ADMIN")
                                                // Rutas SOLO PARA ADMIN (Panel de Administración)
                                                .requestMatchers("/usuario/lista", "/usuario/editar/**",
                                                                "/usuario/desactivar/**", "/usuario/admin/**",
                                                                "/medico/nuevo", "/medico/guardar",
                                                                "/medico/editar/**", "/medico/eliminar/**",
                                                                "/medico/espacio/**", "/cita/admin/**",
                                                                "/cita/admin/confirmar/**", "/cita/admin/presente/**",
                                                                "/cita/admin/ausente/**", "/reportes/**")
                                                .hasRole("ADMIN")
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .loginProcessingUrl("/login")
                                                .successHandler(loginSuccessHandler)
                                                .failureUrl("/login?error=true")
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutSuccessUrl("/login?logout")
                                                .permitAll())
                                .exceptionHandling(ex -> ex.accessDeniedPage("/acceso-denegado"))
                                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"));

                return http.build();
        }
}
