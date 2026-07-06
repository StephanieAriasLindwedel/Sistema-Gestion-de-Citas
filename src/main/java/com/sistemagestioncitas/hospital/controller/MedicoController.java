package com.sistemagestioncitas.hospital.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.sistemagestioncitas.hospital.model.EspacioCita;
import com.sistemagestioncitas.hospital.model.Medico;
import com.sistemagestioncitas.hospital.service.EspacioCitaService;
import com.sistemagestioncitas.hospital.service.MedicoService;

@Controller
public class MedicoController {

    @Autowired
    private MedicoService medicoService;

    @Autowired
    private EspacioCitaService espacioCitaService;

    @GetMapping("/medico")
    public String listarMedicos(Model model) {
        model.addAttribute("medicos", medicoService.listarTodos());
        return "medico/listaMedicos";
    }

    @GetMapping("/medico/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("medico", new Medico());
        return "medico/formularioMedico";
    }

    @GetMapping("/medico/editar/{id}")
    public String editarMedico(@PathVariable Long id, Model model) {

        return medicoService.obtenerPorId(id)
                .map(m -> {
                    model.addAttribute("medico", m);
                    return "medico/formularioMedico";
                })
                .orElse("redirect:/medico");
    }

    @PostMapping("/medico/guardar")
    public String guardarMedico(@ModelAttribute Medico medico) {
        medicoService.guardar(medico);
        return "redirect:/medico";
    }

    @GetMapping("/medico/eliminar/{id}")
    public String eliminarMedico(@PathVariable Long id) {
        medicoService.eliminar(id);
        return "redirect:/medico";
    }

    // FIX NULL SAFE
    @GetMapping("/medico/{id}/espacios")
    public String verEspacios(@PathVariable Long id, Model model) {

        Medico medico = medicoService.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado"));

        model.addAttribute("medico", medico);
        model.addAttribute("espacios", espacioCitaService.listarPorMedico(id));

        return "medico/listaEspacios";
    }

    @GetMapping("/medico/espacio/nuevo/{medicoId}")
    public String nuevoEspacio(@PathVariable Long medicoId, Model model) {
        model.addAttribute("espacio", new EspacioCita());
        model.addAttribute("medicoId", medicoId);
        return "medico/formularioEspacio";
    }

    @PostMapping("/medico/espacio/guardar")
    public String guardarEspacio(@ModelAttribute EspacioCita espacio,
            @RequestParam Long medicoId) {

        Medico medico = medicoService.obtenerPorId(medicoId)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado"));

        espacio.setMedico(medico);
        espacioCitaService.guardar(espacio);

        return "redirect:/medico/" + medicoId + "/espacios";
    }
}