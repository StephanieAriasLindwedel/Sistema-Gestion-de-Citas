package com.sistemagestioncitas.hospital.controller;

import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sistemagestioncitas.hospital.model.Usuario;
import com.sistemagestioncitas.hospital.service.UsuarioService;

@Controller
@RequestMapping("/usuarios")

public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    //SOLO ADMI (Listar todos los usuarios)
    @GetMapping("/lista")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "usuario/listaUsuarios";
    }

    //PERFIL PROPIO
    @GetMapping("/perfil")
    public String verPerfil(Principal principal, Model model) {
        Optional<Usuario> usuario = usuarioService.buscarPorCorreo(principal.getName());
        if (usuario.isPresent()) {
            model.addAttribute("usuario", usuario.get());
            return "usuario/perfil";
        }
        return "redirect:/login";
    }

    //Guardar cambios del perfil
    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario,
            @RequestParam(required = false) String nuevaPassword,
            Principal principal, RedirectAttributes redirectAttributes) {
        //validacion de correo y cedula
        Optional<Usuario> existente = usuarioService.obtenerPorId(usuario.getid());
        if (existente.isPresent()) {
            Usuario actual = existente.get();
            //Cambio de correo, verificacion de existencia
            if (!actual.getcorreo().equals(usuario.getcorreo())
                    && usuarioService.existeCorreo(usuario.getcorreo())) {
                return "redirect:/usuarios/editar/" + usuario.getid();
            }
            //Cambio en cédula, verificacion de existencia
            if (!actual.getcedula().equals(usuario.getcedula())
                    && usuarioService.existeCedula(usuario.getcedula())) {
                redirectAttributes.addFlashAttribute("Error", "La cedula ya esta registrada");
                return "redirect:/usuarios/editar/" + usuario.getid();
            }
            actual.setnombre(usuario.getnombre());
            actual.setcorreo(usuario.getcorreo());
            actual.setcedula(usuario.getcedula());
            actual.setcontacto(usuario.getcontacto());
            //Actualizar contraseña
            if (nuevaPassword != null && !nuevaPassword.isEmpty()) {
                usuarioService.actualizarPassword(actual, nuevaPassword);
            } else {
                usuarioService.actualizar(actual);
            }
            redirectAttributes.addFlashAttribute("Exito", "Perfil actualizado correctamente");
        }
        return "redirect:/usuarios/perfil";
    }

    //Formulario edicion Admin
    @GetMapping("/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Model model) {
        Optional<Usuario> usuario = usuarioService.obtenerPorId(id);
        if (usuario.isPresent()) {
            model.addAttribute("usuario", usuario.get());
            return "usuario/formularioEditar";
        }
        return "redirect:/usuarios/lista";
    }

    //Desactivar (ADMIN)
    @GetMapping("/desactivar/{id}")
    public String desactivarUsuario(@PathVariable Long id) {
        usuarioService.desactivar(id);
        return "redirect:/usuarios/lista";
    }

    //Guardar cambios de Admin
    @PostMapping("/admin/guardar")
    public String guardarUsuarioAdmin(@ModelAttribute Usuario usuario,
            RedirectAttributes redirectAttributes) {
        Optional<Usuario> existente = usuarioService.obtenerPorId(usuario.getid());
        if (existente.isPresent()) {
            Usuario actual = existente.get();
            if (!actual.getcorreo().equals(usuario.getcorreo())
                    && usuarioService.existeCorreo(usuario.getcorreo())) {
                redirectAttributes.addFlashAttribute("Error",
                        "El correo ya esta registrado");
                return "redirect:/usuarios/editar/" + usuario.getid();
            }
            if (!actual.getcedula().equals(usuario.getcedula())
                    && usuarioService.existeCedula(usuario.getcedula())) {
                redirectAttributes.addFlashAttribute("Error",
                        "La cedula ya esta registrada");
                return "redirect:/usuarios/editar/" + usuario.getid();
            }
            actual.setnombre(usuario.getnombre());
            actual.setcorreo(usuario.getcorreo());
            actual.setcedula(usuario.getcedula());
            actual.setcontacto(usuario.getcontacto());
            actual.setrol(usuario.getrol());
            usuarioService.actualizar(actual);
        }
        return "redirect:/usuarios/lista";
    }
}
