/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.almoxarifado.Dto;

/**
 *
 * @author heito
 */
public class StatusOcorrenciaDTO {
    
    private Long idStatusOcorrencia;
    private String nome;

    public StatusOcorrenciaDTO() {
    }

    public Long getIdStatusOcorrencia() {
        return idStatusOcorrencia;
    }

    public void setIdStatusOcorrencia(Long idStatusOcorrencia) {
        this.idStatusOcorrencia = idStatusOcorrencia;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    
}
