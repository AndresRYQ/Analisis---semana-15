package com.example.SistemaSalud.dao;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import com.example.SistemaSalud.entity.AlertaEntity;
import com.example.SistemaSalud.entity.AuditoriaEntity;
import com.example.SistemaSalud.entity.RolEntity;
import com.example.SistemaSalud.entity.UsuarioEntity;

public class InMemorySeguridadDao implements SeguridadDao {

    private final AtomicLong auditoriaSequence = new AtomicLong(4);
    private final UsuarioEntity usuarioSesion = new UsuarioEntity(1L, "Juan Perez", "Paciente", "JP",
            "Asegurado activo");
    private final List<AlertaEntity> alertas = List.of(
            new AlertaEntity(1L, "Cita por confirmar", "Cardiologia - 06 Jul", "Media"),
            new AlertaEntity(2L, "Historia actualizada", "Nuevo diagnostico agregado", "Alta"));
    private final List<RolEntity> roles = List.of(
            new RolEntity(1L, "Paciente", "Consulta citas, confirma turnos y revisa informacion permitida."),
            new RolEntity(2L, "Medico", "Consulta y actualiza historias clinicas de pacientes atendidos."),
            new RolEntity(3L, "Administrativo", "Gestiona agendas, disponibilidad y registros de citas."),
            new RolEntity(4L, "Administrador", "Controla usuarios, permisos, auditorias y configuracion."));
    private final List<AuditoriaEntity> auditorias = new CopyOnWriteArrayList<>(List.of(
            new AuditoriaEntity(1L, LocalDateTime.of(2026, 7, 4, 8, 12), "Medico", "Medico",
                    "Apertura de historia clinica", "Autorizado"),
            new AuditoriaEntity(2L, LocalDateTime.of(2026, 7, 4, 9, 35), "Administrativo", "Administrativo",
                    "Reprogramacion de cita", "Autorizado"),
            new AuditoriaEntity(3L, LocalDateTime.of(2026, 7, 4, 10, 20), "Paciente", "Paciente",
                    "Consulta de proxima cita", "Autorizado"),
            new AuditoriaEntity(4L, LocalDateTime.of(2026, 7, 4, 11, 5), "Usuario externo", "Invitado",
                    "Intento de acceso a historia", "Bloqueado")));

    @Override
    public UsuarioEntity getUsuarioSesion() {
        return usuarioSesion;
    }

    @Override
    public java.util.Optional<UsuarioEntity> findUsuarioByCredentials(String username, String password) {
        if ("admin".equals(username) && "admin123".equals(password)) {
            return java.util.Optional.of(new UsuarioEntity(10L, "admin", "admin123", "Administrador HIS",
                    "Administrador", "AD", "Sesion activa", true));
        }
        return java.util.Optional.empty();
    }

    @Override
    public boolean existsUsername(String username) {
        return "admin".equalsIgnoreCase(username) || "paciente".equalsIgnoreCase(username);
    }

    @Override
    public UsuarioEntity saveUsuario(UsuarioEntity usuario) {
        usuario.setId(99L);
        return usuario;
    }

    @Override
    public List<UsuarioEntity> findUsuarios() {
        return List.of(usuarioSesion);
    }

    @Override
    public List<AlertaEntity> findAlertas() {
        return alertas;
    }

    @Override
    public List<RolEntity> findRoles() {
        return roles;
    }

    @Override
    public List<AuditoriaEntity> findAuditoriasRecientes() {
        return auditorias.stream()
                .sorted(Comparator.comparing(AuditoriaEntity::getFechaHora))
                .toList();
    }

    @Override
    public AuditoriaEntity saveAuditoria(AuditoriaEntity auditoria) {
        auditoria.setId(auditoriaSequence.incrementAndGet());
        auditorias.add(auditoria);
        return auditoria;
    }
}
