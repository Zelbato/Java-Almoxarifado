package com.mycompany.almoxarifado.View;

import com.mycompany.almoxarifado.Dto.LocalizacaoDTO;
import com.mycompany.almoxarifado.Service.LocalizacaoService;
import com.mycompany.almoxarifado.Service.AuthSession;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;

/** Formulário JFrame do NetBeans para localizações. */
public class LocalizacaoView extends javax.swing.JFrame {
    private final LocalizacaoService service = new LocalizacaoService();
    private final Map<String, JTextField> campos = new LinkedHashMap<>();
    private Long idSelecionado;

    public LocalizacaoView() {
        AuthSession.exigirAutenticacao();
        initComponents();
        montarCampos();
        ajustarLayoutFormulario();
        ViewStyle.botoesCrud(btnNovo, btnSalvar, btnEditar, btnExcluir);
        configurarEventos();
        aplicarPermissoes();
        limparFormulario();
        carregarDados();
    }

    private void configurarEventos() {
        btnNovo.addActionListener(e -> limparFormulario()); btnSalvar.addActionListener(e -> cadastrar()); btnEditar.addActionListener(e -> editar()); btnExcluir.addActionListener(e -> excluir());
        tblDados.getSelectionModel().addListSelectionListener(this::selecionarLinha);
    }
    private void aplicarPermissoes() { boolean permitido = AuthSession.podeEscrever(); btnNovo.setEnabled(permitido); btnSalvar.setEnabled(permitido); btnEditar.setEnabled(false); btnExcluir.setEnabled(false); }
    private LocalizacaoDTO lerFormulario() { LocalizacaoDTO dto = new LocalizacaoDTO(); dto.setNome(ViewInputSupport.texto(campos.get("Nome"))); dto.setCorredor(ViewInputSupport.inteiro(campos.get("Corredor"), "O corredor", false)); dto.setPrateleira(ViewInputSupport.inteiro(campos.get("Prateleira"), "A prateleira", false)); dto.setAtivo(chkAtivo.isSelected()); return dto; }
    private void cadastrar() { try { service.cadastrar(lerFormulario()); sucesso("Localização cadastrada com sucesso."); } catch (IllegalArgumentException | SQLException ex) { erro(ex); } }
    private void editar() { if (idSelecionado == null) { aviso("Selecione a localização para editar."); return; } try { LocalizacaoDTO dto = lerFormulario(); dto.setIdLocalizacao(idSelecionado); service.atualizar(dto); sucesso("Localização atualizada com sucesso."); } catch (IllegalArgumentException | SQLException ex) { erro(ex); } }
    private void excluir() { if (idSelecionado == null) { aviso("Selecione a localização para excluir."); return; } if (JOptionPane.showConfirmDialog(this, "Deseja excluir a localização selecionada?", "Confirmação", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return; try { service.excluir(idSelecionado); sucesso("Localização excluída com sucesso."); } catch (IllegalArgumentException | SQLException ex) { erro(ex); } }
    private void carregarDados() { try { DefaultTableModel modelo = (DefaultTableModel) tblDados.getModel(); modelo.setRowCount(0); for (LocalizacaoDTO dto : service.listarTodos()) modelo.addRow(new Object[]{dto.getIdLocalizacao(), dto.getNome(), dto.getCorredor(), dto.getPrateleira(), dto.isAtivo() ? "Ativo" : "Inativo"}); } catch (SQLException ex) { erro(ex); } }
    private void selecionarLinha(ListSelectionEvent evento) { if (evento.getValueIsAdjusting() || tblDados.getSelectedRow() < 0) return; int linha = tblDados.convertRowIndexToModel(tblDados.getSelectedRow()); Long id = Long.valueOf(tblDados.getModel().getValueAt(linha, 0).toString()); try { preencher(service.buscarPorId(id)); idSelecionado = id; btnSalvar.setEnabled(false); boolean permitido = AuthSession.podeEscrever(); btnEditar.setEnabled(permitido); btnExcluir.setEnabled(permitido); } catch (SQLException | IllegalArgumentException ex) { erro(ex); } }
    private void preencher(LocalizacaoDTO dto) { preencher("Nome", dto.getNome()); preencher("Corredor", dto.getCorredor()); preencher("Prateleira", dto.getPrateleira()); chkAtivo.setSelected(dto.isAtivo()); }
    private void preencher(String campo, Object valor) { ViewInputSupport.preencher(campos.get(campo), valor); }
    private void limparFormulario() { idSelecionado = null; campos.values().forEach(c -> c.setText("")); chkAtivo.setSelected(true); tblDados.clearSelection(); boolean permitido = AuthSession.podeEscrever(); btnSalvar.setEnabled(permitido); btnEditar.setEnabled(false); btnExcluir.setEnabled(false); }
    private void sucesso(String mensagem) { JOptionPane.showMessageDialog(this, mensagem); limparFormulario(); carregarDados(); }
    private void aviso(String mensagem) { JOptionPane.showMessageDialog(this, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE); }
    private void erro(Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE); }

    private void montarCampos() {
        campos.put("Nome", txtCampo1);
        campos.put("Corredor", txtCampo2);
        campos.put("Prateleira", txtCampo3);
    }

    private void ajustarLayoutFormulario() {
        scrollCampos.setViewportView(null);

        pnlCampos.removeAll();
        pnlCampos.setLayout(new GridBagLayout());
        pnlCampos.setBackground(java.awt.Color.WHITE);
        pnlCampos.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 14, 14, 14));

