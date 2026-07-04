package com.example.SistemaSalud.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.SistemaSalud.dao.SeguridadDao;
import com.example.SistemaSalud.entity.AlertaEntity;
import com.example.SistemaSalud.entity.AuditoriaEntity;
import com.example.SistemaSalud.entity.RolEntity;
import com.example.SistemaSalud.entity.UsuarioEntity;
import com.example.SistemaSalud.model.Alerta;
import com.example.SistemaSalud.model.Auditoria;
import com.example.SistemaSalud.model.RolSistema;
import com.example.SistemaSalud.model.UsuarioSesion;

@Service
public class SeguridadService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final SeguridadDao seguridadDao;

    public SeguridadService(SeguridadDao seguridadDao) {
        this.seguridadDao = seguridadDao;
    }

    public UsuarioSesion usuarioSesion() {
        return toModel(seguridadDao.getUsuarioSesion());
    }

    public UsuarioSesion login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Ingresa usuario y contrasena.");
        }
        return seguridadDao.findUsuarioByCredentials(username.trim(), password)
                .map(this::toModel)
                .orElseThrow(() -> new IllegalArgumentException("Credenciales incorrectas."));
    }

    public List<Alerta> alertas() {
        return seguridadDao.findAlertas().stream()
                .map(this::toModel)
                .toList();
    }

    public List<RolSistema> roles() {
        return seguridadDao.findRoles().stream()
                .map(this::toModel)
                .toList();
    }

    public List<UsuarioSesion> usuariosSistema() {
        return seguridadDao.findUsuarios().stream().map(this::toModel).toList();
    }

    public List<Auditoria> auditoriasRecientes() {
        return seguridadDao.findAuditoriasRecientes().stream()
                .map(this::toModel)
                .toList();
    }

    public void registrarAuditoria(String usuario, String rol, String accion, String resultado) {
        seguridadDao.saveAuditoria(new AuditoriaEntity(null, LocalDateTime.now(), usuario, rol, accion, resultado));
    }

    private UsuarioSesion toModel(UsuarioEntity usuario) {
        return new UsuarioSesion(usuario.getNombre(), usuario.getRol(), usuario.getIniciales(), usuario.getEstado(),
                usuario.getUsername());
    }

    private Alerta toModel(AlertaEntity alerta) {
        return new Alerta(alerta.getTitulo(), alerta.getDetalle(), alerta.getPrioridad());
    }

    private RolSistema toModel(RolEntity rol) {
        return new RolSistema(rol.getNombre(), rol.getDescripcion());
    }

    private Auditoria toModel(AuditoriaEntity auditoria) {
        return new Auditoria(TIME_FORMATTER.format(auditoria.getFechaHora()), auditoria.getUsuario(),
                auditoria.getAccion(), auditoria.getResultado());
    }
}
