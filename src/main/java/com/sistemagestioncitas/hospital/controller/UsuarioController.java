package com.sistemagestioncitas.hospital.controller;

import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sistemagestioncitas.hospital.model.Usuario;
import com.sistemagestioncitas.hospital.service.UsuarioService;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // LISTA USUARIOS (ADMIN)
    @GetMapping("/usuario/listaUsuarios")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "usuario/listaUsuarios";
    }

    // PERFIL USUARIO (FIX NULL POINTER)
    @GetMapping("/usuario/perfil")
    public String verPerfil(Principal principal, Model model) {

        if (principal == null) {
            return "redirect:/login";
        }
        return usuarioService.buscarPorCorreo(principal.getName())
                .map(u -> {
                    model.addAttribute("usuario", u);
                    return "usuario/perfil";
                })
                .orElse("redirect:/login");
    }

    // GUARDAR PERFIL (FIX getId + null safe)
    @PostMapping("/usuario/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario,
            @RequestParam(required = false) String nuevaPassword,
            RedirectAttributes redirectAttributes) {

        if (usuario.getId() == null) {
            redirectAttributes.addFlashAttribute("error", "Usuario inválido");
            return "redirect:/usuario/perfil";
        }

        return usuarioService.obtenerPorId(usuario.getId())
                .map(actual -> {

                    if (!actual.getCorreo().equals(usuario.getCorreo())
                            && usuarioService.existeCorreo(usuario.getCorreo())) {
                        redirectAttributes.addFlashAttribute("error", "Correo ya registrado");
                        return "redirect:/usuario/perfil";
                    }

                    if (!actual.getCedula().equals(usuario.getCedula())
                            && usuarioService.existeCedula(usuario.getCedula())) {
                        redirectAttributes.addFlashAttribute("error", "Cédula ya registrada");
                        return "redirect:/usuario/perfil";
                    }

                    actual.setNombre(usuario.getNombre());
                    actual.setCorreo(usuario.getCorreo());
                    actual.setCedula(usuario.getCedula());
                    actual.setContacto(usuario.getContacto());

                    if (nuevaPassword != null && !nuevaPassword.isEmpty()) {
                        usuarioService.actualizarPassword(actual, nuevaPassword);
                    } else {
                        usuarioService.actualizar(actual);
                    }

                    redirectAttributes.addFlashAttribute("exito", "Perfil actualizado");
                    return "redirect:/usuario/perfil";
                })
                .orElse("redirect:/login");
    }

    // EDITAR ADMIN
    @GetMapping("/usuario/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Model model) {

        return usuarioService.obtenerPorId(id)
                .map(u -> {
                    model.addAttribute("usuario", u);
                    return "usuario/formularioEditar";
                })
                .orElse("redirect:/usuario/listaUsuarios");
    }

    // GUARDAR ADMIN
    @PostMapping("/usuario/admin/guardar")
    public String guardarDesdeAdmin(@ModelAttribute Usuario usuario,
            RedirectAttributes redirectAttributes) {

        if (usuario.getId() == null) {
            return "redirect:/usuario/listaUsuarios";
        }

        return usuarioService.obtenerPorId(usuario.getId())
                .map(actual -> {

                    if (!actual.getCorreo().equals(usuario.getCorreo())
                            && usuarioService.existeCorreo(usuario.getCorreo())) {
                        redirectAttributes.addFlashAttribute("error", "Correo ya existe");
                        return "redirect:/usuario/editar/" + usuario.getId();
                    }

                    if (!actual.getCedula().equals(usuario.getCedula())
                            && usuarioService.existeCedula(usuario.getCedula())) {
                        redirectAttributes.addFlashAttribute("error", "Cédula ya existe");
                        return "redirect:/usuario/editar/" + usuario.getId();
                    }

                    actual.setNombre(usuario.getNombre());
                    actual.setCorreo(usuario.getCorreo());
                    actual.setCedula(usuario.getCedula());
                    actual.setContacto(usuario.getContacto());
                    actual.setRol(usuario.getRol());

                    usuarioService.actualizar(actual);

                    return "redirect:/usuario/listaUsuarios";
                })
                .orElse("redirect:/usuario/listaUsuarios");
    }

    // DESACTIVAR
    @GetMapping("/usuario/desactivar/{id}")
    public String desactivarUsuario(@PathVariable Long id) {

        usuarioService.desactivar(id);
        return "redirect:/usuario/listaUsuarios";
    }
}