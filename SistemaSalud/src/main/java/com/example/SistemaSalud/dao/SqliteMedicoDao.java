package com.example.SistemaSalud.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.SistemaSalud.entity.MedicoEntity;

@Repository
public class SqliteMedicoDao implements MedicoDao {

    private final JdbcTemplate jdbcTemplate;

    public SqliteMedicoDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<MedicoEntity> findAll() {
        return jdbcTemplate.query("""
                SELECT id, nombre, especialidad, disponibilidad, ubicacion, detalle, disponible_hoy
                FROM medicos
                ORDER BY especialidad, nombre
                """, this::mapMedico);
    }

    @Override
    public List<String> findEspecialidades() {
        return jdbcTemplate.queryForList("""
                SELECT DISTINCT especialidad
                FROM medicos
                ORDER BY especialidad
                """, String.class);
    }

    @Override
    public Optional<MedicoEntity> findDisponibleByEspecialidad(String especialidad) {
        List<MedicoEntity> medicos = jdbcTemplate.query("""
                SELECT id, nombre, especialidad, disponibilidad, ubicacion, detalle, disponible_hoy
                FROM medicos
                WHERE disponible_hoy = 1 AND lower(especialidad) = lower(?)
                ORDER BY nombre
                LIMIT 1
                """, this::mapMedico, especialidad);
        return medicos.stream().findFirst();
    }

    @Override
    public Optional<MedicoEntity> findById(Long id) {
        List<MedicoEntity> medicos = jdbcTemplate.query("""
                SELECT id, nombre, especialidad, disponibilidad, ubicacion, detalle, disponible_hoy
                FROM medicos
                WHERE id = ?
                """, this::mapMedico, id);
        return medicos.stream().findFirst();
    }

    @Override
    public Optional<MedicoEntity> findPrincipalByEspecialidad(String especialidad) {
        List<MedicoEntity> medicos = jdbcTemplate.query("""
                SELECT id, nombre, especialidad, disponibilidad, ubicacion, detalle, disponible_hoy
                FROM medicos
                WHERE lower(especialidad) = lower(?)
                ORDER BY disponible_hoy DESC, nombre
                LIMIT 1
                """, this::mapMedico, especialidad);
        return medicos.stream().findFirst();
    }

    @Override
    public long countDisponiblesHoy() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM medicos WHERE disponible_hoy = 1", Long.class);
        return count == null ? 0 : count;
    }

    @Override
    public MedicoEntity save(MedicoEntity medico) {
        jdbcTemplate.update("""
                INSERT INTO medicos(nombre, especialidad, disponibilidad, ubicacion, detalle, disponible_hoy)
                VALUES (?, ?, ?, ?, ?, ?)
                """, medico.getNombre(), medico.getEspecialidad(), medico.getDisponibilidad(), medico.getUbicacion(),
                medico.getDetalle(), medico.isDisponibleHoy() ? 1 : 0);
        Long id = jdbcTemplate.queryForObject("SELECT last_insert_rowid()", Long.class);
        medico.setId(id);
        return medico;
    }

    @Override
    public MedicoEntity update(MedicoEntity medico) {
        jdbcTemplate.update("""
                UPDATE medicos
                SET nombre = ?, especialidad = ?, disponibilidad = ?, ubicacion = ?, detalle = ?,
                    disponible_hoy = ?
                WHERE id = ?
                """, medico.getNombre(), medico.getEspecialidad(), medico.getDisponibilidad(), medico.getUbicacion(),
                medico.getDetalle(), medico.isDisponibleHoy() ? 1 : 0, medico.getId());
        return medico;
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM medicos WHERE id = ?", id);
    }

    private MedicoEntity mapMedico(ResultSet rs, int rowNum) throws SQLException {
        return new MedicoEntity(
                rs.getLong("id"),
                rs.getString("nombre"),
                rs.getString("especialidad"),
                rs.getString("disponibilidad"),
                rs.getString("ubicacion"),
                rs.getString("detalle"),
                rs.getInt("disponible_hoy") == 1);
    }
}
