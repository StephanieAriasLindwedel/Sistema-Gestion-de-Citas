package com.sistemagestioncitas.hospital.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {
        boolean esAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROL_ADMIN"));
        if (esAdmin) {
            //Si es admin va a la lista de usuarios
            response.sendRedirect("/usuarios/lista");
        } else {
            //Si es usuario normal, va a su perfil 
            response.sendRedirect("/usuarios/perfil");
        }
    }
}
