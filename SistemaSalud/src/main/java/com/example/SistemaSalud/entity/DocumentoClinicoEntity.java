package com.example.SistemaSalud.entity;

public class DocumentoClinicoEntity {

    private Long id;
    private Long pacienteId;
    private String codigo;
    private String pacienteDni;
    private String nombre;
    private String estado;

    public DocumentoClinicoEntity() {
    }

    public DocumentoClinicoEntity(Long id, String codigo, String pacienteDni, String nombre, String estado) {
        this.id = id;
        this.codigo = codigo;
        this.pacienteDni = pacienteDni;
        this.nombre = nombre;
        this.estado = estado;
    }

    public DocumentoClinicoEntity(Long id, Long pacienteId, String codigo, String pacienteDni, String nombre,
            String estado) {
        this(id, codigo, pacienteDni, nombre, estado);
        this.pacienteId = pacienteId;
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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getPacienteDni() {
        return pacienteDni;
    }

    public void setPacienteDni(String pacienteDni) {
        this.pacienteDni = pacienteDni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
