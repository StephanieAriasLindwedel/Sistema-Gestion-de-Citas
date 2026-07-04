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
                //Rutas publicas
                .requestMatchers("/login", "/registro", "/registrar", "/recuperar",
                        "/css/**", "js/**", "/h2-console/**").permitAll()
                //Rutas de Admin y Usuario
                .requestMatchers("/usuarios/perfil", "/usuarios/guardar", "/medicos/",
                        "/medicos/{id}/espacios").hasAnyRole("USUARIO", "ADMIN")
                //TODO LO DEMAS ES ADMIN
                .anyRequest().hasRole("ADMIN")
                )
                .formLogin(form -> form
                .loginPage("/login")
                .successHandler(loginSuccessHandler)
                .permitAll())
                .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll())
                .exceptionHandling(ex -> ex.accessDeniedPage("/login?error=acceso"))
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"));
        return http.build();
    }
}
