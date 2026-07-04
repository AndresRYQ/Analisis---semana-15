package com.example.SistemaSalud.dao;

import java.util.List;
import java.util.Optional;

import com.example.SistemaSalud.entity.PacienteEntity;

public interface PacienteDao {

    List<PacienteEntity> findAll();

    Optional<PacienteEntity> findByDni(String dni);

    PacienteEntity getPacienteDemo();

    PacienteEntity save(PacienteEntity paciente);

    PacienteEntity update(PacienteEntity paciente);

    void deleteByDni(String dni);
}
