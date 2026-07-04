package com.example.SistemaSalud.dao;

import java.util.List;
import java.util.Optional;

import com.example.SistemaSalud.entity.TurnoEntity;

public interface KioscoDao {

    List<TurnoEntity> findTurnosActivos();

    Optional<TurnoEntity> findByCodigo(String codigo);

    Optional<TurnoEntity> findByCitaCodigo(String citaCodigo);

    TurnoEntity save(TurnoEntity turno);

    long nextTurnoNumber();
}
