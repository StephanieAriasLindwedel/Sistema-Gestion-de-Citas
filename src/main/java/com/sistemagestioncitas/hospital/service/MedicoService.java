package com.sistemagestioncitas.hospital.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sistemagestioncitas.hospital.model.Medico;
import com.sistemagestioncitas.hospital.repository.MedicoRepository;

@Service
public class MedicoService {

    @Autowired
    private MedicoRepository medicoRepository;

    public List<Medico> listarTodos() {
        return medicoRepository.findByActivoTrue();
    }

    public Optional<Medico> obtenerPorId(Long id) {
        return medicoRepository.findById(id);
    }

    public Medico guardar(Medico medico) {
        if (medico.getId() == null) {
            medico.setActivo(true); // Aseguramos que el médico sea activo al crearlo
        }
        return medicoRepository.save(medico);
    }

    @Transactional
    public void eliminar(Long id) {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado"));

        // ✅ BORRADO LÓGICO: No usamos medicoRepository.deleteById(id);
        medico.setActivo(false);
        medicoRepository.save(medico);
    }
}
