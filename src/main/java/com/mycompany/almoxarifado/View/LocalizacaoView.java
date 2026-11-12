package com.mycompany.almoxarifado.View;

import com.mycompany.almoxarifado.Dto.LocalizacaoDTO;
import com.mycompany.almoxarifado.Service.LocalizacaoService;
import com.mycompany.almoxarifado.Service.AuthSession;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;

/** Formulário JFrame do NetBeans para localizações. */
public class LocalizacaoView extends javax.swing.JFrame {

    private final LocalizacaoService service = new LocalizacaoService();
    private Long idSelecionado;

    public LocalizacaoView() {
        AuthSession.exigirAutenticacao();
        initComponents();
        configurarEventos();
        aplicarPermissoes();
        limparFormulario();
        carregarDados();
    }

    private void configurarEventos() {
        btnNovo.addActionListener(e -> limparFormulario());
        btnSalvar.addActionListener(e -> cadastrar());
        btnEditar.addActionListener(e -> editar());
        btnExcluir.addActionListener(e -> excluir());
        tblDados.getSelectionModel().addListSelectionListener(this::selecionarLinha);
    }

    // localização é cadastro do Chefe
    private void aplicarPermissoes() {
        boolean permitido = AuthSession.ehChefe();
        btnNovo.setVisible(permitido);
        btnSalvar.setVisible(permitido);
        btnEditar.setVisible(permitido);
        btnExcluir.setVisible(permitido);
    }

    private LocalizacaoDTO lerFormulario() {
        LocalizacaoDTO dto = new LocalizacaoDTO();
        dto.setNome(ViewInputSupport.texto(txtNome));
        dto.setCorredor(ViewInputSupport.inteiro(txtCorredor, "O corredor", false));
        dto.setPrateleira(ViewInputSupport.inteiro(txtPrateleira, "A prateleira", false));
        dto.setAtivo(chkAtivo.isSelected());
        return dto;
    }

    private void cadastrar() {
        try {
            service.cadastrar(lerFormulario());
            sucesso("Localização cadastrada com sucesso.");
        } catch (IllegalArgumentException | SQLException ex) {
            erro(ex);
        }
    }

    private void editar() {
        if (idSelecionado == null) {
            aviso("Selecione a localização para editar.");
            return;
        }
        try {
            LocalizacaoDTO dto = lerFormulario();
            dto.setIdLocalizacao(idSelecionado);
            service.atualizar(dto);
            sucesso("Localização atualizada com sucesso.");
        } catch (IllegalArgumentException | SQLException ex) {
            erro(ex);
        }
    }

    private void excluir() {
        if (idSelecionado == null) {
            aviso("Selecione a localização para excluir.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Deseja excluir a localização selecionada?", "Confirmação", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            service.excluir(idSelecionado);
            sucesso("Localização excluída com sucesso.");
        } catch (IllegalArgumentException | SQLException ex) {
            erro(ex);
        }
    }

    private void carregarDados() {
        try {
            DefaultTableModel modelo = (DefaultTableModel) tblDados.getModel();
            modelo.setRowCount(0);
            for (LocalizacaoDTO dto : service.listarTodos()) {
                modelo.addRow(new Object[]{
                    dto.getIdLocalizacao(),
                    dto.getNome(),
                    dto.getCorredor(),
                    dto.getPrateleira(),
                    dto.isAtivo() ? "Ativo" : "Inativo"
                });
            }
        } catch (SQLException ex) {
            erro(ex);
        }
    }

    private void selecionarLinha(ListSelectionEvent evento) {
        if (evento.getValueIsAdjusting() || tblDados.getSelectedRow() < 0) {
            return;
        }
        int linha = tblDados.convertRowIndexToModel(tblDados.getSelectedRow());
        Long id = Long.valueOf(tblDados.getModel().getValueAt(linha, 0).toString());
        try {
            preencher(service.buscarPorId(id));
            idSelecionado = id;
            btnSalvar.setEnabled(false);
            btnEditar.setEnabled(true);
            btnExcluir.setEnabled(true);
        } catch (SQLException | IllegalArgumentException ex) {
            erro(ex);
        }
    }

    private void preencher(LocalizacaoDTO dto) {
        ViewInputSupport.preencher(txtNome, dto.getNome());
        ViewInputSupport.preencher(txtCorredor, dto.getCorredor());
        ViewInputSupport.preencher(txtPrateleira, dto.getPrateleira());
        chkAtivo.setSelected(dto.isAtivo());
    }

    private void limparFormulario() {
        idSelecionado = null;
        txtNome.setText("");
        txtCorredor.setText("");
        txtPrateleira.setText("");
        chkAtivo.setSelected(true);
        tblDados.clearSelection();
        btnSalvar.setEnabled(true);
        btnEditar.setEnabled(false);
        btnExcluir.setEnabled(false);
    }

    private void sucesso(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem);
        limparFormulario();
        carregarDados();
    }

