package com.example.SistemaSalud.model;

public record Medico(Long id, String nombre, String especialidad, String disponibilidad, String ubicacion,
        String detalle, boolean disponibleHoy) {

    public Medico(String nombre, String especialidad, String disponibilidad, String ubicacion, String detalle) {
        this(null, nombre, especialidad, disponibilidad, ubicacion, detalle, false);
    }
}
