/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.almoxarifado.Dto;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 *
 * @author heito
 */
public class EquipamentoDTO {

    private Long idEquipamento;
    private Long idCategoria;
    private Long idLocalizacao;
    private Long idEstadoConservacao;
    private Long idSituacaoEquipamento;

    private String nome;
    private BigDecimal valor;
    private String descricao;

    private boolean ativo;
    private String marca;
    private String modelo;
    private String numeroPatrimonio;
    private String numeroSerie;

    private LocalDate dataAquisicao;
    private LocalDateTime dataCadastro;

    public EquipamentoDTO() {
    }

    public Long getIdEquipamento() {
        return idEquipamento;
    }

    public void setIdEquipamento(Long idEquipamento) {
        this.idEquipamento = idEquipamento;
    }

    public Long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Long idCategoria) {
        this.idCategoria = idCategoria;
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

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
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

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getNumeroPatrimonio() {
        return numeroPatrimonio;
    }

    public void setNumeroPatrimonio(String numeroPatrimonio) {
        this.numeroPatrimonio = numeroPatrimonio;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    public LocalDate getDataAquisicao() {
        return dataAquisicao;
    }

    public void setDataAquisicao(LocalDate dataAquisicao) {
        this.dataAquisicao = dataAquisicao;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Long getIdEstadoConservacao() {
        return idEstadoConservacao;
    }

    public void setIdEstadoConservacao(Long idEstadoConservacao) {
        this.idEstadoConservacao = idEstadoConservacao;
    }

    public Long getIdSituacaoEquipamento() {
        return idSituacaoEquipamento;
    }

    public void setIdSituacaoEquipamento(Long idSituacaoEquipamento) {
        this.idSituacaoEquipamento = idSituacaoEquipamento;
    }

}
