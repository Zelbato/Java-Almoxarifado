package com.mycompany.almoxarifado.View;

import com.mycompany.almoxarifado.Service.AuthSession;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Formulário JFrame do menu principal protegido pela sessão.
 */
public class MainView extends javax.swing.JFrame {

    private static final Color FUNDO = new Color(248, 249, 250);
    private static final Color BRANCO = Color.WHITE;
    private static final Color TEXTO = new Color(33, 37, 41);
    private static final Color TEXTO_SECUNDARIO = new Color(108, 117, 125);
    private static final Color BORDA = new Color(218, 223, 229);
    private static final Color HOVER = new Color(238, 245, 255);

    public MainView() {
        AuthSession.exigirAutenticacao();
        initComponents();
        lblUsuario.setText(
                AuthSession.usuario().getEmail()
                + "  •  "
                + AuthSession.tipoUsuario()
        );
        configurarCards();
        configurarEventos();
    }

    /**
     * Permite executar esta JFrame isoladamente pelo NetBeans.
     */
    public static void main(String[] args) {
        com.mycompany.almoxarifado.Almoxarifado.main(args);
    }

    private void configurarEventos() {
        btnSetores.addActionListener(e -> abrir(new SetorView()));
        btnCategorias.addActionListener(e -> abrir(new CategoriaView()));
        btnLocalizacoes.addActionListener(e -> abrir(new LocalizacaoView()));

        btnFuncionarios.addActionListener(e -> abrir(new FuncionarioView()));
        btnUsuarios.addActionListener(e -> abrir(new UsuarioView()));

        btnEquipamentos.addActionListener(e -> abrir(new EquipamentoView()));
        btnMovimentacoes.addActionListener(e -> abrir(new MovimentacaoView()));
        btnManutencoes.addActionListener(e -> abrir(new ManutencaoView()));
        btnOcorrencias.addActionListener(e -> abrir(new OcorrenciaView()));

        btnSair.addActionListener(e -> sair());
    }

    private void abrir(JFrame tela) {
        tela.setVisible(true);
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

        AuthSession.sair();
        new LoginView().setVisible(true);
        dispose();
    }

    private void configurarCards() {
        configurarCard(
                btnSetores,
                "Setores",
                "Cadastre e organize os setores da empresa."
        );

        configurarCard(
                btnCategorias,
                "Categorias",
                "Organize os equipamentos por categoria."
        );

        configurarCard(
                btnLocalizacoes,
                "Localizações",
                "Controle corredores, prateleiras e armazenamento."
        );

        configurarCard(
                btnFuncionarios,
                "Funcionários",
                "Cadastre funcionários e vincule seus setores."
        );

        configurarCard(
                btnUsuarios,
                "Usuários",
                "Gerencie as contas de acesso ao sistema."
        );

        configurarCard(
                btnEquipamentos,
                "Equipamentos",
                "Cadastre e acompanhe os equipamentos."
        );

        configurarCard(
                btnMovimentacoes,
                "Movimentações",
                "Registre retiradas, devoluções e prazos."
        );

        configurarCard(
                btnManutencoes,
                "Manutenções",
                "Acompanhe serviços e o histórico de manutenção."
        );

        configurarCard(
                btnOcorrencias,
                "Ocorrências",
                "Registre danos, defeitos e outros problemas."
        );
    }

