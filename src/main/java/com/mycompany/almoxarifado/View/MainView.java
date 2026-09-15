package com.mycompany.almoxarifado.View;

import com.mycompany.almoxarifado.Service.AuthSession;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/** Formulário JFrame do menu principal protegido pela sessão. */
public class MainView extends javax.swing.JFrame {
    public MainView() { AuthSession.exigirAutenticacao(); initComponents(); lblUsuario.setText(AuthSession.usuario().getNome() + " - " + AuthSession.tipoUsuario()); configurarEventos(); }
    /** Permite executar esta JFrame isoladamente pelo NetBeans. */
    public static void main(String[] args) { com.mycompany.almoxarifado.Almoxarifado.main(args); }
    private void configurarEventos() {
        btnCategorias.addActionListener(e -> abrir(new CategoriaView())); btnSetores.addActionListener(e -> abrir(new SetorView())); btnFuncionarios.addActionListener(e -> abrir(new FuncionarioView())); btnLocalizacoes.addActionListener(e -> abrir(new LocalizacaoView()));
        btnUsuarios.addActionListener(e -> abrir(new UsuarioView())); btnEquipamentos.addActionListener(e -> abrir(new EquipamentoView())); btnMovimentacoes.addActionListener(e -> abrir(new MovimentacaoView())); btnManutencoes.addActionListener(e -> abrir(new ManutencaoView())); btnOcorrencias.addActionListener(e -> abrir(new OcorrenciaView()));
        btnTiposUsuario.addActionListener(e -> abrir(new TipoUsuarioView())); btnEstados.addActionListener(e -> abrir(new EstadoConservacaoView())); btnSituacoes.addActionListener(e -> abrir(new SituacaoEquipamentoView())); btnStatusMov.addActionListener(e -> abrir(new StatusMovimentacaoView())); btnStatusMan.addActionListener(e -> abrir(new StatusManutencaoView())); btnTiposMan.addActionListener(e -> abrir(new TipoManutencaoView())); btnStatusOco.addActionListener(e -> abrir(new StatusOcorrenciaView())); btnTiposOco.addActionListener(e -> abrir(new TipoOcorrenciaView())); btnSair.addActionListener(e -> sair());
    }
    private void abrir(JFrame tela) { tela.setVisible(true); }
    private void sair() { if (JOptionPane.showConfirmDialog(this, "Deseja sair do sistema?", "Confirmação", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return; AuthSession.sair(); new LoginView().setVisible(true); dispose(); }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        pnlPrincipal = new javax.swing.JPanel(); lblTitulo = new javax.swing.JLabel(); lblUsuario = new javax.swing.JLabel(); pnlMenu = new javax.swing.JPanel(new java.awt.GridLayout(0, 3, 10, 10));
        btnCategorias = new javax.swing.JButton("Categorias"); btnSetores = new javax.swing.JButton("Setores"); btnFuncionarios = new javax.swing.JButton("Funcionários"); btnLocalizacoes = new javax.swing.JButton("Localizações"); btnUsuarios = new javax.swing.JButton("Usuários"); btnEquipamentos = new javax.swing.JButton("Equipamentos"); btnMovimentacoes = new javax.swing.JButton("Movimentações"); btnManutencoes = new javax.swing.JButton("Manutenções"); btnOcorrencias = new javax.swing.JButton("Ocorrências"); btnTiposUsuario = new javax.swing.JButton("Tipos de usuário"); btnEstados = new javax.swing.JButton("Estados de conservação"); btnSituacoes = new javax.swing.JButton("Situações de equipamento"); btnStatusMov = new javax.swing.JButton("Status de movimentação"); btnStatusMan = new javax.swing.JButton("Status de manutenção"); btnTiposMan = new javax.swing.JButton("Tipos de manutenção"); btnStatusOco = new javax.swing.JButton("Status de ocorrência"); btnTiposOco = new javax.swing.JButton("Tipos de ocorrência"); btnSair = new javax.swing.JButton("Sair");
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE); setTitle("Almoxarifado - Menu principal"); setMinimumSize(new java.awt.Dimension(820, 600));
        getContentPane().setBackground(new java.awt.Color(248, 249, 250)); pnlPrincipal.setBackground(new java.awt.Color(248, 249, 250)); pnlMenu.setBackground(java.awt.Color.WHITE); pnlMenu.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(179, 179, 179)));
        lblTitulo.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 22)); lblTitulo.setForeground(ViewStyle.AZUL); lblTitulo.setText("Sistema de Almoxarifado");
        for (javax.swing.JButton botao : new javax.swing.JButton[]{btnCategorias,btnSetores,btnFuncionarios,btnLocalizacoes,btnUsuarios,btnEquipamentos,btnMovimentacoes,btnManutencoes,btnOcorrencias,btnTiposUsuario,btnEstados,btnSituacoes,btnStatusMov,btnStatusMan,btnTiposMan,btnStatusOco,btnTiposOco}) { ViewStyle.botao(botao, ViewStyle.AZUL, 220); pnlMenu.add(botao); }
        ViewStyle.botao(btnSair, ViewStyle.VERMELHO, 96);
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(pnlPrincipal); pnlPrincipal.setLayout(layout);
        layout.setHorizontalGroup(layout.createSequentialGroup().addGap(24).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(lblTitulo).addGap(0, 0, Short.MAX_VALUE).addComponent(lblUsuario)).addComponent(pnlMenu, javax.swing.GroupLayout.DEFAULT_SIZE, 770, Short.MAX_VALUE).addComponent(btnSair, javax.swing.GroupLayout.Alignment.TRAILING)).addGap(24));
        layout.setVerticalGroup(layout.createSequentialGroup().addGap(24).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(lblTitulo).addComponent(lblUsuario)).addGap(20).addComponent(pnlMenu, javax.swing.GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE).addGap(20).addComponent(btnSair).addGap(24));
        setContentPane(pnlPrincipal); pack(); setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCategorias, btnEquipamentos, btnEstados, btnFuncionarios, btnLocalizacoes, btnManutencoes, btnMovimentacoes, btnOcorrencias, btnSair, btnSetores, btnSituacoes, btnStatusMan, btnStatusMov, btnStatusOco, btnTiposMan, btnTiposOco, btnTiposUsuario, btnUsuarios; private javax.swing.JLabel lblTitulo, lblUsuario; private javax.swing.JPanel pnlMenu, pnlPrincipal;
    // End of variables declaration//GEN-END:variables
}
