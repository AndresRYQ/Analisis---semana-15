package com.example.SistemaSalud.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.SistemaSalud.service.SeguridadService;

import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class GlobalModelAdvice {

    private final SeguridadService seguridadService;

    public GlobalModelAdvice(SeguridadService seguridadService) {
        this.seguridadService = seguridadService;
    }

    @ModelAttribute
    public void cargarDatosBase(Model model, HttpSession session) {
        Object usuarioSesion = session.getAttribute("usuarioSesion");
        model.addAttribute("usuario", usuarioSesion == null ? seguridadService.usuarioSesion() : usuarioSesion);
        model.addAttribute("alertas", seguridadService.alertas());
    }
}
