package com.example.SistemaSalud.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.SistemaSalud.dao.KioscoDao;
import com.example.SistemaSalud.entity.TurnoEntity;
import com.example.SistemaSalud.model.PasoKiosco;
import com.example.SistemaSalud.model.Turno;

@Service
public class KioscoService {

    private final KioscoDao kioscoDao;
    private final CitaService citaService;

    public KioscoService(KioscoDao kioscoDao, CitaService citaService) {
        this.kioscoDao = kioscoDao;
        this.citaService = citaService;
    }

    public List<PasoKiosco> pasosAtencion() {
        return List.of(
                new PasoKiosco("1", "Identificacion", "El asegurado ingresa DNI o numero de asegurado."),
                new PasoKiosco("2", "Verificacion", "El sistema muestra citas, especialidad y sede de atencion."),
                new PasoKiosco("3", "Confirmacion", "La cita queda confirmada y se emite turno digital."),
                new PasoKiosco("4", "Atencion", "El medico accede a la historia clinica antes de la consulta."));
    }

    public List<Turno> turnosActivos() {
        return kioscoDao.findTurnosActivos().stream()
                .map(this::toModel)
                .toList();
    }

    public Turno confirmarTurno(String codigo) {
        TurnoEntity turno = kioscoDao.findByCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro el turno solicitado."));
        turno.setEstado("Confirmado");
        return toModel(kioscoDao.save(turno));
    }

    public List<com.example.SistemaSalud.model.Cita> buscarCitasPorDni(String dni) {
        return citaService.listarPorDni(dni);
    }

    public Turno confirmarLlegada(String codigoCita) {
        com.example.SistemaSalud.model.Cita cita = citaService.listarAgenda().stream()
                .filter(item -> item.codigo().equalsIgnoreCase(codigoCita))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No existe la cita seleccionada."));
        citaService.marcarEnEspera(codigoCita);
        TurnoEntity turno = kioscoDao.findByCitaCodigo(codigoCita).orElseGet(() -> {
            long numero = kioscoDao.nextTurnoNumber();
            return new TurnoEntity(null, null, codigoCita, "A-%03d".formatted(numero), cita.especialidad(),
                    "Consultorio por asignar", "Paciente en espera");
        });
        turno.setCitaCodigo(codigoCita);
        turno.setEspecialidad(cita.especialidad());
        turno.setEstado("Paciente en espera");
        return toModel(kioscoDao.save(turno));
    }

    private Turno toModel(TurnoEntity turno) {
        return new Turno(turno.getCodigo(), turno.getEspecialidad(), turno.getUbicacion(), turno.getEstado());
    }
}
