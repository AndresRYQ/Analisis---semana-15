package com.example.SistemaSalud.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.SistemaSalud.dao.CitaDao;
import com.example.SistemaSalud.dao.MedicoDao;
import com.example.SistemaSalud.dao.PacienteDao;
import com.example.SistemaSalud.entity.CitaEntity;
import com.example.SistemaSalud.entity.MedicoEntity;
import com.example.SistemaSalud.entity.PacienteEntity;
import com.example.SistemaSalud.entity.SlotEntity;
import com.example.SistemaSalud.model.Cita;
import com.example.SistemaSalud.model.Slot;
import com.example.SistemaSalud.model.SolicitudCita;

@Service
public class CitaService {

    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final CitaDao citaDao;
    private final MedicoDao medicoDao;
    private final PacienteDao pacienteDao;

    public CitaService(CitaDao citaDao, MedicoDao medicoDao, PacienteDao pacienteDao) {
        this.citaDao = citaDao;
        this.medicoDao = medicoDao;
        this.pacienteDao = pacienteDao;
    }

    public List<Cita> listarAgenda() {
        return citaDao.findAll().stream()
                .map(this::toModel)
                .toList();
    }

    public List<Slot> listarSlotsSugeridos() {
        return citaDao.findSlots().stream()
                .map(slot -> new Slot(formatHour(slot.getHora()), slot.getEspecialidad(), slot.getEstado()))
                .toList();
    }

    public Cita registrarSolicitud(SolicitudCita solicitud) {
        validarSolicitud(solicitud);
        LocalDate fecha = LocalDate.parse(solicitud.getFecha());

        if (citaDao.existsByDniEspecialidadAndFecha(solicitud.getDni().trim(), solicitud.getEspecialidad().trim(),
                fecha)) {
            throw new IllegalArgumentException("Ya existe una cita registrada para ese DNI, especialidad y fecha.");
        }

        SlotEntity slot = seleccionarSlot(solicitud.getEspecialidad());
        MedicoEntity medico = medicoDao.findPrincipalByEspecialidad(solicitud.getEspecialidad())
                .orElseThrow(() -> new IllegalArgumentException("No hay medico registrado para esa especialidad."));
        if (citaDao.existsByMedicoFechaHora(medico.getId(), fecha, slot.getHora(), "")) {
            throw new IllegalArgumentException("El medico ya tiene una cita registrada en ese horario.");
        }
        PacienteEntity paciente = pacienteDao.findByDni(solicitud.getDni().trim())
                .orElseGet(() -> pacienteDao.save(new PacienteEntity(null, solicitud.getNombre().trim(),
                        solicitud.getDni().trim(), 0, "Por verificar", "Bajo")));
        String estado = calcularEstadoInicial(solicitud.getEspecialidad(), solicitud.getPrioridad());

        CitaEntity cita = new CitaEntity();
        cita.setPacienteId(paciente.getId());
        cita.setMedicoId(medico.getId());
        cita.setPaciente(solicitud.getNombre().trim());
        cita.setDni(solicitud.getDni().trim());
        cita.setEspecialidad(solicitud.getEspecialidad().trim());
        cita.setFechaProgramada(fecha);
        cita.setFechaEtiqueta(formatDate(fecha));
        cita.setHoraProgramada(slot.getHora());
        cita.setEstado(estado);
        cita.setCanal("Web");
        cita.setPrioridad(normalize(solicitud.getPrioridad(), "Normal"));
        cita.setMotivo(normalize(solicitud.getMensaje(), "Sin motivo detallado"));

        return toModel(citaDao.save(cita));
    }

    public List<Cita> listarPorDni(String dni) {
        if (isBlank(dni) || !dni.trim().matches("\\d{8,12}")) {
            throw new IllegalArgumentException("Ingresa un DNI valido para buscar citas.");
        }
        return citaDao.findByDni(dni.trim()).stream().map(this::toModel).toList();
    }

    public void confirmar(String codigo) {
        cambiarEstado(codigo, "Confirmada");
    }

    public void cancelar(String codigo) {
        cambiarEstado(codigo, "Cancelada");
    }

    public void marcarAtendida(String codigo) {
        cambiarEstado(codigo, "Atendida");
    }

    public void marcarEnEspera(String codigo) {
        cambiarEstado(codigo, "Paciente en espera");
    }

