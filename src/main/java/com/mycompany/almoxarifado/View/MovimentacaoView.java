package com.mycompany.almoxarifado.View;

import com.mycompany.almoxarifado.Dto.MovimentacaoDTO;
import com.mycompany.almoxarifado.Service.MovimentacaoService;
import com.mycompany.almoxarifado.Service.AuthSession;
import com.toedter.calendar.JDateChooser;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;

/**
 * Formulário JFrame do NetBeans para movimentações.
 */
public class MovimentacaoView extends javax.swing.JFrame {

    private final MovimentacaoService service = new MovimentacaoService();
    private final Map<String, JTextField> campos = new LinkedHashMap<>();

    private Long idSelecionado;

    private JComboBox<ComboItem> cmbEquipamento;
    private JComboBox<ComboItem> cmbFuncionario;
    private JComboBox<ComboItem> cmbUsuarioRetirada;
    private JComboBox<ComboItem> cmbUsuarioDevolucao;
    private JComboBox<ComboItem> cmbStatus;

    private JDateChooser calDataRetirada;
    private JDateChooser calDataDevolucao;
    private JDateChooser calDevolucaoPrevista;

    private JSpinner spnHoraRetirada;
    private JSpinner spnHoraDevolucao;
    private JSpinner spnHoraPrevista;

    private JPanel pnlDataRetirada;
    private JPanel pnlDataDevolucao;
    private JPanel pnlDevolucaoPrevista;

    public MovimentacaoView() {
        AuthSession.exigirAutenticacao();
        initComponents();
        montarCampos();
        configurarSelects();
        configurarDatasHoras();
        ajustarLayoutFormulario();
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
        cmbStatus.addActionListener(e -> atualizarCamposDevolucao());
        tblDados.getSelectionModel().addListSelectionListener(this::selecionarLinha);
    }

    private void aplicarPermissoes() {
        boolean permitido = AuthSession.podeEscrever();
        btnNovo.setEnabled(permitido);
        btnSalvar.setEnabled(permitido);
        btnEditar.setEnabled(false);
        btnExcluir.setEnabled(false);
    }

    private MovimentacaoDTO lerFormulario() {
        MovimentacaoDTO dto = new MovimentacaoDTO();

        dto.setIdEquipamento(
                ViewInputSupport.id(
                        cmbEquipamento,
                        "O equipamento",
                        true
                )
        );

        dto.setIdFuncionario(
                ViewInputSupport.id(
                        cmbFuncionario,
                        "O funcionário",
                        true
                )
        );

        dto.setIdUsuarioRetirada(
                ViewInputSupport.id(
                        cmbUsuarioRetirada,
                        "O usuário da retirada",
                        true
                )
        );

        dto.setIdStatusMovimentacao(
                ViewInputSupport.id(
                        cmbStatus,
                        "O status",
                        true
                )
        );

        boolean devolvida = statusDevolvidaSelecionado();

        if (devolvida) {
            dto.setIdUsuarioDevolucao(
                    ViewInputSupport.id(
                            cmbUsuarioDevolucao,
                            "O usuário da devolução",
                            true
                    )
            );
        } else {
            dto.setIdUsuarioDevolucao(null);
        }

        dto.setDataRetirada(
                lerDataHora(
                        calDataRetirada,
                        spnHoraRetirada,
                        "A data de retirada",
                        true
                )
        );

        if (devolvida) {
            dto.setDataDevolucao(
                    lerDataHora(
                            calDataDevolucao,
                            spnHoraDevolucao,
                            "A data de devolução",
                            true
                    )
            );
        } else {
            dto.setDataDevolucao(null);
        }

        dto.setDataPrevistaDevolucao(
                lerDataHora(
                        calDevolucaoPrevista,
                        spnHoraPrevista,
                        "A devolução prevista",
                        true
                )
        );

        dto.setFinalidade(
                ViewInputSupport.texto(
                        campos.get("Finalidade")
                )
        );

        dto.setObservacaoRetirada(
                ViewInputSupport.texto(
                        campos.get("Observação retirada")
                )
        );

        dto.setObservacaoDevolucao(
                devolvida
                        ? ViewInputSupport.texto(
                                campos.get("Observação devolução")
                        )
                        : null
        );

        return dto;
    }

