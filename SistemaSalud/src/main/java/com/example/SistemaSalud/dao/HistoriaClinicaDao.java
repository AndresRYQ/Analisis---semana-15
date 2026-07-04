package com.example.SistemaSalud.dao;

import java.util.List;

import com.example.SistemaSalud.entity.DocumentoClinicoEntity;
import com.example.SistemaSalud.entity.HistoriaEventoEntity;

public interface HistoriaClinicaDao {

    List<HistoriaEventoEntity> findEventosByPacienteDni(String dni);

    List<DocumentoClinicoEntity> findDocumentosByPacienteDni(String dni);

    HistoriaEventoEntity saveEvento(HistoriaEventoEntity evento);

    long countHistoriasDisponibles();
}
