/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.almoxarifado.Dto;

/**
 *
 * @author heito
 */
public class StatusManutencaoDTO {
    
    private Long idStatusManutencao;
    private String nome;

    public StatusManutencaoDTO() {
    }

    public Long getIdStatusManutencao() {
        return idStatusManutencao;
    }

    public void setIdStatusManutencao(Long idStatusManutencao) {
        this.idStatusManutencao = idStatusManutencao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }      
    
}
