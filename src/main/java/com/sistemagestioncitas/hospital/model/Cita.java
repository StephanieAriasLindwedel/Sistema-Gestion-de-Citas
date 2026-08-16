package com.sistemagestioncitas.hospital.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "citas")

public class Cita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "espacio_cita_id", nullable = false)
    private EspacioCita espacioCita;
    @Column(nullable = false)
    private LocalDateTime fechaHora;
    @Column(nullable = false)
    private String estado;
    @Column(nullable = false)
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaConfirmacion;
    private LocalDateTime fechaCancelacion;
    private String motivoCancelacion;

    public Cita() {
        this.fechaCreacion = LocalDateTime.now();
        this.estado = "PENDIENTE";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public EspacioCita getEspacio() {
        return espacioCita;
    }

    public void setEspacio(EspacioCita espacio) {
        this.espacioCita = espacio;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaConfirmacion() {
        return fechaConfirmacion;
    }

    public void setFechaConfirmacion(LocalDateTime fechaConfirmacion) {
        this.fechaConfirmacion = fechaConfirmacion;
    }

    public LocalDateTime getFechaCancelacion() {
        return fechaCancelacion;
    }

    public void setFechaCancelacion(LocalDateTime fechaCancelacion) {
        this.fechaCancelacion = fechaCancelacion;
    }

    public String getMotivoCancelacion() {
        return motivoCancelacion;
    }

    public void setMotivoCancelacion(String motivoCancelacion) {
        this.motivoCancelacion = motivoCancelacion;
    }

    public void confirmar() {
        if (!this.estado.equals("PENDIENTE")) {
            throw new IllegalStateException("Solo citas pendientes pueden confirmarse");
        }
        this.estado = "CONFIRMADA";
        this.fechaConfirmacion = LocalDateTime.now();
    }

    public void cancelar(String motivo) {
        if (this.estado.equals("CANCELADA")) {
            throw new IllegalStateException("La cita ya está cancelada");
        }
        this.estado = "CANCELADA";
        this.fechaCancelacion = LocalDateTime.now();
        this.motivoCancelacion = motivo;
    }

    public void marcarComoPresente() {
        this.estado = "PRESENTE";
        this.fechaConfirmacion = LocalDateTime.now();
    }

    public void marcarComoAusente(String motivo) {
        this.estado = "AUSENTE";
        this.motivoCancelacion = (motivo != null && !motivo.isEmpty()) ? motivo : "El paciente no se presentó";
        this.fechaCancelacion = LocalDateTime.now();
    }

    public void marcarComoCompletada() {
        if (!this.estado.equals("CONFIRMADA") && !this.estado.equals("PRESENTE")) {
            throw new IllegalStateException("Solo citas confirmadas o presentes pueden marcarse como completadas");
        }
        this.estado = "COMPLETADA";
    }
}
