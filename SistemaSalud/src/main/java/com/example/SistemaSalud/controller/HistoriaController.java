package com.example.SistemaSalud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.SistemaSalud.model.HistoriaForm;
import com.example.SistemaSalud.service.HistoriaClinicaService;
import com.example.SistemaSalud.service.SeguridadService;

@Controller
public class HistoriaController {

    private final HistoriaClinicaService historiaClinicaService;
    private final SeguridadService seguridadService;

    public HistoriaController(HistoriaClinicaService historiaClinicaService, SeguridadService seguridadService) {
        this.historiaClinicaService = historiaClinicaService;
        this.seguridadService = seguridadService;
    }

    @GetMapping("/historias")
    public String historias(
            @RequestParam(value = "dni", required = false) String dni,
            @RequestParam(value = "success", required = false) String success,
            @RequestParam(value = "error", required = false) String error,
            Model model) {
        try {
            if (dni != null && !dni.isBlank()) {
                model.addAttribute("paciente", historiaClinicaService.buscarPaciente(dni));
                model.addAttribute("historial", historiaClinicaService.historialPorDni(dni));
                model.addAttribute("documentos", historiaClinicaService.documentosPorDni(dni));
            } else {
                model.addAttribute("paciente", historiaClinicaService.pacienteDemo());
                model.addAttribute("historial", historiaClinicaService.historialDemo());
                model.addAttribute("documentos", historiaClinicaService.documentosDemo());
            }
        } catch (IllegalArgumentException exception) {
            model.addAttribute("paciente", historiaClinicaService.pacienteDemo());
            model.addAttribute("historial", historiaClinicaService.historialDemo());
            model.addAttribute("documentos", historiaClinicaService.documentosDemo());
            model.addAttribute("errorMessage", exception.getMessage());
        }
        model.addAttribute("historiaForm", new HistoriaForm());
        if (success != null && !success.isBlank()) {
            model.addAttribute("successMessage", success);
        }
        if (error != null && !error.isBlank()) {
            model.addAttribute("errorMessage", error);
        }
        seguridadService.registrarAuditoria("Medico", "Medico", "Apertura de historia clinica demo",
                "Autorizado");
        return "historias";
    }

    @PostMapping("/historias")
    public String registrarEvento(@ModelAttribute HistoriaForm form, RedirectAttributes attributes) {
        try {
            historiaClinicaService.registrarEvento(form);
            seguridadService.registrarAuditoria("Medico", "Medico", "Registro de evento clinico", "Autorizado");
            attributes.addAttribute("dni", form.getPacienteDni());
            attributes.addAttribute("success", "Evento clinico registrado correctamente.");
        } catch (IllegalArgumentException exception) {
            attributes.addAttribute("error", exception.getMessage());
        }
        return "redirect:/historias";
    }
}
