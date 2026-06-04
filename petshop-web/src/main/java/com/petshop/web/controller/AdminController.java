package com.petshop.web.controller;

import com.petshop.web.client.GatewayApiClient;
import com.petshop.web.dto.AgendamentoDto;
import com.petshop.web.dto.AgendamentoForm;
import com.petshop.web.dto.PetDto;
import com.petshop.web.dto.PetForm;
import com.petshop.web.dto.ServiceStatusDto;
import com.petshop.web.dto.ServicoForm;
import com.petshop.web.service.InfraStatusService;
import com.petshop.web.service.ServicoCatalogoService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final GatewayApiClient api;
    private final InfraStatusService infraStatusService;
    private final ServicoCatalogoService catalogo;

    public AdminController(GatewayApiClient api, InfraStatusService infraStatusService,
                           ServicoCatalogoService catalogo) {
        this.api = api;
        this.infraStatusService = infraStatusService;
        this.catalogo = catalogo;
    }

    @GetMapping
    public String painel(@AuthenticationPrincipal UserDetails usuario, Model model) {
        return renderAdmin(usuario, model, "painel", new AgendamentoForm(), new ServicoForm(), new PetForm());
    }

    @GetMapping("/servicos")
    public String servicos(@AuthenticationPrincipal UserDetails usuario, Model model) {
        return renderAdmin(usuario, model, "servicos", new AgendamentoForm(), new ServicoForm(), new PetForm());
    }

    @GetMapping("/catalogo")
    public String catalogo(@AuthenticationPrincipal UserDetails usuario, Model model) {
        return renderAdmin(usuario, model, "catalogo", new AgendamentoForm(), new ServicoForm(), new PetForm());
    }

    @GetMapping("/pets")
    public String pets(@AuthenticationPrincipal UserDetails usuario, Model model) {
        return renderAdmin(usuario, model, "pets", new AgendamentoForm(), new ServicoForm(), new PetForm());
    }

    @GetMapping("/agendamentos")
    public String agendamentos(@AuthenticationPrincipal UserDetails usuario, Model model) {
        return renderAdmin(usuario, model, "agendamentos", new AgendamentoForm(), new ServicoForm(), new PetForm());
    }

    @PostMapping("/catalogo")
    public String salvarServico(@ModelAttribute("servicoForm") ServicoForm form,
                                @AuthenticationPrincipal UserDetails usuario,
                                RedirectAttributes redirectAttributes) {
        try {
            BigDecimal valor = parseValor(form.getValor());
            catalogo.salvar(form.getNome(), valor, form.getCategoria(), form.getGrupoExclusivo(),
                    form.isPacote(), form.getId());
            redirectAttributes.addFlashAttribute("sucesso",
                    form.getId() != null ? "Serviço atualizado." : "Serviço cadastrado.");
        } catch (ResponseStatusException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getReason());
            redirectAttributes.addFlashAttribute("servicoForm", form);
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("erro", "Valor inválido. Use formato 50,00 ou 50.00");
            redirectAttributes.addFlashAttribute("servicoForm", form);
        }
        return "redirect:/admin/catalogo";
    }

    @GetMapping("/catalogo/{id}/editar")
    public String editarServico(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            var s = catalogo.buscar(id);
            ServicoForm form = new ServicoForm();
            form.setId(s.getId());
            form.setNome(s.getNome());
            form.setValor(s.getValor().toPlainString().replace('.', ','));
            form.setCategoria(s.getCategoria());
            form.setGrupoExclusivo(s.getGrupoExclusivo());
            form.setPacote(s.isPacote());
            redirectAttributes.addFlashAttribute("servicoForm", form);
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/admin/catalogo";
    }

    @PostMapping("/catalogo/{id}/excluir")
    public String excluirServico(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            catalogo.excluir(id);
            redirectAttributes.addFlashAttribute("sucesso", "Serviço removido.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/admin/catalogo";
    }

    @PostMapping("/pets")
    public String salvarPetAdmin(@Valid @ModelAttribute("petForm") PetForm form,
                                 BindingResult bindingResult,
                                 @AuthenticationPrincipal UserDetails usuario,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return renderAdmin(usuario, model, "pets", new AgendamentoForm(), new ServicoForm(), form);
        }
        try {
            PetDto dto = api.toPetDto(form);
            if (form.getId() != null) {
                api.atualizarPet(form.getId(), dto);
                redirectAttributes.addFlashAttribute("sucesso", "Pet atualizado.");
            } else {
                api.criarPet(dto);
                redirectAttributes.addFlashAttribute("sucesso", "Pet cadastrado.");
            }
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("erro", api.extrairMensagemErro(ex));
            redirectAttributes.addFlashAttribute("petForm", form);
        }
        return "redirect:/admin/pets";
    }

    @GetMapping("/pets/{id}/editar")
    public String editarPetAdmin(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            PetDto pet = api.buscarPet(id);
            PetForm form = new PetForm();
            form.setId(pet.getId());
            form.setNome(pet.getNome());
            form.setRaca(pet.getRaca());
            form.setNomeDono(pet.getNomeDono());
            form.setPesoKg(pet.getPesoKg());
            redirectAttributes.addFlashAttribute("petForm", form);
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("erro", api.extrairMensagemErro(ex));
        }
        return "redirect:/admin/pets";
    }

    @PostMapping("/pets/{id}/excluir")
    public String excluirPetAdmin(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            api.excluirPet(id);
            redirectAttributes.addFlashAttribute("sucesso", "Pet removido.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("erro", api.extrairMensagemErro(ex));
        }
        return "redirect:/admin/pets";
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
        if (form.getTipoServico() == null || form.getTipoServico().isBlank()) {
            bindingResult.rejectValue("tipoServico", "tipoServico", "Informe o serviço");
        }
        if (bindingResult.hasErrors()) {
            return renderAdmin(usuario, model, "agendamentos", form, new ServicoForm(), new PetForm());
        }
        try {
            AgendamentoDto dto = api.toAgendamentoDtoAdmin(form);
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
            form.setData(ag.getData().toLocalDate().toString());
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

    private String renderAdmin(UserDetails usuario, Model model, String secao,
                               AgendamentoForm agForm, ServicoForm servicoForm, PetForm petForm) {
        model.addAttribute("adminNome", usuario != null ? usuario.getUsername() : "Admin");
        model.addAttribute("secao", secao);
        carregarStatus(model);
        carregarApi(model, agForm, servicoForm, petForm);
        model.addAttribute("catalogoLoja", catalogo.listarParaExibicao());
        return "admin/painel";
    }

    private void carregarStatus(Model model) {
        List<ServiceStatusDto> servicos = infraStatusService.verificarTodos();
        model.addAttribute("servicosInfra", servicos);
        model.addAttribute("totalOnline", infraStatusService.contarOnline(servicos));
        model.addAttribute("totalServicos", servicos.size());
    }

    private void carregarApi(Model model, AgendamentoForm agForm, ServicoForm servicoForm, PetForm petForm) {
        if (!model.containsAttribute("agendamentoForm")) {
            model.addAttribute("agendamentoForm", agForm);
        }
        if (!model.containsAttribute("servicoForm")) {
            model.addAttribute("servicoForm", servicoForm);
        }
        if (!model.containsAttribute("petForm")) {
            model.addAttribute("petForm", petForm);
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

    private BigDecimal parseValor(String valorStr) {
        if (valorStr == null || valorStr.isBlank()) {
            throw new IllegalArgumentException("Valor obrigatório");
        }
        String normalizado = valorStr.trim().replace("R$", "").replace(" ", "").replace(",", ".");
        return new BigDecimal(normalizado);
    }
}
