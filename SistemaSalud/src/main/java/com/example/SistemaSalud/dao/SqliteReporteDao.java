package com.example.SistemaSalud.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.SistemaSalud.entity.RequerimientoEntity;

@Repository
public class SqliteReporteDao implements ReporteDao {

    private final JdbcTemplate jdbcTemplate;

    public SqliteReporteDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<RequerimientoEntity> findRequerimientosPriorizados() {
        return jdbcTemplate.query("""
                SELECT id, codigo, prioridad, descripcion
                FROM requerimientos
                ORDER BY CASE prioridad WHEN 'Alta' THEN 1 WHEN 'Media' THEN 2 ELSE 3 END, codigo
                """, this::mapRequerimiento);
    }

    @Override
    public Map<String, Long> countCitasPorEstado() {
        return countGrouped("SELECT estado AS etiqueta, COUNT(*) AS total FROM citas GROUP BY estado ORDER BY total DESC");
    }

    @Override
    public Map<String, Long> countCitasPorEspecialidad() {
        return countGrouped("""
                SELECT especialidad AS etiqueta, COUNT(*) AS total
                FROM citas
                GROUP BY especialidad
                ORDER BY total DESC
                """);
    }

    @Override
    public Map<String, Long> countCitasPorMedico() {
        return countGrouped("""
                SELECT COALESCE(m.nombre, 'Sin medico asignado') AS etiqueta, COUNT(c.id) AS total
                FROM citas c
                LEFT JOIN medicos m ON m.id = c.medico_id
                GROUP BY etiqueta
                ORDER BY total DESC
                """);
    }

    @Override
    public long countPacientes() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM pacientes", Long.class);
        return count == null ? 0 : count;
    }

    @Override
    public long countCanceladas() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM citas WHERE estado = 'Cancelada'", Long.class);
        return count == null ? 0 : count;
    }

    private Map<String, Long> countGrouped(String sql) {
        return jdbcTemplate.query(sql, (rs, rowNum) -> Map.entry(rs.getString("etiqueta"), rs.getLong("total")))
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (first, second) -> first,
                        java.util.LinkedHashMap::new));
    }

    private RequerimientoEntity mapRequerimiento(ResultSet rs, int rowNum) throws SQLException {
        return new RequerimientoEntity(
                rs.getLong("id"),
                rs.getString("codigo"),
                rs.getString("prioridad"),
                rs.getString("descripcion"));
    }
}
