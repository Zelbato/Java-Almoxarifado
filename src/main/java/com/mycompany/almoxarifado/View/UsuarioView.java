package com.mycompany.almoxarifado.View;

import com.mycompany.almoxarifado.Dto.UsuarioDTO;
import com.mycompany.almoxarifado.Dto.FuncionarioDTO;
import com.mycompany.almoxarifado.Dto.TipoUsuarioDTO;
import com.mycompany.almoxarifado.Service.UsuarioService;
import com.mycompany.almoxarifado.Service.AuthSession;
import com.mycompany.almoxarifado.Service.FuncionarioService;
import com.mycompany.almoxarifado.Service.TipoUsuarioService;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;

/**
 * Formulário JFrame do NetBeans para usuários.
 */
public class UsuarioView extends javax.swing.JFrame {

    private final UsuarioService service = new UsuarioService();
    private final Map<String, JTextField> campos = new LinkedHashMap<>();
    private Long idSelecionado;
    private JComboBox<ComboItem> cmbTipoUsuario;
    private JComboBox<ComboItem> cmbFuncionario;

    public UsuarioView() {
        AuthSession.exigirAutenticacao();
        initComponents();
        montarCampos();
        configurarSelects();
        ViewStyle.botoesCrud(btnNovo, btnSalvar, btnEditar, btnExcluir);
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

    private void aplicarPermissoes() {
        boolean permitido = AuthSession.podeEscrever();
        btnNovo.setEnabled(permitido);
        btnSalvar.setEnabled(permitido);
        btnEditar.setEnabled(false);
        btnExcluir.setEnabled(false);
    }

    private UsuarioDTO lerFormulario() {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setIdTipoUsuario(ViewInputSupport.id(cmbTipoUsuario, "O tipo de usuário", true));
        dto.setIdFuncionario(ViewInputSupport.id(cmbFuncionario, "O funcionário", false));
        dto.setNome(ViewInputSupport.texto(campos.get("Nome")));
        dto.setCpf(ViewInputSupport.texto(campos.get("CPF")));
        dto.setEmail(ViewInputSupport.texto(campos.get("E-mail")));
        dto.setSenha(ViewInputSupport.texto(campos.get("Senha")));
        dto.setDataNascimento(ViewInputSupport.data(campos.get("Data de nascimento"), "A data de nascimento", false));
        dto.setAtivo(chkAtivo.isSelected());
        return dto;
    }

    private void cadastrar() {
        try {
            service.cadastrar(lerFormulario());
            sucesso("Usuário cadastrada com sucesso.");
        } catch (IllegalArgumentException | SQLException ex) {
            erro(ex);
        }
    }

    private void editar() {
        if (idSelecionado == null) {
            aviso("Selecione o usuário para editar.");
            return;
        }
        try {
            UsuarioDTO dto = lerFormulario();
            dto.setIdUsuario(idSelecionado);
            service.atualizar(dto);
            sucesso("Usuário atualizada com sucesso.");
        } catch (IllegalArgumentException | SQLException ex) {
            erro(ex);
        }
    }

    private void excluir() {
        if (idSelecionado == null) {
            aviso("Selecione o usuário para excluir.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Deseja excluir o usuário selecionada?", "Confirmação", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            service.excluir(idSelecionado);
            sucesso("Usuário excluída com sucesso.");
        } catch (IllegalArgumentException | SQLException ex) {
            erro(ex);
        }
    }

    private void carregarDados() {
        try {
            DefaultTableModel modelo = (DefaultTableModel) tblDados.getModel();
            modelo.setRowCount(0);
            for (UsuarioDTO dto : service.listarTodos()) {
                modelo.addRow(new Object[]{dto.getIdUsuario(), dto.getIdTipoUsuario(), dto.getIdFuncionario() == null ? "-" : dto.getIdFuncionario(), dto.getNome(), dto.getEmail(), dto.getCpf(), dto.isAtivo() ? "Ativo" : "Inativo"});
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
            boolean permitido = AuthSession.podeEscrever();
            btnEditar.setEnabled(permitido);
            btnExcluir.setEnabled(permitido);
        } catch (SQLException | IllegalArgumentException ex) {
            erro(ex);
        }
    }

    private void preencher(UsuarioDTO dto) {
        ViewInputSupport.selecionar(cmbTipoUsuario, dto.getIdTipoUsuario());
        ViewInputSupport.selecionar(cmbFuncionario, dto.getIdFuncionario());
        preencher("Nome", dto.getNome());
        preencher("CPF", dto.getCpf());
        preencher("E-mail", dto.getEmail());
        preencher("Senha", "");
        preencher("Data de nascimento", dto.getDataNascimento());
        chkAtivo.setSelected(dto.isAtivo());
    }

    private void preencher(String campo, Object valor) {
        ViewInputSupport.preencher(campos.get(campo), valor);
    }

    private void limparFormulario() {
        idSelecionado = null;
        campos.values().forEach(c -> c.setText(""));
        ViewInputSupport.selecionar(cmbTipoUsuario, null);
        ViewInputSupport.selecionar(cmbFuncionario, null);
        chkAtivo.setSelected(true);
        tblDados.clearSelection();
        boolean permitido = AuthSession.podeEscrever();
        btnSalvar.setEnabled(permitido);
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

    private void montarCampos() {
        campos.put("Tipo de usuário ID", txtCampo1);
        campos.put("Funcionário ID", txtCampo2);
        campos.put("Nome", txtCampo3);
        campos.put("CPF", txtCampo4);
        campos.put("E-mail", txtCampo5);
        campos.put("Senha", txtCampo6);
        campos.put("Data de nascimento", txtCampo7);
    }

    private void configurarSelects() {
        cmbTipoUsuario = new JComboBox<>();
        cmbFuncionario = new JComboBox<>();
        ViewInputSupport.substituirNoPainel(pnlCampos, txtCampo1, cmbTipoUsuario);
        ViewInputSupport.substituirNoPainel(pnlCampos, txtCampo2, cmbFuncionario);
        try {
            for (TipoUsuarioDTO tipo : new TipoUsuarioService().listarTodos()) {
                cmbTipoUsuario.addItem(new ComboItem(tipo.getIdTipoUsuario(), tipo.getNome()));
            }
            for (FuncionarioDTO funcionario : new FuncionarioService().listarTodos()) {
                cmbFuncionario.addItem(new ComboItem(funcionario.getIdFuncionario(), funcionario.getNome()));
            }
            cmbTipoUsuario.setSelectedIndex(-1);
            cmbFuncionario.setSelectedIndex(-1);
        } catch (SQLException ex) {
            erro(ex);
        }
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
        lblCampo4 = new javax.swing.JLabel();
        txtCampo4 = new javax.swing.JTextField();
        lblCampo5 = new javax.swing.JLabel();
        txtCampo5 = new javax.swing.JTextField();
        lblCampo6 = new javax.swing.JLabel();
        txtCampo6 = new javax.swing.JTextField();
        lblCampo7 = new javax.swing.JLabel();
        txtCampo7 = new javax.swing.JTextField();
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
        setTitle("Almoxarifado - Usuários");
        getContentPane().setBackground(new java.awt.Color(248, 249, 250));

        jPanel1.setBackground(new java.awt.Color(248, 249, 250));
        jPanel1.setPreferredSize(new java.awt.Dimension(719, 600));
        pnlFormulario.setBackground(java.awt.Color.WHITE);
        pnlFormulario.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(179, 179, 179), 1, true));
        pnlPesquisa.setBackground(java.awt.Color.WHITE);
        pnlPesquisa.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(179, 179, 179)));

        java.awt.Font tituloFont = new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12);
        lblTituloFormulario.setFont(tituloFont); lblTituloFormulario.setForeground(new java.awt.Color(0, 78, 174)); lblTituloFormulario.setText("Cadastro / Edição de Usuário");
        lblTituloPesquisa.setFont(tituloFont); lblTituloPesquisa.setForeground(new java.awt.Color(0, 78, 174)); lblTituloPesquisa.setText("Pesquisa");
        lblTituloLista.setFont(tituloFont); lblTituloLista.setForeground(new java.awt.Color(0, 78, 174)); lblTituloLista.setText("Lista de Usuários");
        lblBuscar.setFont(tituloFont); lblBuscar.setText("Buscar:");
        txtBuscar.setText("Digite para buscar...");
        pnlCampos.setLayout(new java.awt.GridLayout(0, 2, 12, 10));
        lblCampo1.setFont(tituloFont); lblCampo1.setText("Tipo de usuário:"); pnlCampos.add(lblCampo1); pnlCampos.add(txtCampo1);
        lblCampo2.setFont(tituloFont); lblCampo2.setText("Funcionário:"); pnlCampos.add(lblCampo2); pnlCampos.add(txtCampo2);
        lblCampo3.setFont(tituloFont); lblCampo3.setText("Nome:"); pnlCampos.add(lblCampo3); pnlCampos.add(txtCampo3);
        lblCampo4.setFont(tituloFont); lblCampo4.setText("CPF:"); pnlCampos.add(lblCampo4); pnlCampos.add(txtCampo4);
        lblCampo5.setFont(tituloFont); lblCampo5.setText("E-mail:"); pnlCampos.add(lblCampo5); pnlCampos.add(txtCampo5);
        lblCampo6.setFont(tituloFont); lblCampo6.setText("Senha:"); pnlCampos.add(lblCampo6); pnlCampos.add(txtCampo6);
        lblCampo7.setFont(tituloFont); lblCampo7.setText("Data de nascimento:"); pnlCampos.add(lblCampo7); pnlCampos.add(txtCampo7);
        chkAtivo = new javax.swing.JCheckBox("Ativo"); pnlCampos.add(chkAtivo); pnlCampos.add(new javax.swing.JLabel());
        scrollCampos.setViewportView(pnlCampos);

