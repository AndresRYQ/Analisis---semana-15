package com.example.SistemaSalud.dao;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import com.example.SistemaSalud.entity.TurnoEntity;

public class InMemoryKioscoDao implements KioscoDao {

    private final List<TurnoEntity> turnos = new CopyOnWriteArrayList<>(List.of(
            new TurnoEntity(21L, "A-021", "Medicina General", "Consultorio 204", "Llamando"),
            new TurnoEntity(22L, "A-022", "Cardiologia", "Consultorio 118", "En espera"),
            new TurnoEntity(23L, "A-023", "Pediatria", "Consultorio 310", "Confirmado")));

    @Override
    public List<TurnoEntity> findTurnosActivos() {
        return turnos;
    }

    @Override
    public Optional<TurnoEntity> findByCodigo(String codigo) {
        return turnos.stream()
                .filter(turno -> turno.getCodigo().equalsIgnoreCase(codigo))
                .findFirst();
    }

    @Override
    public Optional<TurnoEntity> findByCitaCodigo(String citaCodigo) {
        return turnos.stream()
                .filter(turno -> citaCodigo != null && citaCodigo.equalsIgnoreCase(turno.getCitaCodigo()))
                .findFirst();
    }

    @Override
    public TurnoEntity save(TurnoEntity turno) {
        turnos.add(turno);
        return turno;
    }

    @Override
    public long nextTurnoNumber() {
        return turnos.size() + 1L;
    }
}
