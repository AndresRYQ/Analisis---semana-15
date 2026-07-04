package com.example.SistemaSalud.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.example.SistemaSalud.entity.CitaEntity;
import com.example.SistemaSalud.entity.SlotEntity;

public interface CitaDao {

    List<CitaEntity> findAll();

    List<SlotEntity> findSlots();

    Optional<CitaEntity> findByCodigo(String codigo);

    List<CitaEntity> findByDni(String dni);

    CitaEntity save(CitaEntity cita);

    void updateEstado(String codigo, String estado);

    void updateFechaHoraYMedico(String codigo, LocalDate fecha, String fechaEtiqueta, java.time.LocalTime hora,
            Long medicoId, String estado);

    boolean existsByDniEspecialidadAndFecha(String dni, String especialidad, LocalDate fecha);

    boolean existsByMedicoFechaHora(Long medicoId, LocalDate fecha, java.time.LocalTime hora, String ignoredCodigo);

    long countByDni(String dni);

    long countByMedicoId(Long medicoId);
}
