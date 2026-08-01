package com.sistemagestioncitas.hospital.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistemagestioncitas.hospital.model.EspacioCita;
import com.sistemagestioncitas.hospital.repository.EspacioCitaRepository;

@Service
public class EspacioCitaService {

    @Autowired
    private EspacioCitaRepository espacioCitaRepository;

    public List<EspacioCita> listarTodos() {
        return espacioCitaRepository.findAll();
    }

    public List<EspacioCita> listarPorMedico(Long medicoId) {
        return espacioCitaRepository.findByMedicoId(medicoId);
    }

    public List<EspacioCita> listarDisponibles() {
        return espacioCitaRepository.findByOcupadoFalse();
    }

    // Método que conecta con el repository de cita
    public List<EspacioCita> listarDisponiblesPorMedico(Long medicoId) {
        return espacioCitaRepository.findByMedicoIdAndOcupadoFalse(medicoId);
    }

    public Optional<EspacioCita> obtenerPorId(Long id) {
        return espacioCitaRepository.findById(id);
    }

    public EspacioCita guardar(EspacioCita espacio) {
        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();
        // 1- Validad la fecha no sea pasada
        if (espacio.getFecha().isBefore(hoy)) {
            throw new RuntimeException("No se puede crear un espacio de disponibilidad en una fecha pasada");
        }
        // 2- Si es el dia de hoy, se valida que la hora de inicio no haya pasado
        if (espacio.getFecha().isEqual(hoy) && espacio.getHoraInicio().isBefore(ahora)) {
            throw new RuntimeException(
                    "No se puede crear un espacio para el día de hoy con una hora de inicio que ya pasó");
        }
        // 3- Validar que la hora de fin sea posterior a la hora de inicio
        if (!espacio.getHoraFin().isAfter(espacio.getHoraInicio())) {
            throw new RuntimeException(
                    "No se puede crear un espacio para el día de hoy con una hora de fin previa a la hora de inicio");
        }
        return espacioCitaRepository.save(espacio);
    }

    public void ocupar(Long id) {
        EspacioCita espacio = espacioCitaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ESPACIO DE CITA NO ENCONTRADO"));
        espacio.setOcupado(true);
        espacioCitaRepository.save(espacio);
    }
}
