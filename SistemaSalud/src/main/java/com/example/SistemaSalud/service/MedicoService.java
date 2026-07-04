package com.example.SistemaSalud.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.SistemaSalud.dao.MedicoDao;
import com.example.SistemaSalud.dao.CitaDao;
import com.example.SistemaSalud.entity.MedicoEntity;
import com.example.SistemaSalud.model.MedicoForm;
import com.example.SistemaSalud.model.Medico;

@Service
public class MedicoService {

    private final MedicoDao medicoDao;
    private final CitaDao citaDao;

    public MedicoService(MedicoDao medicoDao, CitaDao citaDao) {
        this.medicoDao = medicoDao;
        this.citaDao = citaDao;
    }

    public List<String> listarEspecialidades() {
        return medicoDao.findEspecialidades();
    }

    public List<Medico> listarMedicos() {
        return medicoDao.findAll().stream()
                .map(this::toModel)
                .toList();
    }

    public long contarMedicosDisponibles() {
        return medicoDao.countDisponiblesHoy();
    }

    public Optional<String> doctorPrincipalPorEspecialidad(String especialidad) {
        return medicoDao.findDisponibleByEspecialidad(especialidad)
                .map(MedicoEntity::getNombre);
    }

    public Optional<MedicoEntity> medicoPrincipalPorEspecialidad(String especialidad) {
        return medicoDao.findPrincipalByEspecialidad(especialidad);
    }

    public Medico registrar(MedicoForm form) {
        validar(form);
        return toModel(medicoDao.save(toEntity(form)));
    }

    public Medico actualizar(MedicoForm form) {
        validar(form);
        if (form.getId() == null || medicoDao.findById(form.getId()).isEmpty()) {
            throw new IllegalArgumentException("El medico seleccionado no existe.");
        }
        return toModel(medicoDao.update(toEntity(form)));
    }

    public void eliminar(Long id) {
        if (citaDao.countByMedicoId(id) > 0) {
            throw new IllegalArgumentException("No se puede eliminar un medico con citas registradas.");
        }
        medicoDao.deleteById(id);
    }

    private void validar(MedicoForm form) {
        if (isBlank(form.getNombre()) || isBlank(form.getEspecialidad()) || isBlank(form.getDisponibilidad())
                || isBlank(form.getUbicacion()) || isBlank(form.getDetalle())) {
            throw new IllegalArgumentException("Completa todos los datos del medico.");
        }
    }

    private MedicoEntity toEntity(MedicoForm form) {
        return new MedicoEntity(form.getId(), form.getNombre().trim(), form.getEspecialidad().trim(),
                form.getDisponibilidad().trim(), form.getUbicacion().trim(), form.getDetalle().trim(),
                form.isDisponibleHoy());
    }

    private Medico toModel(MedicoEntity medico) {
        return new Medico(medico.getId(), medico.getNombre(), medico.getEspecialidad(), medico.getDisponibilidad(),
                medico.getUbicacion(), medico.getDetalle(), medico.isDisponibleHoy());
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
