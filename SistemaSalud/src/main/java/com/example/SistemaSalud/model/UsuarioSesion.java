package com.example.SistemaSalud.model;

public record UsuarioSesion(String nombre, String rol, String iniciales, String estado, String username) {

    public UsuarioSesion(String nombre, String rol, String iniciales, String estado) {
        this(nombre, rol, iniciales, estado, "");
    }
}
