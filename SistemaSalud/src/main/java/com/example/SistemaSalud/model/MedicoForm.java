package com.example.SistemaSalud.model;

public class MedicoForm {

    private Long id;
    private String nombre;
    private String especialidad;
    private String disponibilidad;
    private String ubicacion;
    private String detalle;
    private boolean disponibleHoy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(String disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public boolean isDisponibleHoy() {
        return disponibleHoy;
    }

    public void setDisponibleHoy(boolean disponibleHoy) {
        this.disponibleHoy = disponibleHoy;
    }
}
