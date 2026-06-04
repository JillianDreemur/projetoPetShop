package com.petshop.web.controller;

import com.petshop.web.client.GatewayApiClient;
import com.petshop.web.dto.AgendamentoDto;
import com.petshop.web.dto.AgendamentoForm;
import com.petshop.web.dto.PetDto;
import com.petshop.web.dto.ServiceStatusDto;
import com.petshop.web.service.InfraStatusService;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final GatewayApiClient api;
    private final InfraStatusService infraStatusService;

    public AdminController(GatewayApiClient api, InfraStatusService infraStatusService) {
        this.api = api;
        this.infraStatusService = infraStatusService;
    }

    @GetMapping
    public String painel(@AuthenticationPrincipal UserDetails usuario, Model model) {
        return renderAdmin(usuario, model, "painel", new AgendamentoForm());
    }

    @GetMapping("/servicos")
    public String servicos(@AuthenticationPrincipal UserDetails usuario, Model model) {
        return renderAdmin(usuario, model, "servicos", new AgendamentoForm());
    }

    @GetMapping("/agendamentos")
    public String agendamentos(@AuthenticationPrincipal UserDetails usuario, Model model) {
        return renderAdmin(usuario, model, "agendamentos", new AgendamentoForm());
    }

    @GetMapping("/agendamentos/novo")
    public String novoAgendamento(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("agendamentoForm", new AgendamentoForm());
        return "redirect:/admin/agendamentos";
    }

    @PostMapping("/agendamentos")
    public String salvarAgendamento(@Valid @ModelAttribute("agendamentoForm") AgendamentoForm form,
                                    BindingResult bindingResult,
                                    @AuthenticationPrincipal UserDetails usuario,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return renderAdmin(usuario, model, "agendamentos", form);
        }
        try {
            AgendamentoDto dto = api.toAgendamentoDto(form);
            if (form.getId() != null) {
                api.atualizarAgendamento(form.getId(), dto);
                redirectAttributes.addFlashAttribute("sucesso", "Agendamento atualizado.");
            } else {
                api.criarAgendamento(dto);
                redirectAttributes.addFlashAttribute("sucesso", "Agendamento criado.");
            }
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("erro", api.extrairMensagemErro(ex));
            redirectAttributes.addFlashAttribute("agendamentoForm", form);
        }
        return "redirect:/admin/agendamentos";
    }

    @GetMapping("/agendamentos/{id}/editar")
    public String editarAgendamento(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            AgendamentoDto ag = api.buscarAgendamento(id);
            AgendamentoForm form = new AgendamentoForm();
            form.setId(ag.getId());
            form.setData(ag.getData().toString().substring(0, 16));
            form.setTipoServico(ag.getTipoServico());
            form.setPetId(ag.getPetId());
            redirectAttributes.addFlashAttribute("agendamentoForm", form);
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("erro", api.extrairMensagemErro(ex));
        }
        return "redirect:/admin/agendamentos";
    }

    @PostMapping("/agendamentos/{id}/excluir")
    public String excluirAgendamento(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            api.excluirAgendamento(id);
            redirectAttributes.addFlashAttribute("sucesso", "Agendamento excluído.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("erro", api.extrairMensagemErro(ex));
        }
        return "redirect:/admin/agendamentos";
    }

    private String renderAdmin(UserDetails usuario, Model model, String secao, AgendamentoForm formPadrao) {
        model.addAttribute("adminNome", usuario != null ? usuario.getUsername() : "Admin");
        model.addAttribute("secao", secao);
        carregarStatus(model);
        carregarAgendamentos(model, formPadrao);
        return "admin/painel";
    }

    private void carregarStatus(Model model) {
        List<ServiceStatusDto> servicos = infraStatusService.verificarTodos();
        model.addAttribute("servicos", servicos);
        model.addAttribute("totalOnline", infraStatusService.contarOnline(servicos));
        model.addAttribute("totalServicos", servicos.size());
    }

    private void carregarAgendamentos(Model model, AgendamentoForm formPadrao) {
        if (!model.containsAttribute("agendamentoForm")) {
            model.addAttribute("agendamentoForm", formPadrao);
        }
        try {
            List<PetDto> pets = api.listarPets();
            List<AgendamentoDto> agendamentos = api.listarAgendamentos();
            model.addAttribute("pets", pets);
            model.addAttribute("agendamentos", agendamentos);
            model.addAttribute("petNomes", mapaNomesPets(pets));
            model.addAttribute("apiOffline", null);
        } catch (Exception ex) {
            model.addAttribute("pets", List.of());
            model.addAttribute("agendamentos", List.of());
            model.addAttribute("petNomes", Map.of());
            model.addAttribute("apiOffline", api.extrairMensagemErro(ex));
        }
    }

    private Map<UUID, String> mapaNomesPets(List<PetDto> pets) {
        if (pets == null || pets.isEmpty()) {
            return Map.of();
        }
        return pets.stream().collect(Collectors.toMap(
                PetDto::getId,
                PetDto::getNome,
                (a, b) -> a,
                HashMap::new
        ));
    }
}
