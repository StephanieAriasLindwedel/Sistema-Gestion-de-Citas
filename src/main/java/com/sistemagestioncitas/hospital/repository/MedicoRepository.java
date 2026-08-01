package com.sistemagestioncitas.hospital.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import com.sistemagestioncitas.hospital.model.Medico;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {
    List<Medico> findByActivoTrue();
}
