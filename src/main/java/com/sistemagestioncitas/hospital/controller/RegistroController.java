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
        if (usuarioService.existeCorreo(usuario.getcorreo())) {
            redirectAttributes.addFlashAttribute("Error", "El correo ya está registrado");
            return "redirect:/registro";
        }
        if (usuarioService.existeCedula(usuario.getcedula())) {
            redirectAttributes.addFlashAttribute("Error", "La cédula ya está registrada");
            return "redirect:/registro";
        }
        //Asignacion rol por defecto
        usuario.setrol("ROL_USUARIO");
        usuario.setactivo(true);
        usuarioService.guardar(usuario);
        redirectAttributes.addFlashAttribute("Exito", "El registro fue exitoso, por favor inicie sesión");
        return "redirect:/login";
    }
}
