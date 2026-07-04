package com.example.SistemaSalud.dao;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import com.example.SistemaSalud.entity.PacienteEntity;

public class InMemoryPacienteDao implements PacienteDao {

    private final List<PacienteEntity> pacientes = new CopyOnWriteArrayList<>(List.of(
            new PacienteEntity(1L, "Maria Torres Huaman", "45872163", 32, "Asegurada titular", "Bajo"),
            new PacienteEntity(2L, "Luis Ramirez Soto", "44556677", 58, "Asegurado titular", "Medio"),
            new PacienteEntity(3L, "Ana Paredes Leon", "77889900", 8, "Derechohabiente", "Bajo")));

    @Override
    public List<PacienteEntity> findAll() {
        return pacientes;
    }

    @Override
    public Optional<PacienteEntity> findByDni(String dni) {
        return pacientes.stream()
                .filter(paciente -> paciente.getDni().equals(dni))
                .findFirst();
    }

    @Override
    public PacienteEntity getPacienteDemo() {
        return pacientes.get(0);
    }

    @Override
    public PacienteEntity save(PacienteEntity paciente) {
        paciente.setId((long) pacientes.size() + 1);
        pacientes.add(paciente);
        return paciente;
    }

    @Override
    public PacienteEntity update(PacienteEntity paciente) {
        deleteByDni(paciente.getDni());
        pacientes.add(paciente);
        return paciente;
    }

    @Override
    public void deleteByDni(String dni) {
        pacientes.removeIf(paciente -> paciente.getDni().equals(dni));
    }
}
