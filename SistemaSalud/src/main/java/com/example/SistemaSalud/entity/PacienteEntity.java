package com.example.SistemaSalud.entity;

public class PacienteEntity {

    private Long id;
    private String nombre;
    private String dni;
    private int edad;
    private String tipoSeguro;
    private String riesgo;

    public PacienteEntity() {
    }

    public PacienteEntity(Long id, String nombre, String dni, int edad, String tipoSeguro, String riesgo) {
        this.id = id;
        this.nombre = nombre;
        this.dni = dni;
        this.edad = edad;
        this.tipoSeguro = tipoSeguro;
        this.riesgo = riesgo;
    }

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

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getTipoSeguro() {
        return tipoSeguro;
    }

    public void setTipoSeguro(String tipoSeguro) {
        this.tipoSeguro = tipoSeguro;
    }

    public String getRiesgo() {
        return riesgo;
    }

    public void setRiesgo(String riesgo) {
        this.riesgo = riesgo;
    }
}
