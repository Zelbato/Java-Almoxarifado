/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.almoxarifado.Dto;

import java.time.LocalDateTime;

/**
 *
 * @author heito
 */
public class MovimentaçãoDTO {

    private Long idMovimentacao;

    private Long idEquipamento;
    private Long idFuncionario;
    private Long idUsuarioRetirada;
    private Long idUsuarioDevolucao;
    private Long idStatusMovimentacao;

    private LocalDateTime dataRetirada;
    private LocalDateTime dataDevolucao;
    private LocalDateTime dataPrevistaDevolucao;

    private String finalidade;
    private String observacaoRetirada;
    private String observacaoDevolucao;

    public MovimentaçãoDTO() {
    }

    public Long getIdMovimentacao() {
        return idMovimentacao;
    }

    public void setIdMovimentacao(Long idMovimentacao) {
        this.idMovimentacao = idMovimentacao;
    }

    public Long getIdEquipamento() {
        return idEquipamento;
    }

    public void setIdEquipamento(Long idEquipamento) {
        this.idEquipamento = idEquipamento;
    }

    public Long getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(Long idFuncionario) {
        this.idFuncionario = idFuncionario;
    }

    public Long getIdUsuarioRetirada() {
        return idUsuarioRetirada;
    }

    public void setIdUsuarioRetirada(Long idUsuarioRetirada) {
        this.idUsuarioRetirada = idUsuarioRetirada;
    }

    public Long getIdUsuarioDevolucao() {
        return idUsuarioDevolucao;
    }

    public void setIdUsuarioDevolucao(Long idUsuarioDevolucao) {
        this.idUsuarioDevolucao = idUsuarioDevolucao;
    }

    public Long getIdStatusMovimentacao() {
        return idStatusMovimentacao;
    }

    public void setIdStatusMovimentacao(Long idStatusMovimentacao) {
        this.idStatusMovimentacao = idStatusMovimentacao;
    }

    public LocalDateTime getDataRetirada() {
        return dataRetirada;
    }

    public void setDataRetirada(LocalDateTime dataRetirada) {
        this.dataRetirada = dataRetirada;
    }

    public LocalDateTime getDataDevolucao() {
        return dataDevolucao;
    }

    public void setDataDevolucao(LocalDateTime dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }

    public LocalDateTime getDataPrevistaDevolucao() {
        return dataPrevistaDevolucao;
    }

    public void setDataPrevistaDevolucao(LocalDateTime dataPrevistaDevolucao) {
        this.dataPrevistaDevolucao = dataPrevistaDevolucao;
    }

    public String getFinalidade() {
        return finalidade;
    }

    public void setFinalidade(String finalidade) {
        this.finalidade = finalidade;
    }

    public String getObservacaoRetirada() {
        return observacaoRetirada;
    }

    public void setObservacaoRetirada(String observacaoRetirada) {
        this.observacaoRetirada = observacaoRetirada;
    }

    public String getObservacaoDevolucao() {
        return observacaoDevolucao;
    }

    public void setObservacaoDevolucao(String observacaoDevolucao) {
        this.observacaoDevolucao = observacaoDevolucao;
    }

    
}
