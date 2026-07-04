package com.sistemagestioncitas.hospital.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sistemagestioncitas.hospital.model.Usuario;
import com.sistemagestioncitas.hospital.repository.UsuarioRepository;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        //busqueda por correo
        Usuario usuario = usuarioRepository.findByCorreo(correo).orElseThrow(()
                -> new UsernameNotFoundException("Usuario no encontrado con el correo: " + correo));
        //validacion adicional : inactivo
        if (!usuario.isactivo()) {
            throw new UsernameNotFoundException("Usuario inactivo: " + correo);
        }
        return User.builder().username(usuario.getcorreo())
                .password(usuario.getpassword())//Ya viene encriptada
                .authorities(new SimpleGrantedAuthority("ROL_" + usuario.getrol()))//Ya viene desde la entidad
                .build();
    }
}
