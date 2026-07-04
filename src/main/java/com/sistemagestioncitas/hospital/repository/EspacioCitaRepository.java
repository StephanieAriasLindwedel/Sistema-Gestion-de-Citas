package com.sistemagestioncitas.hospital.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sistemagestioncitas.hospital.model.EspacioCita;

@Repository
public interface EspacioCitaRepository extends JpaRepository<EspacioCita, Long> {

}
