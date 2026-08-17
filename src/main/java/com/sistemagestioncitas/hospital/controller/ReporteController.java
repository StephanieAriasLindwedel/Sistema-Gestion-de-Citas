package com.sistemagestioncitas.hospital.controller;
import com.sistemagestioncitas.hospital.model.Cita;
import com.sistemagestioncitas.hospital.model.Medico;
import com.sistemagestioncitas.hospital.service.CitaService;
import com.sistemagestioncitas.hospital.service.MedicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * Controller del Panel de Reportes de Citas.
 * Responsable de manejar las peticiones HTTP para filtrar citas
 * y generar reportes en formato CSV.
 * Solo accesible para administradores (validado en SecurityConfig).
 */
@Controller
public class ReporteController {
    @Autowired
    private CitaService citaService;
    @Autowired
    private MedicoService medicoService;
    /**
     * Muestra el panel de reportes inicial con filtros vacios
     */
    @GetMapping("/reportes")
    public String mostrarPanelReportes(Model model){
        cargarDatosFiltros(model);
        return "reportes/panel";
    }
    /**
     * Aplicar filtros y muestra de resultados con resúmenes estadísticos
     */
    @GetMapping("/reportes/filtrar")
    public String filtrarCitas(@RequestParam(required = false) String fechaInicio,
                                @RequestParam(required = false) String fechaFin,
                                @RequestParam(required = false) Long medicoId,
                                @RequestParam(required = false) String especialidad,
                                @RequestParam(required = false) String estado,
            Model model) {
        List<Cita> citasFiltradas = citaService.filtrarCitas(fechaInicio != null ? LocalDate.parse(fechaInicio) : null,
                fechaFin != null ? LocalDate.parse(fechaFin) : null,
                medicoId, especialidad, estado);
        //Algoritmo de agregación para resúmenes en pantalla
        Map<String, Long> resumenPorEstado = citasFiltradas.stream()
                .collect(Collectors.groupingBy(Cita::getEstado, Collectors.counting()));
        Map<String, Long> resumenPorEspecialidad = citasFiltradas.stream()
                .filter(c -> c.getMedico() != null && c.getMedico().getEspecialidad() != null)
                .collect(Collectors.groupingBy(c -> c.getMedico().getEspecialidad(), Collectors.counting()));
        cargarDatosFiltros(model);
        model.addAttribute("citas", citasFiltradas);
        model.addAttribute("totalCitas", citasFiltradas.size());
        model.addAttribute("resumenPorEstado", resumenPorEstado);
        model.addAttribute("resumenPorEspecialidad", resumenPorEspecialidad);
        //Mantener valores de filtros seleccionados
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        model.addAttribute("medicoId", medicoId);
        model.addAttribute("especialidad", especialidad);
        model.addAttribute("estado", estado);
        return "reportes/panel";
    }
    /**
     * Genera un reporte CSV de las citas filtradas
     */
    @GetMapping("/reportes/exportar-csv")
    public ResponseEntity<byte[]> exportarCitasCSV(@RequestParam(required = false) String fechaInicio,
                                                    @RequestParam(required = false) String fechaFin,
                                                    @RequestParam(required = false) Long medicoId,
                                                    @RequestParam(required = false) String especialidad,
                                                    @RequestParam(required = false) String estado) {
        List<Cita> citasFiltradas = citaService.filtrarCitas(fechaInicio != null ? LocalDate.parse(fechaInicio) : null,
                fechaFin != null ? LocalDate.parse(fechaFin) : null,
                medicoId, especialidad, estado);
        String csv = citaService.generarCSV(citasFiltradas);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "reporte_citas.csv");
        return new ResponseEntity<>(csv.getBytes(StandardCharsets.UTF_8), headers, HttpStatus.OK);
    }
    /**
     * Método auxiliar para cargar listas de filtros reutilizables
     */
    private void cargarDatosFiltros(Model model) {
        List<Medico> medicos = medicoService.listarTodos();
        model.addAttribute("medicos", medicos);
        List<String> especialidades = medicos.stream()
                .map(Medico::getEspecialidad)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        model.addAttribute("especialidades", especialidades);
        model.addAttribute("estados", List.of("PENDIENTE", "CONFIRMADA", "CANCELADA","AUSENTE","PRESENTE","COMPLETADA"));
    }
}
