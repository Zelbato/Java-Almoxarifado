package com.mycompany.almoxarifado.View;

import com.mycompany.almoxarifado.Dto.DashboardDTO;
import com.mycompany.almoxarifado.Service.AuthSession;
import com.mycompany.almoxarifado.Service.DashboardService;
import com.mycompany.almoxarifado.Service.RelatorioPdfService;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

/** JFrame Form com os indicadores e gráficos do almoxarifado, e exportação em PDF. */
public class RelatorioView extends javax.swing.JFrame {

    private final DashboardService dashboardService;
    private final RelatorioPdfService relatorioPdfService;
    private DashboardDTO dashboardAtual;

    public RelatorioView() {
        AuthSession.exigirAutenticacao();
        dashboardService = new DashboardService();
        relatorioPdfService = new RelatorioPdfService();
        initComponents();
        carregarRelatorio();
    }

    private void carregarRelatorio() {
        btnAtualizar.setEnabled(false);
        try {
            dashboardAtual = dashboardService.carregarIndicadores();
            preencher(dashboardAtual);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Não foi possível carregar os dados do relatório.",
                    "Erro ao carregar relatório", JOptionPane.ERROR_MESSAGE);
        } finally {
            btnAtualizar.setEnabled(true);
        }
    }

    private void preencher(DashboardDTO d) {
        lblTotalValor.setText(String.valueOf(d.getTotalEquipamentos()));
        lblDisponiveisValor.setText(String.valueOf(d.getEquipamentosDisponiveis()));
        lblEmprestadosValor.setText(String.valueOf(d.getEquipamentosEmprestados()));
        lblManutencaoValor.setText(String.valueOf(d.getEquipamentosEmManutencao()));
        lblDanificadosValor.setText(String.valueOf(d.getEquipamentosDanificados()));
        lblAtrasadasValor.setText(String.valueOf(d.getMovimentacoesAtrasadas()));
        lblManutencoesPendentesValor.setText(String.valueOf(d.getManutencoesPendentes()));
        lblOcorrenciasAbertasValor.setText(String.valueOf(d.getOcorrenciasAbertas()));

        graficoSituacao.setDados(d.getEquipamentosPorSituacao());
        graficoCategoria.setDados(d.getEquipamentosPorCategoria());
        graficoMovimentacoes.setDados(d.getMovimentacoesPorMes());
        graficoMaisUtilizados.setDados(d.getEquipamentosMaisUtilizados());
        graficoManutencaoTipo.setDados(d.getManutencoesPorTipo());
        graficoManutencaoStatus.setDados(d.getManutencoesPorStatus());
        graficoOcorrenciaTipo.setDados(d.getOcorrenciasPorTipo());
        graficoOcorrenciaStatus.setDados(d.getOcorrenciasPorStatus());
    }

    // desenha cada gráfico numa imagem para ir no PDF
    private Map<String, BufferedImage> imagensDosGraficos(DashboardDTO d) {
        Map<String, BufferedImage> imagens = new LinkedHashMap<>();
        imagens.put("Equipamentos por situação", desenhar(barras(d.getEquipamentosPorSituacao())));
        imagens.put("Equipamentos por categoria", desenhar(barras(d.getEquipamentosPorCategoria())));
        LineChartPanel linha = new LineChartPanel();
        linha.setDados(d.getMovimentacoesPorMes());
        imagens.put("Movimentações ao longo do tempo", desenhar(linha));
        imagens.put("Equipamentos mais utilizados", desenhar(barras(d.getEquipamentosMaisUtilizados())));
        imagens.put("Manutenções por tipo", desenhar(barras(d.getManutencoesPorTipo())));
        imagens.put("Manutenções por status", desenhar(barras(d.getManutencoesPorStatus())));
        imagens.put("Ocorrências por tipo", desenhar(barras(d.getOcorrenciasPorTipo())));
        imagens.put("Ocorrências por status", desenhar(barras(d.getOcorrenciasPorStatus())));
        return imagens;
    }

    private BarChartPanel barras(Map<String, Integer> dados) {
        BarChartPanel grafico = new BarChartPanel();
        grafico.setDados(dados);
        return grafico;
    }

    private BufferedImage desenhar(JPanel grafico) {
        // um pouco maior que a largura da página, para não ficar borrado no PDF
        grafico.setSize(660, 280);
        BufferedImage imagem = new BufferedImage(660, 280, BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g = imagem.createGraphics();
        grafico.paint(g);
        g.dispose();
        return imagem;
    }

    private void gerarPdf() {
        if (dashboardAtual == null) {
            JOptionPane.showMessageDialog(this, "Atualize o relatório antes de exportar.",
                    "Relatório indisponível", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser seletorArquivo = new JFileChooser();
        seletorArquivo.setDialogTitle("Salvar relatório em PDF");
        seletorArquivo.setFileFilter(new FileNameExtensionFilter("Arquivo PDF (*.pdf)", "pdf"));
        seletorArquivo.setSelectedFile(new File("relatorio-almoxarifado-" + LocalDate.now() + ".pdf"));
        if (seletorArquivo.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File arquivo = adicionarExtensaoPdf(seletorArquivo.getSelectedFile());
        if (arquivo.exists() && JOptionPane.showConfirmDialog(this,
                "O arquivo selecionado já existe. Deseja substituí-lo?",
                "Confirmar substituição", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
            return;
        }

        btnGerarPdf.setEnabled(false);
        try {
            dashboardAtual = dashboardService.carregarIndicadores();
            preencher(dashboardAtual);
            relatorioPdfService.gerarRelatorio(arquivo, dashboardAtual, imagensDosGraficos(dashboardAtual));
            JOptionPane.showMessageDialog(this,
                    "Relatório gerado com sucesso em:\n" + arquivo.getAbsolutePath(),
                    "Relatório PDF", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Não foi possível atualizar os dados para gerar o relatório.",
                    "Erro ao gerar relatório", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Não foi possível salvar o arquivo PDF no local escolhido.",
                    "Erro ao gerar relatório", JOptionPane.ERROR_MESSAGE);
        } finally {
            btnGerarPdf.setEnabled(true);
        }
    }

    private File adicionarExtensaoPdf(File arquivo) {
        return arquivo.getName().toLowerCase().endsWith(".pdf")
                ? arquivo : new File(arquivo.getAbsolutePath() + ".pdf");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlPrincipal = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        lblSubtitulo = new javax.swing.JLabel();
        btnAtualizar = new javax.swing.JButton();
        btnGerarPdf = new javax.swing.JButton();
        pnlCardTotal = new javax.swing.JPanel();
        lblTotalTitulo = new javax.swing.JLabel();
        lblTotalValor = new javax.swing.JLabel();
        pnlCardDisponiveis = new javax.swing.JPanel();
        lblDisponiveisTitulo = new javax.swing.JLabel();
        lblDisponiveisValor = new javax.swing.JLabel();
        pnlCardEmprestados = new javax.swing.JPanel();
        lblEmprestadosTitulo = new javax.swing.JLabel();
        lblEmprestadosValor = new javax.swing.JLabel();
        pnlCardManutencao = new javax.swing.JPanel();
        lblManutencaoTitulo = new javax.swing.JLabel();
        lblManutencaoValor = new javax.swing.JLabel();
        pnlCardDanificados = new javax.swing.JPanel();
        lblDanificadosTitulo = new javax.swing.JLabel();
        lblDanificadosValor = new javax.swing.JLabel();
        pnlCardAtrasadas = new javax.swing.JPanel();
        lblAtrasadasTitulo = new javax.swing.JLabel();
        lblAtrasadasValor = new javax.swing.JLabel();
        pnlCardManutencoesPendentes = new javax.swing.JPanel();
        lblManutencoesPendentesTitulo = new javax.swing.JLabel();
        lblManutencoesPendentesValor = new javax.swing.JLabel();
        pnlCardOcorrenciasAbertas = new javax.swing.JPanel();
        lblOcorrenciasAbertasTitulo = new javax.swing.JLabel();
        lblOcorrenciasAbertasValor = new javax.swing.JLabel();
        pnlGraficoSituacao = new javax.swing.JPanel();
        lblGraficoSituacaoTitulo = new javax.swing.JLabel();
        lblGraficoSituacaoDescricao = new javax.swing.JLabel();
        graficoSituacao = new com.mycompany.almoxarifado.View.BarChartPanel();
        pnlGraficoCategoria = new javax.swing.JPanel();
        lblGraficoCategoriaTitulo = new javax.swing.JLabel();
        lblGraficoCategoriaDescricao = new javax.swing.JLabel();
        graficoCategoria = new com.mycompany.almoxarifado.View.BarChartPanel();
        pnlGraficoMovimentacoes = new javax.swing.JPanel();
        lblGraficoMovimentacoesTitulo = new javax.swing.JLabel();
        lblGraficoMovimentacoesDescricao = new javax.swing.JLabel();
        graficoMovimentacoes = new com.mycompany.almoxarifado.View.LineChartPanel();
        pnlGraficoMaisUtilizados = new javax.swing.JPanel();
        lblGraficoMaisUtilizadosTitulo = new javax.swing.JLabel();
        lblGraficoMaisUtilizadosDescricao = new javax.swing.JLabel();
        graficoMaisUtilizados = new com.mycompany.almoxarifado.View.BarChartPanel();
        pnlGraficoManutencaoTipo = new javax.swing.JPanel();
        lblGraficoManutencaoTipoTitulo = new javax.swing.JLabel();
        lblGraficoManutencaoTipoDescricao = new javax.swing.JLabel();
        graficoManutencaoTipo = new com.mycompany.almoxarifado.View.BarChartPanel();
        pnlGraficoManutencaoStatus = new javax.swing.JPanel();
        lblGraficoManutencaoStatusTitulo = new javax.swing.JLabel();
        lblGraficoManutencaoStatusDescricao = new javax.swing.JLabel();
        graficoManutencaoStatus = new com.mycompany.almoxarifado.View.BarChartPanel();
        pnlGraficoOcorrenciaTipo = new javax.swing.JPanel();
        lblGraficoOcorrenciaTipoTitulo = new javax.swing.JLabel();
        lblGraficoOcorrenciaTipoDescricao = new javax.swing.JLabel();
        graficoOcorrenciaTipo = new com.mycompany.almoxarifado.View.BarChartPanel();
        pnlGraficoOcorrenciaStatus = new javax.swing.JPanel();
        lblGraficoOcorrenciaStatusTitulo = new javax.swing.JLabel();
        lblGraficoOcorrenciaStatusDescricao = new javax.swing.JLabel();
        graficoOcorrenciaStatus = new com.mycompany.almoxarifado.View.BarChartPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Almoxarifado - Relatórios");

        pnlPrincipal.setBackground(new java.awt.Color(248, 249, 250));
        lblTitulo.setText("Relatórios");
        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(0, 78, 174));

        lblSubtitulo.setText("Indicadores e gráficos gerados com os dados atuais do banco.");
        lblSubtitulo.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblSubtitulo.setForeground(new java.awt.Color(108, 117, 125));

        btnAtualizar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAtualizar.setText("Atualizar");
        btnAtualizar.setBackground(new java.awt.Color(0, 78, 174));
        btnAtualizar.setForeground(new java.awt.Color(255, 255, 255));
        btnAtualizar.setFocusPainted(false);
        btnAtualizar.addActionListener(this::btnAtualizarActionPerformed);

        btnGerarPdf.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnGerarPdf.setText("Gerar PDF");
        btnGerarPdf.setBackground(new java.awt.Color(53, 158, 77));
        btnGerarPdf.setForeground(new java.awt.Color(255, 255, 255));
        btnGerarPdf.setFocusPainted(false);
        btnGerarPdf.addActionListener(this::btnGerarPdfActionPerformed);

        pnlCardTotal.setBackground(new java.awt.Color(255, 255, 255));
        pnlCardTotal.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(218, 223, 229), 1, true));
        lblTotalTitulo.setText("Total de equipamentos");
        lblTotalTitulo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTotalTitulo.setForeground(new java.awt.Color(108, 117, 125));

        lblTotalValor.setText("0");
        lblTotalValor.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblTotalValor.setForeground(new java.awt.Color(0, 78, 174));

        javax.swing.GroupLayout pnlCardTotalLayout = new javax.swing.GroupLayout(pnlCardTotal);
        pnlCardTotal.setLayout(pnlCardTotalLayout);
        pnlCardTotalLayout.setHorizontalGroup(
            pnlCardTotalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardTotalLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlCardTotalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTotalTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotalValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pnlCardTotalLayout.setVerticalGroup(
            pnlCardTotalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardTotalLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblTotalTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(lblTotalValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        pnlCardDanificados.setBackground(new java.awt.Color(255, 255, 255));
        pnlCardDanificados.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(218, 223, 229), 1, true));
        lblDanificadosTitulo.setText("Danificados");
        lblDanificadosTitulo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblDanificadosTitulo.setForeground(new java.awt.Color(108, 117, 125));

        lblDanificadosValor.setText("0");
        lblDanificadosValor.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblDanificadosValor.setForeground(new java.awt.Color(214, 62, 50));

        javax.swing.GroupLayout pnlCardDanificadosLayout = new javax.swing.GroupLayout(pnlCardDanificados);
        pnlCardDanificados.setLayout(pnlCardDanificadosLayout);
        pnlCardDanificadosLayout.setHorizontalGroup(
            pnlCardDanificadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardDanificadosLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlCardDanificadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDanificadosTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDanificadosValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pnlCardDanificadosLayout.setVerticalGroup(
            pnlCardDanificadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardDanificadosLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblDanificadosTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(lblDanificadosValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );

        pnlCardAtrasadas.setBackground(new java.awt.Color(255, 255, 255));
        pnlCardAtrasadas.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(218, 223, 229), 1, true));
        lblAtrasadasTitulo.setText("Movimentações atrasadas");
        lblAtrasadasTitulo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblAtrasadasTitulo.setForeground(new java.awt.Color(108, 117, 125));

        lblAtrasadasValor.setText("0");
        lblAtrasadasValor.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblAtrasadasValor.setForeground(new java.awt.Color(228, 146, 10));

        javax.swing.GroupLayout pnlCardAtrasadasLayout = new javax.swing.GroupLayout(pnlCardAtrasadas);
        pnlCardAtrasadas.setLayout(pnlCardAtrasadasLayout);
        pnlCardAtrasadasLayout.setHorizontalGroup(
            pnlCardAtrasadasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardAtrasadasLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlCardAtrasadasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblAtrasadasTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAtrasadasValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pnlCardAtrasadasLayout.setVerticalGroup(
            pnlCardAtrasadasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardAtrasadasLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblAtrasadasTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(lblAtrasadasValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );

        pnlCardManutencoesPendentes.setBackground(new java.awt.Color(255, 255, 255));
        pnlCardManutencoesPendentes.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(218, 223, 229), 1, true));
        lblManutencoesPendentesTitulo.setText("Manutenções pendentes");
        lblManutencoesPendentesTitulo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblManutencoesPendentesTitulo.setForeground(new java.awt.Color(108, 117, 125));

        lblManutencoesPendentesValor.setText("0");
        lblManutencoesPendentesValor.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblManutencoesPendentesValor.setForeground(new java.awt.Color(0, 78, 174));

        javax.swing.GroupLayout pnlCardManutencoesPendentesLayout = new javax.swing.GroupLayout(pnlCardManutencoesPendentes);
        pnlCardManutencoesPendentes.setLayout(pnlCardManutencoesPendentesLayout);
        pnlCardManutencoesPendentesLayout.setHorizontalGroup(
            pnlCardManutencoesPendentesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardManutencoesPendentesLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlCardManutencoesPendentesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblManutencoesPendentesTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblManutencoesPendentesValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pnlCardManutencoesPendentesLayout.setVerticalGroup(
            pnlCardManutencoesPendentesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardManutencoesPendentesLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblManutencoesPendentesTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(lblManutencoesPendentesValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );

        pnlCardOcorrenciasAbertas.setBackground(new java.awt.Color(255, 255, 255));
        pnlCardOcorrenciasAbertas.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(218, 223, 229), 1, true));
        lblOcorrenciasAbertasTitulo.setText("Ocorrências abertas");
        lblOcorrenciasAbertasTitulo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblOcorrenciasAbertasTitulo.setForeground(new java.awt.Color(108, 117, 125));

        lblOcorrenciasAbertasValor.setText("0");
        lblOcorrenciasAbertasValor.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblOcorrenciasAbertasValor.setForeground(new java.awt.Color(214, 62, 50));

        javax.swing.GroupLayout pnlCardOcorrenciasAbertasLayout = new javax.swing.GroupLayout(pnlCardOcorrenciasAbertas);
        pnlCardOcorrenciasAbertas.setLayout(pnlCardOcorrenciasAbertasLayout);
        pnlCardOcorrenciasAbertasLayout.setHorizontalGroup(
            pnlCardOcorrenciasAbertasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardOcorrenciasAbertasLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlCardOcorrenciasAbertasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblOcorrenciasAbertasTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblOcorrenciasAbertasValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pnlCardOcorrenciasAbertasLayout.setVerticalGroup(
            pnlCardOcorrenciasAbertasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardOcorrenciasAbertasLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblOcorrenciasAbertasTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(lblOcorrenciasAbertasValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );

        pnlGraficoSituacao.setBackground(new java.awt.Color(255, 255, 255));
        pnlGraficoSituacao.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(218, 223, 229), 1, true));
        lblGraficoSituacaoTitulo.setText("Equipamentos por situação");
        lblGraficoSituacaoTitulo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblGraficoSituacaoTitulo.setForeground(new java.awt.Color(0, 78, 174));

        lblGraficoSituacaoDescricao.setText("Situação atual de cada equipamento");
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

        pnlGraficoCategoria.setBackground(new java.awt.Color(255, 255, 255));
        pnlGraficoCategoria.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(218, 223, 229), 1, true));
        lblGraficoCategoriaTitulo.setText("Equipamentos por categoria");
        lblGraficoCategoriaTitulo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblGraficoCategoriaTitulo.setForeground(new java.awt.Color(0, 78, 174));

        lblGraficoCategoriaDescricao.setText("Quantidade cadastrada em cada categoria");
        lblGraficoCategoriaDescricao.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblGraficoCategoriaDescricao.setForeground(new java.awt.Color(108, 117, 125));

        javax.swing.GroupLayout pnlGraficoCategoriaLayout = new javax.swing.GroupLayout(pnlGraficoCategoria);
        pnlGraficoCategoria.setLayout(pnlGraficoCategoriaLayout);
        pnlGraficoCategoriaLayout.setHorizontalGroup(
            pnlGraficoCategoriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGraficoCategoriaLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlGraficoCategoriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblGraficoCategoriaTitulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblGraficoCategoriaDescricao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(graficoCategoria, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
                .addGap(16, 16, 16))
        );
        pnlGraficoCategoriaLayout.setVerticalGroup(
            pnlGraficoCategoriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGraficoCategoriaLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblGraficoCategoriaTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(lblGraficoCategoriaDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(graficoCategoria, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                .addGap(14, 14, 14))
        );

        pnlGraficoMovimentacoes.setBackground(new java.awt.Color(255, 255, 255));
        pnlGraficoMovimentacoes.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(218, 223, 229), 1, true));
        lblGraficoMovimentacoesTitulo.setText("Movimentações ao longo do tempo");
        lblGraficoMovimentacoesTitulo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblGraficoMovimentacoesTitulo.setForeground(new java.awt.Color(0, 78, 174));

        lblGraficoMovimentacoesDescricao.setText("Retiradas nos últimos seis meses");
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

        pnlGraficoMaisUtilizados.setBackground(new java.awt.Color(255, 255, 255));
        pnlGraficoMaisUtilizados.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(218, 223, 229), 1, true));
        lblGraficoMaisUtilizadosTitulo.setText("Equipamentos mais utilizados");
        lblGraficoMaisUtilizadosTitulo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblGraficoMaisUtilizadosTitulo.setForeground(new java.awt.Color(0, 78, 174));

        lblGraficoMaisUtilizadosDescricao.setText("Os cinco com mais retiradas");
        lblGraficoMaisUtilizadosDescricao.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblGraficoMaisUtilizadosDescricao.setForeground(new java.awt.Color(108, 117, 125));

        javax.swing.GroupLayout pnlGraficoMaisUtilizadosLayout = new javax.swing.GroupLayout(pnlGraficoMaisUtilizados);
        pnlGraficoMaisUtilizados.setLayout(pnlGraficoMaisUtilizadosLayout);
        pnlGraficoMaisUtilizadosLayout.setHorizontalGroup(
            pnlGraficoMaisUtilizadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGraficoMaisUtilizadosLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlGraficoMaisUtilizadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblGraficoMaisUtilizadosTitulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblGraficoMaisUtilizadosDescricao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(graficoMaisUtilizados, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
                .addGap(16, 16, 16))
        );
        pnlGraficoMaisUtilizadosLayout.setVerticalGroup(
            pnlGraficoMaisUtilizadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGraficoMaisUtilizadosLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblGraficoMaisUtilizadosTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(lblGraficoMaisUtilizadosDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(graficoMaisUtilizados, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                .addGap(14, 14, 14))
        );

        pnlGraficoManutencaoTipo.setBackground(new java.awt.Color(255, 255, 255));
        pnlGraficoManutencaoTipo.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(218, 223, 229), 1, true));
        lblGraficoManutencaoTipoTitulo.setText("Manutenções por tipo");
        lblGraficoManutencaoTipoTitulo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblGraficoManutencaoTipoTitulo.setForeground(new java.awt.Color(0, 78, 174));

        lblGraficoManutencaoTipoDescricao.setText("Preventiva, corretiva e inspeção");
        lblGraficoManutencaoTipoDescricao.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblGraficoManutencaoTipoDescricao.setForeground(new java.awt.Color(108, 117, 125));

        javax.swing.GroupLayout pnlGraficoManutencaoTipoLayout = new javax.swing.GroupLayout(pnlGraficoManutencaoTipo);
        pnlGraficoManutencaoTipo.setLayout(pnlGraficoManutencaoTipoLayout);
        pnlGraficoManutencaoTipoLayout.setHorizontalGroup(
            pnlGraficoManutencaoTipoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGraficoManutencaoTipoLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlGraficoManutencaoTipoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblGraficoManutencaoTipoTitulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblGraficoManutencaoTipoDescricao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(graficoManutencaoTipo, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
                .addGap(16, 16, 16))
        );
        pnlGraficoManutencaoTipoLayout.setVerticalGroup(
            pnlGraficoManutencaoTipoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGraficoManutencaoTipoLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblGraficoManutencaoTipoTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(lblGraficoManutencaoTipoDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(graficoManutencaoTipo, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                .addGap(14, 14, 14))
        );

        pnlGraficoManutencaoStatus.setBackground(new java.awt.Color(255, 255, 255));
        pnlGraficoManutencaoStatus.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(218, 223, 229), 1, true));
        lblGraficoManutencaoStatusTitulo.setText("Manutenções por status");
        lblGraficoManutencaoStatusTitulo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblGraficoManutencaoStatusTitulo.setForeground(new java.awt.Color(0, 78, 174));

        lblGraficoManutencaoStatusDescricao.setText("Andamento das manutenções registradas");
        lblGraficoManutencaoStatusDescricao.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblGraficoManutencaoStatusDescricao.setForeground(new java.awt.Color(108, 117, 125));

        javax.swing.GroupLayout pnlGraficoManutencaoStatusLayout = new javax.swing.GroupLayout(pnlGraficoManutencaoStatus);
        pnlGraficoManutencaoStatus.setLayout(pnlGraficoManutencaoStatusLayout);
        pnlGraficoManutencaoStatusLayout.setHorizontalGroup(
            pnlGraficoManutencaoStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGraficoManutencaoStatusLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlGraficoManutencaoStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblGraficoManutencaoStatusTitulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblGraficoManutencaoStatusDescricao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(graficoManutencaoStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
                .addGap(16, 16, 16))
        );
        pnlGraficoManutencaoStatusLayout.setVerticalGroup(
            pnlGraficoManutencaoStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGraficoManutencaoStatusLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblGraficoManutencaoStatusTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(lblGraficoManutencaoStatusDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(graficoManutencaoStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                .addGap(14, 14, 14))
        );

        pnlGraficoOcorrenciaTipo.setBackground(new java.awt.Color(255, 255, 255));
        pnlGraficoOcorrenciaTipo.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(218, 223, 229), 1, true));
        lblGraficoOcorrenciaTipoTitulo.setText("Ocorrências por tipo");
        lblGraficoOcorrenciaTipoTitulo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblGraficoOcorrenciaTipoTitulo.setForeground(new java.awt.Color(0, 78, 174));

        lblGraficoOcorrenciaTipoDescricao.setText("Danos, defeitos, atrasos, extravios e roubos");
        lblGraficoOcorrenciaTipoDescricao.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblGraficoOcorrenciaTipoDescricao.setForeground(new java.awt.Color(108, 117, 125));

        javax.swing.GroupLayout pnlGraficoOcorrenciaTipoLayout = new javax.swing.GroupLayout(pnlGraficoOcorrenciaTipo);
        pnlGraficoOcorrenciaTipo.setLayout(pnlGraficoOcorrenciaTipoLayout);
        pnlGraficoOcorrenciaTipoLayout.setHorizontalGroup(
            pnlGraficoOcorrenciaTipoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGraficoOcorrenciaTipoLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlGraficoOcorrenciaTipoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblGraficoOcorrenciaTipoTitulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblGraficoOcorrenciaTipoDescricao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(graficoOcorrenciaTipo, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
                .addGap(16, 16, 16))
        );
        pnlGraficoOcorrenciaTipoLayout.setVerticalGroup(
            pnlGraficoOcorrenciaTipoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGraficoOcorrenciaTipoLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblGraficoOcorrenciaTipoTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(lblGraficoOcorrenciaTipoDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(graficoOcorrenciaTipo, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                .addGap(14, 14, 14))
        );

        pnlGraficoOcorrenciaStatus.setBackground(new java.awt.Color(255, 255, 255));
        pnlGraficoOcorrenciaStatus.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(218, 223, 229), 1, true));
        lblGraficoOcorrenciaStatusTitulo.setText("Ocorrências por status");
        lblGraficoOcorrenciaStatusTitulo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblGraficoOcorrenciaStatusTitulo.setForeground(new java.awt.Color(0, 78, 174));

        lblGraficoOcorrenciaStatusDescricao.setText("Situação das ocorrências registradas");
        lblGraficoOcorrenciaStatusDescricao.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblGraficoOcorrenciaStatusDescricao.setForeground(new java.awt.Color(108, 117, 125));

        javax.swing.GroupLayout pnlGraficoOcorrenciaStatusLayout = new javax.swing.GroupLayout(pnlGraficoOcorrenciaStatus);
        pnlGraficoOcorrenciaStatus.setLayout(pnlGraficoOcorrenciaStatusLayout);
        pnlGraficoOcorrenciaStatusLayout.setHorizontalGroup(
            pnlGraficoOcorrenciaStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGraficoOcorrenciaStatusLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlGraficoOcorrenciaStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblGraficoOcorrenciaStatusTitulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblGraficoOcorrenciaStatusDescricao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(graficoOcorrenciaStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
                .addGap(16, 16, 16))
        );
        pnlGraficoOcorrenciaStatusLayout.setVerticalGroup(
            pnlGraficoOcorrenciaStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGraficoOcorrenciaStatusLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblGraficoOcorrenciaStatusTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(lblGraficoOcorrenciaStatusDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(graficoOcorrenciaStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
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
                        .addComponent(btnAtualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnGerarPdf, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlPrincipalLayout.createSequentialGroup()
                        .addComponent(pnlCardTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                        .addGap(12, 12, 12)
                        .addComponent(pnlCardDisponiveis, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                        .addGap(12, 12, 12)
                        .addComponent(pnlCardEmprestados, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                        .addGap(12, 12, 12)
                        .addComponent(pnlCardManutencao, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
                    .addGroup(pnlPrincipalLayout.createSequentialGroup()
                        .addComponent(pnlCardDanificados, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                        .addGap(12, 12, 12)
                        .addComponent(pnlCardAtrasadas, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                        .addGap(12, 12, 12)
                        .addComponent(pnlCardManutencoesPendentes, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                        .addGap(12, 12, 12)
                        .addComponent(pnlCardOcorrenciasAbertas, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
                    .addGroup(pnlPrincipalLayout.createSequentialGroup()
                        .addComponent(pnlGraficoSituacao, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
                        .addGap(12, 12, 12)
                        .addComponent(pnlGraficoCategoria, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE))
                    .addGroup(pnlPrincipalLayout.createSequentialGroup()
                        .addComponent(pnlGraficoMovimentacoes, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
                        .addGap(12, 12, 12)
                        .addComponent(pnlGraficoMaisUtilizados, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE))
                    .addGroup(pnlPrincipalLayout.createSequentialGroup()
                        .addComponent(pnlGraficoManutencaoTipo, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
                        .addGap(12, 12, 12)
                        .addComponent(pnlGraficoManutencaoStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE))
                    .addGroup(pnlPrincipalLayout.createSequentialGroup()
                        .addComponent(pnlGraficoOcorrenciaTipo, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
                        .addGap(12, 12, 12)
                        .addComponent(pnlGraficoOcorrenciaStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)))
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
                    .addGroup(pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnAtualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnGerarPdf, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlCardTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlCardDisponiveis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlCardEmprestados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlCardManutencao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlCardDanificados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlCardAtrasadas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlCardManutencoesPendentes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlCardOcorrenciasAbertas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGap(6, 6, 6)
                .addGroup(pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlGraficoSituacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlGraficoCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlGraficoMovimentacoes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlGraficoMaisUtilizados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlGraficoManutencaoTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlGraficoManutencaoStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlGraficoOcorrenciaTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlGraficoOcorrenciaStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGap(12, 12, 12))
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

    private void btnAtualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtualizarActionPerformed
        carregarRelatorio();
    }//GEN-LAST:event_btnAtualizarActionPerformed

    private void btnGerarPdfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGerarPdfActionPerformed
        gerarPdf();
    }//GEN-LAST:event_btnGerarPdfActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAtualizar;
    private javax.swing.JButton btnGerarPdf;
    private com.mycompany.almoxarifado.View.BarChartPanel graficoCategoria;
    private com.mycompany.almoxarifado.View.BarChartPanel graficoMaisUtilizados;
    private com.mycompany.almoxarifado.View.BarChartPanel graficoManutencaoStatus;
    private com.mycompany.almoxarifado.View.BarChartPanel graficoManutencaoTipo;
    private com.mycompany.almoxarifado.View.LineChartPanel graficoMovimentacoes;
    private com.mycompany.almoxarifado.View.BarChartPanel graficoOcorrenciaStatus;
    private com.mycompany.almoxarifado.View.BarChartPanel graficoOcorrenciaTipo;
    private com.mycompany.almoxarifado.View.BarChartPanel graficoSituacao;
    private javax.swing.JLabel lblAtrasadasTitulo;
    private javax.swing.JLabel lblAtrasadasValor;
    private javax.swing.JLabel lblDanificadosTitulo;
    private javax.swing.JLabel lblDanificadosValor;
    private javax.swing.JLabel lblDisponiveisTitulo;
    private javax.swing.JLabel lblDisponiveisValor;
    private javax.swing.JLabel lblEmprestadosTitulo;
    private javax.swing.JLabel lblEmprestadosValor;
    private javax.swing.JLabel lblGraficoCategoriaDescricao;
    private javax.swing.JLabel lblGraficoCategoriaTitulo;
    private javax.swing.JLabel lblGraficoMaisUtilizadosDescricao;
    private javax.swing.JLabel lblGraficoMaisUtilizadosTitulo;
    private javax.swing.JLabel lblGraficoManutencaoStatusDescricao;
    private javax.swing.JLabel lblGraficoManutencaoStatusTitulo;
    private javax.swing.JLabel lblGraficoManutencaoTipoDescricao;
    private javax.swing.JLabel lblGraficoManutencaoTipoTitulo;
    private javax.swing.JLabel lblGraficoMovimentacoesDescricao;
    private javax.swing.JLabel lblGraficoMovimentacoesTitulo;
    private javax.swing.JLabel lblGraficoOcorrenciaStatusDescricao;
    private javax.swing.JLabel lblGraficoOcorrenciaStatusTitulo;
    private javax.swing.JLabel lblGraficoOcorrenciaTipoDescricao;
    private javax.swing.JLabel lblGraficoOcorrenciaTipoTitulo;
    private javax.swing.JLabel lblGraficoSituacaoDescricao;
    private javax.swing.JLabel lblGraficoSituacaoTitulo;
    private javax.swing.JLabel lblManutencaoTitulo;
    private javax.swing.JLabel lblManutencaoValor;
    private javax.swing.JLabel lblManutencoesPendentesTitulo;
    private javax.swing.JLabel lblManutencoesPendentesValor;
    private javax.swing.JLabel lblOcorrenciasAbertasTitulo;
    private javax.swing.JLabel lblOcorrenciasAbertasValor;
    private javax.swing.JLabel lblSubtitulo;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblTotalTitulo;
    private javax.swing.JLabel lblTotalValor;
    private javax.swing.JPanel pnlCardAtrasadas;
    private javax.swing.JPanel pnlCardDanificados;
    private javax.swing.JPanel pnlCardDisponiveis;
    private javax.swing.JPanel pnlCardEmprestados;
    private javax.swing.JPanel pnlCardManutencao;
    private javax.swing.JPanel pnlCardManutencoesPendentes;
    private javax.swing.JPanel pnlCardOcorrenciasAbertas;
    private javax.swing.JPanel pnlCardTotal;
    private javax.swing.JPanel pnlGraficoCategoria;
    private javax.swing.JPanel pnlGraficoMaisUtilizados;
    private javax.swing.JPanel pnlGraficoManutencaoStatus;
    private javax.swing.JPanel pnlGraficoManutencaoTipo;
    private javax.swing.JPanel pnlGraficoMovimentacoes;
    private javax.swing.JPanel pnlGraficoOcorrenciaStatus;
    private javax.swing.JPanel pnlGraficoOcorrenciaTipo;
    private javax.swing.JPanel pnlGraficoSituacao;
    private javax.swing.JPanel pnlPrincipal;
    // End of variables declaration//GEN-END:variables
}
