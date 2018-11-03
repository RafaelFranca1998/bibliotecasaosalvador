package com.example.rafael_cruz.bibliotecasaosalvador.model;

public class Usuario {
    private String idUsuario;
    private String nome;
    private String sobreNome;
    private String email;
    private String senha;
    private String linkImgAccount;

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobreNome() {
        return sobreNome;
    }

    public void setSobreNome(String sobreNome) {
        this.sobreNome = sobreNome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLinkImgAccount() {
        return linkImgAccount;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setLinkImgAccount(String linkImgAccount) {
        this.linkImgAccount = linkImgAccount;
    }
}
