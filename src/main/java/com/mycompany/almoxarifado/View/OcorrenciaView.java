package com.mycompany.almoxarifado.View;

import com.mycompany.almoxarifado.Dto.OcorrenciaDTO;
import com.mycompany.almoxarifado.Service.OcorrenciaService;
import com.mycompany.almoxarifado.Service.AuthSession;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;

/** Formulário JFrame do NetBeans para ocorrências. */
public class OcorrenciaView extends javax.swing.JFrame {

    private final OcorrenciaService service = new OcorrenciaService();
    private Long idSelecionado;

    public OcorrenciaView() {
        AuthSession.exigirAutenticacao();
        initComponents();
        ViewInputSupport.configurarHora(spnHoraOcorrencia, spnHoraResolucao);
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
        cmbStatus.addActionListener(e -> atualizarCamposResolucao());
        tblDados.getSelectionModel().addListSelectionListener(this::selecionarLinha);
    }

    // os dois perfis registram ocorrência, só o Chefe exclui
    private void aplicarPermissoes() {
        boolean permitido = AuthSession.podeEscrever();
        btnNovo.setVisible(permitido);
        btnSalvar.setVisible(permitido);
        btnEditar.setVisible(permitido);
        btnExcluir.setVisible(AuthSession.ehChefe());
    }

    private OcorrenciaDTO lerFormulario() {
        OcorrenciaDTO dto = new OcorrenciaDTO();
        dto.setIdEquipamento(ViewInputSupport.id(cmbEquipamento, "O equipamento", true));
        dto.setIdMovimentacao(ViewInputSupport.id(cmbMovimentacao, "A movimentação", false));
        dto.setIdFuncionario(ViewInputSupport.id(cmbFuncionario, "O funcionário", false));
        dto.setIdUsuarioRegistro(ViewInputSupport.id(cmbUsuarioRegistro, "O usuário do registro", true));
        dto.setIdTipoOcorrencia(ViewInputSupport.id(cmbTipoOcorrencia, "O tipo de ocorrência", true));
        dto.setIdStatusOcorrencia(ViewInputSupport.id(cmbStatus, "O status", true));
        dto.setTitulo(ViewInputSupport.texto(txtTitulo));
        dto.setDescricao(ViewInputSupport.texto(txtDescricao));
        dto.setDataOcorrencia(ViewInputSupport.dataHora(calDataOcorrencia, spnHoraOcorrencia, "A data da ocorrência", true));
        if (statusResolvidaSelecionado()) {
            dto.setSolucao(ViewInputSupport.texto(txtSolucao));
            dto.setDataResolucao(ViewInputSupport.dataHora(calDataResolucao, spnHoraResolucao, "A data de resolução", false));
        }
        return dto;
    }

    private void cadastrar() {
        try {
            service.cadastrar(lerFormulario());
            sucesso("Ocorrência cadastrada com sucesso.");
        } catch (IllegalArgumentException | SQLException ex) {
            erro(ex);
        }
    }

    private void editar() {
        if (idSelecionado == null) {
            aviso("Selecione a ocorrência para editar.");
            return;
        }
        try {
            OcorrenciaDTO dto = lerFormulario();
            dto.setIdOcorrencia(idSelecionado);
            service.atualizar(dto);
            sucesso("Ocorrência atualizada com sucesso.");
        } catch (IllegalArgumentException | SQLException ex) {
            erro(ex);
        }
    }

