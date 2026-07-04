package com.example.SistemaSalud.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.SistemaSalud.dao.CitaDao;
import com.example.SistemaSalud.dao.PacienteDao;
import com.example.SistemaSalud.entity.PacienteEntity;
import com.example.SistemaSalud.model.Paciente;
import com.example.SistemaSalud.model.PacienteForm;

@Service
public class PacienteService {

    private final PacienteDao pacienteDao;
    private final CitaDao citaDao;

    public PacienteService(PacienteDao pacienteDao, CitaDao citaDao) {
        this.pacienteDao = pacienteDao;
        this.citaDao = citaDao;
    }

    public List<Paciente> listarPacientes() {
        return pacienteDao.findAll().stream().map(this::toModel).toList();
    }

    public Paciente buscarPorDni(String dni) {
        return pacienteDao.findByDni(dni)
                .map(this::toModel)
                .orElseThrow(() -> new IllegalArgumentException("No existe un paciente con ese DNI."));
    }

    public Paciente registrar(PacienteForm form) {
        validar(form, true);
        if (pacienteDao.findByDni(form.getDni().trim()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un paciente con ese DNI.");
        }
        return toModel(pacienteDao.save(toEntity(form)));
    }

    public Paciente actualizar(PacienteForm form) {
        validar(form, false);
        PacienteEntity existente = pacienteDao.findByDni(form.getDni().trim())
                .orElseThrow(() -> new IllegalArgumentException("No existe un paciente con ese DNI."));
        PacienteEntity paciente = toEntity(form);
        paciente.setId(existente.getId());
        return toModel(pacienteDao.update(paciente));
    }

    public void eliminar(String dni) {
        if (citaDao.countByDni(dni) > 0) {
            throw new IllegalArgumentException("No se puede eliminar un paciente con citas registradas.");
        }
        pacienteDao.deleteByDni(dni);
    }

    public PacienteEntity asegurarPacienteBasico(String nombre, String dni) {
        return pacienteDao.findByDni(dni)
                .orElseGet(() -> pacienteDao.save(new PacienteEntity(null, nombre, dni, 0,
                        "Por verificar", "Bajo")));
    }

    private void validar(PacienteForm form, boolean nuevo) {
        if (isBlank(form.getNombre()) || isBlank(form.getDni()) || isBlank(form.getTipoSeguro())
                || isBlank(form.getRiesgo()) || form.getEdad() == null) {
            throw new IllegalArgumentException("Completa todos los datos del paciente.");
        }
        if (!form.getDni().trim().matches("\\d{8,12}")) {
            throw new IllegalArgumentException("El DNI debe tener entre 8 y 12 digitos.");
        }
        if (form.getEdad() < 0 || form.getEdad() > 120) {
            throw new IllegalArgumentException("La edad debe estar entre 0 y 120.");
        }
        if (!nuevo && pacienteDao.findByDni(form.getDni().trim()).isEmpty()) {
            throw new IllegalArgumentException("El paciente que intentas editar no existe.");
        }
    }

    private PacienteEntity toEntity(PacienteForm form) {
        return new PacienteEntity(form.getId(), form.getNombre().trim(), form.getDni().trim(), form.getEdad(),
                form.getTipoSeguro().trim(), form.getRiesgo().trim());
    }

    private Paciente toModel(PacienteEntity paciente) {
        return new Paciente(paciente.getId(), paciente.getNombre(), paciente.getDni(), paciente.getEdad() + " anos",
                paciente.getTipoSeguro(), paciente.getRiesgo());
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
