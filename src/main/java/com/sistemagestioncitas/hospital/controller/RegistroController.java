package com.sistemagestioncitas.hospital.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sistemagestioncitas.hospital.model.Usuario;
import com.sistemagestioncitas.hospital.service.UsuarioService;

@Controller
public class RegistroController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/registrar")
    public String registrarUsuario(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes) {
        //validar
        if (usuario.getcorreo() == null || usuario.getcorreo().isEmpty()
                || usuario.getcedula() == null || usuario.getcedula().isEmpty()
                || usuario.getnombre() == null || usuario.getnombre().isEmpty()
                || usuario.getpassword() == null || usuario.getpassword().isEmpty()) {
            redirectAttributes.addFlashAttribute("Error", "Todos los campos son obligatorios");
            return "redirect:/registro";
        }
        //Validaciones de formato
        if (!usuario.getcorreo().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            redirectAttributes.addFlashAttribute("Error", "El correo electrónico no es válido");
            return "redirect:/registro";
        }
        //Validacion de unicos
        if (usuarioService.existeCorreo(usuario.getcorreo())) {
            redirectAttributes.addFlashAttribute("Error", "El correo ya está registrado");
            return "redirect:/registro";
        }
        if (usuarioService.existeCedula(usuario.getcedula())) {
            redirectAttributes.addFlashAttribute("Error", "La cédula ya está registrada");
            return "redirect:/registro";
        }
        //Asignacion rol por defecto
        usuario.setrol("USUARIO");
        usuario.setactivo(true);
        usuarioService.guardar(usuario);
        redirectAttributes.addFlashAttribute("Exito", "El registro fue exitoso, por favor inicie sesión");
        return "redirect:/login";
    }
}