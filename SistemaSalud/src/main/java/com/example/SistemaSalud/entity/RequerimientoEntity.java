package com.example.SistemaSalud.entity;

public class RequerimientoEntity {

    private Long id;
    private String codigo;
    private String prioridad;
    private String descripcion;

    public RequerimientoEntity() {
    }

    public RequerimientoEntity(Long id, String codigo, String prioridad, String descripcion) {
        this.id = id;
        this.codigo = codigo;
        this.prioridad = prioridad;
        this.descripcion = descripcion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
