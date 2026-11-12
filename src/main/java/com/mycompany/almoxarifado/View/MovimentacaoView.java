package com.mycompany.almoxarifado.View;

import com.mycompany.almoxarifado.Dto.MovimentacaoDTO;
import com.mycompany.almoxarifado.Service.MovimentacaoService;
import com.mycompany.almoxarifado.Service.AuthSession;
import com.toedter.calendar.JDateChooser;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;

/**
 * Formulário JFrame do NetBeans para movimentações.
 * Não tem botão de excluir: para anular uma retirada o status vira CANCELADA.
 */
public class MovimentacaoView extends javax.swing.JFrame {

    private final MovimentacaoService service = new MovimentacaoService();

    private Long idSelecionado;

    public MovimentacaoView() {
        AuthSession.exigirAutenticacao();
        initComponents();
        configurarHoras();
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
        cmbStatus.addActionListener(e -> atualizarCamposDevolucao());
        tblDados.getSelectionModel().addListSelectionListener(this::selecionarLinha);
    }

    // Chefe e Júnior registram retiradas e devoluções
    private void aplicarPermissoes() {
        boolean permitido = AuthSession.podeEscrever();
        btnNovo.setVisible(permitido);
        btnSalvar.setVisible(permitido);
        btnEditar.setVisible(permitido);
    }

    private MovimentacaoDTO lerFormulario() {
        MovimentacaoDTO dto = new MovimentacaoDTO();
        boolean devolvida = statusDevolvidaSelecionado();

        dto.setIdEquipamento(ViewInputSupport.id(cmbEquipamento, "O equipamento", true));
        dto.setIdFuncionario(ViewInputSupport.id(cmbFuncionario, "O funcionário", true));
        dto.setIdUsuarioRetirada(ViewInputSupport.id(cmbUsuarioRetirada, "O usuário da retirada", true));
        dto.setIdStatusMovimentacao(ViewInputSupport.id(cmbStatus, "O status", true));
        dto.setDataRetirada(lerDataHora(calDataRetirada, spnHoraRetirada, "A data de retirada", true));
        dto.setDataPrevistaDevolucao(lerDataHora(calDevolucaoPrevista, spnHoraPrevista, "A devolução prevista", true));
        dto.setFinalidade(ViewInputSupport.texto(txtFinalidade));
        dto.setObservacaoRetirada(ViewInputSupport.texto(txtObservacaoRetirada));

        if (devolvida) {
            dto.setIdUsuarioDevolucao(ViewInputSupport.id(cmbUsuarioDevolucao, "O usuário da devolução", true));
            dto.setDataDevolucao(lerDataHora(calDataDevolucao, spnHoraDevolucao, "A data de devolução", true));
            dto.setObservacaoDevolucao(ViewInputSupport.texto(txtObservacaoDevolucao));
        }
        return dto;
    }

    private boolean statusDevolvidaSelecionado() {
        Object selecionado = cmbStatus.getSelectedItem();
        return selecionado != null && "DEVOLVIDA".equalsIgnoreCase(selecionado.toString().trim());
    }

    // os campos de devolução só ficam liberados quando o status é DEVOLVIDA
    private void atualizarCamposDevolucao() {
        boolean devolvida = statusDevolvidaSelecionado();

        cmbUsuarioDevolucao.setEnabled(devolvida);
        calDataDevolucao.setEnabled(devolvida);
        spnHoraDevolucao.setEnabled(devolvida);
        txtObservacaoDevolucao.setEnabled(devolvida);

        if (!devolvida) {
            cmbUsuarioDevolucao.setSelectedIndex(-1);
            calDataDevolucao.setDate(null);
            spnHoraDevolucao.setValue(new Date());
            txtObservacaoDevolucao.setText("");
        }
    }

    private LocalDateTime lerDataHora(JDateChooser calendario, JSpinner spinnerHora, String campo, boolean obrigatorio) {
        Date dataSelecionada = calendario.getDate();

        if (dataSelecionada == null) {
            if (obrigatorio) {
                throw new IllegalArgumentException(campo + " é obrigatória.");
            }
            return null;
        }

        Date horaSelecionada = (Date) spinnerHora.getValue();

        java.time.LocalDate data = dataSelecionada.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        java.time.LocalTime hora = horaSelecionada.toInstant().atZone(ZoneId.systemDefault()).toLocalTime()
                .withSecond(0)
                .withNano(0);

        return LocalDateTime.of(data, hora);
    }