    private void aviso(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    private void erro(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlPrincipal = new javax.swing.JPanel();
        lblTituloFormulario = new javax.swing.JLabel();
        pnlFormulario = new javax.swing.JPanel();
        lblNome = new javax.swing.JLabel();
        txtNome = new javax.swing.JTextField();
        lblCorredor = new javax.swing.JLabel();
        txtCorredor = new javax.swing.JTextField();
        lblPrateleira = new javax.swing.JLabel();
        txtPrateleira = new javax.swing.JTextField();
        lblStatus = new javax.swing.JLabel();
        chkAtivo = new javax.swing.JCheckBox();
        btnNovo = new javax.swing.JButton();
        btnSalvar = new javax.swing.JButton();
        btnEditar = new javax.swing.JButton();
        btnExcluir = new javax.swing.JButton();
        lblTituloPesquisa = new javax.swing.JLabel();
        pnlPesquisa = new javax.swing.JPanel();
        lblBuscar = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        lblTituloLista = new javax.swing.JLabel();
        scrollDados = new javax.swing.JScrollPane();
        tblDados = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Almoxarifado - Localizações");
        setMinimumSize(new java.awt.Dimension(1000, 720));

        pnlPrincipal.setBackground(new java.awt.Color(248, 249, 250));
        lblTituloFormulario.setText("Cadastro / Edição de Localização");
        lblTituloFormulario.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTituloFormulario.setForeground(new java.awt.Color(0, 78, 174));

        pnlFormulario.setBackground(new java.awt.Color(255, 255, 255));
        pnlFormulario.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(179, 179, 179), 1, true));
        lblNome.setText("Nome:");
        lblNome.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        lblCorredor.setText("Corredor:");
        lblCorredor.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        lblPrateleira.setText("Prateleira:");
        lblPrateleira.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        lblStatus.setText("Status:");
        lblStatus.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        chkAtivo.setText("Ativo");
        chkAtivo.setBackground(new java.awt.Color(255, 255, 255));

        btnNovo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnNovo.setText("+ Novo");
        btnNovo.setBackground(new java.awt.Color(0, 78, 174));
        btnNovo.setForeground(new java.awt.Color(255, 255, 255));
        btnNovo.setFocusPainted(false);

        btnSalvar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnSalvar.setText("Salvar");
        btnSalvar.setBackground(new java.awt.Color(53, 158, 77));
        btnSalvar.setForeground(new java.awt.Color(255, 255, 255));
        btnSalvar.setFocusPainted(false);

        btnEditar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnEditar.setText("Editar");
        btnEditar.setBackground(new java.awt.Color(228, 146, 10));
        btnEditar.setForeground(new java.awt.Color(255, 255, 255));
        btnEditar.setFocusPainted(false);

