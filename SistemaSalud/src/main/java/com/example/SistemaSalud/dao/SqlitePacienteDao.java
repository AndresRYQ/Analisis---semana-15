package com.example.SistemaSalud.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.SistemaSalud.entity.PacienteEntity;

@Repository
public class SqlitePacienteDao implements PacienteDao {

    private final JdbcTemplate jdbcTemplate;

    public SqlitePacienteDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<PacienteEntity> findAll() {
        return jdbcTemplate.query("""
                SELECT id, nombre, dni, edad, tipo_seguro, riesgo
                FROM pacientes
                ORDER BY nombre
                """, this::mapPaciente);
    }

    @Override
    public Optional<PacienteEntity> findByDni(String dni) {
        List<PacienteEntity> pacientes = jdbcTemplate.query("""
                SELECT id, nombre, dni, edad, tipo_seguro, riesgo
                FROM pacientes
                WHERE dni = ?
                """, this::mapPaciente, dni);
        return pacientes.stream().findFirst();
    }

    @Override
    public PacienteEntity getPacienteDemo() {
        return jdbcTemplate.query("""
                SELECT id, nombre, dni, edad, tipo_seguro, riesgo
                FROM pacientes
                ORDER BY id
                LIMIT 1
                """, this::mapPaciente).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No hay pacientes registrados en SQLite."));
    }

    @Override
    public PacienteEntity save(PacienteEntity paciente) {
        jdbcTemplate.update("""
                INSERT INTO pacientes(nombre, dni, edad, tipo_seguro, riesgo)
                VALUES (?, ?, ?, ?, ?)
                """, paciente.getNombre(), paciente.getDni(), paciente.getEdad(), paciente.getTipoSeguro(),
                paciente.getRiesgo());
        Long id = jdbcTemplate.queryForObject("SELECT last_insert_rowid()", Long.class);
        paciente.setId(id);
        return paciente;
    }

    @Override
    public PacienteEntity update(PacienteEntity paciente) {
        jdbcTemplate.update("""
                UPDATE pacientes
                SET nombre = ?, edad = ?, tipo_seguro = ?, riesgo = ?
                WHERE dni = ?
                """, paciente.getNombre(), paciente.getEdad(), paciente.getTipoSeguro(), paciente.getRiesgo(),
                paciente.getDni());
        return paciente;
    }

    @Override
    public void deleteByDni(String dni) {
        jdbcTemplate.update("DELETE FROM pacientes WHERE dni = ?", dni);
    }

    private PacienteEntity mapPaciente(ResultSet rs, int rowNum) throws SQLException {
        return new PacienteEntity(
                rs.getLong("id"),
                rs.getString("nombre"),
                rs.getString("dni"),
                rs.getInt("edad"),
                rs.getString("tipo_seguro"),
                rs.getString("riesgo"));
    }
}
