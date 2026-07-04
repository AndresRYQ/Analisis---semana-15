package com.example.SistemaSalud.dao;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.example.SistemaSalud.entity.DocumentoClinicoEntity;
import com.example.SistemaSalud.entity.HistoriaEventoEntity;

public class InMemoryHistoriaClinicaDao implements HistoriaClinicaDao {

    private final List<HistoriaEventoEntity> eventos = new CopyOnWriteArrayList<>(List.of(
            new HistoriaEventoEntity(1L, "45872163", LocalDate.of(2026, 7, 3), "03 Jul 2026",
                    "Consulta de medicina general",
                    "Control preventivo con indicacion de analisis de sangre.", "Dra. Sofia Ramirez", "Finalizada"),
            new HistoriaEventoEntity(2L, "45872163", LocalDate.of(2026, 6, 18), "18 Jun 2026",
                    "Diagnostico respiratorio",
                    "Registro de sintomas leves y tratamiento ambulatorio.", "Dr. Luis Huaman", "Actualizada"),
            new HistoriaEventoEntity(3L, "45872163", LocalDate.of(2026, 5, 2), "02 May 2026",
                    "Control cardiologico",
                    "Electrocardiograma sin hallazgos de alarma.", "Dr. Andres Molina", "Archivada"),
            new HistoriaEventoEntity(4L, "45872163", LocalDate.of(2026, 4, 10), "10 Abr 2026",
                    "Vacunacion preventiva",
                    "Registro de vacuna y proxima fecha de refuerzo.", "Enfermeria", "Validada")));

    private final List<DocumentoClinicoEntity> documentos = List.of(
            new DocumentoClinicoEntity(1L, "HC-2026-001", "45872163", "Historia clinica digital",
                    "Actualizada hoy"),
            new DocumentoClinicoEntity(2L, "LAB-2026-118", "45872163", "Resultados de laboratorio",
                    "Pendiente de revision"),
            new DocumentoClinicoEntity(3L, "AUD-2026-040", "45872163", "Auditoria de accesos",
                    "3 accesos autorizados"));

    @Override
    public List<HistoriaEventoEntity> findEventosByPacienteDni(String dni) {
        return eventos.stream()
                .filter(evento -> evento.getPacienteDni().equals(dni))
                .sorted(Comparator.comparing(HistoriaEventoEntity::getFecha).reversed())
                .toList();
    }

    @Override
    public List<DocumentoClinicoEntity> findDocumentosByPacienteDni(String dni) {
        return documentos.stream()
                .filter(documento -> documento.getPacienteDni().equals(dni))
                .toList();
    }

    @Override
    public HistoriaEventoEntity saveEvento(HistoriaEventoEntity evento) {
        evento.setId((long) eventos.size() + 1);
        eventos.add(evento);
        return evento;
    }

    @Override
    public long countHistoriasDisponibles() {
        return documentos.stream()
                .filter(documento -> documento.getCodigo().startsWith("HC-"))
                .count();
    }
}
