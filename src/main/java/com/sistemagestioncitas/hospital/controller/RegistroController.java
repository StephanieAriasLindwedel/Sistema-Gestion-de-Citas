package com.sistemagestioncitas.hospital.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sistemagestioncitas.hospital.model.Usuario;
import com.sistemagestioncitas.hospital.service.UsuarioService;

@Controller
public class RegistroController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registrar")
    public String registrarUsuario(@ModelAttribute Usuario usuario,
            RedirectAttributes redirectAttributes) {

        if (usuario.getCorreo() == null || usuario.getCorreo().isEmpty()
                || usuario.getCedula() == null || usuario.getCedula().isEmpty()
                || usuario.getNombre() == null || usuario.getNombre().isEmpty()
                || usuario.getPassword() == null || usuario.getPassword().isEmpty()) {

            redirectAttributes.addFlashAttribute("error", "Todos los campos son obligatorios");
            return "redirect:/registro";
        }

        if (!usuario.getCorreo().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            redirectAttributes.addFlashAttribute("error", "Correo inválido");
            return "redirect:/registro";
        }

        if (usuarioService.existeCorreo(usuario.getCorreo())) {
            redirectAttributes.addFlashAttribute("error", "Correo ya registrado");
            return "redirect:/registro";
        }

        if (usuarioService.existeCedula(usuario.getCedula())) {
            redirectAttributes.addFlashAttribute("error", "Cédula ya registrada");
            return "redirect:/registro";
        }

        usuario.setRol("USUARIO");
        usuario.setActivo(true);

        try {
            usuarioService.guardar(usuario);
            redirectAttributes.addFlashAttribute("exito", "Registro exitoso");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            return "redirect:/registro";
        }
    }
}