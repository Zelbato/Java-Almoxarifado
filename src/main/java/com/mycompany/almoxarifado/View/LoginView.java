package com.mycompany.almoxarifado.View;

import com.mycompany.almoxarifado.Service.AutenticacaoService;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/** Formulário JFrame de autenticação, mantido pelo GUI Builder do NetBeans. */
public class LoginView extends javax.swing.JFrame {
    private final AutenticacaoService autenticacaoService = new AutenticacaoService();

    public LoginView() { initComponents(); btnEntrar.addActionListener(e -> entrar()); txtSenha.addActionListener(e -> entrar()); }

    /** Permite executar esta JFrame isoladamente pelo NetBeans. */
    public static void main(String[] args) {
        com.mycompany.almoxarifado.Almoxarifado.main(args);
    }

    private void entrar() {
        btnEntrar.setEnabled(false);
        char[] senha = txtSenha.getPassword();
        try {
            autenticacaoService.autenticar(txtEmail.getText(), new String(senha));
            new MainView().setVisible(true);
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Login", JOptionPane.WARNING_MESSAGE);
            txtSenha.selectAll(); txtSenha.requestFocus();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Não foi possível acessar o banco de dados.", "Login", JOptionPane.ERROR_MESSAGE);
        } finally { java.util.Arrays.fill(senha, '\0'); btnEntrar.setEnabled(true); }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        pnlPrincipal = new javax.swing.JPanel(); lblTitulo = new javax.swing.JLabel(); lblEmail = new javax.swing.JLabel(); lblSenha = new javax.swing.JLabel(); txtEmail = new javax.swing.JTextField(); txtSenha = new javax.swing.JPasswordField(); btnEntrar = new javax.swing.JButton();
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE); setTitle("Almoxarifado - Login"); setResizable(false);
        getContentPane().setBackground(new java.awt.Color(248, 249, 250)); pnlPrincipal.setBackground(java.awt.Color.WHITE); pnlPrincipal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(179, 179, 179)));
        lblTitulo.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 20)); lblTitulo.setForeground(ViewStyle.AZUL); lblTitulo.setText("Acesso ao Almoxarifado"); lblEmail.setText("E-mail:"); lblSenha.setText("Senha:"); btnEntrar.setText("Entrar"); ViewStyle.botao(btnEntrar, ViewStyle.AZUL, 100);
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(pnlPrincipal); pnlPrincipal.setLayout(layout);
        layout.setHorizontalGroup(layout.createSequentialGroup().addGap(32).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(lblTitulo).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(lblEmail).addComponent(lblSenha)).addGap(16).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false).addComponent(txtEmail).addComponent(txtSenha, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(btnEntrar, javax.swing.GroupLayout.Alignment.TRAILING)))).addGap(32));
        layout.setVerticalGroup(layout.createSequentialGroup().addGap(30).addComponent(lblTitulo).addGap(26).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(lblEmail).addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(14).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(lblSenha).addComponent(txtSenha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(22).addComponent(btnEntrar).addGap(30));
        setContentPane(pnlPrincipal); pack(); setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEntrar; private javax.swing.JLabel lblEmail, lblSenha, lblTitulo; private javax.swing.JPanel pnlPrincipal; private javax.swing.JTextField txtEmail; private javax.swing.JPasswordField txtSenha;
    // End of variables declaration//GEN-END:variables
}
