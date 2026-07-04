package com.sistemagestioncitas.hospital.model;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "espacios_cita")
public class EspacioCita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate fecha;
    private LocalTime horainicio;
    private LocalTime horafin;
    private boolean ocupado = false;
    @ManyToOne
    @JoinColumn(name = "medico_id")
    private Medico medico;

    public Long getid() {
        return id;
    }

    public void setid(Long id) {
        this.id = id;
    }

    public LocalDate getfecha() {
        return fecha;
    }

    public void setfecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime gethorainicio() {
        return horainicio;
    }

    public void sethorainicio(LocalTime horainicio) {
        this.horainicio = horainicio;
    }

    public LocalTime gethorafin() {
        return horafin;
    }

    public void sethorafin(LocalTime horafin) {
        this.horafin = horafin;
    }

    public boolean isocupado() {
        return ocupado;
    }

    public void setocupado(boolean ocupado) {
        this.ocupado = ocupado;
    }

    public Medico getmedico() {
        return medico;
    }

    public void setmedico(Medico medico) {
        this.medico = medico;
    }

}
