package com.example.SistemaSalud.dao;

import java.util.List;
import java.util.Optional;

import com.example.SistemaSalud.entity.MedicoEntity;

public interface MedicoDao {

    List<MedicoEntity> findAll();

    List<String> findEspecialidades();

    Optional<MedicoEntity> findDisponibleByEspecialidad(String especialidad);

    Optional<MedicoEntity> findById(Long id);

    Optional<MedicoEntity> findPrincipalByEspecialidad(String especialidad);

    long countDisponiblesHoy();

    MedicoEntity save(MedicoEntity medico);

    MedicoEntity update(MedicoEntity medico);

    void deleteById(Long id);
}
