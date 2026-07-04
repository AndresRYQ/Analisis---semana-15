package com.example.SistemaSalud.dao;

import java.util.List;
import java.util.Map;

import com.example.SistemaSalud.entity.RequerimientoEntity;

public interface ReporteDao {

    List<RequerimientoEntity> findRequerimientosPriorizados();

    Map<String, Long> countCitasPorEstado();

    Map<String, Long> countCitasPorEspecialidad();

    Map<String, Long> countCitasPorMedico();

    long countPacientes();

    long countCanceladas();
}
