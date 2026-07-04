package com.example.SistemaSalud.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.SistemaSalud.model.AccionRapida;
import com.example.SistemaSalud.model.Cita;
import com.example.SistemaSalud.model.Consulta;
import com.example.SistemaSalud.model.DatoSalud;
import com.example.SistemaSalud.model.Kpi;

@Service
public class DashboardService {

    private final MedicoService medicoService;
    private final CitaService citaService;
    private final HistoriaClinicaService historiaClinicaService;

    public DashboardService(MedicoService medicoService, CitaService citaService,
            HistoriaClinicaService historiaClinicaService) {
        this.medicoService = medicoService;
        this.citaService = citaService;
        this.historiaClinicaService = historiaClinicaService;
    }

    public String fechaPanel() {
        LocalDate today = LocalDate.now();
        return "%s, %d de %s".formatted(dayName(today.getDayOfWeek().getValue()), today.getDayOfMonth(),
                monthName(today.getMonthValue()));
    }

    public List<Kpi> kpis() {
        return List.of(
                new Kpi(String.valueOf(medicoService.contarMedicosDisponibles()), "Medicos disponibles",
                        "Atencion inmediata por especialidad", "blue"),
                new Kpi(String.valueOf(citaService.contarCitasPendientes()), "Citas por gestionar",
                        "Solicitudes pendientes o reprogramables", "green"),
                new Kpi(String.valueOf(historiaClinicaService.contarHistoriasDisponibles()),
                        "Historias digitalizadas", "Registros clinicos integrados", "amber"),
                new Kpi("1", "Alerta de prioridad", "Seguimiento de paciente cronico", "red"));
    }

    public Consulta consultaEnVivo() {
        return new Consulta("Dra. Sofia Ramirez", "Medicina General", "14:30", "Teleconsulta activa", "En vivo");
    }

    public List<Consulta> proximasConsultas() {
        return citaService.listarAgenda().stream()
                .limit(3)
                .map(this::toConsulta)
                .toList();
    }

    public List<DatoSalud> indicadoresSalud() {
        return List.of(
                new DatoSalud("Presion arterial", "118/76", "Normal"),
                new DatoSalud("Frecuencia cardiaca", "72 bpm", "Estable"),
                new DatoSalud("Ultima consulta", "Hace 14 dias", "Medicina General"),
                new DatoSalud("Riesgo clinico", "Bajo", "Control preventivo"));
    }

    public List<AccionRapida> accionesRapidas() {
        return List.of(
                new AccionRapida("Programar cita", "Reserva por especialidad y prioridad", "/citas"),
                new AccionRapida("Abrir historia", "Consulta diagnosticos y tratamientos", "/historias"),
                new AccionRapida("Usar kiosco", "Confirmacion presencial de autoservicio", "/kiosco"));
    }

    private Consulta toConsulta(Cita cita) {
        String doctor = medicoService.doctorPrincipalPorEspecialidad(cita.especialidad())
                .orElse("Profesional por asignar");
        return new Consulta(doctor, cita.especialidad(), cita.fecha() + " " + cita.hora(), cita.canal(),
                cita.estado());
    }

    private String dayName(int dayOfWeek) {
        return switch (dayOfWeek) {
            case 1 -> "Lunes";
            case 2 -> "Martes";
            case 3 -> "Miercoles";
            case 4 -> "Jueves";
            case 5 -> "Viernes";
            case 6 -> "Sabado";
            case 7 -> "Domingo";
            default -> "";
        };
    }

    private String monthName(int month) {
        return switch (month) {
            case 1 -> "enero";
            case 2 -> "febrero";
            case 3 -> "marzo";
            case 4 -> "abril";
            case 5 -> "mayo";
            case 6 -> "junio";
            case 7 -> "julio";
            case 8 -> "agosto";
            case 9 -> "setiembre";
            case 10 -> "octubre";
            case 11 -> "noviembre";
            case 12 -> "diciembre";
            default -> "";
        };
    }
}
