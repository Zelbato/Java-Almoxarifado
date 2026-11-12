package com.mycompany.almoxarifado.Dto;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Indicadores consolidados exibidos no painel do almoxarifado.
 */
public class DashboardDTO {

    private long totalEquipamentos;
    private long equipamentosDisponiveis;
    private long equipamentosEmprestados;
    private long equipamentosEmManutencao;
    private long movimentacoesEmAberto;
    private long movimentacoesAtrasadas;
    private Map<String, Integer> equipamentosPorSituacao = new LinkedHashMap<>();
    private Map<String, Integer> movimentacoesPorMes = new LinkedHashMap<>();

    // usados na tela de relatórios
    private long equipamentosDanificados;
    private long manutencoesPendentes;
    private long ocorrenciasAbertas;
    private Map<String, Integer> equipamentosPorCategoria = new LinkedHashMap<>();
    private Map<String, Integer> manutencoesPorTipo = new LinkedHashMap<>();
    private Map<String, Integer> manutencoesPorStatus = new LinkedHashMap<>();
    private Map<String, Integer> ocorrenciasPorTipo = new LinkedHashMap<>();
    private Map<String, Integer> ocorrenciasPorStatus = new LinkedHashMap<>();
    private Map<String, Integer> equipamentosMaisUtilizados = new LinkedHashMap<>();

    public long getEquipamentosDanificados() {
        return equipamentosDanificados;
    }

    public void setEquipamentosDanificados(long equipamentosDanificados) {
        this.equipamentosDanificados = equipamentosDanificados;
    }

    public long getManutencoesPendentes() {
        return manutencoesPendentes;
    }

    public void setManutencoesPendentes(long manutencoesPendentes) {
        this.manutencoesPendentes = manutencoesPendentes;
    }

    public long getOcorrenciasAbertas() {
        return ocorrenciasAbertas;
    }

    public void setOcorrenciasAbertas(long ocorrenciasAbertas) {
        this.ocorrenciasAbertas = ocorrenciasAbertas;
    }

    public Map<String, Integer> getEquipamentosPorCategoria() {
        return equipamentosPorCategoria;
    }

    public void setEquipamentosPorCategoria(Map<String, Integer> equipamentosPorCategoria) {
        this.equipamentosPorCategoria = new LinkedHashMap<>(equipamentosPorCategoria);
    }

    public Map<String, Integer> getManutencoesPorTipo() {
        return manutencoesPorTipo;
    }

    public void setManutencoesPorTipo(Map<String, Integer> manutencoesPorTipo) {
        this.manutencoesPorTipo = new LinkedHashMap<>(manutencoesPorTipo);
    }

    public Map<String, Integer> getManutencoesPorStatus() {
        return manutencoesPorStatus;
    }

    public void setManutencoesPorStatus(Map<String, Integer> manutencoesPorStatus) {
        this.manutencoesPorStatus = new LinkedHashMap<>(manutencoesPorStatus);
    }

    public Map<String, Integer> getOcorrenciasPorTipo() {
        return ocorrenciasPorTipo;
    }

    public void setOcorrenciasPorTipo(Map<String, Integer> ocorrenciasPorTipo) {
        this.ocorrenciasPorTipo = new LinkedHashMap<>(ocorrenciasPorTipo);
    }

    public Map<String, Integer> getOcorrenciasPorStatus() {
        return ocorrenciasPorStatus;
    }

    public void setOcorrenciasPorStatus(Map<String, Integer> ocorrenciasPorStatus) {
        this.ocorrenciasPorStatus = new LinkedHashMap<>(ocorrenciasPorStatus);
    }

    public Map<String, Integer> getEquipamentosMaisUtilizados() {
        return equipamentosMaisUtilizados;
    }

    public void setEquipamentosMaisUtilizados(Map<String, Integer> equipamentosMaisUtilizados) {
        this.equipamentosMaisUtilizados = new LinkedHashMap<>(equipamentosMaisUtilizados);
    }

    public long getTotalEquipamentos() {
        return totalEquipamentos;
    }

    public void setTotalEquipamentos(long totalEquipamentos) {
        this.totalEquipamentos = totalEquipamentos;
    }

    public long getEquipamentosDisponiveis() {
        return equipamentosDisponiveis;
    }

    public void setEquipamentosDisponiveis(long equipamentosDisponiveis) {
        this.equipamentosDisponiveis = equipamentosDisponiveis;
    }

    public long getEquipamentosEmprestados() {
        return equipamentosEmprestados;
    }

    public void setEquipamentosEmprestados(long equipamentosEmprestados) {
        this.equipamentosEmprestados = equipamentosEmprestados;
    }

    public long getEquipamentosEmManutencao() {
        return equipamentosEmManutencao;
    }

    public void setEquipamentosEmManutencao(long equipamentosEmManutencao) {
        this.equipamentosEmManutencao = equipamentosEmManutencao;
    }

    public long getMovimentacoesEmAberto() {
        return movimentacoesEmAberto;
    }

    public void setMovimentacoesEmAberto(long movimentacoesEmAberto) {
        this.movimentacoesEmAberto = movimentacoesEmAberto;
    }

    public long getMovimentacoesAtrasadas() {
        return movimentacoesAtrasadas;
    }

    public void setMovimentacoesAtrasadas(long movimentacoesAtrasadas) {
        this.movimentacoesAtrasadas = movimentacoesAtrasadas;
    }

    public Map<String, Integer> getEquipamentosPorSituacao() {
        return equipamentosPorSituacao;
    }

    public void setEquipamentosPorSituacao(
            Map<String, Integer> equipamentosPorSituacao
    ) {
        this.equipamentosPorSituacao = new LinkedHashMap<>(
                equipamentosPorSituacao
        );
    }

    public Map<String, Integer> getMovimentacoesPorMes() {
        return movimentacoesPorMes;
    }

    public void setMovimentacoesPorMes(
            Map<String, Integer> movimentacoesPorMes
    ) {
        this.movimentacoesPorMes = new LinkedHashMap<>(
                movimentacoesPorMes
        );
    }
}