    public void reprogramar(String codigo, String fechaTexto, String horaTexto) {
        if (isBlank(codigo) || isBlank(fechaTexto) || isBlank(horaTexto)) {
            throw new IllegalArgumentException("Selecciona cita, fecha y hora para reprogramar.");
        }
        CitaEntity cita = citaDao.findByCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException("No existe la cita seleccionada."));
        LocalDate fecha = parseFecha(fechaTexto);
        validarFechaNoPasada(fecha);
        LocalTime hora = LocalTime.parse(horaTexto);
        MedicoEntity medico = medicoDao.findPrincipalByEspecialidad(cita.getEspecialidad())
                .orElseThrow(() -> new IllegalArgumentException("No hay medico para reprogramar esa especialidad."));
        if (citaDao.existsByMedicoFechaHora(medico.getId(), fecha, hora, codigo)) {
            throw new IllegalArgumentException("Ese medico ya tiene una cita en la fecha y hora seleccionada.");
        }
        citaDao.updateFechaHoraYMedico(codigo, fecha, formatDate(fecha), hora, medico.getId(), "Reprogramada");
    }

    public long contarCitasPendientes() {
        return citaDao.findAll().stream()
                .filter(cita -> cita.getEstado().contains("Pendiente") || cita.getEstado().contains("Reprogramable"))
                .count();
    }

    public int contarAgenda() {
        return citaDao.findAll().size();
    }

    private void validarSolicitud(SolicitudCita solicitud) {
        if (isBlank(solicitud.getNombre()) || isBlank(solicitud.getDni()) || isBlank(solicitud.getEspecialidad())
                || isBlank(solicitud.getFecha())) {
            throw new IllegalArgumentException("Completa nombre, DNI, especialidad y fecha.");
        }
        if (!solicitud.getDni().trim().matches("\\d{8,12}")) {
            throw new IllegalArgumentException("El DNI o numero de asegurado debe tener entre 8 y 12 digitos.");
        }
        try {
            validarFechaNoPasada(LocalDate.parse(solicitud.getFecha()));
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException("La fecha solicitada no tiene un formato valido.");
        }
    }

    private void cambiarEstado(String codigo, String estado) {
        if (citaDao.findByCodigo(codigo).isEmpty()) {
            throw new IllegalArgumentException("No existe la cita seleccionada.");
        }
        citaDao.updateEstado(codigo, estado);
    }

    private LocalDate parseFecha(String fechaTexto) {
        try {
            return LocalDate.parse(fechaTexto);
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException("La fecha no tiene un formato valido.");
        }
    }

    private void validarFechaNoPasada(LocalDate fecha) {
        if (fecha.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("No se puede registrar una cita en una fecha pasada.");
        }
    }

    private SlotEntity seleccionarSlot(String especialidad) {
        return citaDao.findSlots().stream()
                .filter(SlotEntity::isDisponible)
                .filter(slot -> slot.getEspecialidad().equalsIgnoreCase(especialidad))
                .findFirst()
                .orElseGet(() -> citaDao.findSlots().stream()
                        .filter(SlotEntity::isDisponible)
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("No hay horarios disponibles.")));
    }

    private String calcularEstadoInicial(String especialidad, String prioridad) {
        boolean medicoDisponible = medicoDao.findDisponibleByEspecialidad(especialidad).isPresent();
        if (!medicoDisponible) {
            return "Pendiente por disponibilidad";
        }
        if ("Paciente cronico".equalsIgnoreCase(prioridad) || "Urgente administrativo".equalsIgnoreCase(prioridad)) {
            return "Pendiente prioritaria";
        }
        return "Pendiente";
    }

    private Cita toModel(CitaEntity cita) {
        return new Cita(cita.getCodigo(), cita.getPaciente(), cita.getEspecialidad(), cita.getFechaEtiqueta(),
                formatHour(cita.getHoraProgramada()), cita.getEstado(), cita.getCanal());
    }

    private String formatHour(LocalTime time) {
        return HOUR_FORMATTER.format(time);
    }

    private String formatDate(LocalDate date) {
        return "%02d %s".formatted(date.getDayOfMonth(), monthName(date.getMonthValue()));
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

    private String normalize(String value, String fallback) {
        return isBlank(value) ? fallback : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
