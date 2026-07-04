package com.example.SistemaSalud.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.SistemaSalud.entity.DocumentoClinicoEntity;
import com.example.SistemaSalud.entity.HistoriaEventoEntity;

@Repository
public class SqliteHistoriaClinicaDao implements HistoriaClinicaDao {

    private final JdbcTemplate jdbcTemplate;

    public SqliteHistoriaClinicaDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<HistoriaEventoEntity> findEventosByPacienteDni(String dni) {
        return jdbcTemplate.query("""
                SELECT id, paciente_id, medico_id, paciente_dni, fecha, fecha_etiqueta, titulo, descripcion, profesional, estado
                FROM historias_eventos
                WHERE paciente_dni = ?
                ORDER BY fecha DESC
                """, this::mapEvento, dni);
    }

    @Override
    public List<DocumentoClinicoEntity> findDocumentosByPacienteDni(String dni) {
        return jdbcTemplate.query("""
                SELECT id, paciente_id, codigo, paciente_dni, nombre, estado
                FROM documentos_clinicos
                WHERE paciente_dni = ?
                ORDER BY codigo
                """, this::mapDocumento, dni);
    }

    @Override
    public long countHistoriasDisponibles() {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM documentos_clinicos WHERE codigo LIKE 'HC-%'", Long.class);
        return count == null ? 0 : count;
    }

    @Override
    public HistoriaEventoEntity saveEvento(HistoriaEventoEntity evento) {
        jdbcTemplate.update("""
                INSERT INTO historias_eventos(paciente_id, medico_id, paciente_dni, fecha, fecha_etiqueta,
                                              titulo, descripcion, profesional, estado)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """, evento.getPacienteId(), evento.getMedicoId(), evento.getPacienteDni(),
                evento.getFecha().toString(), evento.getFechaEtiqueta(), evento.getTitulo(), evento.getDescripcion(),
                evento.getProfesional(), evento.getEstado());
        Long id = jdbcTemplate.queryForObject("SELECT last_insert_rowid()", Long.class);
        evento.setId(id);
        return evento;
    }

    private HistoriaEventoEntity mapEvento(ResultSet rs, int rowNum) throws SQLException {
        return new HistoriaEventoEntity(
                rs.getLong("id"),
                nullableLong(rs, "paciente_id"),
                nullableLong(rs, "medico_id"),
                rs.getString("paciente_dni"),
                LocalDate.parse(rs.getString("fecha")),
                rs.getString("fecha_etiqueta"),
                rs.getString("titulo"),
                rs.getString("descripcion"),
                rs.getString("profesional"),
                rs.getString("estado"));
    }

    private DocumentoClinicoEntity mapDocumento(ResultSet rs, int rowNum) throws SQLException {
        return new DocumentoClinicoEntity(
                rs.getLong("id"),
                nullableLong(rs, "paciente_id"),
                rs.getString("codigo"),
                rs.getString("paciente_dni"),
                rs.getString("nombre"),
                rs.getString("estado"));
    }

    private Long nullableLong(ResultSet rs, String columnName) throws SQLException {
        long value = rs.getLong(columnName);
        return rs.wasNull() ? null : value;
    }
}
