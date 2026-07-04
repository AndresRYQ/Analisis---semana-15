package com.example.SistemaSalud.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.SistemaSalud.entity.CitaEntity;
import com.example.SistemaSalud.entity.SlotEntity;

@Repository
public class SqliteCitaDao implements CitaDao {

    private final JdbcTemplate jdbcTemplate;

    public SqliteCitaDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<CitaEntity> findAll() {
        return jdbcTemplate.query("""
                SELECT id, paciente_id, medico_id, codigo, paciente, dni, especialidad, fecha_programada, fecha_etiqueta,
                       hora_programada, estado, canal, prioridad, motivo
                FROM citas
                ORDER BY fecha_programada, hora_programada
                """, this::mapCita);
    }

    @Override
    public List<SlotEntity> findSlots() {
        return jdbcTemplate.query("""
                SELECT id, hora, especialidad, estado, disponible
                FROM slots
                ORDER BY hora
                """, this::mapSlot);
    }

    @Override
    public Optional<CitaEntity> findByCodigo(String codigo) {
        List<CitaEntity> citas = jdbcTemplate.query("""
                SELECT id, paciente_id, medico_id, codigo, paciente, dni, especialidad, fecha_programada, fecha_etiqueta,
                       hora_programada, estado, canal, prioridad, motivo
                FROM citas
                WHERE lower(codigo) = lower(?)
                """, this::mapCita, codigo);
        return citas.stream().findFirst();
    }

    @Override
    public List<CitaEntity> findByDni(String dni) {
        return jdbcTemplate.query("""
                SELECT id, paciente_id, medico_id, codigo, paciente, dni, especialidad, fecha_programada,
                       fecha_etiqueta, hora_programada, estado, canal, prioridad, motivo
                FROM citas
                WHERE dni = ?
                ORDER BY fecha_programada, hora_programada
                """, this::mapCita, dni);
    }

    @Override
    public CitaEntity save(CitaEntity cita) {
        if (cita.getId() == null) {
            long nextId = nextId();
            cita.setId(nextId);
            cita.setCodigo("C-" + nextId);
            jdbcTemplate.update("""
                    INSERT INTO citas(paciente_id, medico_id, codigo, paciente, dni, especialidad, fecha_programada, fecha_etiqueta,
                                      hora_programada, estado, canal, prioridad, motivo)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """,
                    cita.getPacienteId(), cita.getMedicoId(), cita.getCodigo(), cita.getPaciente(), cita.getDni(), cita.getEspecialidad(),
                    cita.getFechaProgramada().toString(), cita.getFechaEtiqueta(), cita.getHoraProgramada().toString(),
                    cita.getEstado(), cita.getCanal(), cita.getPrioridad(), cita.getMotivo());
            return cita;
        }

        jdbcTemplate.update("""
                UPDATE citas
                SET paciente_id = ?, medico_id = ?, paciente = ?, dni = ?, especialidad = ?, fecha_programada = ?, fecha_etiqueta = ?,
                    hora_programada = ?, estado = ?, canal = ?, prioridad = ?, motivo = ?
                WHERE codigo = ?
                """,
                cita.getPacienteId(), cita.getMedicoId(), cita.getPaciente(), cita.getDni(), cita.getEspecialidad(),
                cita.getFechaProgramada().toString(), cita.getFechaEtiqueta(), cita.getHoraProgramada().toString(),
                cita.getEstado(), cita.getCanal(), cita.getPrioridad(), cita.getMotivo(), cita.getCodigo());
        return cita;
    }

    @Override
    public void updateEstado(String codigo, String estado) {
        jdbcTemplate.update("UPDATE citas SET estado = ? WHERE codigo = ?", estado, codigo);
    }

    @Override
    public void updateFechaHoraYMedico(String codigo, LocalDate fecha, String fechaEtiqueta, LocalTime hora,
            Long medicoId, String estado) {
        jdbcTemplate.update("""
                UPDATE citas
                SET fecha_programada = ?, fecha_etiqueta = ?, hora_programada = ?, medico_id = ?, estado = ?
                WHERE codigo = ?
                """, fecha.toString(), fechaEtiqueta, hora.toString(), medicoId, estado, codigo);
    }

    @Override
    public boolean existsByDniEspecialidadAndFecha(String dni, String especialidad, LocalDate fecha) {
        Integer count = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM citas
                WHERE dni = ? AND lower(especialidad) = lower(?) AND fecha_programada = ?
                """, Integer.class, dni, especialidad, fecha.toString());
        return count != null && count > 0;
    }

    @Override
    public boolean existsByMedicoFechaHora(Long medicoId, LocalDate fecha, LocalTime hora, String ignoredCodigo) {
        Integer count = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM citas
                WHERE medico_id = ? AND fecha_programada = ? AND hora_programada = ?
                  AND lower(codigo) <> lower(?)
                  AND estado NOT IN ('Cancelada', 'Atendida')
                """, Integer.class, medicoId, fecha.toString(), hora.toString(), ignoredCodigo == null ? "" : ignoredCodigo);
        return count != null && count > 0;
    }

    @Override
    public long countByDni(String dni) {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM citas WHERE dni = ?", Long.class, dni);
        return count == null ? 0 : count;
    }

    @Override
    public long countByMedicoId(Long medicoId) {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM citas WHERE medico_id = ?", Long.class,
                medicoId);
        return count == null ? 0 : count;
    }

    private long nextId() {
        Long next = jdbcTemplate.queryForObject("SELECT COALESCE(MAX(id), 1027) + 1 FROM citas", Long.class);
        return next == null ? 1028L : next;
    }

    private CitaEntity mapCita(ResultSet rs, int rowNum) throws SQLException {
        return new CitaEntity(
                rs.getLong("id"),
                nullableLong(rs, "paciente_id"),
                nullableLong(rs, "medico_id"),
                rs.getString("codigo"),
                rs.getString("paciente"),
                rs.getString("dni"),
                rs.getString("especialidad"),
                LocalDate.parse(rs.getString("fecha_programada")),
                rs.getString("fecha_etiqueta"),
                LocalTime.parse(rs.getString("hora_programada")),
                rs.getString("estado"),
                rs.getString("canal"),
                rs.getString("prioridad"),
                rs.getString("motivo"));
    }

    private Long nullableLong(ResultSet rs, String columnName) throws SQLException {
        long value = rs.getLong(columnName);
        return rs.wasNull() ? null : value;
    }

    private SlotEntity mapSlot(ResultSet rs, int rowNum) throws SQLException {
        return new SlotEntity(
                rs.getLong("id"),
                LocalTime.parse(rs.getString("hora")),
                rs.getString("especialidad"),
                rs.getString("estado"),
                rs.getInt("disponible") == 1);
    }
}
