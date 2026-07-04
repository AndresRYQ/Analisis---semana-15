package com.example.SistemaSalud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.SistemaSalud.service.ReporteService;

@Controller
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/reportes")
    public String reportes(Model model) {
        model.addAttribute("indicadoresOperativos", reporteService.indicadoresOperativos());
        model.addAttribute("requerimientos", reporteService.requerimientosPriorizados());
        model.addAttribute("citasPorEstado", reporteService.citasPorEstado());
        model.addAttribute("citasPorEspecialidad", reporteService.citasPorEspecialidad());
        model.addAttribute("citasPorMedico", reporteService.citasPorMedico());
        return "reportes";
    }
}
