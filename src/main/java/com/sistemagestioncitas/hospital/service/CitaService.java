package com.sistemagestioncitas.hospital.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sistemagestioncitas.hospital.model.Cita;
import com.sistemagestioncitas.hospital.model.EspacioCita;
import com.sistemagestioncitas.hospital.model.Usuario;
import com.sistemagestioncitas.hospital.repository.CitaRepository;
import com.sistemagestioncitas.hospital.repository.EspacioCitaRepository;

@Service
public class CitaService {
    @Autowired
    private CitaRepository citaRepository;
    @Autowired
    private EspacioCitaRepository espacioCitaRepository;

    /**
     * RN1 y RN2: Crean la cita con validacion de disponibilidad
     * Transaccional (Transactional) para evitar doble reserva
     */
    @Transactional
    public Cita crearCita(Long usuarioId, Long espacioCitaId, Usuario usuario) {
        // Validacion de espacio existente
        EspacioCita espacio = espacioCitaRepository.findById(espacioCitaId)
                .orElseThrow(() -> new RuntimeException("Espacio de cita no encontrado"));
        // RN1: Verificar que el espacio NO esté ocupado
        if (citaRepository.existsByEspacioCitaIdAndEstadoIn(espacioCitaId, List.of("PENDIENTE", "CONFIRMADA"))) {
            throw new RuntimeException("El espacio seleccionado ya está ocupado");
        }
        // RN4:Validacion de fecha y hora pasada (No es permitido)
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime fechaHoraCita = LocalDateTime.of(espacio.getFecha(), espacio.getHoraInicio());
        if (fechaHoraCita.isBefore(ahora)) {
            throw new RuntimeException("No se pueden crear citas en fechas u horas pasadas");
        }
        // RN6: Verificar que el usuario no tenga citas solapadas
        List<Cita> citasSolapadas = citaRepository.findByUsuarioIdAndHorarioSolapado(
                usuarioId,
                espacio.getFecha(),
                espacio.getHoraInicio(),
                espacio.getHoraFin());
        if (!citasSolapadas.isEmpty()) {
            throw new RuntimeException("Ya tiene una cita programada en este horario");
        }
        // Crear la cita
        Cita cita = new Cita();
        cita.setUsuario(usuario);
        cita.setMedico(espacio.getMedico());
        cita.setEspacio(espacio);
        cita.setEstado("PENDIENTE");
        cita.setFechaHora(fechaHoraCita);
        // Marcar el espacio como ocupado
        espacio.setOcupado(true);
        espacioCitaRepository.save(espacio);
        return citaRepository.save(cita);
    }

