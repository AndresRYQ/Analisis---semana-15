package com.example.SistemaSalud.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.SistemaSalud.dao.ReporteDao;
import com.example.SistemaSalud.entity.RequerimientoEntity;
import com.example.SistemaSalud.model.Kpi;
import com.example.SistemaSalud.model.Requerimiento;
import com.example.SistemaSalud.model.ReporteFila;

@Service
public class ReporteService {

    private final ReporteDao reporteDao;
    private final CitaService citaService;
    private final HistoriaClinicaService historiaClinicaService;

    public ReporteService(ReporteDao reporteDao, CitaService citaService,
            HistoriaClinicaService historiaClinicaService) {
        this.reporteDao = reporteDao;
        this.citaService = citaService;
        this.historiaClinicaService = historiaClinicaService;
    }

    public List<Kpi> indicadoresOperativos() {
        long canceladas = reporteDao.countCanceladas();
        return List.of(
                new Kpi(String.valueOf(reporteDao.countPacientes()), "Pacientes registrados",
                        "Personas aseguradas en SQLite", "green"),
                new Kpi(String.valueOf(historiaClinicaService.contarHistoriasDisponibles()),
                        "Historias disponibles", "Acceso clinico centralizado", "blue"),
                new Kpi(String.valueOf(canceladas), "Citas canceladas", "Seguimiento de demanda perdida", "amber"),
                new Kpi(String.valueOf(citaService.contarAgenda()), "Citas en agenda",
                        "Registros gestionados por la capa DAO", "red"));
    }

    public List<ReporteFila> citasPorEstado() {
        return reporteDao.countCitasPorEstado().entrySet().stream()
                .map(entry -> new ReporteFila(entry.getKey(), String.valueOf(entry.getValue()), "Citas"))
                .toList();
    }

    public List<ReporteFila> citasPorEspecialidad() {
        return reporteDao.countCitasPorEspecialidad().entrySet().stream()
                .map(entry -> new ReporteFila(entry.getKey(), String.valueOf(entry.getValue()), "Especialidad"))
                .toList();
    }

    public List<ReporteFila> citasPorMedico() {
        return reporteDao.countCitasPorMedico().entrySet().stream()
                .map(entry -> new ReporteFila(entry.getKey(), String.valueOf(entry.getValue()), "Medico"))
                .toList();
    }

    public List<Requerimiento> requerimientosPriorizados() {
        return reporteDao.findRequerimientosPriorizados().stream()
                .map(this::toModel)
                .toList();
    }

    private Requerimiento toModel(RequerimientoEntity requerimiento) {
        return new Requerimiento(requerimiento.getCodigo(), requerimiento.getPrioridad(),
                requerimiento.getDescripcion());
    }
}
