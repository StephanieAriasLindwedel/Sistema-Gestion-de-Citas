package com.sistemagestioncitas.hospital.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.sistemagestioncitas.hospital.model.Usuario;
import com.sistemagestioncitas.hospital.repository.UsuarioRepository;

@Component
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;

    public DataLoader(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!usuarioRepository.existsByCorreo("step2299@gmail.com")) {
            Usuario admin = new Usuario();
            admin.setNombre("Administrador");
            admin.setCorreo("step2299@gmail.com");
            admin.setCedula("117580750");
            admin.setContacto("8782-5958");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRol("ADMIN");
            admin.setActivo(true);
            usuarioRepository.save(admin);
            System.out.println("USUARIO ADMIN CREADO: step2299@gmail.com / admin123");
        }
    }
}
