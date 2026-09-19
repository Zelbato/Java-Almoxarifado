package com.mycompany.almoxarifado.View;

import com.mycompany.almoxarifado.Service.AutenticacaoService;
import java.awt.Image;
import java.net.URL;
import java.sql.SQLException;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 * Formulário JFrame de autenticação, mantido pelo GUI Builder do NetBeans.
 */
public class LoginView extends javax.swing.JFrame {

    private final AutenticacaoService autenticacaoService = new AutenticacaoService();

    public LoginView() {
        initComponents();
        carregarLogo();
        btnEntrar.addActionListener(e -> entrar());
        txtSenha.addActionListener(e -> entrar());
        txtEmail.requestFocusInWindow();
    }

    /**
     * Permite executar esta JFrame isoladamente pelo NetBeans.
     */
    public static void main(String[] args) {
        com.mycompany.almoxarifado.Almoxarifado.main(args);
    }

    private void carregarLogo() {
        URL recurso = getClass().getResource("/images/supreme-brasil.png");

        if (recurso == null) {
            lblLogo.setText("Supreme Brasil");
            return;
        }

        ImageIcon original = new ImageIcon(recurso);
        Image imagem = original.getImage().getScaledInstance(
                190,
                125,
                Image.SCALE_SMOOTH
        );

        lblLogo.setText("");
        lblLogo.setIcon(new ImageIcon(imagem));
    }

    private void entrar() {
        btnEntrar.setEnabled(false);
        char[] senha = txtSenha.getPassword();

        try {
            autenticacaoService.autenticar(
                    txtEmail.getText(),
                    new String(senha)
            );

            new MainView().setVisible(true);
            dispose();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Login",
                    JOptionPane.WARNING_MESSAGE
            );

            txtSenha.selectAll();
            txtSenha.requestFocus();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível acessar o banco de dados.",
                    "Login",
                    JOptionPane.ERROR_MESSAGE
            );

        } finally {
            java.util.Arrays.fill(senha, '\0');
            btnEntrar.setEnabled(true);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlPrincipal = new javax.swing.JPanel();
        lblLogo = new javax.swing.JLabel();
        lblTitulo = new javax.swing.JLabel();
        lblSubtitulo = new javax.swing.JLabel();
        lblEmail = new javax.swing.JLabel();
        lblSenha = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        txtSenha = new javax.swing.JPasswordField();
        btnEntrar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Almoxarifado - Login");
        setMinimumSize(new java.awt.Dimension(520, 520));
        setPreferredSize(new java.awt.Dimension(520, 520));
        setResizable(false);
        getContentPane().setBackground(new java.awt.Color(248, 249, 250));

        pnlPrincipal.setBackground(java.awt.Color.WHITE);
        pnlPrincipal.setBorder(
                javax.swing.BorderFactory.createCompoundBorder(
                        javax.swing.BorderFactory.createLineBorder(
                                new java.awt.Color(220, 224, 230)
                        ),
                        javax.swing.BorderFactory.createEmptyBorder(
                                26,
                                42,
                                30,
                                42
                        )
                )
        );

        lblLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLogo.setText("Supreme Brasil");

        lblTitulo.setFont(
                new java.awt.Font(
                        "Segoe UI",
                        java.awt.Font.BOLD,
                        22
                )
        );
        lblTitulo.setForeground(ViewStyle.AZUL);
        lblTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitulo.setText("Acesso ao Almoxarifado");

        lblSubtitulo.setFont(
                new java.awt.Font(
                        "Segoe UI",
                        java.awt.Font.PLAIN,
                        12
                )
        );
        lblSubtitulo.setForeground(
                new java.awt.Color(
                        108,
                        117,
                        125
                )
        );
        lblSubtitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSubtitulo.setText("Entre com suas credenciais para continuar");

        lblEmail.setFont(
                new java.awt.Font(
                        "Segoe UI",
                        java.awt.Font.BOLD,
                        12
                )
        );
        lblEmail.setText("E-mail");

        lblSenha.setFont(
                new java.awt.Font(
                        "Segoe UI",
                        java.awt.Font.BOLD,
                        12
                )
        );
        lblSenha.setText("Senha");

        txtEmail.setPreferredSize(
                new java.awt.Dimension(
                        340,
                        34
                )
        );

        txtSenha.setPreferredSize(
                new java.awt.Dimension(
                        340,
                        34
                )
        );

        btnEntrar.setText("Entrar");
        btnEntrar.setPreferredSize(
                new java.awt.Dimension(
                        340,
                        40
                )
        );

        ViewStyle.botao(
                btnEntrar,
                ViewStyle.AZUL,
                340
        );

        javax.swing.GroupLayout layout
                = new javax.swing.GroupLayout(
                        pnlPrincipal
                );

        pnlPrincipal.setLayout(layout);

        layout.setHorizontalGroup(
                layout.createParallelGroup(
                        javax.swing.GroupLayout.Alignment.CENTER
                )
                        .addComponent(
                                lblLogo,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                220,
                                javax.swing.GroupLayout.PREFERRED_SIZE
                        )
                        .addComponent(
                                lblTitulo,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE
                        )
                        .addComponent(
                                lblSubtitulo,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE
                        )
                        .addGroup(
                                layout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.LEADING
                                )
                                        .addComponent(lblEmail)
                                        .addComponent(
                                                txtEmail,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                340,
                                                javax.swing.GroupLayout.PREFERRED_SIZE
                                        )
                                        .addComponent(lblSenha)
                                        .addComponent(
                                                txtSenha,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                340,
                                                javax.swing.GroupLayout.PREFERRED_SIZE
                                        )
                                        .addComponent(
                                                btnEntrar,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                340,
                                                javax.swing.GroupLayout.PREFERRED_SIZE
                                        )
                        )
        );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(
                                lblLogo,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                125,
                                javax.swing.GroupLayout.PREFERRED_SIZE
                        )
                        .addGap(8)
                        .addComponent(lblTitulo)
                        .addGap(5)
                        .addComponent(lblSubtitulo)
                        .addGap(28)
                        .addComponent(lblEmail)
                        .addGap(6)
                        .addComponent(
                                txtEmail,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                34,
                                javax.swing.GroupLayout.PREFERRED_SIZE
                        )
                        .addGap(16)
                        .addComponent(lblSenha)
                        .addGap(6)
                        .addComponent(
                                txtSenha,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                34,
                                javax.swing.GroupLayout.PREFERRED_SIZE
                        )
                        .addGap(24)
                        .addComponent(
                                btnEntrar,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                40,
                                javax.swing.GroupLayout.PREFERRED_SIZE
                        )
        );

        javax.swing.GroupLayout contentLayout
                = new javax.swing.GroupLayout(
                        getContentPane()
                );

        getContentPane().setLayout(
                contentLayout
        );

        contentLayout.setHorizontalGroup(
                contentLayout.createSequentialGroup()
                        .addGap(28)
                        .addComponent(
                                pnlPrincipal,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE
                        )
                        .addGap(28)
        );

        contentLayout.setVerticalGroup(
                contentLayout.createSequentialGroup()
                        .addGap(24)
                        .addComponent(
                                pnlPrincipal,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE
                        )
                        .addGap(24)
        );

        pack();
        setLocationRelativeTo(null);
    }
    // </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEntrar;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblSenha;
    private javax.swing.JLabel lblSubtitulo;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel pnlPrincipal;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JPasswordField txtSenha;
    // End of variables declaration//GEN-END:variables
}
