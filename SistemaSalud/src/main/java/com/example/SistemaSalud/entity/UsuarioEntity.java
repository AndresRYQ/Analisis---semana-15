package com.example.SistemaSalud.entity;

public class UsuarioEntity {

    private Long id;
    private String username;
    private String password;
    private String nombre;
    private String rol;
    private String iniciales;
    private String estado;
    private boolean activo = true;

    public UsuarioEntity() {
    }

    public UsuarioEntity(Long id, String nombre, String rol, String iniciales, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.rol = rol;
        this.iniciales = iniciales;
        this.estado = estado;
    }

    public UsuarioEntity(Long id, String username, String password, String nombre, String rol, String iniciales,
            String estado, boolean activo) {
        this(id, nombre, rol, iniciales, estado);
        this.username = username;
        this.password = password;
        this.activo = activo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getIniciales() {
        return iniciales;
    }

    public void setIniciales(String iniciales) {
        this.iniciales = iniciales;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