    private boolean statusDevolvidaSelecionado() {
        Object selecionado = cmbStatus.getSelectedItem();
        return selecionado != null
                && "DEVOLVIDA".equalsIgnoreCase(
                        selecionado.toString().trim()
                );
    }

    private void atualizarCamposDevolucao() {
        boolean devolvida = statusDevolvidaSelecionado();

        cmbUsuarioDevolucao.setEnabled(devolvida);
        calDataDevolucao.setEnabled(devolvida);
        spnHoraDevolucao.setEnabled(devolvida);
        campos.get("Observação devolução").setEnabled(devolvida);

        if (!devolvida) {
            cmbUsuarioDevolucao.setSelectedIndex(-1);
            calDataDevolucao.setDate(null);
            spnHoraDevolucao.setValue(new Date());
            campos.get("Observação devolução").setText("");
        }
    }

    private LocalDateTime lerDataHora(
            JDateChooser calendario,
            JSpinner spinnerHora,
            String campo,
            boolean obrigatorio
    ) {
        Date dataSelecionada = calendario.getDate();

        if (dataSelecionada == null) {
            if (obrigatorio) {
                throw new IllegalArgumentException(
                        campo + " é obrigatória."
                );
            }

            return null;
        }

        Date horaSelecionada = (Date) spinnerHora.getValue();

        java.time.LocalDate data = dataSelecionada
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        java.time.LocalTime hora = horaSelecionada
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalTime()
                .withSecond(0)
                .withNano(0);

        return LocalDateTime.of(data, hora);
    }

