package com.example.SistemaSalud.entity;

public class TurnoEntity {

    private Long id;
    private String dni;
    private String citaCodigo;
    private String codigo;
    private String especialidad;
    private String ubicacion;
    private String estado;

    public TurnoEntity() {
    }

    public TurnoEntity(Long id, String codigo, String especialidad, String ubicacion, String estado) {
        this.id = id;
        this.codigo = codigo;
        this.especialidad = especialidad;
        this.ubicacion = ubicacion;
        this.estado = estado;
    }

    public TurnoEntity(Long id, String dni, String citaCodigo, String codigo, String especialidad, String ubicacion,
            String estado) {
        this(id, codigo, especialidad, ubicacion, estado);
        this.dni = dni;
        this.citaCodigo = citaCodigo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getCitaCodigo() {
        return citaCodigo;
    }

    public void setCitaCodigo(String citaCodigo) {
        this.citaCodigo = citaCodigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
