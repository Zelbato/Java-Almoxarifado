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
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;

/**
 * Formulário JFrame do NetBeans para equipamentos.
 */
public class EquipamentoView extends javax.swing.JFrame {

    private final EquipamentoService service = new EquipamentoService();
    private Long idSelecionado;

    public EquipamentoView() {
        AuthSession.exigirAutenticacao();
        initComponents();
        carregarSelects();
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

    // o Júnior só consulta equipamentos
    private void aplicarPermissoes() {
        boolean permitido = AuthSession.ehChefe();
        btnNovo.setVisible(permitido);
        btnSalvar.setVisible(permitido);
        btnEditar.setVisible(permitido);
        btnExcluir.setVisible(permitido);
    }

    private EquipamentoDTO lerFormulario() {
        EquipamentoDTO dto = new EquipamentoDTO();
        dto.setIdCategoria(ViewInputSupport.id(cmbCategoria, "A categoria", true));
        dto.setIdLocalizacao(ViewInputSupport.id(cmbLocalizacao, "A localização", true));
        dto.setIdEstadoConservacao(ViewInputSupport.id(cmbEstadoConservacao, "O estado de conservação", true));
        dto.setIdSituacaoEquipamento(ViewInputSupport.id(cmbSituacao, "A situação", true));
        dto.setNome(ViewInputSupport.texto(txtNome));
        dto.setValor(ViewInputSupport.decimal(txtValor, "O valor", false));
        dto.setDescricao(ViewInputSupport.texto(txtDescricao));
        dto.setMarca(ViewInputSupport.texto(txtMarca));
        dto.setModelo(ViewInputSupport.texto(txtModelo));
        dto.setNumeroPatrimonio(ViewInputSupport.texto(txtPatrimonio));
        dto.setNumeroSerie(ViewInputSupport.texto(txtNumeroSerie));
        dto.setDataAquisicao(ViewInputSupport.data(calDataAquisicao, "A data de aquisição", false));
        dto.setAtivo(chkAtivo.isSelected());
        return dto;
    }

    private void cadastrar() {
        try {
            service.cadastrar(lerFormulario());
            sucesso("Equipamento cadastrado com sucesso.");
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
            sucesso("Equipamento atualizado com sucesso.");
        } catch (IllegalArgumentException | SQLException ex) {
            erro(ex);
        }
    }

    private void excluir() {
        if (idSelecionado == null) {
            aviso("Selecione o equipamento para excluir.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Deseja excluir o equipamento selecionado?", "Confirmação", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            service.excluir(idSelecionado);
            sucesso("Equipamento excluído com sucesso.");
        } catch (IllegalArgumentException | SQLException ex) {
            erro(ex);
        }
    }

    private void carregarDados() {
        try {
            DefaultTableModel modelo = (DefaultTableModel) tblDados.getModel();
            modelo.setRowCount(0);
            for (EquipamentoDTO dto : service.listarTodos()) {
                modelo.addRow(new Object[]{
                    dto.getIdEquipamento(),
                    dto.getNome(),
                    dto.getNumeroPatrimonio(),
                    ViewInputSupport.nomeDoItem(cmbCategoria, dto.getIdCategoria()),
                    ViewInputSupport.nomeDoItem(cmbLocalizacao, dto.getIdLocalizacao()),
                    ViewInputSupport.nomeDoItem(cmbSituacao, dto.getIdSituacaoEquipamento()),
                    dto.getMarca(),
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

    private void preencher(EquipamentoDTO dto) {
        ViewInputSupport.selecionar(cmbCategoria, dto.getIdCategoria());
        ViewInputSupport.selecionar(cmbLocalizacao, dto.getIdLocalizacao());
        ViewInputSupport.selecionar(cmbEstadoConservacao, dto.getIdEstadoConservacao());
        ViewInputSupport.selecionar(cmbSituacao, dto.getIdSituacaoEquipamento());
        ViewInputSupport.preencher(txtNome, dto.getNome());
        ViewInputSupport.preencher(txtValor, dto.getValor());
        ViewInputSupport.preencher(txtDescricao, dto.getDescricao());
        ViewInputSupport.preencher(txtMarca, dto.getMarca());
        ViewInputSupport.preencher(txtModelo, dto.getModelo());
        ViewInputSupport.preencher(txtPatrimonio, dto.getNumeroPatrimonio());
        ViewInputSupport.preencher(txtNumeroSerie, dto.getNumeroSerie());
        ViewInputSupport.selecionar(calDataAquisicao, dto.getDataAquisicao());
        chkAtivo.setSelected(dto.isAtivo());
    }

    private void limparFormulario() {
        idSelecionado = null;
        txtNome.setText("");
        txtValor.setText("");
        txtDescricao.setText("");
        txtMarca.setText("");
        txtModelo.setText("");
        txtPatrimonio.setText("");
        txtNumeroSerie.setText("");
        ViewInputSupport.selecionar(cmbCategoria, null);
        ViewInputSupport.selecionar(cmbLocalizacao, null);
        ViewInputSupport.selecionar(cmbEstadoConservacao, null);
        ViewInputSupport.selecionar(cmbSituacao, null);
        ViewInputSupport.selecionar(calDataAquisicao, null);
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

    private void carregarSelects() {
        try {
            for (CategoriaDTO item : new CategoriaService().listarTodos()) {
                cmbCategoria.addItem(new ComboItem(item.getIdCategoria(), item.getNome()));
            }
            for (LocalizacaoDTO item : new LocalizacaoService().listarTodos()) {
                cmbLocalizacao.addItem(new ComboItem(item.getIdLocalizacao(), item.getNome()));
            }
            for (EstadoConservacaoDTO item : new EstadoConservacaoService().listarTodos()) {
                cmbEstadoConservacao.addItem(new ComboItem(item.getIdEstadoConservacao(), item.getNome()));
            }
            for (SituacaoEquipamentoDTO item : new SituacaoEquipamentoService().listarTodos()) {
                cmbSituacao.addItem(new ComboItem(item.getIdSituacaoEquipamento(), item.getNome()));
            }
            cmbCategoria.setSelectedIndex(-1);
            cmbLocalizacao.setSelectedIndex(-1);
            cmbEstadoConservacao.setSelectedIndex(-1);
            cmbSituacao.setSelectedIndex(-1);
        } catch (SQLException ex) {
            erro(ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlPrincipal = new javax.swing.JPanel();
        lblTituloFormulario = new javax.swing.JLabel();
        pnlFormulario = new javax.swing.JPanel();
        lblSecaoIdentificacao = new javax.swing.JLabel();
        lblNome = new javax.swing.JLabel();
        txtNome = new javax.swing.JTextField();
        lblPatrimonio = new javax.swing.JLabel();
        txtPatrimonio = new javax.swing.JTextField();
        lblMarca = new javax.swing.JLabel();
        txtMarca = new javax.swing.JTextField();
        lblModelo = new javax.swing.JLabel();
        txtModelo = new javax.swing.JTextField();
        lblNumeroSerie = new javax.swing.JLabel();
        txtNumeroSerie = new javax.swing.JTextField();
        lblDescricao = new javax.swing.JLabel();
        txtDescricao = new javax.swing.JTextField();
        lblSecaoControle = new javax.swing.JLabel();
        lblCategoria = new javax.swing.JLabel();
        cmbCategoria = new javax.swing.JComboBox<>();
        lblLocalizacao = new javax.swing.JLabel();
        cmbLocalizacao = new javax.swing.JComboBox<>();
        lblEstadoConservacao = new javax.swing.JLabel();
        cmbEstadoConservacao = new javax.swing.JComboBox<>();
        lblSituacao = new javax.swing.JLabel();
        cmbSituacao = new javax.swing.JComboBox<>();
        lblSecaoAquisicao = new javax.swing.JLabel();
        lblValor = new javax.swing.JLabel();
        txtValor = new javax.swing.JTextField();
        lblDataAquisicao = new javax.swing.JLabel();
        calDataAquisicao = new com.toedter.calendar.JDateChooser();
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
        setTitle("Almoxarifado - Equipamentos");
        setMinimumSize(new java.awt.Dimension(1000, 680));

        pnlPrincipal.setBackground(new java.awt.Color(248, 249, 250));
        lblTituloFormulario.setText("Cadastro / Edição de Equipamento");
        lblTituloFormulario.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTituloFormulario.setForeground(new java.awt.Color(0, 78, 174));

        pnlFormulario.setBackground(new java.awt.Color(255, 255, 255));
        pnlFormulario.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(179, 179, 179), 1, true));
        lblSecaoIdentificacao.setText("Identificação");
        lblSecaoIdentificacao.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblSecaoIdentificacao.setForeground(new java.awt.Color(108, 117, 125));

        lblNome.setText("Nome:");
        lblNome.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        lblPatrimonio.setText("Patrimônio:");
        lblPatrimonio.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        lblMarca.setText("Marca:");
        lblMarca.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        lblModelo.setText("Modelo:");
        lblModelo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        lblNumeroSerie.setText("Número de série:");
        lblNumeroSerie.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        lblDescricao.setText("Descrição:");
        lblDescricao.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        lblSecaoControle.setText("Classificação e situação");
        lblSecaoControle.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblSecaoControle.setForeground(new java.awt.Color(108, 117, 125));

        lblCategoria.setText("Categoria:");
        lblCategoria.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        lblLocalizacao.setText("Localização:");
        lblLocalizacao.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        lblEstadoConservacao.setText("Conservação:");
        lblEstadoConservacao.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        lblSituacao.setText("Situação:");
        lblSituacao.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        lblSecaoAquisicao.setText("Aquisição");
        lblSecaoAquisicao.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblSecaoAquisicao.setForeground(new java.awt.Color(108, 117, 125));

        lblValor.setText("Valor (R$):");
        lblValor.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        lblDataAquisicao.setText("Data de aquisição:");
        lblDataAquisicao.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        calDataAquisicao.setDateFormatString("dd/MM/yyyy");

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
                    .addComponent(lblSecaoIdentificacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSecaoControle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSecaoAquisicao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlFormularioLayout.createSequentialGroup()
                        .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblNome, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblMarca, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblNumeroSerie, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblEstadoConservacao, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblValor, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNome, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(txtMarca, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(txtNumeroSerie, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(cmbCategoria, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(cmbEstadoConservacao, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(txtValor, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(chkAtivo, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE))
                        .addGap(24, 24, 24)
                        .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblPatrimonio, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblModelo, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblLocalizacao, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblSituacao, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblDataAquisicao, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPatrimonio, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(txtModelo, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(txtDescricao, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(cmbLocalizacao, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(cmbSituacao, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(calDataAquisicao, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)))
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
                .addComponent(lblSecaoIdentificacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblNome, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPatrimonio, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPatrimonio, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblMarca, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMarca, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblModelo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtModelo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblNumeroSerie, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNumeroSerie, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGap(8, 8, 8)
                .addComponent(lblSecaoControle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLocalizacao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbLocalizacao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblEstadoConservacao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbEstadoConservacao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSituacao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbSituacao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGap(8, 8, 8)
                .addComponent(lblSecaoAquisicao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblValor, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtValor, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDataAquisicao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(calDataAquisicao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
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

        lblTituloLista.setText("Lista de Equipamentos");
        lblTituloLista.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTituloLista.setForeground(new java.awt.Color(0, 78, 174));

        tblDados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nome", "Patrimônio", "Categoria", "Localização", "Situação", "Marca", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
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
                .addComponent(scrollDados, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
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
    private com.toedter.calendar.JDateChooser calDataAquisicao;
    private javax.swing.JCheckBox chkAtivo;
    private javax.swing.JComboBox<ComboItem> cmbCategoria;
    private javax.swing.JComboBox<ComboItem> cmbEstadoConservacao;
    private javax.swing.JComboBox<ComboItem> cmbLocalizacao;
    private javax.swing.JComboBox<ComboItem> cmbSituacao;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblCategoria;
    private javax.swing.JLabel lblDataAquisicao;
    private javax.swing.JLabel lblDescricao;
    private javax.swing.JLabel lblEstadoConservacao;
    private javax.swing.JLabel lblLocalizacao;
    private javax.swing.JLabel lblMarca;
    private javax.swing.JLabel lblModelo;
    private javax.swing.JLabel lblNome;
    private javax.swing.JLabel lblNumeroSerie;
    private javax.swing.JLabel lblPatrimonio;
    private javax.swing.JLabel lblSecaoAquisicao;
    private javax.swing.JLabel lblSecaoControle;
    private javax.swing.JLabel lblSecaoIdentificacao;
    private javax.swing.JLabel lblSituacao;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblTituloFormulario;
    private javax.swing.JLabel lblTituloLista;
    private javax.swing.JLabel lblTituloPesquisa;
    private javax.swing.JLabel lblValor;
    private javax.swing.JPanel pnlFormulario;
    private javax.swing.JPanel pnlPesquisa;
    private javax.swing.JPanel pnlPrincipal;
    private javax.swing.JScrollPane scrollDados;
    private javax.swing.JTable tblDados;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtDescricao;
    private javax.swing.JTextField txtMarca;
    private javax.swing.JTextField txtModelo;
    private javax.swing.JTextField txtNome;
    private javax.swing.JTextField txtNumeroSerie;
    private javax.swing.JTextField txtPatrimonio;
    private javax.swing.JTextField txtValor;
    // End of variables declaration//GEN-END:variables
}
