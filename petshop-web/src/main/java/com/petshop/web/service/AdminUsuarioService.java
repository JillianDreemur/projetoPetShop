package com.petshop.web.service;

import com.petshop.web.config.AdminProperties;
import com.petshop.web.entity.AdminUsuario;
import com.petshop.web.repository.AdminUsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AdminUsuarioService implements UserDetailsService {

    private final AdminUsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AdminProperties adminProperties;

    public AdminUsuarioService(AdminUsuarioRepository repository,
                               PasswordEncoder passwordEncoder,
                               AdminProperties adminProperties) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.adminProperties = adminProperties;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AdminUsuario admin = repository.findByNomeIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        return User.builder()
                .username(admin.getNome())
                .password(admin.getSenha())
                .roles("ADMIN")
                .build();
    }

    public void registrar(String nome, String senha, String confirmarSenha, String chaveAcesso) {
        if (!adminProperties.getChaveAcesso().equals(chaveAcesso)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chave de acesso inválida");
        }
        if (!senha.equals(confirmarSenha)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "As senhas não conferem");
        }
        if (repository.existsByNomeIgnoreCase(nome)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este nome de usuário já existe");
        }
        AdminUsuario admin = new AdminUsuario();
        admin.setNome(nome.trim());
        admin.setSenha(passwordEncoder.encode(senha));
        repository.save(admin);
    }
}