        btnNovo.setBackground(new java.awt.Color(0, 78, 174)); btnNovo.setFont(tituloFont); btnNovo.setForeground(java.awt.Color.WHITE); btnNovo.setText("+ Novo"); btnNovo.setPreferredSize(new java.awt.Dimension(86, 38));
        btnSalvar.setBackground(new java.awt.Color(53, 158, 77)); btnSalvar.setFont(tituloFont); btnSalvar.setForeground(java.awt.Color.WHITE); btnSalvar.setText("Salvar"); btnSalvar.setPreferredSize(new java.awt.Dimension(87, 38));
        btnEditar.setBackground(new java.awt.Color(228, 146, 10)); btnEditar.setFont(tituloFont); btnEditar.setForeground(java.awt.Color.WHITE); btnEditar.setText("Editar"); btnEditar.setPreferredSize(new java.awt.Dimension(95, 38));
        btnExcluir.setBackground(new java.awt.Color(214, 62, 50)); btnExcluir.setFont(tituloFont); btnExcluir.setForeground(java.awt.Color.WHITE); btnExcluir.setText("Excluir"); btnExcluir.setPreferredSize(new java.awt.Dimension(96, 38));

        tblDados.setModel(new DefaultTableModel(new Object[][] {}, new String[] {"ID", "Tipo", "Funcionário", "Nome", "E-mail", "CPF", "Status"}) { public boolean isCellEditable(int row, int column) { return false; } });
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
    /**
     * Permite executar esta JFrame isoladamente pelo NetBeans.
     */
    public static void main(String[] args) {
        com.mycompany.almoxarifado.Almoxarifado.main(args);
    }

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
    private javax.swing.JLabel lblCampo4;
    private javax.swing.JLabel lblCampo5;
    private javax.swing.JLabel lblCampo6;
    private javax.swing.JLabel lblCampo7;
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
    private javax.swing.JTextField txtCampo4;
    private javax.swing.JTextField txtCampo5;
    private javax.swing.JTextField txtCampo6;
    private javax.swing.JTextField txtCampo7;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables
}
