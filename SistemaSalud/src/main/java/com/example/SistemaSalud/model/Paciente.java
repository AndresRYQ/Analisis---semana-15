package com.example.SistemaSalud.model;

public record Paciente(Long id, String nombre, String dni, String edad, String tipoSeguro, String riesgo) {

    public Paciente(String nombre, String dni, String edad, String tipoSeguro, String riesgo) {
        this(null, nombre, dni, edad, tipoSeguro, riesgo);
    }
}
