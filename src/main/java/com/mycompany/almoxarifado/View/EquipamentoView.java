package com.mycompany.almoxarifado.View;

import com.mycompany.almoxarifado.Dto.EquipamentoDTO;
import com.mycompany.almoxarifado.Dto.CategoriaDTO;
import com.mycompany.almoxarifado.Dto.LocalizacaoDTO;
import com.mycompany.almoxarifado.Dto.EstadoConservacaoDTO;
import com.mycompany.almoxarifado.Dto.SituacaoEquipamentoDTO;
import com.mycompany.almoxarifado.Service.EquipamentoService;
import com.mycompany.almoxarifado.Service.AuthSession;
import com.mycompany.almoxarifado.Service.CategoriaService;
import com.mycompany.almoxarifado.Service.LocalizacaoService;
import com.mycompany.almoxarifado.Service.EstadoConservacaoService;
import com.mycompany.almoxarifado.Service.SituacaoEquipamentoService;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;

/**
 * Formulário JFrame do NetBeans para equipamentos.
 */
public class EquipamentoView extends javax.swing.JFrame {

    private final EquipamentoService service = new EquipamentoService();
    private final Map<String, JTextField> campos = new LinkedHashMap<>();
    private Long idSelecionado;
    private JComboBox<ComboItem> cmbCategoria, cmbLocalizacao, cmbEstadoConservacao, cmbSituacao;

