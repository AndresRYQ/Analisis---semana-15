package com.example.SistemaSalud.entity;

public class AlertaEntity {

    private Long id;
    private String titulo;
    private String detalle;
    private String prioridad;

    public AlertaEntity() {
    }

    public AlertaEntity(Long id, String titulo, String detalle, String prioridad) {
        this.id = id;
        this.titulo = titulo;
        this.detalle = detalle;
        this.prioridad = prioridad;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }
}
