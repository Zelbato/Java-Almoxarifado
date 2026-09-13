/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.almoxarifado.Dto;

/**
 *
 * @author heito
 */
public class LocalizacaoDTO {
    
    private Long idLocalizacao;
    private String nome;
    private Integer corredor;
    private Integer prateleira;
    private boolean ativo;

    public LocalizacaoDTO() {
    }

    public Long getIdLocalizacao() {
        return idLocalizacao;
    }

    public void setIdLocalizacao(Long idLocalizacao) {
        this.idLocalizacao = idLocalizacao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getCorredor() {
        return corredor;
    }

    public void setCorredor(Integer corredor) {
        this.corredor = corredor;
    }

    public Integer getPrateleira() {
        return prateleira;
    }

    public void setPrateleira(Integer prateleira) {
        this.prateleira = prateleira;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
    
    
}
