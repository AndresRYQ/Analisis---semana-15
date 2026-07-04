package com.example.SistemaSalud.entity;

import java.time.LocalDate;
import java.time.LocalTime;

public class CitaEntity {

    private Long id;
    private Long pacienteId;
    private Long medicoId;
    private String codigo;
    private String paciente;
    private String dni;
    private String especialidad;
    private LocalDate fechaProgramada;
    private String fechaEtiqueta;
    private LocalTime horaProgramada;
    private String estado;
    private String canal;
    private String prioridad;
    private String motivo;

    public CitaEntity() {
    }

    public CitaEntity(Long id, String codigo, String paciente, String dni, String especialidad,
            LocalDate fechaProgramada, String fechaEtiqueta, LocalTime horaProgramada, String estado, String canal,
            String prioridad, String motivo) {
        this.id = id;
        this.codigo = codigo;
        this.paciente = paciente;
        this.dni = dni;
        this.especialidad = especialidad;
        this.fechaProgramada = fechaProgramada;
        this.fechaEtiqueta = fechaEtiqueta;
        this.horaProgramada = horaProgramada;
        this.estado = estado;
        this.canal = canal;
        this.prioridad = prioridad;
        this.motivo = motivo;
    }

    public CitaEntity(Long id, Long pacienteId, Long medicoId, String codigo, String paciente, String dni,
            String especialidad, LocalDate fechaProgramada, String fechaEtiqueta, LocalTime horaProgramada,
            String estado, String canal, String prioridad, String motivo) {
        this(id, codigo, paciente, dni, especialidad, fechaProgramada, fechaEtiqueta, horaProgramada, estado, canal,
                prioridad, motivo);
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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getPaciente() {
        return paciente;
    }

    public void setPaciente(String paciente) {
        this.paciente = paciente;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public LocalDate getFechaProgramada() {
        return fechaProgramada;
    }

    public void setFechaProgramada(LocalDate fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    public String getFechaEtiqueta() {
        return fechaEtiqueta;
    }

    public void setFechaEtiqueta(String fechaEtiqueta) {
        this.fechaEtiqueta = fechaEtiqueta;
    }

    public LocalTime getHoraProgramada() {
        return horaProgramada;
    }

    public void setHoraProgramada(LocalTime horaProgramada) {
        this.horaProgramada = horaProgramada;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
