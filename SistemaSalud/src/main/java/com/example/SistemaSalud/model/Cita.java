package com.example.SistemaSalud.model;

public record Cita(String codigo, String paciente, String especialidad, String fecha, String hora, String estado,
        String canal) {
}
