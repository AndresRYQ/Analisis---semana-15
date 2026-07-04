package com.example.SistemaSalud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.SistemaSalud.model.LoginForm;
import com.example.SistemaSalud.model.UsuarioSesion;
import com.example.SistemaSalud.service.SeguridadService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    private final SeguridadService seguridadService;

    public AuthController(SeguridadService seguridadService) {
        this.seguridadService = seguridadService;
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "login";
    }

    @PostMapping("/login")
    public String autenticar(@ModelAttribute LoginForm form, HttpSession session, RedirectAttributes attributes) {
        try {
            UsuarioSesion usuario = seguridadService.login(form.getUsername(), form.getPassword());
            session.setAttribute("usuarioSesion", usuario);
            seguridadService.registrarAuditoria(usuario.nombre(), usuario.rol(), "Inicio de sesion", "Autorizado");
            attributes.addAttribute("success", "Sesion iniciada como " + usuario.rol());
            return "redirect:/";
        } catch (IllegalArgumentException exception) {
            attributes.addAttribute("error", exception.getMessage());
            return "redirect:/login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes attributes) {
        session.invalidate();
        attributes.addAttribute("success", "Sesion cerrada");
        return "redirect:/login";
    }
}
