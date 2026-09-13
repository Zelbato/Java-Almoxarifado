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
public class ManutencaoDTO {

    private Long idManutencao;

    private Long idEquipamento;
    private Long idUsuario;
    private Long idTipoManutencao;
    private Long idStatusManutencao;

    private String descricaoProblema;
    private String servicoRealizado;
    private String tecnicoResponsavel;

    private LocalDateTime dataEntrada;
    private LocalDateTime dataPrevistaConclusao;
    private LocalDateTime dataConclusao;

    private String observacao;

    public ManutencaoDTO() {
    }

    public Long getIdManutencao() {
        return idManutencao;
    }

    public void setIdManutencao(Long idManutencao) {
        this.idManutencao = idManutencao;
    }

    public Long getIdEquipamento() {
        return idEquipamento;
    }

    public void setIdEquipamento(Long idEquipamento) {
        this.idEquipamento = idEquipamento;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Long getIdTipoManutencao() {
        return idTipoManutencao;
    }

    public void setIdTipoManutencao(Long idTipoManutencao) {
        this.idTipoManutencao = idTipoManutencao;
    }

    public Long getIdStatusManutencao() {
        return idStatusManutencao;
    }

    public void setIdStatusManutencao(Long idStatusManutencao) {
        this.idStatusManutencao = idStatusManutencao;
    }

    public String getDescricaoProblema() {
        return descricaoProblema;
    }

    public void setDescricaoProblema(String descricaoProblema) {
        this.descricaoProblema = descricaoProblema;
    }

    public String getServicoRealizado() {
        return servicoRealizado;
    }

    public void setServicoRealizado(String servicoRealizado) {
        this.servicoRealizado = servicoRealizado;
    }

    public String getTecnicoResponsavel() {
        return tecnicoResponsavel;
    }

    public void setTecnicoResponsavel(String tecnicoResponsavel) {
        this.tecnicoResponsavel = tecnicoResponsavel;
    }

    public LocalDateTime getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(LocalDateTime dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public LocalDateTime getDataPrevistaConclusao() {
        return dataPrevistaConclusao;
    }

    public void setDataPrevistaConclusao(LocalDateTime dataPrevistaConclusao) {
        this.dataPrevistaConclusao = dataPrevistaConclusao;
    }

    public LocalDateTime getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(LocalDateTime dataConclusao) {
        this.dataConclusao = dataConclusao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
    
    

}
