package com.example.SistemaSalud.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.SistemaSalud.dao.HistoriaClinicaDao;
import com.example.SistemaSalud.dao.PacienteDao;
import com.example.SistemaSalud.entity.DocumentoClinicoEntity;
import com.example.SistemaSalud.entity.HistoriaEventoEntity;
import com.example.SistemaSalud.entity.PacienteEntity;
import com.example.SistemaSalud.model.HistoriaForm;
import com.example.SistemaSalud.entity.PacienteEntity;
import com.example.SistemaSalud.model.DocumentoClinico;
import com.example.SistemaSalud.model.HistoriaEvento;
import com.example.SistemaSalud.model.Paciente;

@Service
public class HistoriaClinicaService {

    private final PacienteDao pacienteDao;
    private final HistoriaClinicaDao historiaClinicaDao;

    public HistoriaClinicaService(PacienteDao pacienteDao, HistoriaClinicaDao historiaClinicaDao) {
        this.pacienteDao = pacienteDao;
        this.historiaClinicaDao = historiaClinicaDao;
    }

    public Paciente pacienteDemo() {
        return toPaciente(pacienteDao.getPacienteDemo());
    }

    public Paciente buscarPaciente(String dni) {
        return toPaciente(pacienteDao.findByDni(dni)
                .orElseThrow(() -> new IllegalArgumentException("No existe paciente con ese DNI.")));
    }

    public List<HistoriaEvento> historialDemo() {
        return historiaClinicaDao.findEventosByPacienteDni(pacienteDao.getPacienteDemo().getDni()).stream()
                .map(this::toEvento)
                .toList();
    }

    public List<HistoriaEvento> historialPorDni(String dni) {
        return historiaClinicaDao.findEventosByPacienteDni(dni).stream().map(this::toEvento).toList();
    }

    public List<DocumentoClinico> documentosDemo() {
        return historiaClinicaDao.findDocumentosByPacienteDni(pacienteDao.getPacienteDemo().getDni()).stream()
                .map(this::toDocumento)
                .toList();
    }

    public List<DocumentoClinico> documentosPorDni(String dni) {
        return historiaClinicaDao.findDocumentosByPacienteDni(dni).stream().map(this::toDocumento).toList();
    }

    public HistoriaEvento registrarEvento(HistoriaForm form) {
        if (isBlank(form.getPacienteDni()) || isBlank(form.getTitulo()) || isBlank(form.getDescripcion())
                || isBlank(form.getProfesional()) || isBlank(form.getEstado())) {
            throw new IllegalArgumentException("Completa todos los campos de la historia clinica.");
        }
        PacienteEntity paciente = pacienteDao.findByDni(form.getPacienteDni().trim())
                .orElseThrow(() -> new IllegalArgumentException("No existe paciente para registrar historia."));
        HistoriaEventoEntity evento = new HistoriaEventoEntity();
        evento.setPacienteId(paciente.getId());
        evento.setPacienteDni(paciente.getDni());
        evento.setFecha(java.time.LocalDate.now());
        evento.setFechaEtiqueta(formatDate(java.time.LocalDate.now()));
        evento.setTitulo(form.getTitulo().trim());
        evento.setDescripcion(form.getDescripcion().trim());
        evento.setProfesional(form.getProfesional().trim());
        evento.setEstado(form.getEstado().trim());
        return toEvento(historiaClinicaDao.saveEvento(evento));
    }

    public long contarHistoriasDisponibles() {
        return historiaClinicaDao.countHistoriasDisponibles();
    }

    private Paciente toPaciente(PacienteEntity paciente) {
        return new Paciente(paciente.getId(), paciente.getNombre(), paciente.getDni(), paciente.getEdad() + " anos",
                paciente.getTipoSeguro(), paciente.getRiesgo());
    }

    private HistoriaEvento toEvento(HistoriaEventoEntity evento) {
        return new HistoriaEvento(evento.getFechaEtiqueta(), evento.getTitulo(), evento.getDescripcion(),
                evento.getProfesional(), evento.getEstado());
    }

    private DocumentoClinico toDocumento(DocumentoClinicoEntity documento) {
        return new DocumentoClinico(documento.getCodigo(), documento.getNombre(), documento.getEstado());
    }

    private String formatDate(java.time.LocalDate date) {
        return "%02d %s %d".formatted(date.getDayOfMonth(), monthName(date.getMonthValue()), date.getYear());
    }

    private String monthName(int month) {
        return switch (month) {
            case 1 -> "Ene";
            case 2 -> "Feb";
            case 3 -> "Mar";
            case 4 -> "Abr";
            case 5 -> "May";
            case 6 -> "Jun";
            case 7 -> "Jul";
            case 8 -> "Ago";
            case 9 -> "Set";
            case 10 -> "Oct";
            case 11 -> "Nov";
            case 12 -> "Dic";
            default -> "";
        };
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
