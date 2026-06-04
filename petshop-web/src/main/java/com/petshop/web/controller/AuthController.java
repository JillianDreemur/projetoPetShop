package com.petshop.web.controller;

import com.petshop.web.dto.RegisterForm;
import com.petshop.web.security.AdminSessionHelper;
import com.petshop.web.service.AdminUsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final AdminUsuarioService adminUsuarioService;

    public AuthController(AdminUsuarioService adminUsuarioService) {
        this.adminUsuarioService = adminUsuarioService;
    }

    @GetMapping("/login")
    public String login() {
        if (AdminSessionHelper.isAdminLogado()) {
            return "redirect:/admin";
        }
        return "login";
    }

    @GetMapping("/registro")
    public String registro(Model model) {
        if (AdminSessionHelper.isAdminLogado()) {
            return "redirect:/admin";
        }
        if (!model.containsAttribute("registerForm")) {
            model.addAttribute("registerForm", new RegisterForm());
        }
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(@Valid @ModelAttribute("registerForm") RegisterForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "registro";
        }
        try {
            adminUsuarioService.registrar(form.getNome(), form.getSenha(), form.getConfirmarSenha(), form.getChaveAcesso());
            redirectAttributes.addFlashAttribute("sucesso", "Administrador cadastrado! Faça login.");
            return "redirect:/login";
        } catch (ResponseStatusException ex) {
            model.addAttribute("erro", ex.getReason());
            return "registro";
        }
    }
}