        btnExcluir.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnExcluir.setText("Excluir");
        btnExcluir.setBackground(new java.awt.Color(214, 62, 50));
        btnExcluir.setForeground(new java.awt.Color(255, 255, 255));
        btnExcluir.setFocusPainted(false);

        javax.swing.GroupLayout pnlFormularioLayout = new javax.swing.GroupLayout(pnlFormulario);
        pnlFormulario.setLayout(pnlFormularioLayout);
        pnlFormularioLayout.setHorizontalGroup(
            pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormularioLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlFormularioLayout.createSequentialGroup()
                        .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblNome, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblPrateleira, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNome, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(txtPrateleira, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE))
                        .addGap(24, 24, 24)
                        .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblCorredor, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCorredor, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(chkAtivo, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)))
                    .addGroup(pnlFormularioLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnNovo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(16, 16, 16))
        );
        pnlFormularioLayout.setVerticalGroup(
            pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormularioLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblNome, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCorredor, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCorredor, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblPrateleira, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPrateleira, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkAtivo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGap(8, 8, 8)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNovo, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16))
        );

        lblTituloPesquisa.setText("Pesquisa");
        lblTituloPesquisa.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTituloPesquisa.setForeground(new java.awt.Color(0, 78, 174));

        pnlPesquisa.setBackground(new java.awt.Color(255, 255, 255));
        pnlPesquisa.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(179, 179, 179), 1, true));
        lblBuscar.setText("Buscar:");
        lblBuscar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        javax.swing.GroupLayout pnlPesquisaLayout = new javax.swing.GroupLayout(pnlPesquisa);
        pnlPesquisa.setLayout(pnlPesquisaLayout);
        pnlPesquisaLayout.setHorizontalGroup(
            pnlPesquisaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPesquisaLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(lblBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(txtBuscar, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
                .addGap(16, 16, 16))
        );
        pnlPesquisaLayout.setVerticalGroup(
            pnlPesquisaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPesquisaLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(pnlPesquisaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12))
        );

        lblTituloLista.setText("Lista de Localizações");
        lblTituloLista.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTituloLista.setForeground(new java.awt.Color(0, 78, 174));

        tblDados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nome", "Corredor", "Prateleira", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDados.setRowHeight(26);

        scrollDados.setViewportView(tblDados);

        javax.swing.GroupLayout pnlPrincipalLayout = new javax.swing.GroupLayout(pnlPrincipal);
        pnlPrincipal.setLayout(pnlPrincipalLayout);
        pnlPrincipalLayout.setHorizontalGroup(
            pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPrincipalLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTituloFormulario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlFormulario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblTituloPesquisa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlPesquisa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblTituloLista, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scrollDados, javax.swing.GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE))
                .addGap(24, 24, 24))
        );
        pnlPrincipalLayout.setVerticalGroup(
            pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPrincipalLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(lblTituloFormulario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(pnlFormulario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(lblTituloPesquisa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(pnlPesquisa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(lblTituloLista, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(scrollDados, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                .addGap(20, 20, 20))
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
    /** Permite executar esta JFrame isoladamente pelo NetBeans. */
    public static void main(String[] args) { com.mycompany.almoxarifado.Almoxarifado.main(args); }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnNovo;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JCheckBox chkAtivo;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblCorredor;
    private javax.swing.JLabel lblNome;
    private javax.swing.JLabel lblPrateleira;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblTituloFormulario;
    private javax.swing.JLabel lblTituloLista;
    private javax.swing.JLabel lblTituloPesquisa;
    private javax.swing.JPanel pnlFormulario;
    private javax.swing.JPanel pnlPesquisa;
    private javax.swing.JPanel pnlPrincipal;
    private javax.swing.JScrollPane scrollDados;
    private javax.swing.JTable tblDados;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtCorredor;
    private javax.swing.JTextField txtNome;
    private javax.swing.JTextField txtPrateleira;
    // End of variables declaration//GEN-END:variables
}
