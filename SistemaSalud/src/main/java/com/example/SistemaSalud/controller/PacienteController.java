package com.example.SistemaSalud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.SistemaSalud.model.PacienteForm;
import com.example.SistemaSalud.service.PacienteService;
import com.example.SistemaSalud.service.SeguridadService;

@Controller
public class PacienteController {

    private final PacienteService pacienteService;
    private final SeguridadService seguridadService;

    public PacienteController(PacienteService pacienteService, SeguridadService seguridadService) {
        this.pacienteService = pacienteService;
        this.seguridadService = seguridadService;
    }

    @GetMapping("/pacientes")
    public String pacientes(
            @RequestParam(value = "dni", required = false) String dni,
            @RequestParam(value = "success", required = false) String success,
            @RequestParam(value = "error", required = false) String error,
            Model model) {
        model.addAttribute("pacientes", pacienteService.listarPacientes());
        model.addAttribute("pacienteForm", new PacienteForm());
        if (dni != null && !dni.isBlank()) {
            try {
                model.addAttribute("pacienteEncontrado", pacienteService.buscarPorDni(dni));
            } catch (IllegalArgumentException exception) {
                model.addAttribute("errorMessage", exception.getMessage());
            }
        }
        if (success != null && !success.isBlank()) {
            model.addAttribute("successMessage", success);
        }
        if (error != null && !error.isBlank()) {
            model.addAttribute("errorMessage", error);
        }
        return "pacientes";
    }

    @PostMapping("/pacientes")
    public String registrar(@ModelAttribute PacienteForm form, RedirectAttributes attributes) {
        try {
            pacienteService.registrar(form);
            seguridadService.registrarAuditoria("Administrativo", "Administrativo", "Registro de paciente",
                    "Autorizado");
            attributes.addAttribute("success", "Paciente registrado correctamente.");
        } catch (IllegalArgumentException exception) {
            attributes.addAttribute("error", exception.getMessage());
        }
        return "redirect:/pacientes";
    }

    @PostMapping("/pacientes/{dni}/actualizar")
    public String actualizar(@PathVariable String dni, @ModelAttribute PacienteForm form,
            RedirectAttributes attributes) {
        try {
            form.setDni(dni);
            pacienteService.actualizar(form);
            attributes.addAttribute("success", "Paciente actualizado correctamente.");
        } catch (IllegalArgumentException exception) {
            attributes.addAttribute("error", exception.getMessage());
        }
        return "redirect:/pacientes";
    }

    @PostMapping("/pacientes/{dni}/eliminar")
    public String eliminar(@PathVariable String dni, RedirectAttributes attributes) {
        try {
            pacienteService.eliminar(dni);
            attributes.addAttribute("success", "Paciente eliminado correctamente.");
        } catch (IllegalArgumentException exception) {
            attributes.addAttribute("error", exception.getMessage());
        }
        return "redirect:/pacientes";
    }
}