        java.awt.Font fonte = new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12);

        lblCampo1.setFont(fonte);
        lblCampo2.setFont(fonte);
        lblCampo3.setFont(fonte);

        txtCampo1.setPreferredSize(new Dimension(300, 32));
        txtCampo2.setPreferredSize(new Dimension(300, 32));
        txtCampo3.setPreferredSize(new Dimension(300, 32));

        adicionarCampo(lblCampo1, txtCampo1, 0, 0);
        adicionarCampo(lblCampo2, txtCampo2, 2, 0);
        adicionarCampo(lblCampo3, txtCampo3, 0, 1);

        javax.swing.JLabel lblStatus = new javax.swing.JLabel("Status:");
        lblStatus.setFont(fonte);
        adicionarCampo(lblStatus, chkAtivo, 2, 1);

        javax.swing.JPanel pnlAcoes = new javax.swing.JPanel(
                new FlowLayout(FlowLayout.RIGHT, 12, 0)
        );
        pnlAcoes.setOpaque(false);
        pnlAcoes.add(btnNovo);
        pnlAcoes.add(btnSalvar);
        pnlAcoes.add(btnEditar);
        pnlAcoes.add(btnExcluir);

        pnlFormulario.removeAll();
        pnlFormulario.setLayout(new BorderLayout(0, 14));
        pnlFormulario.add(pnlCampos, BorderLayout.NORTH);
        pnlFormulario.add(pnlAcoes, BorderLayout.SOUTH);
        pnlFormulario.setPreferredSize(new Dimension(950, 240));

        pnlPesquisa.removeAll();
        pnlPesquisa.setLayout(new BorderLayout(16, 0));
        pnlPesquisa.setBorder(
                javax.swing.BorderFactory.createCompoundBorder(
                        javax.swing.BorderFactory.createLineBorder(
                                new java.awt.Color(179, 179, 179)
                        ),
                        javax.swing.BorderFactory.createEmptyBorder(14, 16, 14, 16)
                )
        );
        pnlPesquisa.add(lblBuscar, BorderLayout.WEST);
        pnlPesquisa.add(txtBuscar, BorderLayout.CENTER);

        tblDados.setRowHeight(26);
        tblDados.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblDados.getTableHeader().setReorderingAllowed(false);

        jPanel1.setPreferredSize(new Dimension(1000, 760));
        setMinimumSize(new Dimension(1030, 790));

        pnlCampos.revalidate();
        pnlCampos.repaint();
        pnlFormulario.revalidate();
        pnlFormulario.repaint();
        pnlPesquisa.revalidate();
        pnlPesquisa.repaint();

        pack();
        setLocationRelativeTo(null);
    }

    private void adicionarCampo(
            javax.swing.JLabel label,
            java.awt.Component campo,
            int coluna,
            int linha
    ) {
        GridBagConstraints gbcLabel = new GridBagConstraints();
        gbcLabel.gridx = coluna;
        gbcLabel.gridy = linha;
        gbcLabel.weightx = 0;
        gbcLabel.fill = GridBagConstraints.NONE;
        gbcLabel.anchor = GridBagConstraints.WEST;
        gbcLabel.insets = new Insets(8, 0, 8, 12);

        pnlCampos.add(label, gbcLabel);

        GridBagConstraints gbcCampo = new GridBagConstraints();
        gbcCampo.gridx = coluna + 1;
        gbcCampo.gridy = linha;
        gbcCampo.weightx = 1;
        gbcCampo.fill = GridBagConstraints.HORIZONTAL;
        gbcCampo.anchor = GridBagConstraints.WEST;
        gbcCampo.insets = new Insets(8, 0, 8, coluna == 0 ? 24 : 0);

        pnlCampos.add(campo, gbcCampo);
    }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        pnlFormulario = new javax.swing.JPanel();
        pnlCampos = new javax.swing.JPanel();
        lblCampo1 = new javax.swing.JLabel();
        txtCampo1 = new javax.swing.JTextField();
        lblCampo2 = new javax.swing.JLabel();
        txtCampo2 = new javax.swing.JTextField();
        lblCampo3 = new javax.swing.JLabel();
        txtCampo3 = new javax.swing.JTextField();
        scrollCampos = new javax.swing.JScrollPane();
        pnlPesquisa = new javax.swing.JPanel();
        lblTituloFormulario = new javax.swing.JLabel();
        lblTituloPesquisa = new javax.swing.JLabel();
        lblTituloLista = new javax.swing.JLabel();
        lblBuscar = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        btnNovo = new javax.swing.JButton();
        btnSalvar = new javax.swing.JButton();
        btnEditar = new javax.swing.JButton();
        btnExcluir = new javax.swing.JButton();
        scrollDados = new javax.swing.JScrollPane();
        tblDados = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Almoxarifado - Localizações");
        getContentPane().setBackground(new java.awt.Color(248, 249, 250));

        jPanel1.setBackground(new java.awt.Color(248, 249, 250));
        jPanel1.setPreferredSize(new java.awt.Dimension(719, 600));
        pnlFormulario.setBackground(java.awt.Color.WHITE);
        pnlFormulario.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(179, 179, 179), 1, true));
        pnlPesquisa.setBackground(java.awt.Color.WHITE);
        pnlPesquisa.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(179, 179, 179)));

        java.awt.Font tituloFont = new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12);
        lblTituloFormulario.setFont(tituloFont); lblTituloFormulario.setForeground(new java.awt.Color(0, 78, 174)); lblTituloFormulario.setText("Cadastro / Edição de Localização");
        lblTituloPesquisa.setFont(tituloFont); lblTituloPesquisa.setForeground(new java.awt.Color(0, 78, 174)); lblTituloPesquisa.setText("Pesquisa");
        lblTituloLista.setFont(tituloFont); lblTituloLista.setForeground(new java.awt.Color(0, 78, 174)); lblTituloLista.setText("Lista de Localizações");
        lblBuscar.setFont(tituloFont); lblBuscar.setText("Buscar:");
        txtBuscar.setText("Digite para buscar...");
        pnlCampos.setLayout(new java.awt.GridLayout(0, 2, 12, 10));
        lblCampo1.setFont(tituloFont); lblCampo1.setText("Nome:"); pnlCampos.add(lblCampo1); pnlCampos.add(txtCampo1);
        lblCampo2.setFont(tituloFont); lblCampo2.setText("Corredor:"); pnlCampos.add(lblCampo2); pnlCampos.add(txtCampo2);
        lblCampo3.setFont(tituloFont); lblCampo3.setText("Prateleira:"); pnlCampos.add(lblCampo3); pnlCampos.add(txtCampo3);
        chkAtivo = new javax.swing.JCheckBox("Ativo"); pnlCampos.add(chkAtivo); pnlCampos.add(new javax.swing.JLabel());
        scrollCampos.setViewportView(pnlCampos);

        btnNovo.setBackground(new java.awt.Color(0, 78, 174)); btnNovo.setFont(tituloFont); btnNovo.setForeground(java.awt.Color.WHITE); btnNovo.setText("+ Novo"); btnNovo.setPreferredSize(new java.awt.Dimension(86, 38));
        btnSalvar.setBackground(new java.awt.Color(53, 158, 77)); btnSalvar.setFont(tituloFont); btnSalvar.setForeground(java.awt.Color.WHITE); btnSalvar.setText("Salvar"); btnSalvar.setPreferredSize(new java.awt.Dimension(87, 38));
        btnEditar.setBackground(new java.awt.Color(228, 146, 10)); btnEditar.setFont(tituloFont); btnEditar.setForeground(java.awt.Color.WHITE); btnEditar.setText("Editar"); btnEditar.setPreferredSize(new java.awt.Dimension(95, 38));
        btnExcluir.setBackground(new java.awt.Color(214, 62, 50)); btnExcluir.setFont(tituloFont); btnExcluir.setForeground(java.awt.Color.WHITE); btnExcluir.setText("Excluir"); btnExcluir.setPreferredSize(new java.awt.Dimension(96, 38));

        tblDados.setModel(new DefaultTableModel(new Object[][] {}, new String[] {"ID", "Nome", "Corredor", "Prateleira", "Status"}) { public boolean isCellEditable(int row, int column) { return false; } });
        scrollDados.setViewportView(tblDados);

        javax.swing.GroupLayout formularioLayout = new javax.swing.GroupLayout(pnlFormulario);
        pnlFormulario.setLayout(formularioLayout);
        formularioLayout.setHorizontalGroup(formularioLayout.createSequentialGroup().addGap(14).addGroup(formularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(scrollCampos, 0, 665, Short.MAX_VALUE).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, formularioLayout.createSequentialGroup().addComponent(btnNovo, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(12).addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(12).addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(8).addComponent(btnExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(14)));
        formularioLayout.setVerticalGroup(formularioLayout.createSequentialGroup().addGap(14).addComponent(scrollCampos, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(12).addGroup(formularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(btnNovo, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(btnExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(14));

        javax.swing.GroupLayout pesquisaLayout = new javax.swing.GroupLayout(pnlPesquisa);
        pnlPesquisa.setLayout(pesquisaLayout);
        pesquisaLayout.setHorizontalGroup(pesquisaLayout.createSequentialGroup().addGap(16).addComponent(lblBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(18).addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 486, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(16));
        pesquisaLayout.setVerticalGroup(pesquisaLayout.createSequentialGroup().addGap(14).addGroup(pesquisaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(lblBuscar).addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(14));

        javax.swing.GroupLayout painelLayout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(painelLayout);
        painelLayout.setHorizontalGroup(painelLayout.createSequentialGroup().addGap(25).addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(lblTituloFormulario).addComponent(pnlFormulario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(lblTituloPesquisa).addComponent(pnlPesquisa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(lblTituloLista).addComponent(scrollDados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addGap(25));
        painelLayout.setVerticalGroup(painelLayout.createSequentialGroup().addGap(31).addComponent(lblTituloFormulario).addGap(6).addComponent(pnlFormulario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(18).addComponent(lblTituloPesquisa).addGap(6).addComponent(pnlPesquisa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(20).addComponent(lblTituloLista).addGap(6).addComponent(scrollDados, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(20));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createSequentialGroup().addGap(14).addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGap(15));
        layout.setVerticalGroup(layout.createSequentialGroup().addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE));
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblCampo1;
    private javax.swing.JLabel lblCampo2;
    private javax.swing.JLabel lblCampo3;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblTituloFormulario;
    private javax.swing.JLabel lblTituloLista;
    private javax.swing.JLabel lblTituloPesquisa;
    private javax.swing.JPanel pnlCampos;
    private javax.swing.JPanel pnlFormulario;
    private javax.swing.JPanel pnlPesquisa;
    private javax.swing.JScrollPane scrollCampos;
    private javax.swing.JScrollPane scrollDados;
    private javax.swing.JTable tblDados;
    private javax.swing.JTextField txtCampo1;
    private javax.swing.JTextField txtCampo2;
    private javax.swing.JTextField txtCampo3;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables
}
