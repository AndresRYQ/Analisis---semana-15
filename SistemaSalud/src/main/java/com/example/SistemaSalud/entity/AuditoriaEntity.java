package com.example.SistemaSalud.entity;

import java.time.LocalDateTime;

public class AuditoriaEntity {

    private Long id;
    private LocalDateTime fechaHora;
    private String usuario;
    private String rol;
    private String accion;
    private String resultado;

    public AuditoriaEntity() {
    }

    public AuditoriaEntity(Long id, LocalDateTime fechaHora, String usuario, String rol, String accion,
            String resultado) {
        this.id = id;
        this.fechaHora = fechaHora;
        this.usuario = usuario;
        this.rol = rol;
        this.accion = accion;
        this.resultado = resultado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }
}
