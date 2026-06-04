package com.petshop.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterForm {

    @NotBlank(message = "Informe o nome de usuário")
    @Size(min = 3, max = 60, message = "Nome deve ter entre 3 e 60 caracteres")
    private String nome;

    @NotBlank(message = "Informe a senha")
    @Size(min = 4, max = 100, message = "Senha deve ter pelo menos 4 caracteres")
    private String senha;

    @NotBlank(message = "Confirme a senha")
    private String confirmarSenha;

    @NotBlank(message = "Informe a chave de acesso")
    private String chaveAcesso;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getConfirmarSenha() {
        return confirmarSenha;
    }

    public void setConfirmarSenha(String confirmarSenha) {
        this.confirmarSenha = confirmarSenha;
    }

    public String getChaveAcesso() {
        return chaveAcesso;
    }

    public void setChaveAcesso(String chaveAcesso) {
        this.chaveAcesso = chaveAcesso;
    }
}