    public EquipamentoView() {
        AuthSession.exigirAutenticacao();
        initComponents();
        montarCampos();
        configurarSelects();
        aplicarEstilo();
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

    private void aplicarEstilo() {
        getContentPane().setBackground(new java.awt.Color(248, 249, 250));
        jPanel1.setBackground(new java.awt.Color(248, 249, 250));
        pnlFormulario.setBackground(java.awt.Color.WHITE);
        pnlPesquisa.setBackground(java.awt.Color.WHITE);
        ViewStyle.titulo(lblTituloFormulario);
        ViewStyle.titulo(lblTituloPesquisa);
        ViewStyle.titulo(lblTituloLista);
        ViewStyle.botoesCrud(btnNovo, btnSalvar, btnEditar, btnExcluir);
    }

    private void aplicarPermissoes() {
        boolean permitido = AuthSession.podeEscrever();
        btnNovo.setEnabled(permitido);
        btnSalvar.setEnabled(permitido);
        btnEditar.setEnabled(false);
        btnExcluir.setEnabled(false);
    }

    private EquipamentoDTO lerFormulario() {
        EquipamentoDTO dto = new EquipamentoDTO();
        dto.setIdCategoria(ViewInputSupport.id(cmbCategoria, "A categoria", true));
        dto.setIdLocalizacao(ViewInputSupport.id(cmbLocalizacao, "A localização", true));
        dto.setIdEstadoConservacao(ViewInputSupport.id(cmbEstadoConservacao, "O estado de conservação", true));
        dto.setIdSituacaoEquipamento(ViewInputSupport.id(cmbSituacao, "A situação", true));
        dto.setNome(ViewInputSupport.texto(campos.get("Nome")));
        dto.setValor(ViewInputSupport.decimal(campos.get("Valor"), "O valor", false));
        dto.setDescricao(ViewInputSupport.texto(campos.get("Descrição")));
        dto.setMarca(ViewInputSupport.texto(campos.get("Marca")));
        dto.setModelo(ViewInputSupport.texto(campos.get("Modelo")));
        dto.setNumeroPatrimonio(ViewInputSupport.texto(campos.get("Patrimônio")));
        dto.setNumeroSerie(ViewInputSupport.texto(campos.get("Número de série")));
        dto.setDataAquisicao(ViewInputSupport.data(campos.get("Data de aquisição"), "A data de aquisição", false));
        dto.setAtivo(chkAtivo.isSelected());
        return dto;
    }

    private void cadastrar() {
        try {
            service.cadastrar(lerFormulario());
            sucesso("Equipamento cadastrada com sucesso.");
        } catch (IllegalArgumentException | SQLException ex) {
            erro(ex);
        }
    }

    private void editar() {
        if (idSelecionado == null) {
            aviso("Selecione o equipamento para editar.");
            return;
        }
        try {
            EquipamentoDTO dto = lerFormulario();
            dto.setIdEquipamento(idSelecionado);
            service.atualizar(dto);
            sucesso("Equipamento atualizada com sucesso.");
        } catch (IllegalArgumentException | SQLException ex) {
            erro(ex);
        }
    }

    private void excluir() {
        if (idSelecionado == null) {
            aviso("Selecione o equipamento para excluir.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Deseja excluir o equipamento selecionada?", "Confirmação", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            service.excluir(idSelecionado);
            sucesso("Equipamento excluída com sucesso.");
        } catch (IllegalArgumentException | SQLException ex) {
            erro(ex);
        }
    }

    private void carregarDados() {
        try {
            DefaultTableModel modelo = (DefaultTableModel) tblDados.getModel();
            modelo.setRowCount(0);
            for (EquipamentoDTO dto : service.listarTodos()) {
                modelo.addRow(new Object[]{dto.getIdEquipamento(), dto.getNome(), dto.getIdCategoria(), dto.getIdLocalizacao(), dto.getNumeroPatrimonio(), dto.getMarca(), dto.getModelo(), dto.isAtivo() ? "Ativo" : "Inativo"});
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

    private void preencher(EquipamentoDTO dto) {
        ViewInputSupport.selecionar(cmbCategoria, dto.getIdCategoria());
        ViewInputSupport.selecionar(cmbLocalizacao, dto.getIdLocalizacao());
        ViewInputSupport.selecionar(cmbEstadoConservacao, dto.getIdEstadoConservacao());
        ViewInputSupport.selecionar(cmbSituacao, dto.getIdSituacaoEquipamento());
        preencher("Nome", dto.getNome());
        preencher("Valor", dto.getValor());
        preencher("Descrição", dto.getDescricao());
        preencher("Marca", dto.getMarca());
        preencher("Modelo", dto.getModelo());
        preencher("Patrimônio", dto.getNumeroPatrimonio());
        preencher("Número de série", dto.getNumeroSerie());
        preencher("Data de aquisição", dto.getDataAquisicao());
        chkAtivo.setSelected(dto.isAtivo());
    }

    private void preencher(String campo, Object valor) {
        ViewInputSupport.preencher(campos.get(campo), valor);
    }

    private void limparFormulario() {
        idSelecionado = null;
        campos.values().forEach(c -> c.setText(""));
        ViewInputSupport.selecionar(cmbCategoria, null);
        ViewInputSupport.selecionar(cmbLocalizacao, null);
        ViewInputSupport.selecionar(cmbEstadoConservacao, null);
        ViewInputSupport.selecionar(cmbSituacao, null);
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
        pnlCampos.setLayout(new java.awt.GridLayout(0, 2, 12, 10));
        for (String nome : new String[]{"Categoria", "Localização", "Estado de conservação", "Situação", "Nome", "Valor", "Descrição", "Marca", "Modelo", "Patrimônio", "Número de série", "Data de aquisição"}) {
            javax.swing.JLabel label = new javax.swing.JLabel(nome + ":");
            label.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
            JTextField campo = new JTextField();
            campos.put(nome, campo);
            pnlCampos.add(label);
            pnlCampos.add(campo);
        }
        chkAtivo = new javax.swing.JCheckBox("Ativo");
        pnlCampos.add(chkAtivo);
        pnlCampos.add(new javax.swing.JLabel());
        scrollCampos.setViewportView(pnlCampos);
    }

    private void configurarSelects() {
        cmbCategoria = new JComboBox<>(); cmbLocalizacao = new JComboBox<>(); cmbEstadoConservacao = new JComboBox<>(); cmbSituacao = new JComboBox<>();
        ViewInputSupport.substituirNoPainel(pnlCampos, campos.get("Categoria"), cmbCategoria);
        ViewInputSupport.substituirNoPainel(pnlCampos, campos.get("Localização"), cmbLocalizacao);
        ViewInputSupport.substituirNoPainel(pnlCampos, campos.get("Estado de conservação"), cmbEstadoConservacao);
        ViewInputSupport.substituirNoPainel(pnlCampos, campos.get("Situação"), cmbSituacao);
        try {
            for (CategoriaDTO item : new CategoriaService().listarTodos()) cmbCategoria.addItem(new ComboItem(item.getIdCategoria(), item.getNome()));
            for (LocalizacaoDTO item : new LocalizacaoService().listarTodos()) cmbLocalizacao.addItem(new ComboItem(item.getIdLocalizacao(), item.getNome()));
            for (EstadoConservacaoDTO item : new EstadoConservacaoService().listarTodos()) cmbEstadoConservacao.addItem(new ComboItem(item.getIdEstadoConservacao(), item.getNome()));
            for (SituacaoEquipamentoDTO item : new SituacaoEquipamentoService().listarTodos()) cmbSituacao.addItem(new ComboItem(item.getIdSituacaoEquipamento(), item.getNome()));
            cmbCategoria.setSelectedIndex(-1); cmbLocalizacao.setSelectedIndex(-1); cmbEstadoConservacao.setSelectedIndex(-1); cmbSituacao.setSelectedIndex(-1);
        } catch (SQLException ex) { erro(ex); }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lblTituloFormulario = new javax.swing.JLabel();
        pnlFormulario = new javax.swing.JPanel();
        scrollCampos = new javax.swing.JScrollPane();
        pnlCampos = new javax.swing.JPanel();
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
        setTitle("Almoxarifado - Equipamentos");

        jPanel1.setPreferredSize(new java.awt.Dimension(719, 600));

        lblTituloFormulario.setText("Cadastro / Edição de Equipamento");

        pnlFormulario.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(179, 179, 179), 1, true));

        javax.swing.GroupLayout pnlCamposLayout = new javax.swing.GroupLayout(pnlCampos);
        pnlCampos.setLayout(pnlCamposLayout);
        pnlCamposLayout.setHorizontalGroup(
            pnlCamposLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 650, Short.MAX_VALUE)
        );
        pnlCamposLayout.setVerticalGroup(
            pnlCamposLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 120, Short.MAX_VALUE)
        );

        scrollCampos.setViewportView(pnlCampos);

        btnNovo.setText("+ Novo");

        btnSalvar.setText("Salvar");

        btnEditar.setText("Editar");

        btnExcluir.setText("Excluir");

        javax.swing.GroupLayout pnlFormularioLayout = new javax.swing.GroupLayout(pnlFormulario);
        pnlFormulario.setLayout(pnlFormularioLayout);
        pnlFormularioLayout.setHorizontalGroup(
            pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormularioLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollCampos)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlFormularioLayout.createSequentialGroup()
                        .addComponent(btnNovo, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(btnExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(14, 14, 14))
        );
        pnlFormularioLayout.setVerticalGroup(
            pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormularioLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(scrollCampos, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNovo, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14))
        );

        lblTituloPesquisa.setText("Pesquisa");

        pnlPesquisa.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(179, 179, 179)));

        lblBuscar.setText("Buscar:");

        txtBuscar.setText("Digite para buscar...");

        javax.swing.GroupLayout pnlPesquisaLayout = new javax.swing.GroupLayout(pnlPesquisa);
        pnlPesquisa.setLayout(pnlPesquisaLayout);
        pnlPesquisaLayout.setHorizontalGroup(
            pnlPesquisaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPesquisaLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(lblBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 486, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlPesquisaLayout.setVerticalGroup(
            pnlPesquisaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPesquisaLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(pnlPesquisaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBuscar)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14))
        );

        lblTituloLista.setText("Lista de Equipamentos");

        scrollDados.setViewportView(tblDados);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTituloFormulario)
                    .addComponent(pnlFormulario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblTituloPesquisa)
                    .addComponent(pnlPesquisa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblTituloLista)
                    .addComponent(scrollDados))
                .addGap(25, 25, 25))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(lblTituloFormulario)
                .addGap(6, 6, 6)
                .addComponent(pnlFormulario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblTituloPesquisa)
                .addGap(6, 6, 6)
                .addComponent(pnlPesquisa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(lblTituloLista)
                .addGap(6, 6, 6)
                .addComponent(scrollDados, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(15, 15, 15))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
    private javax.swing.JPanel jPanel1;
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
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables
}
