package com.example.SistemaSalud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.SistemaSalud.service.DashboardService;

@Controller
public class HomeController {

    private final DashboardService dashboardService;

    public HomeController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("fechaPanel", dashboardService.fechaPanel());
        model.addAttribute("kpis", dashboardService.kpis());
        model.addAttribute("consultaEnVivo", dashboardService.consultaEnVivo());
        model.addAttribute("proximasConsultas", dashboardService.proximasConsultas());
        model.addAttribute("salud", dashboardService.indicadoresSalud());
        model.addAttribute("acciones", dashboardService.accionesRapidas());
        return "index";
    }
}
