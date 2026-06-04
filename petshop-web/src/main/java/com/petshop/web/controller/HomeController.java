package com.petshop.web.controller;

import com.petshop.web.client.GatewayApiClient;
import com.petshop.web.dto.AgendamentoDto;
import com.petshop.web.dto.AgendamentoForm;
import com.petshop.web.dto.PetDto;
import com.petshop.web.dto.PetForm;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    private final GatewayApiClient api;

    public HomeController(GatewayApiClient api) {
        this.api = api;
    }

    @GetMapping({"/", "/inicio"})
    public String inicio(Model model) {
        return renderPagina(model, "inicio");
    }

    @GetMapping("/pets")
    public String paginaPets(Model model) {
        return renderPagina(model, "pets");
    }

    @GetMapping("/agendamentos")
    public String paginaAgendamentos(Model model) {
        return renderPagina(model, "agendamentos");
    }

    @PostMapping("/pets")
    public String salvarPet(@Valid @ModelAttribute("petForm") PetForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("secao", "pets");
            prepararFormularios(model);
            carregarListas(model);
            return "index";
        }
        try {
            PetDto dto = api.toPetDto(form);
            if (form.getId() != null) {
                api.atualizarPet(form.getId(), dto);
                redirectAttributes.addFlashAttribute("sucesso", "Pet atualizado com sucesso.");
            } else {
                api.criarPet(dto);
                redirectAttributes.addFlashAttribute("sucesso", "Pet cadastrado com sucesso.");
            }
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("erro", api.extrairMensagemErro(ex));
            redirectAttributes.addFlashAttribute("petForm", form);
        }
        return "redirect:/pets";
    }

    @GetMapping("/pets/{id}/editar")
    public String editarPet(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
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
        return "redirect:/pets";
    }

    @GetMapping("/pets/novo")
    public String novoPet(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("petForm", new PetForm());
        return "redirect:/pets";
    }

    @PostMapping("/pets/{id}/excluir")
    public String excluirPet(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            api.excluirPet(id);
            redirectAttributes.addFlashAttribute("sucesso", "Pet removido.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("erro", api.extrairMensagemErro(ex));
        }
        return "redirect:/pets";
    }

    @PostMapping("/agendamentos")
    public String salvarAgendamento(@Valid @ModelAttribute("agendamentoForm") AgendamentoForm form,
                                    BindingResult bindingResult,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("secao", "agendamentos");
            prepararFormularios(model);
            carregarListas(model);
            return "index";
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
        return "redirect:/agendamentos";
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
        return "redirect:/agendamentos";
    }

    @GetMapping("/agendamentos/novo")
    public String novoAgendamento(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("agendamentoForm", new AgendamentoForm());
        return "redirect:/agendamentos";
    }

    @PostMapping("/agendamentos/{id}/cancelar")
    public String cancelarAgendamento(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            api.excluirAgendamento(id);
            redirectAttributes.addFlashAttribute("sucesso", "Agendamento cancelado.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("erro", api.extrairMensagemErro(ex));
        }
        return "redirect:/agendamentos";
    }

    private String renderPagina(Model model, String secao) {
        model.addAttribute("secao", secao);
        prepararFormularios(model);
        carregarListas(model);
        return "index";
    }

    private void prepararFormularios(Model model) {
        if (!model.containsAttribute("petForm")) {
            model.addAttribute("petForm", new PetForm());
        }
        if (!model.containsAttribute("agendamentoForm")) {
            model.addAttribute("agendamentoForm", new AgendamentoForm());
        }
    }

    private void carregarListas(Model model) {
        try {
            List<PetDto> pets = api.listarPets();
            List<AgendamentoDto> agendamentos = api.listarAgendamentos();
            model.addAttribute("pets", pets);
            model.addAttribute("agendamentos", agendamentos);
            model.addAttribute("petNomes", mapaNomesPets(pets));
        } catch (Exception ex) {
            model.addAttribute("pets", List.of());
            model.addAttribute("agendamentos", List.of());
            model.addAttribute("petNomes", Map.of());
            model.addAttribute("offline",
                    "Verifique se Eureka, Gateway e os microsserviços estão rodando.");
            if (!model.containsAttribute("erro")) {
                model.addAttribute("erro", api.extrairMensagemErro(ex));
            }
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
