package com.mycompany.almoxarifado.View;

import com.mycompany.almoxarifado.Dto.TipoOcorrenciaDTO;
import com.mycompany.almoxarifado.Service.TipoOcorrenciaService;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/** JFrame de consulta de tipos de ocorrência, seguindo o padrão CategoriaView. */
public class TipoOcorrenciaView extends javax.swing.JFrame {
    private final TipoOcorrenciaService service = new TipoOcorrenciaService();

    public TipoOcorrenciaView() { initComponents(); carregarDados(); }

    private void carregarDados() {
        try {
            DefaultTableModel modelo = (DefaultTableModel) tblTipoOcorrencias.getModel();
            modelo.setRowCount(0);
            for (TipoOcorrenciaDTO dto : service.listarTodos()) modelo.addRow(new Object[]{dto.getIdTipoOcorrencia(), dto.getNome(), "Pré-cadastrado"});
        } catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Não foi possível carregar os tipos de ocorrência: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE); }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        pnlPesquisa = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        lblTituloPesquisa = new javax.swing.JLabel();
        lblBuscar = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        lblTituloLista = new javax.swing.JLabel();
        scrollTipoOcorrencia = new javax.swing.JScrollPane();
        tblTipoOcorrencias = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Almoxarifado - Tipos de Ocorrência");
        getContentPane().setBackground(new java.awt.Color(248, 249, 250));
        jPanel1.setBackground(new java.awt.Color(248, 249, 250));
        jPanel1.setPreferredSize(new java.awt.Dimension(719, 600));
        java.awt.Font tituloFont = new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12);
        lblTitulo.setFont(tituloFont); lblTitulo.setForeground(new java.awt.Color(0, 78, 174)); lblTitulo.setText("Consulta de tipos de ocorrência");
        lblTituloPesquisa.setFont(tituloFont); lblTituloPesquisa.setForeground(new java.awt.Color(0, 78, 174)); lblTituloPesquisa.setText("Pesquisa");
        lblTituloLista.setFont(tituloFont); lblTituloLista.setForeground(new java.awt.Color(0, 78, 174)); lblTituloLista.setText("Lista de tipos de ocorrência");
        pnlPesquisa.setBackground(java.awt.Color.WHITE); pnlPesquisa.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(179, 179, 179)));
        lblBuscar.setFont(tituloFont); lblBuscar.setText("Buscar:");
        txtBuscar.setText("Digite para buscar..."); txtBuscar.setEditable(false);
        tblTipoOcorrencias.setModel(new DefaultTableModel(new Object[][] {}, new String[] {"ID", "Nome", "Origem"}) { public boolean isCellEditable(int row, int column) { return false; } });
        scrollTipoOcorrencia.setViewportView(tblTipoOcorrencias);

        javax.swing.GroupLayout pesquisaLayout = new javax.swing.GroupLayout(pnlPesquisa); pnlPesquisa.setLayout(pesquisaLayout);
        pesquisaLayout.setHorizontalGroup(pesquisaLayout.createSequentialGroup().addGap(16).addComponent(lblBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(18).addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 486, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(16));
        pesquisaLayout.setVerticalGroup(pesquisaLayout.createSequentialGroup().addGap(14).addGroup(pesquisaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(lblBuscar).addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(14));

        javax.swing.GroupLayout painelLayout = new javax.swing.GroupLayout(jPanel1); jPanel1.setLayout(painelLayout);
        painelLayout.setHorizontalGroup(painelLayout.createSequentialGroup().addGap(25).addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(lblTitulo).addComponent(lblTituloPesquisa).addComponent(pnlPesquisa, javax.swing.GroupLayout.DEFAULT_SIZE, 669, Short.MAX_VALUE).addComponent(lblTituloLista).addComponent(scrollTipoOcorrencia, javax.swing.GroupLayout.DEFAULT_SIZE, 669, Short.MAX_VALUE)).addGap(25));
        painelLayout.setVerticalGroup(painelLayout.createSequentialGroup().addGap(31).addComponent(lblTitulo).addGap(24).addComponent(lblTituloPesquisa).addGap(6).addComponent(pnlPesquisa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(20).addComponent(lblTituloLista).addGap(6).addComponent(scrollTipoOcorrencia, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(20));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane()); getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createSequentialGroup().addGap(14).addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGap(15));
        layout.setVerticalGroup(layout.createSequentialGroup().addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE));
        pack(); setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /** Permite executar esta JFrame isoladamente pelo NetBeans. */
    public static void main(String[] args) { com.mycompany.almoxarifado.Almoxarifado.main(args); }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblTituloLista;
    private javax.swing.JLabel lblTituloPesquisa;
    private javax.swing.JPanel pnlPesquisa;
    private javax.swing.JScrollPane scrollTipoOcorrencia;
    private javax.swing.JTable tblTipoOcorrencias;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables
}