    private void excluir() {
        if (idSelecionado == null) {
            aviso("Selecione a ocorrência para excluir.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Deseja excluir a ocorrência selecionada?", "Confirmação", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            service.excluir(idSelecionado);
            sucesso("Ocorrência excluída com sucesso.");
        } catch (IllegalArgumentException | SQLException ex) {
            erro(ex);
        }
    }

    private void carregarDados() {
        try {
            DefaultTableModel modelo = (DefaultTableModel) tblDados.getModel();
            modelo.setRowCount(0);
            for (OcorrenciaDTO dto : service.listarTodos()) {
                modelo.addRow(new Object[]{
                    dto.getIdOcorrencia(),
                    dto.getTitulo(),
                    ViewInputSupport.nomeDoItem(cmbEquipamento, dto.getIdEquipamento()),
                    ViewInputSupport.nomeDoItem(cmbTipoOcorrencia, dto.getIdTipoOcorrencia()),
                    ViewInputSupport.nomeDoItem(cmbStatus, dto.getIdStatusOcorrencia()),
                    ViewInputSupport.formatar(dto.getDataOcorrencia()),
                    ViewInputSupport.formatar(dto.getDataResolucao())
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

    private void preencher(OcorrenciaDTO dto) {
        ViewInputSupport.selecionar(cmbEquipamento, dto.getIdEquipamento());
        ViewInputSupport.selecionar(cmbMovimentacao, dto.getIdMovimentacao());
        ViewInputSupport.selecionar(cmbFuncionario, dto.getIdFuncionario());
        ViewInputSupport.selecionar(cmbUsuarioRegistro, dto.getIdUsuarioRegistro());
        ViewInputSupport.selecionar(cmbTipoOcorrencia, dto.getIdTipoOcorrencia());
        ViewInputSupport.selecionar(cmbStatus, dto.getIdStatusOcorrencia());
        ViewInputSupport.preencher(txtTitulo, dto.getTitulo());
        ViewInputSupport.preencher(txtDescricao, dto.getDescricao());
        ViewInputSupport.selecionar(calDataOcorrencia, spnHoraOcorrencia, dto.getDataOcorrencia());
        ViewInputSupport.preencher(txtSolucao, dto.getSolucao());
        ViewInputSupport.selecionar(calDataResolucao, spnHoraResolucao, dto.getDataResolucao());
        atualizarCamposResolucao();
    }

    private void limparFormulario() {
        idSelecionado = null;
        txtTitulo.setText("");
        txtDescricao.setText("");
        txtSolucao.setText("");
        ViewInputSupport.selecionar(cmbEquipamento, null);
        ViewInputSupport.selecionar(cmbMovimentacao, null);
        ViewInputSupport.selecionar(cmbFuncionario, null);
        ViewInputSupport.selecionar(cmbTipoOcorrencia, null);
        ViewInputSupport.selecionar(cmbStatus, null);
        // quem está logado já vem como responsável pelo registro
        ViewInputSupport.selecionar(cmbUsuarioRegistro, AuthSession.usuario().getIdUsuario());
        ViewInputSupport.selecionar(calDataOcorrencia, spnHoraOcorrencia, java.time.LocalDateTime.now());
        ViewInputSupport.selecionar(calDataResolucao, spnHoraResolucao, null);
        tblDados.clearSelection();
        btnSalvar.setEnabled(true);
        btnEditar.setEnabled(false);
        btnExcluir.setEnabled(false);
        atualizarCamposResolucao();
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
            for (com.mycompany.almoxarifado.Dto.EquipamentoDTO item : new com.mycompany.almoxarifado.Service.EquipamentoService().listarTodos()) {
                cmbEquipamento.addItem(new ComboItem(item.getIdEquipamento(), item.getNome()));
            }
            for (com.mycompany.almoxarifado.Dto.MovimentacaoDTO item : new com.mycompany.almoxarifado.Service.MovimentacaoService().listarTodos()) {
                cmbMovimentacao.addItem(new ComboItem(item.getIdMovimentacao(), "Movimentação #" + item.getIdMovimentacao()));
            }
            for (com.mycompany.almoxarifado.Dto.FuncionarioDTO item : new com.mycompany.almoxarifado.Service.FuncionarioService().listarTodos()) {
                cmbFuncionario.addItem(new ComboItem(item.getIdFuncionario(), item.getNome()));
            }
            for (com.mycompany.almoxarifado.Dto.UsuarioDTO item : new com.mycompany.almoxarifado.Service.UsuarioService().listarTodos()) {
                cmbUsuarioRegistro.addItem(new ComboItem(item.getIdUsuario(), item.getEmail()));
            }
            for (com.mycompany.almoxarifado.Dto.TipoOcorrenciaDTO item : new com.mycompany.almoxarifado.Service.TipoOcorrenciaService().listarTodos()) {
                cmbTipoOcorrencia.addItem(new ComboItem(item.getIdTipoOcorrencia(), item.getNome()));
            }
            for (com.mycompany.almoxarifado.Dto.StatusOcorrenciaDTO item : new com.mycompany.almoxarifado.Service.StatusOcorrenciaService().listarTodos()) {
                cmbStatus.addItem(new ComboItem(item.getIdStatusOcorrencia(), item.getNome()));
            }
            cmbEquipamento.setSelectedIndex(-1);
            cmbMovimentacao.setSelectedIndex(-1);
            cmbFuncionario.setSelectedIndex(-1);
            cmbUsuarioRegistro.setSelectedIndex(-1);
            cmbTipoOcorrencia.setSelectedIndex(-1);
            cmbStatus.setSelectedIndex(-1);
        } catch (SQLException ex) {
            erro(ex);
        }
    }

    private boolean statusResolvidaSelecionado() {
        Object selecionado = cmbStatus.getSelectedItem();
        return selecionado != null && "RESOLVIDA".equalsIgnoreCase(selecionado.toString().trim());
    }

    // solução e data de resolução só valem quando o status é RESOLVIDA
    private void atualizarCamposResolucao() {
        boolean resolvida = statusResolvidaSelecionado();
        txtSolucao.setEnabled(resolvida);
        calDataResolucao.setEnabled(resolvida);
        spnHoraResolucao.setEnabled(resolvida);
        lblSolucao.setEnabled(resolvida);
        lblDataResolucao.setEnabled(resolvida);
        if (!resolvida) {
            txtSolucao.setText("");
            calDataResolucao.setDate(null);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlPrincipal = new javax.swing.JPanel();
        lblTituloFormulario = new javax.swing.JLabel();
        pnlFormulario = new javax.swing.JPanel();
        lblSecaoOcorrencia = new javax.swing.JLabel();
        lblTitulo = new javax.swing.JLabel();
        txtTitulo = new javax.swing.JTextField();
        lblTipoOcorrencia = new javax.swing.JLabel();
        cmbTipoOcorrencia = new javax.swing.JComboBox<>();
        lblEquipamento = new javax.swing.JLabel();
        cmbEquipamento = new javax.swing.JComboBox<>();
        lblStatus = new javax.swing.JLabel();
        cmbStatus = new javax.swing.JComboBox<>();
        lblDataOcorrencia = new javax.swing.JLabel();
        pnlDataOcorrencia = new javax.swing.JPanel();
        calDataOcorrencia = new com.toedter.calendar.JDateChooser();
        spnHoraOcorrencia = new javax.swing.JSpinner();
        lblDescricao = new javax.swing.JLabel();
        txtDescricao = new javax.swing.JTextField();
        lblSecaoEnvolvidos = new javax.swing.JLabel();
        lblMovimentacao = new javax.swing.JLabel();
        cmbMovimentacao = new javax.swing.JComboBox<>();
        lblFuncionario = new javax.swing.JLabel();
        cmbFuncionario = new javax.swing.JComboBox<>();
        lblUsuarioRegistro = new javax.swing.JLabel();
        cmbUsuarioRegistro = new javax.swing.JComboBox<>();
        lblSecaoResolucao = new javax.swing.JLabel();
        lblSolucao = new javax.swing.JLabel();
        txtSolucao = new javax.swing.JTextField();
        lblDataResolucao = new javax.swing.JLabel();
        pnlDataResolucao = new javax.swing.JPanel();
        calDataResolucao = new com.toedter.calendar.JDateChooser();
        spnHoraResolucao = new javax.swing.JSpinner();
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
        setTitle("Almoxarifado - Ocorrências");
        setMinimumSize(new java.awt.Dimension(1000, 680));

        pnlPrincipal.setBackground(new java.awt.Color(248, 249, 250));
        lblTituloFormulario.setText("Cadastro / Edição de Ocorrência");
        lblTituloFormulario.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTituloFormulario.setForeground(new java.awt.Color(0, 78, 174));

        pnlFormulario.setBackground(new java.awt.Color(255, 255, 255));
        pnlFormulario.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(179, 179, 179), 1, true));
        lblSecaoOcorrencia.setText("Ocorrência");
        lblSecaoOcorrencia.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblSecaoOcorrencia.setForeground(new java.awt.Color(108, 117, 125));

        lblTitulo.setText("Título:");
        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        lblTipoOcorrencia.setText("Tipo:");
        lblTipoOcorrencia.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        lblEquipamento.setText("Equipamento:");
        lblEquipamento.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        lblStatus.setText("Status:");
        lblStatus.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        lblDataOcorrencia.setText("Data:");
        lblDataOcorrencia.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        pnlDataOcorrencia.setBackground(new java.awt.Color(255, 255, 255));
        calDataOcorrencia.setDateFormatString("dd/MM/yyyy");

        javax.swing.GroupLayout pnlDataOcorrenciaLayout = new javax.swing.GroupLayout(pnlDataOcorrencia);
        pnlDataOcorrencia.setLayout(pnlDataOcorrenciaLayout);
        pnlDataOcorrenciaLayout.setHorizontalGroup(
            pnlDataOcorrenciaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDataOcorrenciaLayout.createSequentialGroup()
                .addComponent(calDataOcorrencia, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                .addGap(6, 6, 6)
                .addComponent(spnHoraOcorrencia, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlDataOcorrenciaLayout.setVerticalGroup(
            pnlDataOcorrenciaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
            .addComponent(calDataOcorrencia, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(spnHoraOcorrencia, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        lblDescricao.setText("Descrição:");
        lblDescricao.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        lblSecaoEnvolvidos.setText("Envolvidos");
        lblSecaoEnvolvidos.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblSecaoEnvolvidos.setForeground(new java.awt.Color(108, 117, 125));

        lblMovimentacao.setText("Movimentação:");
        lblMovimentacao.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        lblFuncionario.setText("Funcionário:");
        lblFuncionario.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        lblUsuarioRegistro.setText("Registrado por:");
        lblUsuarioRegistro.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        lblSecaoResolucao.setText("Resolução");
        lblSecaoResolucao.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblSecaoResolucao.setForeground(new java.awt.Color(108, 117, 125));

        lblSolucao.setText("Solução:");
        lblSolucao.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        lblDataResolucao.setText("Resolvida em:");
        lblDataResolucao.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        pnlDataResolucao.setBackground(new java.awt.Color(255, 255, 255));
        calDataResolucao.setDateFormatString("dd/MM/yyyy");

        javax.swing.GroupLayout pnlDataResolucaoLayout = new javax.swing.GroupLayout(pnlDataResolucao);
        pnlDataResolucao.setLayout(pnlDataResolucaoLayout);
        pnlDataResolucaoLayout.setHorizontalGroup(
            pnlDataResolucaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDataResolucaoLayout.createSequentialGroup()
                .addComponent(calDataResolucao, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                .addGap(6, 6, 6)
                .addComponent(spnHoraResolucao, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlDataResolucaoLayout.setVerticalGroup(
            pnlDataResolucaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
            .addComponent(calDataResolucao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(spnHoraResolucao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

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
                    .addComponent(lblSecaoOcorrencia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSecaoEnvolvidos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSecaoResolucao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlFormularioLayout.createSequentialGroup()
                        .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblEquipamento, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblDataOcorrencia, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblMovimentacao, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblUsuarioRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblSolucao, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTitulo, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(cmbEquipamento, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(pnlDataOcorrencia, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(cmbMovimentacao, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(cmbUsuarioRegistro, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(txtSolucao, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE))
                        .addGap(24, 24, 24)
                        .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTipoOcorrencia, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblDataResolucao, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbTipoOcorrencia, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(cmbStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(txtDescricao, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(cmbFuncionario, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(pnlDataResolucao, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)))
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
                .addComponent(lblSecaoOcorrencia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTipoOcorrencia, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbTipoOcorrencia, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblEquipamento, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbEquipamento, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblDataOcorrencia, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlDataOcorrencia, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGap(8, 8, 8)
                .addComponent(lblSecaoEnvolvidos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblMovimentacao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbMovimentacao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblUsuarioRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbUsuarioRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGap(8, 8, 8)
                .addComponent(lblSecaoResolucao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblSolucao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSolucao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDataResolucao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlDataResolucao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
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

        lblTituloLista.setText("Lista de Ocorrências");
        lblTituloLista.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTituloLista.setForeground(new java.awt.Color(0, 78, 174));

        tblDados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Título", "Equipamento", "Tipo", "Status", "Data", "Resolução"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
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
                .addComponent(scrollDados, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
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
    private com.toedter.calendar.JDateChooser calDataOcorrencia;
    private com.toedter.calendar.JDateChooser calDataResolucao;
    private javax.swing.JComboBox<ComboItem> cmbEquipamento;
    private javax.swing.JComboBox<ComboItem> cmbFuncionario;
    private javax.swing.JComboBox<ComboItem> cmbMovimentacao;
    private javax.swing.JComboBox<ComboItem> cmbStatus;
    private javax.swing.JComboBox<ComboItem> cmbTipoOcorrencia;
    private javax.swing.JComboBox<ComboItem> cmbUsuarioRegistro;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblDataOcorrencia;
    private javax.swing.JLabel lblDataResolucao;
    private javax.swing.JLabel lblDescricao;
    private javax.swing.JLabel lblEquipamento;
    private javax.swing.JLabel lblFuncionario;
    private javax.swing.JLabel lblMovimentacao;
    private javax.swing.JLabel lblSecaoEnvolvidos;
    private javax.swing.JLabel lblSecaoOcorrencia;
    private javax.swing.JLabel lblSecaoResolucao;
    private javax.swing.JLabel lblSolucao;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblTipoOcorrencia;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblTituloFormulario;
    private javax.swing.JLabel lblTituloLista;
    private javax.swing.JLabel lblTituloPesquisa;
    private javax.swing.JLabel lblUsuarioRegistro;
    private javax.swing.JPanel pnlDataOcorrencia;
    private javax.swing.JPanel pnlDataResolucao;
    private javax.swing.JPanel pnlFormulario;
    private javax.swing.JPanel pnlPesquisa;
    private javax.swing.JPanel pnlPrincipal;
    private javax.swing.JScrollPane scrollDados;
    private javax.swing.JSpinner spnHoraOcorrencia;
    private javax.swing.JSpinner spnHoraResolucao;
    private javax.swing.JTable tblDados;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtDescricao;
    private javax.swing.JTextField txtSolucao;
    private javax.swing.JTextField txtTitulo;
    // End of variables declaration//GEN-END:variables
}
