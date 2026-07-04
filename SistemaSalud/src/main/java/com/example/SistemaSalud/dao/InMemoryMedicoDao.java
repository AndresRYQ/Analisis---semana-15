package com.example.SistemaSalud.dao;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import com.example.SistemaSalud.entity.MedicoEntity;

public class InMemoryMedicoDao implements MedicoDao {

    private final List<MedicoEntity> medicos = new CopyOnWriteArrayList<>(List.of(
            new MedicoEntity(1L, "Dra. Sofia Ramirez", "Medicina General", "Disponible hoy", "Teleconsulta",
                    "14 pacientes atendidos", true),
            new MedicoEntity(2L, "Dr. Andres Molina", "Cardiologia", "Alta demanda", "Hospital Rebagliati",
                    "Agenda hasta 18:00", true),
            new MedicoEntity(3L, "Dra. Isabel Quispe", "Psicologia", "Disponible", "Virtual",
                    "Seguimiento preventivo", true),
            new MedicoEntity(4L, "Dr. Luis Huaman", "Pediatria", "Disponible manana", "Policlinico Central",
                    "Atencion familiar", false),
            new MedicoEntity(5L, "Dra. Carla Rojas", "Medicina Interna", "Disponible", "Hospital Almenara",
                    "Casos cronicos", true),
            new MedicoEntity(6L, "Dra. Elena Torres", "Odontologia", "Turno tarde", "Centro Odontologico",
                    "Control preventivo", true)));

    @Override
    public List<MedicoEntity> findAll() {
        return medicos.stream()
                .sorted(Comparator.comparing(MedicoEntity::getEspecialidad))
                .toList();
    }

    @Override
    public List<String> findEspecialidades() {
        return medicos.stream()
                .map(MedicoEntity::getEspecialidad)
                .distinct()
                .sorted()
                .toList();
    }

    @Override
    public Optional<MedicoEntity> findDisponibleByEspecialidad(String especialidad) {
        return medicos.stream()
                .filter(MedicoEntity::isDisponibleHoy)
                .filter(medico -> medico.getEspecialidad().equalsIgnoreCase(especialidad))
                .findFirst();
    }

    @Override
    public Optional<MedicoEntity> findById(Long id) {
        return medicos.stream().filter(medico -> medico.getId().equals(id)).findFirst();
    }

    @Override
    public Optional<MedicoEntity> findPrincipalByEspecialidad(String especialidad) {
        return medicos.stream()
                .filter(medico -> medico.getEspecialidad().equalsIgnoreCase(especialidad))
                .findFirst();
    }

    @Override
    public long countDisponiblesHoy() {
        return medicos.stream()
                .filter(MedicoEntity::isDisponibleHoy)
                .count();
    }

    @Override
    public MedicoEntity save(MedicoEntity medico) {
        medico.setId((long) medicos.size() + 1);
        medicos.add(medico);
        return medico;
    }

    @Override
    public MedicoEntity update(MedicoEntity medico) {
        deleteById(medico.getId());
        medicos.add(medico);
        return medico;
    }

    @Override
    public void deleteById(Long id) {
        medicos.removeIf(medico -> medico.getId().equals(id));
    }
}