    private void cadastrar() {
        try {
            service.cadastrar(lerFormulario());
            sucesso("Movimentação cadastrada com sucesso.");
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

    private void excluir() {
        if (idSelecionado == null) {
            aviso("Selecione a movimentação para excluir.");
            return;
        }

        if (JOptionPane.showConfirmDialog(
                this,
                "Deseja excluir a movimentação selecionada?",
                "Confirmação",
                JOptionPane.YES_NO_OPTION
        ) != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            service.excluir(idSelecionado);
            sucesso("Movimentação excluída com sucesso.");
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
                    dto.getIdEquipamento(),
                    dto.getIdFuncionario(),
                    dto.getIdStatusMovimentacao(),
                    dto.getDataRetirada(),
                    dto.getDataPrevistaDevolucao(),
                    dto.getDataDevolucao()
                });
            }

        } catch (SQLException ex) {
            erro(ex);
        }
    }

    private void selecionarLinha(ListSelectionEvent evento) {
        if (evento.getValueIsAdjusting()
                || tblDados.getSelectedRow() < 0) {
            return;
        }

        int linha = tblDados.convertRowIndexToModel(
                tblDados.getSelectedRow()
        );

        Long id = Long.valueOf(
                tblDados.getModel()
                        .getValueAt(linha, 0)
                        .toString()
        );

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

    private void preencher(MovimentacaoDTO dto) {
        ViewInputSupport.selecionar(
                cmbEquipamento,
                dto.getIdEquipamento()
        );

        ViewInputSupport.selecionar(
                cmbFuncionario,
                dto.getIdFuncionario()
        );

        ViewInputSupport.selecionar(
                cmbUsuarioRetirada,
                dto.getIdUsuarioRetirada()
        );

        ViewInputSupport.selecionar(
                cmbUsuarioDevolucao,
                dto.getIdUsuarioDevolucao()
        );

        ViewInputSupport.selecionar(
                cmbStatus,
                dto.getIdStatusMovimentacao()
        );

        preencherDataHora(
                calDataRetirada,
                spnHoraRetirada,
                dto.getDataRetirada()
        );

        preencherDataHora(
                calDataDevolucao,
                spnHoraDevolucao,
                dto.getDataDevolucao()
        );

        preencherDataHora(
                calDevolucaoPrevista,
                spnHoraPrevista,
                dto.getDataPrevistaDevolucao()
        );

        preencher(
                "Finalidade",
                dto.getFinalidade()
        );

        preencher(
                "Observação retirada",
                dto.getObservacaoRetirada()
        );

        preencher(
                "Observação devolução",
                dto.getObservacaoDevolucao()
        );

        atualizarCamposDevolucao();
    }

    private void preencherDataHora(
            JDateChooser calendario,
            JSpinner spinnerHora,
            LocalDateTime valor
    ) {
        if (valor == null) {
            calendario.setDate(null);
            spinnerHora.setValue(new Date());
            return;
        }

        Date data = Date.from(
                valor
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        );

        calendario.setDate(data);
        spinnerHora.setValue(data);
    }

    private void preencher(String campo, Object valor) {
        ViewInputSupport.preencher(
                campos.get(campo),
                valor
        );
    }

    private void limparFormulario() {
        idSelecionado = null;

        campos.values().forEach(
                campo -> campo.setText("")
        );

        ViewInputSupport.selecionar(
                cmbEquipamento,
                null
        );

        ViewInputSupport.selecionar(
                cmbFuncionario,
                null
        );

        ViewInputSupport.selecionar(
                cmbUsuarioRetirada,
                null
        );

        ViewInputSupport.selecionar(
                cmbUsuarioDevolucao,
                null
        );

        ViewInputSupport.selecionar(
                cmbStatus,
                null
        );

        calDataRetirada.setDate(null);
        calDataDevolucao.setDate(null);
        calDevolucaoPrevista.setDate(null);

        Date agora = new Date();

        spnHoraRetirada.setValue(agora);
        spnHoraDevolucao.setValue(agora);
        spnHoraPrevista.setValue(agora);

        tblDados.clearSelection();

        boolean permitido = AuthSession.podeEscrever();

        btnSalvar.setEnabled(permitido);
        btnEditar.setEnabled(false);
        btnExcluir.setEnabled(false);

        atualizarCamposDevolucao();
    }

    private void sucesso(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem);
        limparFormulario();
        carregarDados();
    }

    private void aviso(String mensagem) {
        JOptionPane.showMessageDialog(
                this,
                mensagem,
                "Atenção",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void erro(Exception ex) {
        JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void montarCampos() {
        campos.put("Finalidade", txtCampo9);
        campos.put("Observação retirada", txtCampo10);
        campos.put("Observação devolução", txtCampo11);
    }

    private void configurarSelects() {
        cmbEquipamento = new JComboBox<>();
        cmbFuncionario = new JComboBox<>();
        cmbUsuarioRetirada = new JComboBox<>();
        cmbUsuarioDevolucao = new JComboBox<>();
        cmbStatus = new JComboBox<>();

        ViewInputSupport.substituirNoPainel(
                pnlCampos,
                txtCampo1,
                cmbEquipamento
        );

        ViewInputSupport.substituirNoPainel(
                pnlCampos,
                txtCampo2,
                cmbFuncionario
        );

        ViewInputSupport.substituirNoPainel(
                pnlCampos,
                txtCampo3,
                cmbUsuarioRetirada
        );

        ViewInputSupport.substituirNoPainel(
                pnlCampos,
                txtCampo4,
                cmbUsuarioDevolucao
        );

        ViewInputSupport.substituirNoPainel(
                pnlCampos,
                txtCampo5,
                cmbStatus
        );

        try {
            for (com.mycompany.almoxarifado.Dto.EquipamentoDTO item
                    : new com.mycompany.almoxarifado.Service.EquipamentoService().listarTodos()) {

                cmbEquipamento.addItem(
                        new ComboItem(
                                item.getIdEquipamento(),
                                item.getNome()
                        )
                );
            }

            for (com.mycompany.almoxarifado.Dto.FuncionarioDTO item
                    : new com.mycompany.almoxarifado.Service.FuncionarioService().listarTodos()) {

                cmbFuncionario.addItem(
                        new ComboItem(
                                item.getIdFuncionario(),
                                item.getNome()
                        )
                );
            }

            for (com.mycompany.almoxarifado.Dto.UsuarioDTO item
                    : new com.mycompany.almoxarifado.Service.UsuarioService().listarTodos()) {

                cmbUsuarioRetirada.addItem(
                        new ComboItem(
                                item.getIdUsuario(),
                                item.getEmail()
                        )
                );

                cmbUsuarioDevolucao.addItem(
                        new ComboItem(
                                item.getIdUsuario(),
                                item.getEmail()
                        )
                );
            }

            for (com.mycompany.almoxarifado.Dto.StatusMovimentacaoDTO item
                    : new com.mycompany.almoxarifado.Service.StatusMovimentacaoService().listarTodos()) {

                cmbStatus.addItem(
                        new ComboItem(
                                item.getIdStatusMovimentacao(),
                                item.getNome()
                        )
                );
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

    private void configurarDatasHoras() {
        calDataRetirada = new JDateChooser();
        calDataDevolucao = new JDateChooser();
        calDevolucaoPrevista = new JDateChooser();

        calDataRetirada.setDateFormatString("dd/MM/yyyy");
        calDataDevolucao.setDateFormatString("dd/MM/yyyy");
        calDevolucaoPrevista.setDateFormatString("dd/MM/yyyy");

        spnHoraRetirada = criarSpinnerHora();
        spnHoraDevolucao = criarSpinnerHora();
        spnHoraPrevista = criarSpinnerHora();

        pnlDataRetirada = criarPainelDataHora(
                calDataRetirada,
                spnHoraRetirada
        );

        pnlDataDevolucao = criarPainelDataHora(
                calDataDevolucao,
                spnHoraDevolucao
        );

        pnlDevolucaoPrevista = criarPainelDataHora(
                calDevolucaoPrevista,
                spnHoraPrevista
        );

        substituirComponente(txtCampo6, pnlDataRetirada);
        substituirComponente(txtCampo7, pnlDataDevolucao);
        substituirComponente(txtCampo8, pnlDevolucaoPrevista);
    }

    private JSpinner criarSpinnerHora() {
        SpinnerDateModel modelo = new SpinnerDateModel(
                new Date(),
                null,
                null,
                Calendar.MINUTE
        );

        JSpinner spinner = new JSpinner(modelo);

        spinner.setEditor(
                new JSpinner.DateEditor(
                        spinner,
                        "HH:mm"
                )
        );

        spinner.setPreferredSize(new Dimension(82, 32));

        return spinner;
    }

    private JPanel criarPainelDataHora(
            JDateChooser calendario,
            JSpinner spinnerHora
    ) {
        JPanel painel = new JPanel(
                new BorderLayout(6, 0)
        );

        painel.setOpaque(false);
        calendario.setPreferredSize(new Dimension(180, 32));

        painel.add(calendario, BorderLayout.CENTER);
        painel.add(spinnerHora, BorderLayout.EAST);

        return painel;
    }

    private void substituirComponente(
            java.awt.Component antigo,
            java.awt.Component novo
    ) {
        for (int i = 0; i < pnlCampos.getComponentCount(); i++) {
            if (pnlCampos.getComponent(i) == antigo) {
                pnlCampos.remove(i);
                pnlCampos.add(novo, i);
                pnlCampos.revalidate();
                pnlCampos.repaint();
                return;
            }
        }
    }

    private void ajustarLayoutFormulario() {
        pnlCampos.removeAll();
        pnlCampos.setLayout(new GridBagLayout());
        pnlCampos.setBackground(java.awt.Color.WHITE);
        pnlCampos.setBorder(
                javax.swing.BorderFactory.createEmptyBorder(
                        12,
                        12,
                        12,
                        12
                )
        );

        java.awt.Font fonte = new java.awt.Font(
                "Segoe UI",
                java.awt.Font.BOLD,
                12
        );

        javax.swing.JLabel[] labels = {
            lblCampo1,
            lblCampo2,
            lblCampo3,
            lblCampo4,
            lblCampo5,
            lblCampo6,
            lblCampo7,
            lblCampo8,
            lblCampo9,
            lblCampo10,
            lblCampo11
        };

        for (javax.swing.JLabel label : labels) {
            label.setFont(fonte);
        }

        adicionarCampo(lblCampo1, cmbEquipamento, 0, 0);
        adicionarCampo(lblCampo2, cmbFuncionario, 2, 0);

        adicionarCampo(lblCampo3, cmbUsuarioRetirada, 0, 1);
        adicionarCampo(lblCampo4, cmbUsuarioDevolucao, 2, 1);

        adicionarCampo(lblCampo5, cmbStatus, 0, 2);
        adicionarCampo(lblCampo9, txtCampo9, 2, 2);

        adicionarCampo(lblCampo6, pnlDataRetirada, 0, 3);
        adicionarCampo(lblCampo8, pnlDevolucaoPrevista, 2, 3);

        adicionarCampo(lblCampo7, pnlDataDevolucao, 0, 4);
        adicionarCampo(lblCampo10, txtCampo10, 2, 4);

        adicionarCampoLargo(
                lblCampo11,
                txtCampo11,
                5
        );

        javax.swing.JPanel pnlAcoes = new javax.swing.JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        12,
                        0
                )
        );

        pnlAcoes.setOpaque(false);
        pnlAcoes.add(btnNovo);
        pnlAcoes.add(btnSalvar);
        pnlAcoes.add(btnEditar);
        pnlAcoes.add(btnExcluir);

        pnlFormulario.removeAll();
        pnlFormulario.setLayout(
                new BorderLayout(
                        0,
                        14
                )
        );

        pnlFormulario.add(
                pnlCampos,
                BorderLayout.CENTER
        );

        pnlFormulario.add(
                pnlAcoes,
                BorderLayout.SOUTH
        );

        pnlFormulario.setPreferredSize(
                new Dimension(
                        950,
                        360
                )
        );

        pnlPesquisa.removeAll();
        pnlPesquisa.setLayout(
                new BorderLayout(
                        16,
                        0
                )
        );

        pnlPesquisa.setBorder(
                javax.swing.BorderFactory.createCompoundBorder(
                        javax.swing.BorderFactory.createLineBorder(
                                new java.awt.Color(
                                        179,
                                        179,
                                        179
                                )
                        ),
                        javax.swing.BorderFactory.createEmptyBorder(
                                14,
                                16,
                                14,
                                16
                        )
                )
        );

        pnlPesquisa.add(
                lblBuscar,
                BorderLayout.WEST
        );

        pnlPesquisa.add(
                txtBuscar,
                BorderLayout.CENTER
        );

        tblDados.setRowHeight(26);
        tblDados.setSelectionMode(
                javax.swing.ListSelectionModel.SINGLE_SELECTION
        );

        tblDados.getTableHeader()
                .setReorderingAllowed(false);

        jPanel1.setPreferredSize(
                new Dimension(
                        1000,
                        760
                )
        );

        setMinimumSize(
                new Dimension(
                        1030,
                        790
                )
        );

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
        GridBagConstraints gbcLabel
                = new GridBagConstraints();

        gbcLabel.gridx = coluna;
        gbcLabel.gridy = linha;
        gbcLabel.weightx = 0;
        gbcLabel.fill = GridBagConstraints.NONE;
        gbcLabel.anchor = GridBagConstraints.WEST;
        gbcLabel.insets = new Insets(
                7,
                0,
                7,
                12
        );

        pnlCampos.add(
                label,
                gbcLabel
        );

        GridBagConstraints gbcCampo
                = new GridBagConstraints();

        gbcCampo.gridx = coluna + 1;
        gbcCampo.gridy = linha;
        gbcCampo.weightx = 1;
        gbcCampo.fill = GridBagConstraints.HORIZONTAL;
        gbcCampo.anchor = GridBagConstraints.WEST;
        gbcCampo.insets = new Insets(
                7,
                0,
                7,
                coluna == 0 ? 24 : 0
        );

        campo.setPreferredSize(
                new Dimension(
                        250,
                        32
                )
        );

        pnlCampos.add(
                campo,
                gbcCampo
        );
    }

    private void adicionarCampoLargo(
            javax.swing.JLabel label,
            java.awt.Component campo,
            int linha
    ) {
        GridBagConstraints gbcLabel
                = new GridBagConstraints();

        gbcLabel.gridx = 0;
        gbcLabel.gridy = linha;
        gbcLabel.weightx = 0;
        gbcLabel.fill = GridBagConstraints.NONE;
        gbcLabel.anchor = GridBagConstraints.WEST;
        gbcLabel.insets = new Insets(
                7,
                0,
                7,
                12
        );

        pnlCampos.add(
                label,
                gbcLabel
        );

        GridBagConstraints gbcCampo
                = new GridBagConstraints();

        gbcCampo.gridx = 1;
        gbcCampo.gridy = linha;
        gbcCampo.gridwidth = 3;
        gbcCampo.weightx = 1;
        gbcCampo.fill = GridBagConstraints.HORIZONTAL;
        gbcCampo.insets = new Insets(
                7,
                0,
                7,
                0
        );

        campo.setPreferredSize(
                new Dimension(
                        650,
                        32
                )
        );

        pnlCampos.add(
                campo,
                gbcCampo
        );
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
        lblCampo8 = new javax.swing.JLabel();
        txtCampo8 = new javax.swing.JTextField();
        lblCampo9 = new javax.swing.JLabel();
        txtCampo9 = new javax.swing.JTextField();
        lblCampo10 = new javax.swing.JLabel();
        txtCampo10 = new javax.swing.JTextField();
        lblCampo11 = new javax.swing.JLabel();
        txtCampo11 = new javax.swing.JTextField();
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
        setTitle("Almoxarifado - Movimentações");
        setMinimumSize(new java.awt.Dimension(1030, 790));
        getContentPane().setBackground(new java.awt.Color(248, 249, 250));

        jPanel1.setBackground(new java.awt.Color(248, 249, 250));
        jPanel1.setPreferredSize(new java.awt.Dimension(1000, 760));

        pnlFormulario.setBackground(java.awt.Color.WHITE);
        pnlFormulario.setBorder(
                new javax.swing.border.LineBorder(
                        new java.awt.Color(179, 179, 179),
                        1,
                        true
                )
        );

        pnlPesquisa.setBackground(java.awt.Color.WHITE);

        pnlPesquisa.setBorder(
                javax.swing.BorderFactory.createLineBorder(
                        new java.awt.Color(179, 179, 179)
                )
        );

        java.awt.Font tituloFont
                = new java.awt.Font(
                        "Segoe UI",
                        java.awt.Font.BOLD,
                        12
                );

        lblTituloFormulario.setFont(tituloFont);
        lblTituloFormulario.setForeground(
                new java.awt.Color(0, 78, 174)
        );
        lblTituloFormulario.setText(
                "Cadastro / Edição de Movimentação"
        );

        lblTituloPesquisa.setFont(tituloFont);
        lblTituloPesquisa.setForeground(
                new java.awt.Color(0, 78, 174)
        );
        lblTituloPesquisa.setText("Pesquisa");

        lblTituloLista.setFont(tituloFont);
        lblTituloLista.setForeground(
                new java.awt.Color(0, 78, 174)
        );
        lblTituloLista.setText("Lista de Movimentações");

        lblBuscar.setFont(tituloFont);
        lblBuscar.setText("Buscar:");

        txtBuscar.setText("Digite para buscar...");

        pnlCampos.setLayout(
                new java.awt.GridLayout(
                        0,
                        2,
                        12,
                        10
                )
        );

        lblCampo1.setFont(tituloFont);
        lblCampo1.setText("Equipamento:");
        pnlCampos.add(lblCampo1);
        pnlCampos.add(txtCampo1);

        lblCampo2.setFont(tituloFont);
        lblCampo2.setText("Funcionário:");
        pnlCampos.add(lblCampo2);
        pnlCampos.add(txtCampo2);

        lblCampo3.setFont(tituloFont);
        lblCampo3.setText("Usuário da retirada:");
        pnlCampos.add(lblCampo3);
        pnlCampos.add(txtCampo3);

        lblCampo4.setFont(tituloFont);
        lblCampo4.setText("Usuário da devolução:");
        pnlCampos.add(lblCampo4);
        pnlCampos.add(txtCampo4);

        lblCampo5.setFont(tituloFont);
        lblCampo5.setText("Status:");
        pnlCampos.add(lblCampo5);
        pnlCampos.add(txtCampo5);

        lblCampo6.setFont(tituloFont);
        lblCampo6.setText("Data retirada:");
        pnlCampos.add(lblCampo6);
        pnlCampos.add(txtCampo6);

        lblCampo7.setFont(tituloFont);
        lblCampo7.setText("Data devolução:");
        pnlCampos.add(lblCampo7);
        pnlCampos.add(txtCampo7);

        lblCampo8.setFont(tituloFont);
        lblCampo8.setText("Devolução prevista:");
        pnlCampos.add(lblCampo8);
        pnlCampos.add(txtCampo8);

        lblCampo9.setFont(tituloFont);
        lblCampo9.setText("Finalidade:");
        pnlCampos.add(lblCampo9);
        pnlCampos.add(txtCampo9);

        lblCampo10.setFont(tituloFont);
        lblCampo10.setText("Observação retirada:");
        pnlCampos.add(lblCampo10);
        pnlCampos.add(txtCampo10);

        lblCampo11.setFont(tituloFont);
        lblCampo11.setText("Observação devolução:");
        pnlCampos.add(lblCampo11);
        pnlCampos.add(txtCampo11);


        btnNovo.setBackground(
                new java.awt.Color(0, 78, 174)
        );
        btnNovo.setFont(tituloFont);
        btnNovo.setForeground(java.awt.Color.WHITE);
        btnNovo.setText("+ Novo");
        btnNovo.setPreferredSize(
                new java.awt.Dimension(86, 38)
        );

        btnSalvar.setBackground(
                new java.awt.Color(53, 158, 77)
        );
        btnSalvar.setFont(tituloFont);
        btnSalvar.setForeground(java.awt.Color.WHITE);
        btnSalvar.setText("Salvar");
        btnSalvar.setPreferredSize(
                new java.awt.Dimension(87, 38)
        );

        btnEditar.setBackground(
                new java.awt.Color(228, 146, 10)
        );
        btnEditar.setFont(tituloFont);
        btnEditar.setForeground(java.awt.Color.WHITE);
        btnEditar.setText("Editar");
        btnEditar.setPreferredSize(
                new java.awt.Dimension(95, 38)
        );

        btnExcluir.setBackground(
                new java.awt.Color(214, 62, 50)
        );
        btnExcluir.setFont(tituloFont);
        btnExcluir.setForeground(java.awt.Color.WHITE);
        btnExcluir.setText("Excluir");
        btnExcluir.setPreferredSize(
                new java.awt.Dimension(96, 38)
        );

        tblDados.setModel(
                new DefaultTableModel(
                        new Object[][]{},
                        new String[]{
                            "ID",
                            "Equipamento",
                            "Funcionário",
                            "Status",
                            "Retirada",
                            "Devolução prevista",
                            "Devolução"
                        }
                ) {
                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {
                        return false;
                    }
                }
        );

        scrollDados.setViewportView(tblDados);

        javax.swing.GroupLayout formularioLayout
                = new javax.swing.GroupLayout(
                        pnlFormulario
                );

        pnlFormulario.setLayout(formularioLayout);

        formularioLayout.setHorizontalGroup(
                formularioLayout
                        .createSequentialGroup()
                        .addGap(14)
                        .addGroup(
                                formularioLayout
                                        .createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.LEADING
                                        )
                                        .addComponent(
                                                pnlCampos,
                                                0,
                                                665,
                                                Short.MAX_VALUE
                                        )
                                        .addGroup(
                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                formularioLayout
                                                        .createSequentialGroup()
                                                        .addComponent(
                                                                btnNovo,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                86,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE
                                                        )
                                                        .addGap(12)
                                                        .addComponent(
                                                                btnSalvar,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                87,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE
                                                        )
                                                        .addGap(12)
                                                        .addComponent(
                                                                btnEditar,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                95,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE
                                                        )
                                                        .addGap(8)
                                                        .addComponent(
                                                                btnExcluir,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                96,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE
                                                        )
                                        )
                        )
                        .addGap(14)
        );

        formularioLayout.setVerticalGroup(
                formularioLayout
                        .createSequentialGroup()
                        .addGap(14)
                        .addComponent(
                                pnlCampos,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                250,
                                javax.swing.GroupLayout.PREFERRED_SIZE
                        )
                        .addGap(12)
                        .addGroup(
                                formularioLayout
                                        .createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.BASELINE
                                        )
                                        .addComponent(
                                                btnNovo,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                38,
                                                javax.swing.GroupLayout.PREFERRED_SIZE
                                        )
                                        .addComponent(
                                                btnSalvar,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                38,
                                                javax.swing.GroupLayout.PREFERRED_SIZE
                                        )
                                        .addComponent(
                                                btnEditar,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                38,
                                                javax.swing.GroupLayout.PREFERRED_SIZE
                                        )
                                        .addComponent(
                                                btnExcluir,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                38,
                                                javax.swing.GroupLayout.PREFERRED_SIZE
                                        )
                        )
                        .addGap(14)
        );

        javax.swing.GroupLayout pesquisaLayout
                = new javax.swing.GroupLayout(
                        pnlPesquisa
                );

        pnlPesquisa.setLayout(pesquisaLayout);

        pesquisaLayout.setHorizontalGroup(
                pesquisaLayout
                        .createSequentialGroup()
                        .addGap(16)
                        .addComponent(
                                lblBuscar,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                62,
                                javax.swing.GroupLayout.PREFERRED_SIZE
                        )
                        .addGap(18)
                        .addComponent(
                                txtBuscar,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                486,
                                javax.swing.GroupLayout.PREFERRED_SIZE
                        )
                        .addGap(16)
        );

        pesquisaLayout.setVerticalGroup(
                pesquisaLayout
                        .createSequentialGroup()
                        .addGap(14)
                        .addGroup(
                                pesquisaLayout
                                        .createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.BASELINE
                                        )
                                        .addComponent(lblBuscar)
                                        .addComponent(
                                                txtBuscar,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE
                                        )
                        )
                        .addGap(14)
        );

        javax.swing.GroupLayout painelLayout
                = new javax.swing.GroupLayout(
                        jPanel1
                );

        jPanel1.setLayout(painelLayout);

        painelLayout.setHorizontalGroup(
                painelLayout
                        .createSequentialGroup()
                        .addGap(25)
                        .addGroup(
                                painelLayout
                                        .createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.LEADING
                                        )
                                        .addComponent(lblTituloFormulario)
                                        .addComponent(
                                                pnlFormulario,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE
                                        )
                                        .addComponent(lblTituloPesquisa)
                                        .addComponent(
                                                pnlPesquisa,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE
                                        )
                                        .addComponent(lblTituloLista)
                                        .addComponent(
                                                scrollDados,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE
                                        )
                        )
                        .addGap(25)
        );

        painelLayout.setVerticalGroup(
                painelLayout
                        .createSequentialGroup()
                        .addGap(31)
                        .addComponent(lblTituloFormulario)
                        .addGap(6)
                        .addComponent(
                                pnlFormulario,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE
                        )
                        .addGap(18)
                        .addComponent(lblTituloPesquisa)
                        .addGap(6)
                        .addComponent(
                                pnlPesquisa,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE
                        )
                        .addGap(20)
                        .addComponent(lblTituloLista)
                        .addGap(6)
                        .addComponent(
                                scrollDados,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                189,
                                javax.swing.GroupLayout.PREFERRED_SIZE
                        )
                        .addGap(20)
        );

        javax.swing.GroupLayout layout
                = new javax.swing.GroupLayout(
                        getContentPane()
                );

        getContentPane().setLayout(layout);

        layout.setHorizontalGroup(
                layout
                        .createSequentialGroup()
                        .addGap(14)
                        .addComponent(
                                jPanel1,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE
                        )
                        .addGap(15)
        );

        layout.setVerticalGroup(
                layout
                        .createSequentialGroup()
                        .addComponent(
                                jPanel1,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE
                        )
        );

        pack();
        setLocationRelativeTo(null);
    }
    // </editor-fold>//GEN-END:initComponents

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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblCampo1;
    private javax.swing.JLabel lblCampo2;
    private javax.swing.JLabel lblCampo3;
    private javax.swing.JLabel lblCampo4;
    private javax.swing.JLabel lblCampo5;
    private javax.swing.JLabel lblCampo6;
    private javax.swing.JLabel lblCampo7;
    private javax.swing.JLabel lblCampo8;
    private javax.swing.JLabel lblCampo9;
    private javax.swing.JLabel lblCampo10;
    private javax.swing.JLabel lblCampo11;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblTituloFormulario;
    private javax.swing.JLabel lblTituloLista;
    private javax.swing.JLabel lblTituloPesquisa;
    private javax.swing.JPanel pnlCampos;
    private javax.swing.JPanel pnlFormulario;
    private javax.swing.JPanel pnlPesquisa;
    private javax.swing.JScrollPane scrollDados;
    private javax.swing.JTable tblDados;
    private javax.swing.JTextField txtCampo1;
    private javax.swing.JTextField txtCampo2;
    private javax.swing.JTextField txtCampo3;
    private javax.swing.JTextField txtCampo4;
    private javax.swing.JTextField txtCampo5;
    private javax.swing.JTextField txtCampo6;
    private javax.swing.JTextField txtCampo7;
    private javax.swing.JTextField txtCampo8;
    private javax.swing.JTextField txtCampo9;
    private javax.swing.JTextField txtCampo10;
    private javax.swing.JTextField txtCampo11;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables
}
