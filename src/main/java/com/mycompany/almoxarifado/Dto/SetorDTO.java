/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.almoxarifado.Dto;

/**
 *
 * @author heito
 */
public class SetorDTO {
    
    private Long IdSetor;
    private String nome;
    private String descricao;
    private boolean ativo;

    public SetorDTO() {
    }

    public SetorDTO(Long IdSetor, String nome, String descricao, boolean ativo) {
        this.IdSetor = IdSetor;
        this.nome = nome;
        this.descricao = descricao;
        this.ativo = ativo;
    }

    public Long getIdSetor() {
        return IdSetor;
    }

    public void setIdSetor(Long IdSetor) {
        this.IdSetor = IdSetor;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
    
}
