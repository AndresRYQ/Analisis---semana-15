package com.example.SistemaSalud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.SistemaSalud.service.KioscoService;

@Controller
public class KioscoController {

    private final KioscoService kioscoService;

    public KioscoController(KioscoService kioscoService) {
        this.kioscoService = kioscoService;
    }

    @GetMapping("/kiosco")
    public String kiosco(
            @RequestParam(value = "dni", required = false) String dni,
            @RequestParam(value = "success", required = false) String success,
            @RequestParam(value = "error", required = false) String error,
            Model model) {
        model.addAttribute("pasosKiosco", kioscoService.pasosAtencion());
        model.addAttribute("turnos", kioscoService.turnosActivos());
        if (dni != null && !dni.isBlank()) {
            try {
                model.addAttribute("dniConsultado", dni);
                model.addAttribute("citasKiosco", kioscoService.buscarCitasPorDni(dni));
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
        return "kiosco";
    }

    @PostMapping("/kiosco/buscar")
    public String buscar(@RequestParam String dni, RedirectAttributes attributes) {
        attributes.addAttribute("dni", dni);
        return "redirect:/kiosco";
    }

    @PostMapping("/kiosco/confirmar")
    public String confirmar(@RequestParam String codigo, RedirectAttributes attributes) {
        try {
            kioscoService.confirmarLlegada(codigo);
            attributes.addAttribute("success", "Llegada confirmada y turno generado.");
        } catch (IllegalArgumentException exception) {
            attributes.addAttribute("error", exception.getMessage());
        }
        return "redirect:/kiosco";
    }
}
