package com.mycompany.almoxarifado.View;

import com.mycompany.almoxarifado.Dto.DashboardDTO;
import com.mycompany.almoxarifado.Service.AuthSession;
import com.mycompany.almoxarifado.Service.DashboardService;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/** Formulário de indicadores e gráficos do almoxarifado. */
public class DashboardView extends javax.swing.JFrame {

    private final DashboardService dashboardService;

    public DashboardView() {
        AuthSession.exigirAutenticacao();
        dashboardService = new DashboardService();
        initComponents();
        carregarIndicadores();
    }

    private void carregarIndicadores() {
        btnAtualizar.setEnabled(false);

        try {
            preencherIndicadores(dashboardService.carregarIndicadores());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível carregar os indicadores do Dashboard.",
                    "Erro ao carregar Dashboard",
                    JOptionPane.ERROR_MESSAGE
            );
        } finally {
            btnAtualizar.setEnabled(true);
        }
    }

    private void preencherIndicadores(DashboardDTO dashboard) {
        lblTotalEquipamentosValor.setText(String.valueOf(dashboard.getTotalEquipamentos()));
        lblDisponiveisValor.setText(String.valueOf(dashboard.getEquipamentosDisponiveis()));
        lblEmprestadosValor.setText(String.valueOf(dashboard.getEquipamentosEmprestados()));
        lblManutencaoValor.setText(String.valueOf(dashboard.getEquipamentosEmManutencao()));
        lblSituacaoMovimentacoes.setText(
                "Movimentações em aberto: "
                + dashboard.getMovimentacoesEmAberto()
                + "   •   Movimentações atrasadas: "
                + dashboard.getMovimentacoesAtrasadas()
        );

        graficoSituacao.setDados(dashboard.getEquipamentosPorSituacao());
        graficoMovimentacoes.setDados(dashboard.getMovimentacoesPorMes());
    }

    private void btnAtualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtualizarActionPerformed
        carregarIndicadores();
    }//GEN-LAST:event_btnAtualizarActionPerformed

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlPrincipal = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        lblSubtitulo = new javax.swing.JLabel();
        btnAtualizar = new javax.swing.JButton();
        pnlCardTotal = new javax.swing.JPanel();
        lblTotalEquipamentosTitulo = new javax.swing.JLabel();
        lblTotalEquipamentosValor = new javax.swing.JLabel();
        pnlCardDisponiveis = new javax.swing.JPanel();
        lblDisponiveisTitulo = new javax.swing.JLabel();
        lblDisponiveisValor = new javax.swing.JLabel();
        pnlCardEmprestados = new javax.swing.JPanel();
        lblEmprestadosTitulo = new javax.swing.JLabel();
        lblEmprestadosValor = new javax.swing.JLabel();
        pnlCardManutencao = new javax.swing.JPanel();
        lblManutencaoTitulo = new javax.swing.JLabel();
        lblManutencaoValor = new javax.swing.JLabel();
        lblSituacaoMovimentacoes = new javax.swing.JLabel();
        pnlGraficoSituacao = new javax.swing.JPanel();
        lblGraficoSituacaoTitulo = new javax.swing.JLabel();
        lblGraficoSituacaoDescricao = new javax.swing.JLabel();
        graficoSituacao = new com.mycompany.almoxarifado.View.BarChartPanel();
        pnlGraficoMovimentacoes = new javax.swing.JPanel();
        lblGraficoMovimentacoesTitulo = new javax.swing.JLabel();
        lblGraficoMovimentacoesDescricao = new javax.swing.JLabel();
        graficoMovimentacoes = new com.mycompany.almoxarifado.View.LineChartPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Almoxarifado - Dashboard");

        pnlPrincipal.setBackground(new java.awt.Color(248, 249, 250));
        lblTitulo.setText("Dashboard");
        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(0, 78, 174));

        lblSubtitulo.setText("Visão geral dos equipamentos e movimentações do almoxarifado");
        lblSubtitulo.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblSubtitulo.setForeground(new java.awt.Color(108, 117, 125));

        btnAtualizar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAtualizar.setText("Atualizar");
        btnAtualizar.setBackground(new java.awt.Color(0, 78, 174));
        btnAtualizar.setForeground(new java.awt.Color(255, 255, 255));
        btnAtualizar.setFocusPainted(false);
        btnAtualizar.addActionListener(this::btnAtualizarActionPerformed);

        pnlCardTotal.setBackground(new java.awt.Color(255, 255, 255));
        pnlCardTotal.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(218, 223, 229), 1, true));
        lblTotalEquipamentosTitulo.setText("Total de equipamentos");
        lblTotalEquipamentosTitulo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTotalEquipamentosTitulo.setForeground(new java.awt.Color(108, 117, 125));

        lblTotalEquipamentosValor.setText("0");
        lblTotalEquipamentosValor.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblTotalEquipamentosValor.setForeground(new java.awt.Color(0, 78, 174));

        javax.swing.GroupLayout pnlCardTotalLayout = new javax.swing.GroupLayout(pnlCardTotal);
        pnlCardTotal.setLayout(pnlCardTotalLayout);
        pnlCardTotalLayout.setHorizontalGroup(
            pnlCardTotalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardTotalLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlCardTotalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTotalEquipamentosTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotalEquipamentosValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pnlCardTotalLayout.setVerticalGroup(
            pnlCardTotalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardTotalLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblTotalEquipamentosTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(lblTotalEquipamentosValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );

        pnlCardDisponiveis.setBackground(new java.awt.Color(255, 255, 255));
        pnlCardDisponiveis.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(218, 223, 229), 1, true));
        lblDisponiveisTitulo.setText("Disponíveis");
        lblDisponiveisTitulo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblDisponiveisTitulo.setForeground(new java.awt.Color(108, 117, 125));

        lblDisponiveisValor.setText("0");
        lblDisponiveisValor.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblDisponiveisValor.setForeground(new java.awt.Color(53, 158, 77));

        javax.swing.GroupLayout pnlCardDisponiveisLayout = new javax.swing.GroupLayout(pnlCardDisponiveis);
        pnlCardDisponiveis.setLayout(pnlCardDisponiveisLayout);
        pnlCardDisponiveisLayout.setHorizontalGroup(
            pnlCardDisponiveisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardDisponiveisLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlCardDisponiveisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDisponiveisTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDisponiveisValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pnlCardDisponiveisLayout.setVerticalGroup(
            pnlCardDisponiveisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardDisponiveisLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblDisponiveisTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(lblDisponiveisValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );

        pnlCardEmprestados.setBackground(new java.awt.Color(255, 255, 255));
        pnlCardEmprestados.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(218, 223, 229), 1, true));
        lblEmprestadosTitulo.setText("Emprestados");
        lblEmprestadosTitulo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblEmprestadosTitulo.setForeground(new java.awt.Color(108, 117, 125));

        lblEmprestadosValor.setText("0");
        lblEmprestadosValor.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblEmprestadosValor.setForeground(new java.awt.Color(228, 146, 10));

        javax.swing.GroupLayout pnlCardEmprestadosLayout = new javax.swing.GroupLayout(pnlCardEmprestados);
        pnlCardEmprestados.setLayout(pnlCardEmprestadosLayout);
        pnlCardEmprestadosLayout.setHorizontalGroup(
            pnlCardEmprestadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardEmprestadosLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlCardEmprestadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblEmprestadosTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEmprestadosValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pnlCardEmprestadosLayout.setVerticalGroup(
            pnlCardEmprestadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardEmprestadosLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblEmprestadosTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(lblEmprestadosValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );

        pnlCardManutencao.setBackground(new java.awt.Color(255, 255, 255));
        pnlCardManutencao.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(218, 223, 229), 1, true));
        lblManutencaoTitulo.setText("Em manutenção");
        lblManutencaoTitulo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblManutencaoTitulo.setForeground(new java.awt.Color(108, 117, 125));

        lblManutencaoValor.setText("0");
        lblManutencaoValor.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblManutencaoValor.setForeground(new java.awt.Color(214, 62, 50));

        javax.swing.GroupLayout pnlCardManutencaoLayout = new javax.swing.GroupLayout(pnlCardManutencao);
        pnlCardManutencao.setLayout(pnlCardManutencaoLayout);
        pnlCardManutencaoLayout.setHorizontalGroup(
            pnlCardManutencaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardManutencaoLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlCardManutencaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblManutencaoTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblManutencaoValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pnlCardManutencaoLayout.setVerticalGroup(
            pnlCardManutencaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardManutencaoLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblManutencaoTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(lblManutencaoValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );

        lblSituacaoMovimentacoes.setText("Movimentações em aberto: 0   •   Movimentações atrasadas: 0");
        lblSituacaoMovimentacoes.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblSituacaoMovimentacoes.setForeground(new java.awt.Color(108, 117, 125));

        pnlGraficoSituacao.setBackground(new java.awt.Color(255, 255, 255));
        pnlGraficoSituacao.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(218, 223, 229), 1, true));
        lblGraficoSituacaoTitulo.setText("Equipamentos por situação");
        lblGraficoSituacaoTitulo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblGraficoSituacaoTitulo.setForeground(new java.awt.Color(0, 78, 174));

        lblGraficoSituacaoDescricao.setText("Distribuição atual dos equipamentos cadastrados");
        lblGraficoSituacaoDescricao.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblGraficoSituacaoDescricao.setForeground(new java.awt.Color(108, 117, 125));

        javax.swing.GroupLayout pnlGraficoSituacaoLayout = new javax.swing.GroupLayout(pnlGraficoSituacao);
        pnlGraficoSituacao.setLayout(pnlGraficoSituacaoLayout);
        pnlGraficoSituacaoLayout.setHorizontalGroup(
            pnlGraficoSituacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGraficoSituacaoLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlGraficoSituacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblGraficoSituacaoTitulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblGraficoSituacaoDescricao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(graficoSituacao, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
                .addGap(16, 16, 16))
        );
        pnlGraficoSituacaoLayout.setVerticalGroup(
            pnlGraficoSituacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGraficoSituacaoLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblGraficoSituacaoTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(lblGraficoSituacaoDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(graficoSituacao, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                .addGap(14, 14, 14))
        );

        pnlGraficoMovimentacoes.setBackground(new java.awt.Color(255, 255, 255));
        pnlGraficoMovimentacoes.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(218, 223, 229), 1, true));
        lblGraficoMovimentacoesTitulo.setText("Movimentações por mês");
        lblGraficoMovimentacoesTitulo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblGraficoMovimentacoesTitulo.setForeground(new java.awt.Color(0, 78, 174));

        lblGraficoMovimentacoesDescricao.setText("Retiradas registradas nos últimos seis meses");
        lblGraficoMovimentacoesDescricao.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblGraficoMovimentacoesDescricao.setForeground(new java.awt.Color(108, 117, 125));

        javax.swing.GroupLayout pnlGraficoMovimentacoesLayout = new javax.swing.GroupLayout(pnlGraficoMovimentacoes);
        pnlGraficoMovimentacoes.setLayout(pnlGraficoMovimentacoesLayout);
        pnlGraficoMovimentacoesLayout.setHorizontalGroup(
            pnlGraficoMovimentacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGraficoMovimentacoesLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlGraficoMovimentacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblGraficoMovimentacoesTitulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblGraficoMovimentacoesDescricao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(graficoMovimentacoes, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
                .addGap(16, 16, 16))
        );
        pnlGraficoMovimentacoesLayout.setVerticalGroup(
            pnlGraficoMovimentacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGraficoMovimentacoesLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblGraficoMovimentacoesTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(lblGraficoMovimentacoesDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(graficoMovimentacoes, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                .addGap(14, 14, 14))
        );

        javax.swing.GroupLayout pnlPrincipalLayout = new javax.swing.GroupLayout(pnlPrincipal);
        pnlPrincipal.setLayout(pnlPrincipalLayout);
        pnlPrincipalLayout.setHorizontalGroup(
            pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPrincipalLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlPrincipalLayout.createSequentialGroup()
                        .addGroup(pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblSubtitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnAtualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlPrincipalLayout.createSequentialGroup()
                        .addComponent(pnlCardTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                        .addGap(12, 12, 12)
                        .addComponent(pnlCardDisponiveis, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                        .addGap(12, 12, 12)
                        .addComponent(pnlCardEmprestados, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                        .addGap(12, 12, 12)
                        .addComponent(pnlCardManutencao, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
                    .addComponent(lblSituacaoMovimentacoes, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlPrincipalLayout.createSequentialGroup()
                        .addComponent(pnlGraficoSituacao, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
                        .addGap(12, 12, 12)
                        .addComponent(pnlGraficoMovimentacoes, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)))
                .addGap(24, 24, 24))
        );
        pnlPrincipalLayout.setVerticalGroup(
            pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPrincipalLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlPrincipalLayout.createSequentialGroup()
                        .addComponent(lblTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(lblSubtitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnAtualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlCardTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlCardDisponiveis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlCardEmprestados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlCardManutencao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addComponent(lblSituacaoMovimentacoes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addGroup(pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlGraficoSituacao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlGraficoMovimentacoes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(24, 24, 24))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAtualizar;
    private com.mycompany.almoxarifado.View.LineChartPanel graficoMovimentacoes;
    private com.mycompany.almoxarifado.View.BarChartPanel graficoSituacao;
    private javax.swing.JLabel lblDisponiveisTitulo;
    private javax.swing.JLabel lblDisponiveisValor;
    private javax.swing.JLabel lblEmprestadosTitulo;
    private javax.swing.JLabel lblEmprestadosValor;
    private javax.swing.JLabel lblGraficoMovimentacoesDescricao;
    private javax.swing.JLabel lblGraficoMovimentacoesTitulo;
    private javax.swing.JLabel lblGraficoSituacaoDescricao;
    private javax.swing.JLabel lblGraficoSituacaoTitulo;
    private javax.swing.JLabel lblManutencaoTitulo;
    private javax.swing.JLabel lblManutencaoValor;
    private javax.swing.JLabel lblSituacaoMovimentacoes;
    private javax.swing.JLabel lblSubtitulo;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblTotalEquipamentosTitulo;
    private javax.swing.JLabel lblTotalEquipamentosValor;
    private javax.swing.JPanel pnlCardDisponiveis;
    private javax.swing.JPanel pnlCardEmprestados;
    private javax.swing.JPanel pnlCardManutencao;
    private javax.swing.JPanel pnlCardTotal;
    private javax.swing.JPanel pnlGraficoMovimentacoes;
    private javax.swing.JPanel pnlGraficoSituacao;
    private javax.swing.JPanel pnlPrincipal;
    // End of variables declaration//GEN-END:variables
}
