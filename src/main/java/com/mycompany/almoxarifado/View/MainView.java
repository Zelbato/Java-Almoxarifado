package com.mycompany.almoxarifado.View;

import com.mycompany.almoxarifado.Service.AuthSession;
import java.awt.Color;
import java.util.function.Supplier;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Janela principal: menu lateral, cabeçalho e a área central onde as telas abrem.
 * Cada módulo continua sendo um JFrame Form; aqui só o conteúdo dele é mostrado
 * dentro da área central, então o sistema fica numa janela só.
 */
public class MainView extends javax.swing.JFrame {

    private static final Color MENU_NORMAL = Color.WHITE;
    private static final Color MENU_SELECIONADO = new Color(232, 240, 252);

    private JFrame telaAtual;
    private JButton botaoAtual;

    public MainView() {
        AuthSession.exigirAutenticacao();
        initComponents();
        lblUsuario.setText(AuthSession.usuario().getEmail() + "  •  " + nomePerfil());
        configurarEventos();
        aplicarPermissoes();
        abrir("Dashboard", btnDashboard, DashboardView::new);
    }

    /**
     * Permite executar esta JFrame isoladamente pelo NetBeans.
     */
    public static void main(String[] args) {
        com.mycompany.almoxarifado.Almoxarifado.main(args);
    }

    private String nomePerfil() {
        return AuthSession.ehChefe() ? "Almoxarifado Chefe" : "Almoxarifado Júnior";
    }

    private void configurarEventos() {
        btnDashboard.addActionListener(e -> abrir("Dashboard", btnDashboard, DashboardView::new));
        btnMovimentacoes.addActionListener(e -> abrir("Movimentações", btnMovimentacoes, MovimentacaoView::new));
        btnManutencoes.addActionListener(e -> abrir("Manutenções", btnManutencoes, ManutencaoView::new));
        btnOcorrencias.addActionListener(e -> abrir("Ocorrências", btnOcorrencias, OcorrenciaView::new));
        btnEquipamentos.addActionListener(e -> abrir("Equipamentos", btnEquipamentos, EquipamentoView::new));
        btnSetores.addActionListener(e -> abrir("Setores", btnSetores, SetorView::new));
        btnCategorias.addActionListener(e -> abrir("Categorias", btnCategorias, CategoriaView::new));
        btnLocalizacoes.addActionListener(e -> abrir("Localizações", btnLocalizacoes, LocalizacaoView::new));
        btnFuncionarios.addActionListener(e -> abrir("Funcionários", btnFuncionarios, FuncionarioView::new));
        btnUsuarios.addActionListener(e -> abrir("Usuários", btnUsuarios, UsuarioView::new));
        btnRelatorios.addActionListener(e -> abrir("Relatórios", btnRelatorios, RelatorioView::new));
        btnSair.addActionListener(e -> sair());
    }

    // o Júnior não vê a gestão de usuários (o Service também bloqueia)
    private void aplicarPermissoes() {
        btnUsuarios.setVisible(AuthSession.ehChefe());
    }

