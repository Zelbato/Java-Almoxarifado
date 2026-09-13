/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.almoxarifado.Dto;

/**
 *
 * @author heito
 */
public class StatusMovimentacaoDTO {
    
    private Long idStatusMovimentacao;
    private String nome;

    public StatusMovimentacaoDTO() {
    }

    public Long getIdStatusMovimentacao() {
        return idStatusMovimentacao;
    }

    public void setIdStatusMovimentacao(Long idStatusMovimentacao) {
        this.idStatusMovimentacao = idStatusMovimentacao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    
}
