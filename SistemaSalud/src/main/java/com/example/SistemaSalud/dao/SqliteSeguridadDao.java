package com.example.SistemaSalud.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.SistemaSalud.entity.AlertaEntity;
import com.example.SistemaSalud.entity.AuditoriaEntity;
import com.example.SistemaSalud.entity.RolEntity;
import com.example.SistemaSalud.entity.UsuarioEntity;

@Repository
public class SqliteSeguridadDao implements SeguridadDao {

    private final JdbcTemplate jdbcTemplate;

    public SqliteSeguridadDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UsuarioEntity getUsuarioSesion() {
        return jdbcTemplate.query("""
                SELECT id, username, password, nombre, rol, iniciales, estado, activo
                FROM usuarios
                WHERE activo = 1
                ORDER BY CASE rol WHEN 'Administrador' THEN 1 WHEN 'Administrativo' THEN 2 ELSE 3 END, id
                LIMIT 1
                """, this::mapUsuario).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No hay usuarios registrados en SQLite."));
    }

    @Override
    public Optional<UsuarioEntity> findUsuarioByCredentials(String username, String password) {
        List<UsuarioEntity> usuarios = jdbcTemplate.query("""
                SELECT id, username, password, nombre, rol, iniciales, estado, activo
                FROM usuarios
                WHERE lower(username) = lower(?) AND password = ? AND activo = 1
                """, this::mapUsuario, username, password);
        return usuarios.stream().findFirst();
    }

    @Override
    public List<UsuarioEntity> findUsuarios() {
        return jdbcTemplate.query("""
                SELECT id, username, password, nombre, rol, iniciales, estado, activo
                FROM usuarios
                ORDER BY rol, nombre
                """, this::mapUsuario);
    }

    @Override
    public List<AlertaEntity> findAlertas() {
        return jdbcTemplate.query("""
                SELECT id, titulo, detalle, prioridad
                FROM alertas
                ORDER BY id
                """, this::mapAlerta);
    }

    @Override
    public List<RolEntity> findRoles() {
        return jdbcTemplate.query("""
                SELECT id, nombre, descripcion
                FROM roles
                ORDER BY id
                """, this::mapRol);
    }

    @Override
    public List<AuditoriaEntity> findAuditoriasRecientes() {
        return jdbcTemplate.query("""
                SELECT id, fecha_hora, usuario, rol, accion, resultado
                FROM auditorias
                ORDER BY fecha_hora
                LIMIT 20
                """, this::mapAuditoria);
    }

    @Override
    public AuditoriaEntity saveAuditoria(AuditoriaEntity auditoria) {
        jdbcTemplate.update("""
                INSERT INTO auditorias(fecha_hora, usuario, rol, accion, resultado)
                VALUES (?, ?, ?, ?, ?)
                """, auditoria.getFechaHora().toString(), auditoria.getUsuario(), auditoria.getRol(),
                auditoria.getAccion(), auditoria.getResultado());
        Long id = jdbcTemplate.queryForObject("SELECT last_insert_rowid()", Long.class);
        auditoria.setId(id);
        return auditoria;
    }

    private UsuarioEntity mapUsuario(ResultSet rs, int rowNum) throws SQLException {
        return new UsuarioEntity(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("nombre"),
                rs.getString("rol"),
                rs.getString("iniciales"),
                rs.getString("estado"),
                rs.getInt("activo") == 1);
    }

    private AlertaEntity mapAlerta(ResultSet rs, int rowNum) throws SQLException {
        return new AlertaEntity(
                rs.getLong("id"),
                rs.getString("titulo"),
                rs.getString("detalle"),
                rs.getString("prioridad"));
    }

    private RolEntity mapRol(ResultSet rs, int rowNum) throws SQLException {
        return new RolEntity(
                rs.getLong("id"),
                rs.getString("nombre"),
                rs.getString("descripcion"));
    }

    private AuditoriaEntity mapAuditoria(ResultSet rs, int rowNum) throws SQLException {
        return new AuditoriaEntity(
                rs.getLong("id"),
                LocalDateTime.parse(rs.getString("fecha_hora")),
                rs.getString("usuario"),
                rs.getString("rol"),
                rs.getString("accion"),
                rs.getString("resultado"));
    }
}
