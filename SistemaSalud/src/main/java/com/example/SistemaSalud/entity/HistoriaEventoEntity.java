package com.example.SistemaSalud.entity;

import java.time.LocalDate;

public class HistoriaEventoEntity {

    private Long id;
    private Long pacienteId;
    private Long medicoId;
    private String pacienteDni;
    private LocalDate fecha;
    private String fechaEtiqueta;
    private String titulo;
    private String descripcion;
    private String profesional;
    private String estado;

    public HistoriaEventoEntity() {
    }

    public HistoriaEventoEntity(Long id, String pacienteDni, LocalDate fecha, String fechaEtiqueta, String titulo,
            String descripcion, String profesional, String estado) {
        this.id = id;
        this.pacienteDni = pacienteDni;
        this.fecha = fecha;
        this.fechaEtiqueta = fechaEtiqueta;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.profesional = profesional;
        this.estado = estado;
    }

    public HistoriaEventoEntity(Long id, Long pacienteId, Long medicoId, String pacienteDni, LocalDate fecha,
            String fechaEtiqueta, String titulo, String descripcion, String profesional, String estado) {
        this(id, pacienteDni, fecha, fechaEtiqueta, titulo, descripcion, profesional, estado);
        this.pacienteId = pacienteId;
        this.medicoId = medicoId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    public Long getMedicoId() {
        return medicoId;
    }

    public void setMedicoId(Long medicoId) {
        this.medicoId = medicoId;
    }

    public String getPacienteDni() {
        return pacienteDni;
    }

    public void setPacienteDni(String pacienteDni) {
        this.pacienteDni = pacienteDni;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getFechaEtiqueta() {
        return fechaEtiqueta;
    }

    public void setFechaEtiqueta(String fechaEtiqueta) {
        this.fechaEtiqueta = fechaEtiqueta;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getProfesional() {
        return profesional;
    }

    public void setProfesional(String profesional) {
        this.profesional = profesional;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