    private void cadastrar() {
        try {
            service.cadastrar(lerFormulario());
            sucesso("Retirada registrada com sucesso.");
        } catch (IllegalArgumentException | SQLException ex) {
            erro(ex);
        }
    }

    private void editar() {
        if (idSelecionado == null) {
            aviso("Selecione a movimentação para editar.");
            return;
        }
        try {
            MovimentacaoDTO dto = lerFormulario();
            dto.setIdMovimentacao(idSelecionado);
            service.atualizar(dto);
            sucesso("Movimentação atualizada com sucesso.");
        } catch (IllegalArgumentException | SQLException ex) {
            erro(ex);
        }
    }

    private void carregarDados() {
        try {
            DefaultTableModel modelo = (DefaultTableModel) tblDados.getModel();
            modelo.setRowCount(0);

            for (MovimentacaoDTO dto : service.listarTodos()) {
                modelo.addRow(new Object[]{
                    dto.getIdMovimentacao(),
                    ViewInputSupport.nomeDoItem(cmbEquipamento, dto.getIdEquipamento()),
                    ViewInputSupport.nomeDoItem(cmbFuncionario, dto.getIdFuncionario()),
                    ViewInputSupport.nomeDoItem(cmbStatus, dto.getIdStatusMovimentacao()),
                    ViewInputSupport.formatar(dto.getDataRetirada()),
                    ViewInputSupport.formatar(dto.getDataPrevistaDevolucao()),
                    ViewInputSupport.formatar(dto.getDataDevolucao())
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
        } catch (SQLException | IllegalArgumentException ex) {
            erro(ex);
        }
    }

    private void preencher(MovimentacaoDTO dto) {
        ViewInputSupport.selecionar(cmbEquipamento, dto.getIdEquipamento());
        ViewInputSupport.selecionar(cmbFuncionario, dto.getIdFuncionario());
        ViewInputSupport.selecionar(cmbUsuarioRetirada, dto.getIdUsuarioRetirada());
        ViewInputSupport.selecionar(cmbStatus, dto.getIdStatusMovimentacao());
        ViewInputSupport.selecionar(cmbUsuarioDevolucao, dto.getIdUsuarioDevolucao());

        preencherDataHora(calDataRetirada, spnHoraRetirada, dto.getDataRetirada());
        preencherDataHora(calDevolucaoPrevista, spnHoraPrevista, dto.getDataPrevistaDevolucao());
        preencherDataHora(calDataDevolucao, spnHoraDevolucao, dto.getDataDevolucao());

        ViewInputSupport.preencher(txtFinalidade, dto.getFinalidade());
        ViewInputSupport.preencher(txtObservacaoRetirada, dto.getObservacaoRetirada());
        ViewInputSupport.preencher(txtObservacaoDevolucao, dto.getObservacaoDevolucao());

        atualizarCamposDevolucao();
    }

    private void preencherDataHora(JDateChooser calendario, JSpinner spinnerHora, LocalDateTime valor) {
        if (valor == null) {
            calendario.setDate(null);
            spinnerHora.setValue(new Date());
            return;
        }

        Date data = Date.from(valor.atZone(ZoneId.systemDefault()).toInstant());
        calendario.setDate(data);
        spinnerHora.setValue(data);
    }

    private void limparFormulario() {
        idSelecionado = null;

        txtFinalidade.setText("");
        txtObservacaoRetirada.setText("");
        txtObservacaoDevolucao.setText("");

        ViewInputSupport.selecionar(cmbEquipamento, null);
        ViewInputSupport.selecionar(cmbFuncionario, null);
        ViewInputSupport.selecionar(cmbUsuarioDevolucao, null);
        ViewInputSupport.selecionar(cmbStatus, null);
        // retirada nova sempre começa EM_ABERTO
        for (int i = 0; i < cmbStatus.getItemCount(); i++) {
            if (cmbStatus.getItemAt(i).toString().equals("EM_ABERTO")) {
                cmbStatus.setSelectedIndex(i);
            }
        }

        // quem está logado já vem como responsável pela retirada
        ViewInputSupport.selecionar(cmbUsuarioRetirada, AuthSession.usuario().getIdUsuario());

        calDataRetirada.setDate(new Date());
        calDataDevolucao.setDate(null);
        calDevolucaoPrevista.setDate(null);

        Date agora = new Date();
        spnHoraRetirada.setValue(agora);
        spnHoraDevolucao.setValue(agora);
        spnHoraPrevista.setValue(agora);

        tblDados.clearSelection();
        btnSalvar.setEnabled(true);
        btnEditar.setEnabled(false);

        atualizarCamposDevolucao();
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
            for (com.mycompany.almoxarifado.Dto.EquipamentoDTO item
                    : new com.mycompany.almoxarifado.Service.EquipamentoService().listarTodos()) {
                cmbEquipamento.addItem(new ComboItem(item.getIdEquipamento(), item.getNome()));
            }

            for (com.mycompany.almoxarifado.Dto.FuncionarioDTO item
                    : new com.mycompany.almoxarifado.Service.FuncionarioService().listarTodos()) {
                cmbFuncionario.addItem(new ComboItem(item.getIdFuncionario(), item.getNome()));
            }

            for (com.mycompany.almoxarifado.Dto.UsuarioDTO item
                    : new com.mycompany.almoxarifado.Service.UsuarioService().listarTodos()) {
                cmbUsuarioRetirada.addItem(new ComboItem(item.getIdUsuario(), item.getEmail()));
                cmbUsuarioDevolucao.addItem(new ComboItem(item.getIdUsuario(), item.getEmail()));
            }

            for (com.mycompany.almoxarifado.Dto.StatusMovimentacaoDTO item
                    : new com.mycompany.almoxarifado.Service.StatusMovimentacaoService().listarTodos()) {
                cmbStatus.addItem(new ComboItem(item.getIdStatusMovimentacao(), item.getNome()));
            }

            cmbEquipamento.setSelectedIndex(-1);
            cmbFuncionario.setSelectedIndex(-1);
            cmbUsuarioRetirada.setSelectedIndex(-1);
            cmbUsuarioDevolucao.setSelectedIndex(-1);
            cmbStatus.setSelectedIndex(-1);
        } catch (SQLException ex) {
            erro(ex);
        }
    }

    // os spinners de hora estão no form; aqui só troca o modelo para mostrar HH:mm
    private void configurarHoras() {
        for (JSpinner spinner : new JSpinner[]{spnHoraRetirada, spnHoraPrevista, spnHoraDevolucao}) {
            spinner.setModel(new SpinnerDateModel(new Date(), null, null, Calendar.MINUTE));
            spinner.setEditor(new JSpinner.DateEditor(spinner, "HH:mm"));
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlPrincipal = new javax.swing.JPanel();
        lblTituloFormulario = new javax.swing.JLabel();
        pnlFormulario = new javax.swing.JPanel();
        lblSecaoRetirada = new javax.swing.JLabel();
        lblEquipamento = new javax.swing.JLabel();
        cmbEquipamento = new javax.swing.JComboBox<>();
        lblFuncionario = new javax.swing.JLabel();
        cmbFuncionario = new javax.swing.JComboBox<>();
        lblUsuarioRetirada = new javax.swing.JLabel();
        cmbUsuarioRetirada = new javax.swing.JComboBox<>();
        lblStatus = new javax.swing.JLabel();
        cmbStatus = new javax.swing.JComboBox<>();
        lblDataRetirada = new javax.swing.JLabel();
        pnlDataRetirada = new javax.swing.JPanel();
        calDataRetirada = new com.toedter.calendar.JDateChooser();
        spnHoraRetirada = new javax.swing.JSpinner();
        lblDevolucaoPrevista = new javax.swing.JLabel();
        pnlDevolucaoPrevista = new javax.swing.JPanel();
        calDevolucaoPrevista = new com.toedter.calendar.JDateChooser();
        spnHoraPrevista = new javax.swing.JSpinner();
        lblFinalidade = new javax.swing.JLabel();
        txtFinalidade = new javax.swing.JTextField();
        lblObservacaoRetirada = new javax.swing.JLabel();
        txtObservacaoRetirada = new javax.swing.JTextField();
        lblSecaoDevolucao = new javax.swing.JLabel();
        lblUsuarioDevolucao = new javax.swing.JLabel();
        cmbUsuarioDevolucao = new javax.swing.JComboBox<>();
        lblDataDevolucao = new javax.swing.JLabel();
        pnlDataDevolucao = new javax.swing.JPanel();
        calDataDevolucao = new com.toedter.calendar.JDateChooser();
        spnHoraDevolucao = new javax.swing.JSpinner();
        lblObservacaoDevolucao = new javax.swing.JLabel();
        txtObservacaoDevolucao = new javax.swing.JTextField();
        btnNovo = new javax.swing.JButton();
        btnSalvar = new javax.swing.JButton();
        btnEditar = new javax.swing.JButton();
        lblTituloPesquisa = new javax.swing.JLabel();
        pnlPesquisa = new javax.swing.JPanel();
        lblBuscar = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        lblTituloLista = new javax.swing.JLabel();
        scrollDados = new javax.swing.JScrollPane();
        tblDados = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Almoxarifado - Movimentações");
        setMinimumSize(new java.awt.Dimension(1000, 680));

        pnlPrincipal.setBackground(new java.awt.Color(248, 249, 250));
        lblTituloFormulario.setText("Retirada / Devolução de Equipamento");
        lblTituloFormulario.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTituloFormulario.setForeground(new java.awt.Color(0, 78, 174));

        pnlFormulario.setBackground(new java.awt.Color(255, 255, 255));
        pnlFormulario.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(179, 179, 179), 1, true));
        lblSecaoRetirada.setText("Retirada");
        lblSecaoRetirada.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblSecaoRetirada.setForeground(new java.awt.Color(108, 117, 125));

        lblEquipamento.setText("Equipamento:");
        lblEquipamento.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        lblFuncionario.setText("Funcionário:");
        lblFuncionario.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        lblUsuarioRetirada.setText("Responsável:");
        lblUsuarioRetirada.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        lblStatus.setText("Status:");
        lblStatus.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        lblDataRetirada.setText("Data da retirada:");
        lblDataRetirada.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        pnlDataRetirada.setBackground(new java.awt.Color(255, 255, 255));
        calDataRetirada.setDateFormatString("dd/MM/yyyy");

        javax.swing.GroupLayout pnlDataRetiradaLayout = new javax.swing.GroupLayout(pnlDataRetirada);
        pnlDataRetirada.setLayout(pnlDataRetiradaLayout);
        pnlDataRetiradaLayout.setHorizontalGroup(
            pnlDataRetiradaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDataRetiradaLayout.createSequentialGroup()
                .addComponent(calDataRetirada, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                .addGap(6, 6, 6)
                .addComponent(spnHoraRetirada, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlDataRetiradaLayout.setVerticalGroup(
            pnlDataRetiradaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
            .addComponent(calDataRetirada, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(spnHoraRetirada, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        lblDevolucaoPrevista.setText("Devolver até:");
        lblDevolucaoPrevista.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        pnlDevolucaoPrevista.setBackground(new java.awt.Color(255, 255, 255));
        calDevolucaoPrevista.setDateFormatString("dd/MM/yyyy");

        javax.swing.GroupLayout pnlDevolucaoPrevistaLayout = new javax.swing.GroupLayout(pnlDevolucaoPrevista);
        pnlDevolucaoPrevista.setLayout(pnlDevolucaoPrevistaLayout);
        pnlDevolucaoPrevistaLayout.setHorizontalGroup(
            pnlDevolucaoPrevistaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDevolucaoPrevistaLayout.createSequentialGroup()
                .addComponent(calDevolucaoPrevista, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                .addGap(6, 6, 6)
                .addComponent(spnHoraPrevista, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlDevolucaoPrevistaLayout.setVerticalGroup(
            pnlDevolucaoPrevistaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
            .addComponent(calDevolucaoPrevista, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(spnHoraPrevista, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        lblFinalidade.setText("Finalidade:");
        lblFinalidade.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        lblObservacaoRetirada.setText("Observação:");
        lblObservacaoRetirada.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        lblSecaoDevolucao.setText("Devolução");
        lblSecaoDevolucao.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblSecaoDevolucao.setForeground(new java.awt.Color(108, 117, 125));

        lblUsuarioDevolucao.setText("Recebido por:");
        lblUsuarioDevolucao.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        lblDataDevolucao.setText("Data da devolução:");
        lblDataDevolucao.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        pnlDataDevolucao.setBackground(new java.awt.Color(255, 255, 255));
        calDataDevolucao.setDateFormatString("dd/MM/yyyy");

        javax.swing.GroupLayout pnlDataDevolucaoLayout = new javax.swing.GroupLayout(pnlDataDevolucao);
        pnlDataDevolucao.setLayout(pnlDataDevolucaoLayout);
        pnlDataDevolucaoLayout.setHorizontalGroup(
            pnlDataDevolucaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDataDevolucaoLayout.createSequentialGroup()
                .addComponent(calDataDevolucao, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                .addGap(6, 6, 6)
                .addComponent(spnHoraDevolucao, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlDataDevolucaoLayout.setVerticalGroup(
            pnlDataDevolucaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
            .addComponent(calDataDevolucao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(spnHoraDevolucao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        lblObservacaoDevolucao.setText("Observação:");
        lblObservacaoDevolucao.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

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

        javax.swing.GroupLayout pnlFormularioLayout = new javax.swing.GroupLayout(pnlFormulario);
        pnlFormulario.setLayout(pnlFormularioLayout);
        pnlFormularioLayout.setHorizontalGroup(
            pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormularioLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblSecaoRetirada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSecaoDevolucao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlFormularioLayout.createSequentialGroup()
                        .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblEquipamento, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblUsuarioRetirada, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblDataRetirada, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblFinalidade, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblUsuarioDevolucao, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblObservacaoDevolucao, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbEquipamento, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(cmbUsuarioRetirada, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(pnlDataRetirada, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(txtFinalidade, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(cmbUsuarioDevolucao, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(txtObservacaoDevolucao, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE))
                        .addGap(24, 24, 24)
                        .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblDevolucaoPrevista, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblObservacaoRetirada, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblDataDevolucao, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbFuncionario, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(cmbStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(pnlDevolucaoPrevista, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(txtObservacaoRetirada, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(pnlDataDevolucao, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)))
                    .addGroup(pnlFormularioLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnNovo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(16, 16, 16))
        );
        pnlFormularioLayout.setVerticalGroup(
            pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormularioLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(lblSecaoRetirada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblEquipamento, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbEquipamento, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblUsuarioRetirada, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbUsuarioRetirada, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblDataRetirada, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlDataRetirada, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDevolucaoPrevista, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlDevolucaoPrevista, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblFinalidade, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFinalidade, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblObservacaoRetirada, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtObservacaoRetirada, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGap(8, 8, 8)
                .addComponent(lblSecaoDevolucao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblUsuarioDevolucao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbUsuarioDevolucao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDataDevolucao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlDataDevolucao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblObservacaoDevolucao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtObservacaoDevolucao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGap(8, 8, 8)
                .addGroup(pnlFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNovo, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
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

        lblTituloLista.setText("Lista de Movimentações");
        lblTituloLista.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTituloLista.setForeground(new java.awt.Color(0, 78, 174));

        tblDados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Equipamento", "Funcionário", "Status", "Retirada", "Devolver até", "Devolução"
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

    /**
     * Permite executar esta JFrame isoladamente pelo NetBeans.
     */
    public static void main(String[] args) {
        com.mycompany.almoxarifado.Almoxarifado.main(args);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnNovo;
    private javax.swing.JButton btnSalvar;
    private com.toedter.calendar.JDateChooser calDataDevolucao;
    private com.toedter.calendar.JDateChooser calDataRetirada;
    private com.toedter.calendar.JDateChooser calDevolucaoPrevista;
    private javax.swing.JComboBox<ComboItem> cmbEquipamento;
    private javax.swing.JComboBox<ComboItem> cmbFuncionario;
    private javax.swing.JComboBox<ComboItem> cmbStatus;
    private javax.swing.JComboBox<ComboItem> cmbUsuarioDevolucao;
    private javax.swing.JComboBox<ComboItem> cmbUsuarioRetirada;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblDataDevolucao;
    private javax.swing.JLabel lblDataRetirada;
    private javax.swing.JLabel lblDevolucaoPrevista;
    private javax.swing.JLabel lblEquipamento;
    private javax.swing.JLabel lblFinalidade;
    private javax.swing.JLabel lblFuncionario;
    private javax.swing.JLabel lblObservacaoDevolucao;
    private javax.swing.JLabel lblObservacaoRetirada;
    private javax.swing.JLabel lblSecaoDevolucao;
    private javax.swing.JLabel lblSecaoRetirada;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblTituloFormulario;
    private javax.swing.JLabel lblTituloLista;
    private javax.swing.JLabel lblTituloPesquisa;
    private javax.swing.JLabel lblUsuarioDevolucao;
    private javax.swing.JLabel lblUsuarioRetirada;
    private javax.swing.JPanel pnlDataDevolucao;
    private javax.swing.JPanel pnlDataRetirada;
    private javax.swing.JPanel pnlDevolucaoPrevista;
    private javax.swing.JPanel pnlFormulario;
    private javax.swing.JPanel pnlPesquisa;
    private javax.swing.JPanel pnlPrincipal;
    private javax.swing.JScrollPane scrollDados;
    private javax.swing.JSpinner spnHoraDevolucao;
    private javax.swing.JSpinner spnHoraPrevista;
    private javax.swing.JSpinner spnHoraRetirada;
    private javax.swing.JTable tblDados;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtFinalidade;
    private javax.swing.JTextField txtObservacaoDevolucao;
    private javax.swing.JTextField txtObservacaoRetirada;
    // End of variables declaration//GEN-END:variables
}