    private void abrir(String titulo, JButton botao, Supplier<JFrame> criarTela) {
        JFrame novaTela;
        try {
            novaTela = criarTela.get();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Acesso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (telaAtual != null) {
            telaAtual.dispose();
        }
        telaAtual = novaTela;

        scrollConteudo.setViewportView(novaTela.getContentPane());
        scrollConteudo.getVerticalScrollBar().setUnitIncrement(16);
        lblTituloPagina.setText(titulo);

        if (botaoAtual != null) {
            botaoAtual.setBackground(MENU_NORMAL);
        }
        botaoAtual = botao;
        botao.setBackground(MENU_SELECIONADO);
    }

    private void sair() {
        if (JOptionPane.showConfirmDialog(
                this,
                "Deseja sair do sistema?",
                "Confirmação",
                JOptionPane.YES_NO_OPTION
        ) != JOptionPane.YES_OPTION) {
            return;
        }

        if (telaAtual != null) {
            telaAtual.dispose();
        }
        AuthSession.sair();
        new LoginView().setVisible(true);
        dispose();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlPrincipal = new javax.swing.JPanel();
        pnlMenu = new javax.swing.JPanel();
        lblLogo = new javax.swing.JLabel();
        lblNomeSistema = new javax.swing.JLabel();
        lblGrupoVisao = new javax.swing.JLabel();
        btnDashboard = new javax.swing.JButton();
        lblGrupoOperacao = new javax.swing.JLabel();
        btnMovimentacoes = new javax.swing.JButton();
        btnManutencoes = new javax.swing.JButton();
        btnOcorrencias = new javax.swing.JButton();
        lblGrupoCadastros = new javax.swing.JLabel();
        btnEquipamentos = new javax.swing.JButton();
        btnSetores = new javax.swing.JButton();
        btnCategorias = new javax.swing.JButton();
        btnLocalizacoes = new javax.swing.JButton();
        btnFuncionarios = new javax.swing.JButton();
        btnUsuarios = new javax.swing.JButton();
        lblGrupoRelatorios = new javax.swing.JLabel();
        btnRelatorios = new javax.swing.JButton();
        btnSair = new javax.swing.JButton();
        pnlCabecalho = new javax.swing.JPanel();
        lblTituloPagina = new javax.swing.JLabel();
        lblUsuario = new javax.swing.JLabel();
        scrollConteudo = new javax.swing.JScrollPane();
        pnlBoasVindas = new javax.swing.JPanel();
        lblBoasVindas = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sistema de Almoxarifado");
        setMinimumSize(new java.awt.Dimension(1100, 680));

        pnlPrincipal.setBackground(new java.awt.Color(218, 223, 229));
        pnlMenu.setBackground(new java.awt.Color(255, 255, 255));
        lblLogo.setText("");
        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/logo-menu.png"))); // NOI18N
        lblLogo.setHorizontalAlignment(0);

        lblNomeSistema.setText("Almoxarifado");
        lblNomeSistema.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblNomeSistema.setHorizontalAlignment(0);
        lblNomeSistema.setForeground(new java.awt.Color(0, 78, 174));

        lblGrupoVisao.setText("VISÃO GERAL");
        lblGrupoVisao.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblGrupoVisao.setForeground(new java.awt.Color(108, 117, 125));

        btnDashboard.setText("Dashboard");
        btnDashboard.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        btnDashboard.setBackground(new java.awt.Color(255, 255, 255));
        btnDashboard.setForeground(new java.awt.Color(33, 37, 41));
        btnDashboard.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btnDashboard.setBorderPainted(false);
        btnDashboard.setFocusPainted(false);
        btnDashboard.setHorizontalAlignment(2);

        lblGrupoOperacao.setText("OPERAÇÃO");
        lblGrupoOperacao.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblGrupoOperacao.setForeground(new java.awt.Color(108, 117, 125));

        btnMovimentacoes.setText("Movimentações");
        btnMovimentacoes.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        btnMovimentacoes.setBackground(new java.awt.Color(255, 255, 255));
        btnMovimentacoes.setForeground(new java.awt.Color(33, 37, 41));
        btnMovimentacoes.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btnMovimentacoes.setBorderPainted(false);
        btnMovimentacoes.setFocusPainted(false);
        btnMovimentacoes.setHorizontalAlignment(2);

        btnManutencoes.setText("Manutenções");
        btnManutencoes.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        btnManutencoes.setBackground(new java.awt.Color(255, 255, 255));
        btnManutencoes.setForeground(new java.awt.Color(33, 37, 41));
        btnManutencoes.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btnManutencoes.setBorderPainted(false);
        btnManutencoes.setFocusPainted(false);
        btnManutencoes.setHorizontalAlignment(2);

        btnOcorrencias.setText("Ocorrências");
        btnOcorrencias.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        btnOcorrencias.setBackground(new java.awt.Color(255, 255, 255));
        btnOcorrencias.setForeground(new java.awt.Color(33, 37, 41));
        btnOcorrencias.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btnOcorrencias.setBorderPainted(false);
        btnOcorrencias.setFocusPainted(false);
        btnOcorrencias.setHorizontalAlignment(2);

        lblGrupoCadastros.setText("CADASTROS");
        lblGrupoCadastros.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblGrupoCadastros.setForeground(new java.awt.Color(108, 117, 125));

        btnEquipamentos.setText("Equipamentos");
        btnEquipamentos.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        btnEquipamentos.setBackground(new java.awt.Color(255, 255, 255));
        btnEquipamentos.setForeground(new java.awt.Color(33, 37, 41));
        btnEquipamentos.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btnEquipamentos.setBorderPainted(false);
        btnEquipamentos.setFocusPainted(false);
        btnEquipamentos.setHorizontalAlignment(2);

        btnSetores.setText("Setores");
        btnSetores.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        btnSetores.setBackground(new java.awt.Color(255, 255, 255));
        btnSetores.setForeground(new java.awt.Color(33, 37, 41));
        btnSetores.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btnSetores.setBorderPainted(false);
        btnSetores.setFocusPainted(false);
        btnSetores.setHorizontalAlignment(2);

        btnCategorias.setText("Categorias");
        btnCategorias.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        btnCategorias.setBackground(new java.awt.Color(255, 255, 255));
        btnCategorias.setForeground(new java.awt.Color(33, 37, 41));
        btnCategorias.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btnCategorias.setBorderPainted(false);
        btnCategorias.setFocusPainted(false);
        btnCategorias.setHorizontalAlignment(2);

        btnLocalizacoes.setText("Localizações");
        btnLocalizacoes.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        btnLocalizacoes.setBackground(new java.awt.Color(255, 255, 255));
        btnLocalizacoes.setForeground(new java.awt.Color(33, 37, 41));
        btnLocalizacoes.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btnLocalizacoes.setBorderPainted(false);
        btnLocalizacoes.setFocusPainted(false);
        btnLocalizacoes.setHorizontalAlignment(2);

        btnFuncionarios.setText("Funcionários");
        btnFuncionarios.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        btnFuncionarios.setBackground(new java.awt.Color(255, 255, 255));
        btnFuncionarios.setForeground(new java.awt.Color(33, 37, 41));
        btnFuncionarios.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btnFuncionarios.setBorderPainted(false);
        btnFuncionarios.setFocusPainted(false);
        btnFuncionarios.setHorizontalAlignment(2);

        btnUsuarios.setText("Usuários");
        btnUsuarios.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        btnUsuarios.setBackground(new java.awt.Color(255, 255, 255));
        btnUsuarios.setForeground(new java.awt.Color(33, 37, 41));
        btnUsuarios.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btnUsuarios.setBorderPainted(false);
        btnUsuarios.setFocusPainted(false);
        btnUsuarios.setHorizontalAlignment(2);

        lblGrupoRelatorios.setText("RELATÓRIOS");
        lblGrupoRelatorios.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblGrupoRelatorios.setForeground(new java.awt.Color(108, 117, 125));

        btnRelatorios.setText("Relatórios");
        btnRelatorios.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        btnRelatorios.setBackground(new java.awt.Color(255, 255, 255));
        btnRelatorios.setForeground(new java.awt.Color(33, 37, 41));
        btnRelatorios.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btnRelatorios.setBorderPainted(false);
        btnRelatorios.setFocusPainted(false);
        btnRelatorios.setHorizontalAlignment(2);

        btnSair.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnSair.setText("Sair");
        btnSair.setBackground(new java.awt.Color(214, 62, 50));
        btnSair.setForeground(new java.awt.Color(255, 255, 255));
        btnSair.setFocusPainted(false);

        javax.swing.GroupLayout pnlMenuLayout = new javax.swing.GroupLayout(pnlMenu);
        pnlMenu.setLayout(pnlMenuLayout);
        pnlMenuLayout.setHorizontalGroup(
            pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMenuLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblLogo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblNomeSistema, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblGrupoVisao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDashboard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblGrupoOperacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnMovimentacoes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnManutencoes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnOcorrencias, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblGrupoCadastros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEquipamentos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSetores, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCategorias, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnLocalizacoes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnFuncionarios, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnUsuarios, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblGrupoRelatorios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRelatorios, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSair, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12))
        );
        pnlMenuLayout.setVerticalGroup(
            pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMenuLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(lblLogo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(lblNomeSistema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addGap(10, 10, 10)
                .addComponent(lblGrupoVisao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(btnDashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(lblGrupoOperacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(btnMovimentacoes, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnManutencoes, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnOcorrencias, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(lblGrupoCadastros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(btnEquipamentos, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnSetores, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnCategorias, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnLocalizacoes, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnFuncionarios, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnUsuarios, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(lblGrupoRelatorios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(btnRelatorios, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnSair, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );

        pnlCabecalho.setBackground(new java.awt.Color(255, 255, 255));
        lblTituloPagina.setText("Dashboard");
        lblTituloPagina.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblTituloPagina.setForeground(new java.awt.Color(0, 78, 174));

        lblUsuario.setText("usuario@almoxarifado.local  •  PERFIL");
        lblUsuario.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblUsuario.setForeground(new java.awt.Color(108, 117, 125));

        javax.swing.GroupLayout pnlCabecalhoLayout = new javax.swing.GroupLayout(pnlCabecalho);
        pnlCabecalho.setLayout(pnlCabecalhoLayout);
        pnlCabecalhoLayout.setHorizontalGroup(
            pnlCabecalhoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCabecalhoLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(lblTituloPagina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(lblUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );
        pnlCabecalhoLayout.setVerticalGroup(
            pnlCabecalhoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCabecalhoLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlCabecalhoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblTituloPagina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16))
        );

        scrollConteudo.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        pnlBoasVindas.setBackground(new java.awt.Color(248, 249, 250));
        lblBoasVindas.setText("Selecione um módulo no menu ao lado.");
        lblBoasVindas.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblBoasVindas.setForeground(new java.awt.Color(108, 117, 125));

        javax.swing.GroupLayout pnlBoasVindasLayout = new javax.swing.GroupLayout(pnlBoasVindas);
        pnlBoasVindas.setLayout(pnlBoasVindasLayout);
        pnlBoasVindasLayout.setHorizontalGroup(
            pnlBoasVindasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBoasVindasLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(lblBoasVindas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pnlBoasVindasLayout.setVerticalGroup(
            pnlBoasVindasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBoasVindasLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(lblBoasVindas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        scrollConteudo.setViewportView(pnlBoasVindas);

        javax.swing.GroupLayout pnlPrincipalLayout = new javax.swing.GroupLayout(pnlPrincipal);
        pnlPrincipal.setLayout(pnlPrincipalLayout);
        pnlPrincipalLayout.setHorizontalGroup(
            pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPrincipalLayout.createSequentialGroup()
                .addComponent(pnlMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addGroup(pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlCabecalho, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scrollConteudo, javax.swing.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE)))
        );
        pnlPrincipalLayout.setVerticalGroup(
            pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlMenu, javax.swing.GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE)
            .addGroup(pnlPrincipalLayout.createSequentialGroup()
                .addComponent(pnlCabecalho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(scrollConteudo, javax.swing.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE))
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
    private javax.swing.JButton btnCategorias;
    private javax.swing.JButton btnDashboard;
    private javax.swing.JButton btnEquipamentos;
    private javax.swing.JButton btnFuncionarios;
    private javax.swing.JButton btnLocalizacoes;
    private javax.swing.JButton btnManutencoes;
    private javax.swing.JButton btnMovimentacoes;
    private javax.swing.JButton btnOcorrencias;
    private javax.swing.JButton btnRelatorios;
    private javax.swing.JButton btnSair;
    private javax.swing.JButton btnSetores;
    private javax.swing.JButton btnUsuarios;
    private javax.swing.JLabel lblBoasVindas;
    private javax.swing.JLabel lblGrupoCadastros;
    private javax.swing.JLabel lblGrupoOperacao;
    private javax.swing.JLabel lblGrupoRelatorios;
    private javax.swing.JLabel lblGrupoVisao;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblNomeSistema;
    private javax.swing.JLabel lblTituloPagina;
    private javax.swing.JLabel lblUsuario;
    private javax.swing.JPanel pnlBoasVindas;
    private javax.swing.JPanel pnlCabecalho;
    private javax.swing.JPanel pnlMenu;
    private javax.swing.JPanel pnlPrincipal;
    private javax.swing.JScrollPane scrollConteudo;
    // End of variables declaration//GEN-END:variables
}
