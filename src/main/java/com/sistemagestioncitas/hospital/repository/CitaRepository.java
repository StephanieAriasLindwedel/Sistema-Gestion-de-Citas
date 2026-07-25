package com.sistemagestioncitas.hospital.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sistemagestioncitas.hospital.model.Cita;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    // Buscar citas por usuario
    List<Cita> findByUsuarioId(Long usuarioId);

    // Buscar citas por médico
    List<Cita> findByMedicoId(Long medicoId);

    // Buscar citas por estado
    List<Cita> findByEstado(String estado);

    // Buscar citas por usuario y estado
    List<Cita> findByUsuarioIdAndEstado(Long usuarioId, String estado);

    // RN1 y RN2: Verificar si un espacio ya tiene una cita activa
    boolean existsByEspacioCitaIdAndEstadoIn(Long espacioCitaId, List<String> estados);

    // Buscar citas por médico y estado
    List<Cita> findByMedicoIdAndEstado(Long medicoId, String estado);

    // Buscar citas en un rango de fechas
    @Query("SELECT c FROM Cita c WHERE c.espacioCita.fecha BETWEEN :fechaInicio AND :fechaFin")
    List<Cita> findByRangoFechas(@Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    // RN6: Verificar si un usuario tiene citas que se solapan en el mismo horario
    @Query("SELECT c FROM Cita c WHERE c.usuario.id = :usuarioId " +
            "AND c.estado IN ('PENDIENTE', 'CONFIRMADA') " +
            "AND c.espacioCita.fecha = :fecha " +
            "AND ((" +
            "   (:horaInicio >= c.espacioCita.horaInicio AND :horaInicio < c.espacioCita.horaFin) OR " +
            "   (:horaFin > c.espacioCita.horaInicio AND :horaFin <= c.espacioCita.horaFin) OR " +
            "   (:horaInicio <= c.espacioCita.horaInicio AND :horaFin >= c.espacioCita.horaFin)" +
            "))")
    List<Cita> findByUsuarioIdAndHorarioSolapado(
            @Param("usuarioId") Long usuarioId,
            @Param("fecha") LocalDate fecha,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin);
}
