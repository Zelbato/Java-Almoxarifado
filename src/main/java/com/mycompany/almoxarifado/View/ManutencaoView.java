package com.mycompany.almoxarifado.View;

import com.mycompany.almoxarifado.Dto.ManutencaoDTO;
import com.mycompany.almoxarifado.Service.ManutencaoService;
import com.mycompany.almoxarifado.Service.AuthSession;
import java.sql.SQLException;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;

/** Formulário JFrame do NetBeans para manutenções. */
public class ManutencaoView extends javax.swing.JFrame {

    private final ManutencaoService service = new ManutencaoService();
    private Long idSelecionado;

    public ManutencaoView() {
        AuthSession.exigirAutenticacao();
        initComponents();
        ViewInputSupport.configurarHora(spnHoraEntrada, spnHoraPrevista, spnHoraConclusao);
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

    // os dois perfis registram manutenção, só o Chefe exclui
    private void aplicarPermissoes() {
        boolean permitido = AuthSession.podeEscrever();
        btnNovo.setVisible(permitido);
        btnSalvar.setVisible(permitido);
        btnEditar.setVisible(permitido);
        btnExcluir.setVisible(AuthSession.ehChefe());
    }

    private ManutencaoDTO lerFormulario() {
        ManutencaoDTO dto = new ManutencaoDTO();
        dto.setIdEquipamento(ViewInputSupport.id(cmbEquipamento, "O equipamento", true));
        dto.setIdUsuario(ViewInputSupport.id(cmbUsuario, "O usuário", true));
        dto.setIdTipoManutencao(ViewInputSupport.id(cmbTipoManutencao, "O tipo de manutenção", true));
        dto.setIdStatusManutencao(ViewInputSupport.id(cmbStatus, "O status", true));
        dto.setDescricaoProblema(ViewInputSupport.texto(txtDescricaoProblema));
        dto.setServicoRealizado(ViewInputSupport.texto(txtServicoRealizado));
        dto.setTecnicoResponsavel(textoSelecionado(cmbTecnicoResponsavel));
        dto.setDataEntrada(ViewInputSupport.dataHora(calDataEntrada, spnHoraEntrada, "A data de entrada", true));
        dto.setDataPrevistaConclusao(ViewInputSupport.dataHora(calConclusaoPrevista, spnHoraPrevista, "A conclusão prevista", false));
        dto.setDataConclusao(ViewInputSupport.dataHora(calDataConclusao, spnHoraConclusao, "A data de conclusão", false));
        dto.setObservacao(ViewInputSupport.texto(txtObservacao));
        return dto;
    }

    private void cadastrar() {
        try {
            service.cadastrar(lerFormulario());
            sucesso("Manutenção cadastrada com sucesso.");
        } catch (IllegalArgumentException | SQLException ex) {
            erro(ex);
        }
    }

    private void editar() {
        if (idSelecionado == null) {
            aviso("Selecione a manutenção para editar.");
            return;
        }
        try {
            ManutencaoDTO dto = lerFormulario();
            dto.setIdManutencao(idSelecionado);
            service.atualizar(dto);
            sucesso("Manutenção atualizada com sucesso.");
        } catch (IllegalArgumentException | SQLException ex) {
            erro(ex);
        }
    }

    private void excluir() {
        if (idSelecionado == null) {
            aviso("Selecione a manutenção para excluir.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Deseja excluir a manutenção selecionada?", "Confirmação", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            service.excluir(idSelecionado);
            sucesso("Manutenção excluída com sucesso.");
        } catch (IllegalArgumentException | SQLException ex) {
            erro(ex);
        }
    }

    private void carregarDados() {
        try {
            DefaultTableModel modelo = (DefaultTableModel) tblDados.getModel();
            modelo.setRowCount(0);
            for (ManutencaoDTO dto : service.listarTodos()) {
                modelo.addRow(new Object[]{
                    dto.getIdManutencao(),
                    ViewInputSupport.nomeDoItem(cmbEquipamento, dto.getIdEquipamento()),
                    ViewInputSupport.nomeDoItem(cmbTipoManutencao, dto.getIdTipoManutencao()),
                    ViewInputSupport.nomeDoItem(cmbStatus, dto.getIdStatusManutencao()),
                    ViewInputSupport.formatar(dto.getDataEntrada()),
                    ViewInputSupport.formatar(dto.getDataPrevistaConclusao()),
                    ViewInputSupport.formatar(dto.getDataConclusao())
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

    private void preencher(ManutencaoDTO dto) {
        ViewInputSupport.selecionar(cmbEquipamento, dto.getIdEquipamento());
        ViewInputSupport.selecionar(cmbUsuario, dto.getIdUsuario());
        ViewInputSupport.selecionar(cmbTipoManutencao, dto.getIdTipoManutencao());
        ViewInputSupport.selecionar(cmbStatus, dto.getIdStatusManutencao());
        selecionarPorTexto(cmbTecnicoResponsavel, dto.getTecnicoResponsavel());
        ViewInputSupport.preencher(txtDescricaoProblema, dto.getDescricaoProblema());
        ViewInputSupport.preencher(txtServicoRealizado, dto.getServicoRealizado());
        ViewInputSupport.preencher(txtObservacao, dto.getObservacao());
        ViewInputSupport.selecionar(calDataEntrada, spnHoraEntrada, dto.getDataEntrada());
        ViewInputSupport.selecionar(calConclusaoPrevista, spnHoraPrevista, dto.getDataPrevistaConclusao());
        ViewInputSupport.selecionar(calDataConclusao, spnHoraConclusao, dto.getDataConclusao());
    }

    private void limparFormulario() {
        idSelecionado = null;
        txtDescricaoProblema.setText("");
        txtServicoRealizado.setText("");
        txtObservacao.setText("");
        ViewInputSupport.selecionar(cmbEquipamento, null);
        ViewInputSupport.selecionar(cmbTipoManutencao, null);
        ViewInputSupport.selecionar(cmbStatus, null);
        cmbTecnicoResponsavel.setSelectedIndex(-1);
        // quem está logado já vem como responsável pelo registro
        ViewInputSupport.selecionar(cmbUsuario, AuthSession.usuario().getIdUsuario());
        ViewInputSupport.selecionar(calDataEntrada, spnHoraEntrada, java.time.LocalDateTime.now());
        ViewInputSupport.selecionar(calConclusaoPrevista, spnHoraPrevista, null);
        ViewInputSupport.selecionar(calDataConclusao, spnHoraConclusao, null);
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
            for (com.mycompany.almoxarifado.Dto.EquipamentoDTO item : new com.mycompany.almoxarifado.Service.EquipamentoService().listarTodos()) {
                cmbEquipamento.addItem(new ComboItem(item.getIdEquipamento(), item.getNome()));
            }
            for (com.mycompany.almoxarifado.Dto.UsuarioDTO item : new com.mycompany.almoxarifado.Service.UsuarioService().listarTodos()) {
                cmbUsuario.addItem(new ComboItem(item.getIdUsuario(), item.getEmail()));
            }
            for (com.mycompany.almoxarifado.Dto.TipoManutencaoDTO item : new com.mycompany.almoxarifado.Service.TipoManutencaoService().listarTodos()) {
                cmbTipoManutencao.addItem(new ComboItem(item.getIdTipoManutencao(), item.getNome()));
            }
            for (com.mycompany.almoxarifado.Dto.StatusManutencaoDTO item : new com.mycompany.almoxarifado.Service.StatusManutencaoService().listarTodos()) {
                cmbStatus.addItem(new ComboItem(item.getIdStatusManutencao(), item.getNome()));
            }
            // técnico é guardado como texto, a lista vem dos funcionários
            for (com.mycompany.almoxarifado.Dto.FuncionarioDTO item : new com.mycompany.almoxarifado.Service.FuncionarioService().listarTodos()) {
                cmbTecnicoResponsavel.addItem(new ComboItem(item.getIdFuncionario(), item.getNome()));
            }
            cmbEquipamento.setSelectedIndex(-1);
            cmbUsuario.setSelectedIndex(-1);
            cmbTipoManutencao.setSelectedIndex(-1);
            cmbStatus.setSelectedIndex(-1);
            cmbTecnicoResponsavel.setSelectedIndex(-1);
        } catch (SQLException ex) {
            erro(ex);
        }
    }

    private String textoSelecionado(JComboBox<ComboItem> combo) {
        Object selecionado = combo.getSelectedItem();
        return selecionado == null ? null : selecionado.toString();
    }

    private void selecionarPorTexto(JComboBox<ComboItem> combo, String valor) {
        combo.setSelectedIndex(-1);
        if (valor == null || valor.isBlank()) {
            return;
        }
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (valor.equals(combo.getItemAt(i).toString())) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlPrincipal = new javax.swing.JPanel();
        lblTituloFormulario = new javax.swing.JLabel();
        pnlFormulario = new javax.swing.JPanel();
        lblSecaoEntrada = new javax.swing.JLabel();
        lblEquipamento = new javax.swing.JLabel();
        cmbEquipamento = new javax.swing.JComboBox<>();
        lblTipoManutencao = new javax.swing.JLabel();
        cmbTipoManutencao = new javax.swing.JComboBox<>();
        lblStatus = new javax.swing.JLabel();
        cmbStatus = new javax.swing.JComboBox<>();
        lblUsuario = new javax.swing.JLabel();
        cmbUsuario = new javax.swing.JComboBox<>();
        lblDataEntrada = new javax.swing.JLabel();
        pnlDataEntrada = new javax.swing.JPanel();
        calDataEntrada = new com.toedter.calendar.JDateChooser();
        spnHoraEntrada = new javax.swing.JSpinner();
        lblConclusaoPrevista = new javax.swing.JLabel();
        pnlConclusaoPrevista = new javax.swing.JPanel();
        calConclusaoPrevista = new com.toedter.calendar.JDateChooser();
        spnHoraPrevista = new javax.swing.JSpinner();
        lblDescricaoProblema = new javax.swing.JLabel();
        txtDescricaoProblema = new javax.swing.JTextField();
        lblSecaoServico = new javax.swing.JLabel();
        lblTecnicoResponsavel = new javax.swing.JLabel();
        cmbTecnicoResponsavel = new javax.swing.JComboBox<>();
        lblDataConclusao = new javax.swing.JLabel();
        pnlDataConclusao = new javax.swing.JPanel();
        calDataConclusao = new com.toedter.calendar.JDateChooser();
        spnHoraConclusao = new javax.swing.JSpinner();
        lblServicoRealizado = new javax.swing.JLabel();
        txtServicoRealizado = new javax.swing.JTextField();
        lblObservacao = new javax.swing.JLabel();
        txtObservacao = new javax.swing.JTextField();
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
        setTitle("Almoxarifado - Manutenções");
        setMinimumSize(new java.awt.Dimension(1000, 680));

        pnlPrincipal.setBackground(new java.awt.Color(248, 249, 250));
        lblTituloFormulario.setText("Cadastro / Edição de Manutenção");
        lblTituloFormulario.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTituloFormulario.setForeground(new java.awt.Color(0, 78, 174));

        pnlFormulario.setBackground(new java.awt.Color(255, 255, 255));
        pnlFormulario.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(179, 179, 179), 1, true));
        lblSecaoEntrada.setText("Entrada");
        lblSecaoEntrada.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblSecaoEntrada.setForeground(new java.awt.Color(108, 117, 125));

        lblEquipamento.setText("Equipamento:");
        lblEquipamento.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        lblTipoManutencao.setText("Tipo:");
        lblTipoManutencao.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        lblStatus.setText("Status:");
        lblStatus.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        lblUsuario.setText("Registrado por:");
        lblUsuario.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        lblDataEntrada.setText("Data de entrada:");
        lblDataEntrada.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        pnlDataEntrada.setBackground(new java.awt.Color(255, 255, 255));
        calDataEntrada.setDateFormatString("dd/MM/yyyy");

        javax.swing.GroupLayout pnlDataEntradaLayout = new javax.swing.GroupLayout(pnlDataEntrada);
        pnlDataEntrada.setLayout(pnlDataEntradaLayout);
        pnlDataEntradaLayout.setHorizontalGroup(
            pnlDataEntradaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDataEntradaLayout.createSequentialGroup()
                .addComponent(calDataEntrada, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                .addGap(6, 6, 6)
                .addComponent(spnHoraEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlDataEntradaLayout.setVerticalGroup(
            pnlDataEntradaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
            .addComponent(calDataEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(spnHoraEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        lblConclusaoPrevista.setText("Previsão:");
        lblConclusaoPrevista.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        pnlConclusaoPrevista.setBackground(new java.awt.Color(255, 255, 255));
        calConclusaoPrevista.setDateFormatString("dd/MM/yyyy");

        javax.swing.GroupLayout pnlConclusaoPrevistaLayout = new javax.swing.GroupLayout(pnlConclusaoPrevista);
        pnlConclusaoPrevista.setLayout(pnlConclusaoPrevistaLayout);
        pnlConclusaoPrevistaLayout.setHorizontalGroup(
            pnlConclusaoPrevistaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlConclusaoPrevistaLayout.createSequentialGroup()
                .addComponent(calConclusaoPrevista, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                .addGap(6, 6, 6)
                .addComponent(spnHoraPrevista, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlConclusaoPrevistaLayout.setVerticalGroup(
            pnlConclusaoPrevistaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
            .addComponent(calConclusaoPrevista, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(spnHoraPrevista, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        lblDescricaoProblema.setText("Problema:");
        lblDescricaoProblema.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        lblSecaoServico.setText("Serviço");
        lblSecaoServico.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblSecaoServico.setForeground(new java.awt.Color(108, 117, 125));

        lblTecnicoResponsavel.setText("Técnico:");
        lblTecnicoResponsavel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        lblDataConclusao.setText("Conclusão:");
        lblDataConclusao.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        pnlDataConclusao.setBackground(new java.awt.Color(255, 255, 255));
        calDataConclusao.setDateFormatString("dd/MM/yyyy");

        javax.swing.GroupLayout pnlDataConclusaoLayout = new javax.swing.GroupLayout(pnlDataConclusao);
        pnlDataConclusao.setLayout(pnlDataConclusaoLayout);
        pnlDataConclusaoLayout.setHorizontalGroup(
            pnlDataConclusaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDataConclusaoLayout.createSequentialGroup()
                .addComponent(calDataConclusao, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                .addGap(6, 6, 6)
                .addComponent(spnHoraConclusao, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlDataConclusaoLayout.setVerticalGroup(
            pnlDataConclusaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
            .addComponent(calDataConclusao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(spnHoraConclusao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        lblServicoRealizado.setText("Serviço realizado:");
        lblServicoRealizado.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        lblObservacao.setText("Observação:");
        lblObservacao.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

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
                    .addComponent(lblSecaoEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSecaoServico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlFormularioLayout.createSequentialGroup()
                        .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblEquipamento, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblDataEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblDescricaoProblema, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblTecnicoResponsavel, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblServicoRealizado, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbEquipamento, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(cmbStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(pnlDataEntrada, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(txtDescricaoProblema, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(cmbTecnicoResponsavel, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(txtServicoRealizado, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE))
                        .addGap(24, 24, 24)
                        .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTipoManutencao, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblConclusaoPrevista, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblDataConclusao, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblObservacao, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbTipoManutencao, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(cmbUsuario, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(pnlConclusaoPrevista, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(pnlDataConclusao, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(txtObservacao, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)))
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
                .addComponent(lblSecaoEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblEquipamento, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbEquipamento, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTipoManutencao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbTipoManutencao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblDataEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlDataEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblConclusaoPrevista, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlConclusaoPrevista, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblDescricaoProblema, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDescricaoProblema, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGap(8, 8, 8)
                .addComponent(lblSecaoServico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblTecnicoResponsavel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbTecnicoResponsavel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDataConclusao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlDataConclusao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblServicoRealizado, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtServicoRealizado, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblObservacao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtObservacao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
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

        lblTituloLista.setText("Lista de Manutenções");
        lblTituloLista.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTituloLista.setForeground(new java.awt.Color(0, 78, 174));

        tblDados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Equipamento", "Tipo", "Status", "Entrada", "Previsão", "Conclusão"
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
    private com.toedter.calendar.JDateChooser calConclusaoPrevista;
    private com.toedter.calendar.JDateChooser calDataConclusao;
    private com.toedter.calendar.JDateChooser calDataEntrada;
    private javax.swing.JComboBox<ComboItem> cmbEquipamento;
    private javax.swing.JComboBox<ComboItem> cmbStatus;
    private javax.swing.JComboBox<ComboItem> cmbTecnicoResponsavel;
    private javax.swing.JComboBox<ComboItem> cmbTipoManutencao;
    private javax.swing.JComboBox<ComboItem> cmbUsuario;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblConclusaoPrevista;
    private javax.swing.JLabel lblDataConclusao;
    private javax.swing.JLabel lblDataEntrada;
    private javax.swing.JLabel lblDescricaoProblema;
    private javax.swing.JLabel lblEquipamento;
    private javax.swing.JLabel lblObservacao;
    private javax.swing.JLabel lblSecaoEntrada;
    private javax.swing.JLabel lblSecaoServico;
    private javax.swing.JLabel lblServicoRealizado;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblTecnicoResponsavel;
    private javax.swing.JLabel lblTipoManutencao;
    private javax.swing.JLabel lblTituloFormulario;
    private javax.swing.JLabel lblTituloLista;
    private javax.swing.JLabel lblTituloPesquisa;
    private javax.swing.JLabel lblUsuario;
    private javax.swing.JPanel pnlConclusaoPrevista;
    private javax.swing.JPanel pnlDataConclusao;
    private javax.swing.JPanel pnlDataEntrada;
    private javax.swing.JPanel pnlFormulario;
    private javax.swing.JPanel pnlPesquisa;
    private javax.swing.JPanel pnlPrincipal;
    private javax.swing.JScrollPane scrollDados;
    private javax.swing.JSpinner spnHoraConclusao;
    private javax.swing.JSpinner spnHoraEntrada;
    private javax.swing.JSpinner spnHoraPrevista;
    private javax.swing.JTable tblDados;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtDescricaoProblema;
    private javax.swing.JTextField txtObservacao;
    private javax.swing.JTextField txtServicoRealizado;
    // End of variables declaration//GEN-END:variables
}
