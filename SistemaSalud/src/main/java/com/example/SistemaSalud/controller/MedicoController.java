package com.example.SistemaSalud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.SistemaSalud.model.MedicoForm;
import com.example.SistemaSalud.service.MedicoService;

@Controller
public class MedicoController {

    private final MedicoService medicoService;

    public MedicoController(MedicoService medicoService) {
        this.medicoService = medicoService;
    }

    @GetMapping("/medicos")
    public String medicos(@RequestParam(value = "success", required = false) String success,
            @RequestParam(value = "error", required = false) String error, Model model) {
        model.addAttribute("especialidades", medicoService.listarEspecialidades());
        model.addAttribute("medicos", medicoService.listarMedicos());
        model.addAttribute("medicoForm", new MedicoForm());
        if (success != null && !success.isBlank()) {
            model.addAttribute("successMessage", success);
        }
        if (error != null && !error.isBlank()) {
            model.addAttribute("errorMessage", error);
        }
        return "medicos";
    }

    @PostMapping("/medicos")
    public String registrar(@ModelAttribute MedicoForm form, RedirectAttributes attributes) {
        try {
            medicoService.registrar(form);
            attributes.addAttribute("success", "Medico registrado correctamente.");
        } catch (IllegalArgumentException exception) {
            attributes.addAttribute("error", exception.getMessage());
        }
        return "redirect:/medicos";
    }

    @PostMapping("/medicos/{id}/actualizar")
    public String actualizar(@PathVariable Long id, @ModelAttribute MedicoForm form, RedirectAttributes attributes) {
        try {
            form.setId(id);
            medicoService.actualizar(form);
            attributes.addAttribute("success", "Medico actualizado correctamente.");
        } catch (IllegalArgumentException exception) {
            attributes.addAttribute("error", exception.getMessage());
        }
        return "redirect:/medicos";
    }

    @PostMapping("/medicos/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes attributes) {
        try {
            medicoService.eliminar(id);
            attributes.addAttribute("success", "Medico eliminado correctamente.");
        } catch (IllegalArgumentException exception) {
            attributes.addAttribute("error", exception.getMessage());
        }
        return "redirect:/medicos";
    }
}
