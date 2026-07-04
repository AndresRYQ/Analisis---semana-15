package com.example.SistemaSalud.entity;

import java.time.LocalTime;

public class SlotEntity {

    private Long id;
    private LocalTime hora;
    private String especialidad;
    private String estado;
    private boolean disponible;

    public SlotEntity() {
    }

    public SlotEntity(Long id, LocalTime hora, String especialidad, String estado, boolean disponible) {
        this.id = id;
        this.hora = hora;
        this.especialidad = especialidad;
        this.estado = estado;
        this.disponible = disponible;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
}
