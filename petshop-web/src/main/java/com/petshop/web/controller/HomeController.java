package com.petshop.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petshop.web.client.GatewayApiClient;
import com.petshop.web.dto.AgendamentoDto;
import com.petshop.web.dto.AgendamentoForm;
import com.petshop.web.dto.PetDto;
import com.petshop.web.dto.PetForm;
import com.petshop.web.entity.ServicoOferecido;
import com.petshop.web.service.AgendamentoMontador;
import com.petshop.web.service.ServicoCatalogoService;
import com.petshop.web.service.VagaBanhoTosaService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.Comparator;
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
    private final ServicoCatalogoService catalogo;
    private final AgendamentoMontador agendamentoMontador;
    private final VagaBanhoTosaService vagaBanhoTosa;
    private final ObjectMapper objectMapper;

    public HomeController(GatewayApiClient api, ServicoCatalogoService catalogo,
                          AgendamentoMontador agendamentoMontador,
                          VagaBanhoTosaService vagaBanhoTosa,
                          ObjectMapper objectMapper) {
        this.api = api;
        this.catalogo = catalogo;
        this.agendamentoMontador = agendamentoMontador;
        this.vagaBanhoTosa = vagaBanhoTosa;
        this.objectMapper = objectMapper;
    }

    @GetMapping({"/", "/inicio"})
    public String inicio(Model model) {
        model.addAttribute("secao", "inicio");
        model.addAttribute("servicos", catalogo.listarParaExibicao());
        return "index";
    }

    @GetMapping("/pets")
    public String paginaPets(Model model) {
        model.addAttribute("secao", "pets");
        prepararFormularios(model);
        return "index";
    }

    @GetMapping("/agendamentos")
    public String paginaAgendamentos(Model model) {
        model.addAttribute("secao", "agendamentos");
        model.addAttribute("catalogoServicos", catalogo.listarParaExibicao());
        model.addAttribute("dataMinima", LocalDate.now().toString());
        model.addAttribute("limiteBanhoTosa", VagaBanhoTosaService.LIMITE_DIARIO);
        prepararFormularios(model);
        carregarDadosAgendamentos(model);
        return "index";
    }

    @PostMapping("/pets")
    public String salvarPet(@Valid @ModelAttribute("petForm") PetForm form,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("secao", "pets");
            prepararFormularios(model);
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
            redirectAttributes.addFlashAttribute("petForm", form);
            redirectAttributes.addFlashAttribute("aviso", api.mensagemOperacaoCliente(ex));
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
        } catch (Exception ignored) {
            // Falhas de API não são exibidas na área do cliente
        }
        return "redirect:/pets";
    }

    @GetMapping("/pets/novo")
    public String novoPet(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("petForm", new PetForm());
        return "redirect:/pets";
    }

    @PostMapping("/agendamentos")
    public String salvarAgendamento(@Valid @ModelAttribute("agendamentoForm") AgendamentoForm form,
                                    BindingResult bindingResult,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        List<AgendamentoDto> existentes = listarAgendamentosOrdenados();
        validarAgendamentoCliente(form, bindingResult, existentes);
        if (bindingResult.hasErrors()) {
            model.addAttribute("secao", "agendamentos");
            model.addAttribute("catalogoServicos", catalogo.listarParaExibicao());
            model.addAttribute("dataMinima", LocalDate.now().toString());
            model.addAttribute("limiteBanhoTosa", VagaBanhoTosaService.LIMITE_DIARIO);
            prepararFormularios(model);
            carregarDadosAgendamentos(model, existentes);
            return "index";
        }
        try {
            AgendamentoDto dto = agendamentoMontador.montarParaApi(form);
            api.criarAgendamento(dto);
            redirectAttributes.addFlashAttribute("sucesso", "Agendamento confirmado. Pagamento registrado.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("agendamentoForm", form);
            redirectAttributes.addFlashAttribute("aviso", api.mensagemOperacaoCliente(ex));
        }
        return "redirect:/agendamentos";
    }

    @GetMapping("/agendamentos/novo")
    public String novoAgendamento(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("agendamentoForm", new AgendamentoForm());
        return "redirect:/agendamentos";
    }

    private void validarAgendamentoCliente(AgendamentoForm form, BindingResult bindingResult,
                                           List<AgendamentoDto> existentes) {
        if (form.getServicosSelecionados() == null || form.getServicosSelecionados().isEmpty()) {
            bindingResult.rejectValue("servicosSelecionados", "servicos.vazio",
                    "Selecione ao menos um serviço");
        }
        if (form.getFormaPagamento() == null || form.getFormaPagamento().isBlank()) {
            bindingResult.rejectValue("formaPagamento", "pagamento.obrigatorio",
                    "Escolha a forma de pagamento");
        }
        LocalDate data = null;
        if (form.getData() != null && !form.getData().isBlank()) {
            try {
                data = LocalDate.parse(form.getData());
                if (data.isBefore(LocalDate.now())) {
                    bindingResult.rejectValue("data", "data.passado",
                            "A data não pode ser anterior a hoje");
                }
            } catch (Exception ex) {
                bindingResult.rejectValue("data", "data.invalida", "Data inválida");
            }
        }

        if (data != null && !bindingResult.hasFieldErrors("data")
                && form.getServicosSelecionados() != null && !form.getServicosSelecionados().isEmpty()) {
            List<ServicoOferecido> selecionados = catalogo.buscarPorIds(form.getServicosSelecionados());
            if (vagaBanhoTosa.selecaoIncluiBanhoTosa(selecionados)
                    && vagaBanhoTosa.diaCheio(data, existentes, null)) {
                bindingResult.rejectValue("data", "data.cheio",
                        vagaBanhoTosa.mensagemDiaCheio(data, existentes, null));
            }
        }
    }

    private void prepararFormularios(Model model) {
        if (!model.containsAttribute("petForm")) {
            model.addAttribute("petForm", new PetForm());
        }
        if (!model.containsAttribute("agendamentoForm")) {
            model.addAttribute("agendamentoForm", new AgendamentoForm());
        }
    }

    private void carregarDadosAgendamentos(Model model) {
        carregarDadosAgendamentos(model, listarAgendamentosOrdenados());
    }

    private void carregarDadosAgendamentos(Model model, List<AgendamentoDto> agendamentos) {
        try {
            List<PetDto> pets = api.listarPets();
            model.addAttribute("pets", pets);
            model.addAttribute("agendamentos", agendamentos);
            model.addAttribute("petNomes", mapaNomesPets(pets));
            model.addAttribute("ocupacaoBanhoTosaJson", serializarOcupacao(agendamentos));
        } catch (Exception ignored) {
            model.addAttribute("pets", List.of());
            model.addAttribute("agendamentos", List.of());
            model.addAttribute("petNomes", Map.of());
            model.addAttribute("ocupacaoBanhoTosaJson", "{}");
        }
    }

    private List<AgendamentoDto> listarAgendamentosOrdenados() {
        try {
            return api.listarAgendamentos().stream()
                    .sorted(Comparator.comparing(AgendamentoDto::getData))
                    .toList();
        } catch (Exception ex) {
            return List.of();
        }
    }

    private String serializarOcupacao(List<AgendamentoDto> agendamentos) {
        try {
            return objectMapper.writeValueAsString(vagaBanhoTosa.mapaOcupacaoPorData(agendamentos));
        } catch (JsonProcessingException ex) {
            return "{}";
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
