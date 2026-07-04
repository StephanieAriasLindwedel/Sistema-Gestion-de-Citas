package com.sistemagestioncitas.hospital.service;

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

    private List<EspacioCita> listarTodos() {
        return espacioCitaRepository.findAll();
    }

    private List<EspacioCita> listarPorMedico(Long medicoId) {
        return espacioCitaRepository.findByMedicoId(medicoId);
    }

    private List<EspacioCita> listarDisponibles() {
        return espacioCitaRepository.findByOcupadoFalse();
    }

    public Optional<EspacioCita> obtenerPorId(Long id) {
        return espacioCitaRepository.findById(id);
    }

    public EspacioCita guardar(EspacioCita espacio) {
        return espacioCitaRepository.save(espacio);
    }

    public void ocupar(Long id) {
        EspacioCita espacio = espacioCitaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("USUARIO NO ENCONTRADO"));
        espacio.setocupado(true);
        espacioCitaRepository.save(espacio);
    }
}
