package com.example.SistemaSalud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.SistemaSalud.service.SeguridadService;

@Controller
public class SeguridadController {

    private final SeguridadService seguridadService;

    public SeguridadController(SeguridadService seguridadService) {
        this.seguridadService = seguridadService;
    }

    @GetMapping("/seguridad")
    public String seguridad(Model model) {
        model.addAttribute("roles", seguridadService.roles());
        model.addAttribute("auditorias", seguridadService.auditoriasRecientes());
        model.addAttribute("usuariosSistema", seguridadService.usuariosSistema());
        return "seguridad";
    }
}
