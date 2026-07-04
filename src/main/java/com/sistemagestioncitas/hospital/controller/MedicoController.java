package com.sistemagestioncitas.hospital.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sistemagestioncitas.hospital.model.EspacioCita;
import com.sistemagestioncitas.hospital.model.Medico;
import com.sistemagestioncitas.hospital.service.EspacioCitaService;
import com.sistemagestioncitas.hospital.service.MedicoService;

@Controller
@RequestMapping("/medicos")
public class MedicoController {

    @Autowired
    private MedicoService medicoService;
    @Autowired
    private EspacioCitaService espacioCitaService;

    //Listar médicos
    @GetMapping("/")
    public String listarMedicos(Model model) {
        model.addAttribute("medicos", medicoService.listarTodos());
        return "medico/listaMedicos";
    }

    //Formulario nuevo medico
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("medico", new Medico());
        return "medico/formularioMedico";
    }

    //Editar medico
    @GetMapping("/editar/{id}")
    public String editarMedico(@PathVariable Long id, Model model) {
        model.addAttribute("medico", medicoService.obtenerPorId(id).orElse(new Medico()));
        return "medico/formularioMedico";
    }

    //Guardar Medico
    @PostMapping("/guardar")
    public String guardarMedico(@ModelAttribute Medico medico) {
        medicoService.guardar(medico);
        return "redirect:/medicos/";
    }

    //Eliminar medico
    @GetMapping("/eliminar/{id}")
    public String eliminarMedico(@PathVariable Long id) {
        medicoService.eliminar(id);
        return "redirect:/medicos/";
    }

    //Espacios medico
    @GetMapping("/{id}/espacios")
    public String verEspacios(@PathVariable Long id, Model model) {
        model.addAttribute("medico", medicoService.obtenerPorId(id).orElse(null));
        model.addAttribute("espacios", espacioCitaService.listarPorMedico(id));
        return "espacio/listaEspacios";
    }

    //Nuevos Espacios
    @GetMapping("/espacio/nuevo/{medicoId}")
    public String nuevoEspacio(@PathVariable Long medicoId, Model model) {
        model.addAttribute("espacio", new EspacioCita());
        model.addAttribute("medicoId", medicoId);
        return "espacio/formularioEspacio";
    }

    //Guardar espacio
    @PostMapping("/espacio/guardar")
    public String guardarEspacio(@ModelAttribute EspacioCita espacio, @RequestParam Long medicoId) {
        Medico medico = medicoService.obtenerPorId(medicoId)
                .orElseThrow(() -> new RuntimeException("MEDICO NO ENCONTRADO"));
        espacio.setmedico(medico);
        espacioCitaService.guardar(espacio);
        return "redirect:/medicos/" + medicoId + "/espacios";
    }
}
