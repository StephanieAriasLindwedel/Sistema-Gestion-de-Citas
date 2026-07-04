package com.sistemagestioncitas.hospital.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    @Column(unique = true, nullable = false)
    private String cedula;
    @Column(unique = true, nullable = false)
    private String correo;
    private String contacto;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String rol;
    private boolean activo;

    public Usuario() {
        this.activo = true;
    }

    public Long getid() {
        return id;
    }

    public void setid(Long id) {
        this.id = id;
    }

    public String getnombre() {
        return nombre;
    }

    public void setnombre(String nombre) {
        this.nombre = nombre;
    }

    public String getcedula() {
        return cedula;
    }

    public void setcedula(String cedula) {
        this.cedula = cedula;
    }

    public String getcorreo() {
        return correo;
    }

    public void setcorreo(String correo) {
        this.correo = correo;
    }

    public String getcontacto() {
        return contacto;
    }

    public void setcontacto(String contacto) {
        this.contacto = contacto;
    }

    public String getpassword() {
        return password;
    }

    public void setpassword(String password) {
        this.password = password;
    }

    public String getrol() {
        return rol;
    }

    public void setrol(String rol) {
        this.rol = rol;
    }

    public boolean isactivo() {
        return activo;
    }

    public void setactivo(boolean activo) {
        this.activo = activo;
    }
}
