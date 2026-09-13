/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.almoxarifado.Dto;

/**
 *
 * @author heito
 */
public class SituacaoEquipamentoDTO {
    
    private Long idSituacaoEquipamento;
    private String nome;

    public SituacaoEquipamentoDTO() {
    }

    public Long getIdSituacaoEquipamento() {
        return idSituacaoEquipamento;
    }

    public void setIdSituacaoEquipamento(Long idSituacaoEquipamento) {
        this.idSituacaoEquipamento = idSituacaoEquipamento;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    
}
