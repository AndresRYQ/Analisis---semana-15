package com.example.SistemaSalud.dao;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import com.example.SistemaSalud.entity.CitaEntity;
import com.example.SistemaSalud.entity.SlotEntity;

public class InMemoryCitaDao implements CitaDao {

    private final AtomicLong sequence = new AtomicLong(1027);
    private final List<CitaEntity> citas = new CopyOnWriteArrayList<>(List.of(
            new CitaEntity(1024L, "C-1024", "Maria Torres Huaman", "45872163", "Medicina General",
                    LocalDate.of(2026, 7, 4), "Hoy", LocalTime.of(14, 30), "Confirmada", "Virtual", "Normal", ""),
            new CitaEntity(1025L, "C-1025", "Luis Ramirez Soto", "44556677", "Cardiologia",
                    LocalDate.of(2026, 7, 6), "06 Jul", LocalTime.of(11, 0), "Pendiente", "Presencial",
                    "Paciente cronico", "Control cardiologico"),
            new CitaEntity(1026L, "C-1026", "Ana Paredes Leon", "77889900", "Pediatria",
                    LocalDate.of(2026, 7, 7), "07 Jul", LocalTime.of(9, 10), "Confirmada", "Kiosco", "Normal",
                    "Control pediatrico"),
            new CitaEntity(1027L, "C-1027", "Carlos Medina Flores", "66778899", "Odontologia",
                    LocalDate.of(2026, 7, 9), "09 Jul", LocalTime.of(16, 20), "Reprogramable", "Presencial",
                    "Normal", "Control preventivo")));

    private final List<SlotEntity> slots = List.of(
            new SlotEntity(1L, LocalTime.of(8, 30), "Medicina General", "Disponible", true),
            new SlotEntity(2L, LocalTime.of(10, 0), "Cardiologia", "Alta demanda", true),
            new SlotEntity(3L, LocalTime.of(12, 15), "Pediatria", "Disponible", true),
            new SlotEntity(4L, LocalTime.of(15, 40), "Odontologia", "Confirmacion kiosco", true),
            new SlotEntity(5L, LocalTime.of(17, 0), "Psicologia", "Disponible", true));

    @Override
    public List<CitaEntity> findAll() {
        return citas.stream()
                .sorted(Comparator.comparing(CitaEntity::getFechaProgramada)
                        .thenComparing(CitaEntity::getHoraProgramada))
                .toList();
    }

    @Override
    public List<SlotEntity> findSlots() {
        return slots;
    }

    @Override
    public Optional<CitaEntity> findByCodigo(String codigo) {
        return citas.stream()
                .filter(cita -> cita.getCodigo().equalsIgnoreCase(codigo))
                .findFirst();
    }

    @Override
    public List<CitaEntity> findByDni(String dni) {
        return citas.stream()
                .filter(cita -> cita.getDni().equals(dni))
                .toList();
    }

    @Override
    public CitaEntity save(CitaEntity cita) {
        long nextId = sequence.incrementAndGet();
        cita.setId(nextId);
        cita.setCodigo("C-" + nextId);
        citas.add(cita);
        return cita;
    }

    @Override
    public void updateEstado(String codigo, String estado) {
        findByCodigo(codigo).ifPresent(cita -> cita.setEstado(estado));
    }

    @Override
    public void updateFechaHoraYMedico(String codigo, LocalDate fecha, String fechaEtiqueta, LocalTime hora,
            Long medicoId, String estado) {
        findByCodigo(codigo).ifPresent(cita -> {
            cita.setFechaProgramada(fecha);
            cita.setFechaEtiqueta(fechaEtiqueta);
            cita.setHoraProgramada(hora);
            cita.setMedicoId(medicoId);
            cita.setEstado(estado);
        });
    }

    @Override
    public boolean existsByDniEspecialidadAndFecha(String dni, String especialidad, LocalDate fecha) {
        return citas.stream().anyMatch(cita -> cita.getDni().equals(dni)
                && cita.getEspecialidad().equalsIgnoreCase(especialidad)
                && cita.getFechaProgramada().equals(fecha));
    }

    @Override
    public boolean existsByMedicoFechaHora(Long medicoId, LocalDate fecha, LocalTime hora, String ignoredCodigo) {
        return citas.stream().anyMatch(cita -> medicoId != null && medicoId.equals(cita.getMedicoId())
                && cita.getFechaProgramada().equals(fecha)
                && cita.getHoraProgramada().equals(hora)
                && !cita.getCodigo().equalsIgnoreCase(ignoredCodigo));
    }

    @Override
    public long countByDni(String dni) {
        return citas.stream().filter(cita -> cita.getDni().equals(dni)).count();
    }

    @Override
    public long countByMedicoId(Long medicoId) {
        return citas.stream().filter(cita -> medicoId != null && medicoId.equals(cita.getMedicoId())).count();
    }
}
