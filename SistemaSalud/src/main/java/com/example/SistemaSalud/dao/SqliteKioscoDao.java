package com.example.SistemaSalud.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.SistemaSalud.entity.TurnoEntity;

@Repository
public class SqliteKioscoDao implements KioscoDao {

    private final JdbcTemplate jdbcTemplate;

    public SqliteKioscoDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<TurnoEntity> findTurnosActivos() {
        return jdbcTemplate.query("""
                SELECT id, dni, cita_codigo, codigo, especialidad, ubicacion, estado
                FROM turnos
                ORDER BY codigo
                """, this::mapTurno);
    }

    @Override
    public Optional<TurnoEntity> findByCodigo(String codigo) {
        List<TurnoEntity> turnos = jdbcTemplate.query("""
                SELECT id, dni, cita_codigo, codigo, especialidad, ubicacion, estado
                FROM turnos
                WHERE lower(codigo) = lower(?)
                """, this::mapTurno, codigo);
        return turnos.stream().findFirst();
    }

    @Override
    public Optional<TurnoEntity> findByCitaCodigo(String citaCodigo) {
        List<TurnoEntity> turnos = jdbcTemplate.query("""
                SELECT id, dni, cita_codigo, codigo, especialidad, ubicacion, estado
                FROM turnos
                WHERE lower(cita_codigo) = lower(?)
                """, this::mapTurno, citaCodigo);
        return turnos.stream().findFirst();
    }

    @Override
    public TurnoEntity save(TurnoEntity turno) {
        if (findByCodigo(turno.getCodigo()).isPresent()) {
            jdbcTemplate.update("""
                    UPDATE turnos
                    SET dni = ?, cita_codigo = ?, especialidad = ?, ubicacion = ?, estado = ?
                    WHERE codigo = ?
                    """, turno.getDni(), turno.getCitaCodigo(), turno.getEspecialidad(), turno.getUbicacion(),
                    turno.getEstado(), turno.getCodigo());
            return turno;
        }

        jdbcTemplate.update("""
                INSERT INTO turnos(dni, cita_codigo, codigo, especialidad, ubicacion, estado)
                VALUES (?, ?, ?, ?, ?, ?)
                """, turno.getDni(), turno.getCitaCodigo(), turno.getCodigo(), turno.getEspecialidad(),
                turno.getUbicacion(), turno.getEstado());
        return turno;
    }

    @Override
    public long nextTurnoNumber() {
        Long next = jdbcTemplate.queryForObject("SELECT COALESCE(MAX(id), 0) + 1 FROM turnos", Long.class);
        return next == null ? 1L : next;
    }

    private TurnoEntity mapTurno(ResultSet rs, int rowNum) throws SQLException {
        return new TurnoEntity(
                rs.getLong("id"),
                rs.getString("dni"),
                rs.getString("cita_codigo"),
                rs.getString("codigo"),
                rs.getString("especialidad"),
                rs.getString("ubicacion"),
                rs.getString("estado"));
    }
}