    private void configurarCard(
            JButton botao,
            String titulo,
            String descricao
    ) {
        botao.setText(
                "<html>"
                + "<div style='width:190px;text-align:left;'>"
                + "<span style='font-size:13px;font-weight:bold;'>"
                + titulo
                + "</span>"
                + "<br>"
                + "<span style='font-size:10px;color:#6C757D;'>"
                + descricao
                + "</span>"
                + "</div>"
                + "</html>"
        );

        botao.setBackground(BRANCO);
        botao.setForeground(TEXTO);
        botao.setHorizontalAlignment(SwingConstants.LEFT);
        botao.setFocusPainted(false);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botao.setPreferredSize(new Dimension(250, 98));
        botao.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDA, 1, true),
                        BorderFactory.createEmptyBorder(12, 14, 12, 14)
                )
        );

        botao.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                botao.setBackground(HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                botao.setBackground(BRANCO);
            }
        });
    }

    private JPanel criarSecao(String titulo, int linhas, int colunas) {
        JPanel painel = new JPanel(new BorderLayout(0, 12));
        painel.setBackground(BRANCO);
        painel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDA, 1, true),
                        BorderFactory.createEmptyBorder(14, 14, 14, 14)
                )
        );

        JLabel label = new JLabel(titulo);
        label.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        label.setForeground(ViewStyle.AZUL);

        JPanel conteudo = new JPanel(
                new GridLayout(linhas, colunas, 12, 12)
        );
        conteudo.setBackground(BRANCO);

        painel.add(label, BorderLayout.NORTH);
        painel.add(conteudo, BorderLayout.CENTER);

        painel.putClientProperty("conteudo", conteudo);

        return painel;
    }

    private void configurarTamanhoSecao(JPanel painel, int altura) {
        painel.setPreferredSize(new Dimension(1000, altura));
        painel.setMinimumSize(new Dimension(100, altura));
        painel.setMaximumSize(new Dimension(Integer.MAX_VALUE, altura));
        painel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
    }

    private JPanel conteudoSecao(JPanel painel) {
        return (JPanel) painel.getClientProperty("conteudo");
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlPrincipal = new javax.swing.JPanel();
        pnlCabecalho = new javax.swing.JPanel();
        pnlTitulos = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        lblSubtitulo = new javax.swing.JLabel();
        pnlConta = new javax.swing.JPanel();
        lblUsuario = new javax.swing.JLabel();
        btnSair = new javax.swing.JButton();
        pnlConteudo = new javax.swing.JPanel();
        pnlCadastros = new javax.swing.JPanel();
        pnlPessoas = new javax.swing.JPanel();
        pnlOperacao = new javax.swing.JPanel();
        btnSetores = new javax.swing.JButton();
        btnCategorias = new javax.swing.JButton();
        btnLocalizacoes = new javax.swing.JButton();
        btnFuncionarios = new javax.swing.JButton();
        btnUsuarios = new javax.swing.JButton();
        btnEquipamentos = new javax.swing.JButton();
        btnMovimentacoes = new javax.swing.JButton();
        btnManutencoes = new javax.swing.JButton();
        btnOcorrencias = new javax.swing.JButton();
        lblRodape = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Almoxarifado - Menu principal");
        setMinimumSize(new java.awt.Dimension(1180, 780));
        setPreferredSize(new java.awt.Dimension(1180, 780));
        getContentPane().setBackground(FUNDO);

        pnlPrincipal.setBackground(FUNDO);
        pnlPrincipal.setBorder(
                BorderFactory.createEmptyBorder(22, 28, 16, 28)
        );
        pnlPrincipal.setLayout(new BorderLayout(0, 16));

        pnlCabecalho.setBackground(FUNDO);
        pnlCabecalho.setLayout(new BorderLayout(20, 0));

        pnlTitulos.setBackground(FUNDO);
        pnlTitulos.setLayout(
                new javax.swing.BoxLayout(
                        pnlTitulos,
                        javax.swing.BoxLayout.Y_AXIS
                )
        );

        lblTitulo.setFont(
                new java.awt.Font(
                        "Segoe UI",
                        java.awt.Font.BOLD,
                        26
                )
        );
        lblTitulo.setForeground(ViewStyle.AZUL);
        lblTitulo.setText("Sistema de Almoxarifado");

        lblSubtitulo.setFont(
                new java.awt.Font(
                        "Segoe UI",
                        java.awt.Font.PLAIN,
                        13
                )
        );
        lblSubtitulo.setForeground(TEXTO_SECUNDARIO);
        lblSubtitulo.setText(
                "Gestão de equipamentos, movimentações e manutenção"
        );

        pnlTitulos.add(lblTitulo);
        pnlTitulos.add(javax.swing.Box.createVerticalStrut(4));
        pnlTitulos.add(lblSubtitulo);

        pnlConta.setBackground(FUNDO);
        pnlConta.setLayout(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        12,
                        8
                )
        );

        lblUsuario.setFont(
                new java.awt.Font(
                        "Segoe UI",
                        java.awt.Font.PLAIN,
                        12
                )
        );
        lblUsuario.setForeground(TEXTO_SECUNDARIO);

        btnSair.setText("Sair");
        ViewStyle.botao(
                btnSair,
                ViewStyle.VERMELHO,
                90
        );

        pnlConta.add(lblUsuario);
        pnlConta.add(btnSair);

        pnlCabecalho.add(pnlTitulos, BorderLayout.WEST);
        pnlCabecalho.add(pnlConta, BorderLayout.EAST);

        pnlConteudo.setBackground(FUNDO);
        pnlConteudo.setLayout(
                new javax.swing.BoxLayout(
                        pnlConteudo,
                        javax.swing.BoxLayout.Y_AXIS
                )
        );

        pnlCadastros = criarSecao(
                "Cadastros básicos",
                1,
                3
        );

        conteudoSecao(pnlCadastros).add(btnSetores);
        conteudoSecao(pnlCadastros).add(btnCategorias);
        conteudoSecao(pnlCadastros).add(btnLocalizacoes);

        pnlPessoas = criarSecao(
                "Pessoas e acesso",
                1,
                2
        );

        conteudoSecao(pnlPessoas).add(btnFuncionarios);
        conteudoSecao(pnlPessoas).add(btnUsuarios);

        pnlOperacao = criarSecao(
                "Operação",
                2,
                2
        );

        conteudoSecao(pnlOperacao).add(btnEquipamentos);
        conteudoSecao(pnlOperacao).add(btnMovimentacoes);
        conteudoSecao(pnlOperacao).add(btnManutencoes);
        conteudoSecao(pnlOperacao).add(btnOcorrencias);

        configurarTamanhoSecao(pnlCadastros, 155);
        configurarTamanhoSecao(pnlPessoas, 155);
        configurarTamanhoSecao(pnlOperacao, 265);

        pnlConteudo.add(pnlCadastros);
        pnlConteudo.add(javax.swing.Box.createVerticalStrut(14));
        pnlConteudo.add(pnlPessoas);
        pnlConteudo.add(javax.swing.Box.createVerticalStrut(14));
        pnlConteudo.add(pnlOperacao);

        lblRodape.setFont(
                new java.awt.Font(
                        "Segoe UI",
                        java.awt.Font.PLAIN,
                        11
                )
        );
        lblRodape.setForeground(TEXTO_SECUNDARIO);
        lblRodape.setHorizontalAlignment(SwingConstants.CENTER);
        lblRodape.setText(
                "Supreme Brasil • Controle de Almoxarifado"
        );

        pnlPrincipal.add(pnlCabecalho, BorderLayout.NORTH);
        pnlPrincipal.add(pnlConteudo, BorderLayout.CENTER);
        pnlPrincipal.add(lblRodape, BorderLayout.SOUTH);

        setContentPane(pnlPrincipal);
        pack();
        setLocationRelativeTo(null);
    }
    // </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCategorias;
    private javax.swing.JButton btnEquipamentos;
    private javax.swing.JButton btnFuncionarios;
    private javax.swing.JButton btnLocalizacoes;
    private javax.swing.JButton btnManutencoes;
    private javax.swing.JButton btnMovimentacoes;
    private javax.swing.JButton btnOcorrencias;
    private javax.swing.JButton btnSair;
    private javax.swing.JButton btnSetores;
    private javax.swing.JButton btnUsuarios;
    private javax.swing.JLabel lblRodape;
    private javax.swing.JLabel lblSubtitulo;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblUsuario;
    private javax.swing.JPanel pnlCabecalho;
    private javax.swing.JPanel pnlCadastros;
    private javax.swing.JPanel pnlConta;
    private javax.swing.JPanel pnlConteudo;
    private javax.swing.JPanel pnlOperacao;
    private javax.swing.JPanel pnlPessoas;
    private javax.swing.JPanel pnlPrincipal;
    private javax.swing.JPanel pnlTitulos;
    // End of variables declaration//GEN-END:variables
}
