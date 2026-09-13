/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.almoxarifado.Dto;

/**
 *
 * @author heito
 */
public class TipoOcorrenciaDTO {
    
    private Long idTipoOcorrencia;
    private String nome;

    public TipoOcorrenciaDTO() {
    }

    public Long getIdTipoOcorrencia() {
        return idTipoOcorrencia;
    }

    public void setIdTipoOcorrencia(Long idTipoOcorrencia) {
        this.idTipoOcorrencia = idTipoOcorrencia;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    
}
