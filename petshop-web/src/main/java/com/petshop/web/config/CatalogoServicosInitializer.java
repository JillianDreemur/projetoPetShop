package com.petshop.web.config;

import com.petshop.web.entity.ServicoOferecido;
import com.petshop.web.repository.ServicoOferecidoRepository;
import java.math.BigDecimal;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class CatalogoServicosInitializer implements ApplicationRunner {

    private final ServicoOferecidoRepository repository;

    public CatalogoServicosInitializer(ServicoOferecidoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (repository.count() > 0) {
            return;
        }
        repository.save(servico("Banho", "50.00", "estetica", "banho_tosa", false));
        repository.save(servico("Tosa", "30.00", "estetica", "banho_tosa", false));
        repository.save(servico("Banho e tosa", "70.00", "estetica", "banho_tosa", true));
        repository.save(servico("Vacina", "100.00", "vacina", null, false));
        repository.save(servico("Remédio para carrapato", "80.00", "remedio", "remedio", false));
        repository.save(servico("Remédio para verme", "60.00", "remedio", "remedio", false));
        repository.save(servico("Remédio carrapato e verme (2 em 1)", "100.00", "remedio", "remedio", true));
    }

    private static ServicoOferecido servico(String nome, String valor, String categoria,
                                            String grupo, boolean pacote) {
        ServicoOferecido s = new ServicoOferecido();
        s.setNome(nome);
        s.setValor(new BigDecimal(valor));
        s.setCategoria(categoria);
        s.setGrupoExclusivo(grupo);
        s.setPacote(pacote);
        return s;
    }
}
