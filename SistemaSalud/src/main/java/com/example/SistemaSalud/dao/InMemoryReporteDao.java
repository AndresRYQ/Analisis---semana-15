package com.example.SistemaSalud.dao;

import java.util.List;
import java.util.Map;

import com.example.SistemaSalud.entity.RequerimientoEntity;

public class InMemoryReporteDao implements ReporteDao {

    private final List<RequerimientoEntity> requerimientos = List.of(
            new RequerimientoEntity(1L, "RF02", "Alta",
                    "Programar citas medicas desde web o kiosco digital."),
            new RequerimientoEntity(2L, "RF05", "Alta",
                    "Acceso del medico al historial clinico digital."),
            new RequerimientoEntity(3L, "RF11", "Alta",
                    "Evitar registros duplicados de pacientes e historias clinicas."),
            new RequerimientoEntity(4L, "RF14", "Alta",
                    "Registrar auditorias de acceso a informacion clinica."),
            new RequerimientoEntity(5L, "RNF04", "Alta",
                    "Abrir historias clinicas en menos de 3 segundos."),
            new RequerimientoEntity(6L, "RNF09", "Media",
                    "Funcionamiento en computadoras, tablets y kioscos."));

    @Override
    public List<RequerimientoEntity> findRequerimientosPriorizados() {
        return requerimientos;
    }

    @Override
    public Map<String, Long> countCitasPorEstado() {
        return Map.of("Pendiente", 1L, "Confirmada", 2L, "Reprogramable", 1L);
    }

    @Override
    public Map<String, Long> countCitasPorEspecialidad() {
        return Map.of("Medicina General", 1L, "Cardiologia", 1L, "Pediatria", 1L, "Odontologia", 1L);
    }

    @Override
    public Map<String, Long> countCitasPorMedico() {
        return Map.of("Dra. Sofia Ramirez", 1L, "Dr. Andres Molina", 1L);
    }

    @Override
    public long countPacientes() {
        return 3;
    }

    @Override
    public long countCanceladas() {
        return 0;
    }
}