    /**
     * Confirmar cita (SOLO ADMIN)
     */
    @Transactional
    public Cita confirmarCita(Long citaId) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no fue encontrada"));
        // RN3: Solo confirma si la cita está pendiente
        if (!"PENDIENTE".equals(cita.getEstado())) {
            throw new RuntimeException("Solo se pueden confirmar citas que esten pendientes");
        }
        cita.confirmar();
        return citaRepository.save(cita);
    }

    /**
     * Cancelacion de citas por usuario (RN5)
     */
    @Transactional
    public Cita cancelarCitaUsuario(Long citaId, Usuario usuario) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no fue encontrada"));
        // RN8: Solo puede cancelar su propia cita
        if (!cita.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tiene permiso para cancelar esta cita");
        }
        // RN5: Solo puede cancelarse si la fecha aún no ha pasado
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime fechaHoraCita = LocalDateTime.of(
                cita.getEspacio().getFecha(),
                cita.getEspacio().getHoraInicio());
        if (fechaHoraCita.isBefore(ahora)) {
            throw new RuntimeException("No se puede cancelar una cita cuya fecha ya pasó");
        }
        // RN3: Solo puede cancelar si la cita esta PENDIENTE o CONFIRMADA
        if (!"PENDIENTE".equals(cita.getEstado()) && !"CONFIRMADA".equals(cita.getEstado())) {
            throw new RuntimeException("Solo se pueden cancelar citas pendientes o confirmadas");
        }
        cancelarCitaInternamente(cita, "Cancelada por el usuario");
        return citaRepository.save(cita);
    }

    /**
     * Cancelacion de citas por administrador (Sin restricciones)
     */
    @Transactional
    public Cita cancelarCitaAdmin(Long citaId, String motivo) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no fue encontrada"));
        // RN3: Solo se puede cancelar si está PENDIENTE, CONFIRMADA o AUSENTE
        if (!"PENDIENTE".equals(cita.getEstado()) && !"CONFIRMADA".equals(cita.getEstado())
                && !"AUSENTE".equals(cita.getEstado())) {
            throw new RuntimeException(
                    "Solo se pueden cancelar citas pendientes, confirmadas o si el paciente quedó ausente");
        }
        cancelarCitaInternamente(cita, motivo != null ? motivo : "Cancelada por el administrador");
        return citaRepository.save(cita);
    }

    /**
     * Marcar cita como Presente
     */
    @Transactional
    public Cita marcarComoPresente(Long citaId) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no fue encontrada"));
        // RN3: Solo se marca como presente si esta pendiente o confirmada
        if (!"PENDIENTE".equals(cita.getEstado()) && !"CONFIRMADA".equals(cita.getEstado())) {
            throw new RuntimeException(
                    "Solo se pueden marcar como presentes citas pendientes o confirmadas");
        }
        cita.marcarComoPresente();
        return citaRepository.save(cita);
    }

    /**
     * Marcar cita como Ausente
     */
    @Transactional
    public Cita marcarComoAusente(Long citaId, String motivo) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no fue encontrada"));
        if (!"PENDIENTE".equals(cita.getEstado()) && !"CONFIRMADA".equals(cita.getEstado())) {
            throw new RuntimeException(
                    "Solo se pueden marcar como ausente citas pendientes o confirmadas");
        }
        cita.marcarComoAusente(motivo);
        // RN7: Liberar el espacio inmediatamente
        EspacioCita espacio = cita.getEspacio();
        espacio.setOcupado(false);
        espacioCitaRepository.save(espacio);
        return citaRepository.save(cita);
    }

    private void cancelarCitaInternamente(Cita cita, String motivo) {
        cita.cancelar(motivo);
        // RN7: Liberar el espacio inmediatamente
        EspacioCita espacio = cita.getEspacio();
        espacio.setOcupado(false);
        espacioCitaRepository.save(espacio);
    }

    // Métodos de consulta
    public List<Cita> getCitasPorUsuario(Long usuarioId) {
        return citaRepository.findByUsuarioId(usuarioId);
    }

    public List<Cita> getCitasPorUsuarioYEstado(Long usuarioId, String estado) {
        return citaRepository.findByUsuarioIdAndEstado(usuarioId, estado);
    }

    public List<Cita> getCitasPorMedico(Long medicoId) {
        return citaRepository.findByMedicoId(medicoId);
    }

    public List<Cita> getCitasPorEstado(String estado) {
        return citaRepository.findByEstado(estado);
    }

    public List<Cita> getAllCitas() {
        return citaRepository.findAll();
    }

    public Optional<Cita> getCitaById(Long id) {
        return citaRepository.findById(id);
    }

    @Transactional
    public Cita marcarComoCompletada(Long citaId) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no fue encontrada"));
        // RN3: Solo se puede marcar como completada si está CONFIRMADA o PRESENTE
        if (!"CONFIRMADA".equals(cita.getEstado()) && !"PRESENTE".equals(cita.getEstado())) {
            throw new RuntimeException(
                    "Solo se pueden marcar como completadas citas confirmadas o presentes");
        }
        cita.marcarComoCompletada();
        return citaRepository.save(cita);
    }

    /**
     * /**
     * Filtra citas según múltiples criterios combinables
     * Implementa lógica de filtrado dinámico sin consultas JPQL complejas
     */
    public List<Cita> filtrarCitas(LocalDate fechaInicio, LocalDate fechaFin,
            Long medicoId, String especialidad, String estado) {
        List<Cita> todas = citaRepository.findAll();

        return todas.stream()
                .filter(c -> fechaInicio == null || !c.getEspacio().getFecha().isBefore(fechaInicio))
                .filter(c -> fechaFin == null || !c.getEspacio().getFecha().isAfter(fechaFin))
                .filter(c -> medicoId == null || c.getMedico().getId().equals(medicoId))
                .filter(c -> especialidad == null || especialidad.isEmpty() ||
                        c.getMedico().getEspecialidad().equalsIgnoreCase(especialidad))
                .filter(c -> estado == null || estado.isEmpty() || c.getEstado().equals(estado))
                .sorted(Comparator.comparing((Cita c) -> c.getEspacio().getFecha())
                        .thenComparing((Cita c) -> c.getEspacio().getHoraInicio()))
                .collect(Collectors.toList());
    }

    /**
     * Genera contenido CSV fiel a los datos filtrados mostrados en pantalla.
     */
    public String generarCSV(List<Cita> citas) {
        StringBuilder sb = new StringBuilder();

        // 1. Agregar BOM UTF-8 para que Excel reconozca caracteres especiales (ñ,
        // acentos, etc.)
        sb.append("\uFEFF");

        // 2. Encabezado con metadatos
        sb.append("\"Reporte de Citas Hospitalarias\"\n");
        sb.append("\"Fecha de generación: ").append(java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\"\n");
        sb.append("\"Total de registros: ").append(citas.size()).append("\"\n");
        sb.append("\n"); // Línea en blanco separadora

        // 3. Encabezados de columnas
        sb.append(
                "\"ID\",\"Paciente\",\"Cédula\",\"Médico\",\"Especialidad\",\"Fecha\",\"Hora Inicio\",\"Hora Fin\",\"Estado\",\"Fecha de Creación\"\n");

        // 4. Datos de cada cita
        for (Cita c : citas) {
            sb.append(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                    escapeCSV(String.valueOf(c.getId())),
                    escapeCSV(c.getUsuario().getNombre()),
                    escapeCSV(c.getUsuario().getCedula()),
                    escapeCSV(c.getMedico().getNombre()),
                    escapeCSV(c.getMedico().getEspecialidad()),
                    escapeCSV(c.getEspacio().getFecha().toString()),
                    escapeCSV(c.getEspacio().getHoraInicio().toString()),
                    escapeCSV(c.getEspacio().getHoraFin().toString()),
                    escapeCSV(c.getEstado()),
                    escapeCSV(c.getFechaCreacion() != null ? c.getFechaCreacion()
                            .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "")));
        }

        return sb.toString();
    }

    /**
     * Escapa caracteres especiales para CSV:
     * - Duplica comillas dobles internas
     * - Maneja saltos de línea y comas dentro del texto
     */
    private String escapeCSV(String value) {
        if (value == null) {
            return "";
        }
        // Reemplazar comillas dobles por dos comillas dobles
        return value.replace("\"", "\"\"");
    }
}
