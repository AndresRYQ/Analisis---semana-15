package com.example.SistemaSalud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.SistemaSalud.model.Cita;
import com.example.SistemaSalud.model.SolicitudCita;
import com.example.SistemaSalud.service.CitaService;
import com.example.SistemaSalud.service.SeguridadService;

@Controller
public class CitaController {

    private final CitaService citaService;
    private final SeguridadService seguridadService;

    public CitaController(CitaService citaService, SeguridadService seguridadService) {
        this.citaService = citaService;
        this.seguridadService = seguridadService;
    }

    @GetMapping("/citas")
    public String citas(
            @RequestParam(value = "success", required = false) Boolean success,
            @RequestParam(value = "codigo", required = false) String codigo,
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "message", required = false) String message,
            Model model) {
        model.addAttribute("citas", citaService.listarAgenda());
        model.addAttribute("slots", citaService.listarSlotsSugeridos());
        if (Boolean.TRUE.equals(success)) {
            model.addAttribute("successMessage",
                    "Solicitud registrada en SQLite. Codigo generado: " + codigo + ".");
        }
        if (message != null && !message.isBlank()) {
            model.addAttribute("successMessage", message);
        }
        if (error != null && !error.isBlank()) {
            model.addAttribute("errorMessage", error);
        }
        return "citas";
    }

    @PostMapping("/citas")
    public String reservarCita(@ModelAttribute SolicitudCita solicitud, RedirectAttributes redirectAttributes) {
        try {
            Cita cita = citaService.registrarSolicitud(solicitud);
            seguridadService.registrarAuditoria("Paciente", "Paciente",
                    "Registro de cita " + cita.codigo(), "Autorizado");
            redirectAttributes.addAttribute("success", true);
            redirectAttributes.addAttribute("codigo", cita.codigo());
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addAttribute("error", exception.getMessage());
        }
        return "redirect:/citas";
    }

    @PostMapping("/citas/{codigo}/confirmar")
    public String confirmar(@org.springframework.web.bind.annotation.PathVariable String codigo,
            RedirectAttributes attributes) {
        return ejecutarAccionCita(() -> citaService.confirmar(codigo), "Cita confirmada.", attributes);
    }

    @PostMapping("/citas/{codigo}/cancelar")
    public String cancelar(@org.springframework.web.bind.annotation.PathVariable String codigo,
            RedirectAttributes attributes) {
        return ejecutarAccionCita(() -> citaService.cancelar(codigo), "Cita cancelada.", attributes);
    }

    @PostMapping("/citas/{codigo}/atender")
    public String atender(@org.springframework.web.bind.annotation.PathVariable String codigo,
            RedirectAttributes attributes) {
        return ejecutarAccionCita(() -> citaService.marcarAtendida(codigo), "Cita marcada como atendida.",
                attributes);
    }

    @PostMapping("/citas/{codigo}/reprogramar")
    public String reprogramar(
            @org.springframework.web.bind.annotation.PathVariable String codigo,
            @RequestParam String fecha,
            @RequestParam String hora,
            RedirectAttributes attributes) {
        return ejecutarAccionCita(() -> citaService.reprogramar(codigo, fecha, hora), "Cita reprogramada.",
                attributes);
    }

    private String ejecutarAccionCita(Runnable action, String message, RedirectAttributes attributes) {
        try {
            action.run();
            attributes.addAttribute("message", message);
        } catch (IllegalArgumentException exception) {
            attributes.addAttribute("error", exception.getMessage());
        }
        return "redirect:/citas";
    }
}
