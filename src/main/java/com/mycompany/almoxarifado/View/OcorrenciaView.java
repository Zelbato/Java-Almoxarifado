package com.mycompany.almoxarifado.View;

import com.mycompany.almoxarifado.Dto.OcorrenciaDTO;
import com.mycompany.almoxarifado.Service.OcorrenciaService;
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

/** Formulário JFrame do NetBeans para ocorrências. */
public class OcorrenciaView extends javax.swing.JFrame {
    private final OcorrenciaService service = new OcorrenciaService();
    private final Map<String, JTextField> campos = new LinkedHashMap<>();
    private Long idSelecionado;
    private JComboBox<ComboItem> cmbEquipamento, cmbMovimentacao, cmbFuncionario, cmbUsuarioRegistro, cmbTipoOcorrencia, cmbStatus;
    private JDateChooser calDataOcorrencia, calDataResolucao;
    private JSpinner spnHoraOcorrencia, spnHoraResolucao;
    private JPanel pnlDataOcorrencia, pnlDataResolucao;

    public OcorrenciaView() {
        AuthSession.exigirAutenticacao();
        initComponents();
        montarCampos();
        configurarSelects();
        configurarDatas();
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
        cmbStatus.addActionListener(e -> atualizarCamposResolucao());
        tblDados.getSelectionModel().addListSelectionListener(this::selecionarLinha);
    }
    private void aplicarPermissoes() { boolean permitido = AuthSession.podeEscrever(); btnNovo.setEnabled(permitido); btnSalvar.setEnabled(permitido); btnEditar.setEnabled(false); btnExcluir.setEnabled(false); }
    private OcorrenciaDTO lerFormulario() {
        OcorrenciaDTO dto = new OcorrenciaDTO();
        dto.setIdEquipamento(ViewInputSupport.id(cmbEquipamento, "O equipamento", true));
        dto.setIdMovimentacao(ViewInputSupport.id(cmbMovimentacao, "A movimentação", false));
        dto.setIdFuncionario(ViewInputSupport.id(cmbFuncionario, "O funcionário", false));
        dto.setIdUsuarioRegistro(ViewInputSupport.id(cmbUsuarioRegistro, "O usuário do registro", true));
        dto.setIdTipoOcorrencia(ViewInputSupport.id(cmbTipoOcorrencia, "O tipo de ocorrência", true));
        dto.setIdStatusOcorrencia(ViewInputSupport.id(cmbStatus, "O status", true));
        dto.setTitulo(ViewInputSupport.texto(campos.get("Título")));
        dto.setDescricao(ViewInputSupport.texto(campos.get("Descrição")));
        dto.setDataOcorrencia(lerDataHora(calDataOcorrencia, spnHoraOcorrencia, "A data da ocorrência", true));
        if (statusResolvidaSelecionado()) {
            dto.setSolucao(ViewInputSupport.texto(campos.get("Solução")));
            dto.setDataResolucao(
                    lerDataHora(
                            calDataResolucao,
                            spnHoraResolucao,
                            "A data de resolução",
                            false
                    )
            );
        } else {
            dto.setSolucao(null);
            dto.setDataResolucao(null);
        }
        return dto;
    }

    private void cadastrar() { try { service.cadastrar(lerFormulario()); sucesso("Ocorrência cadastrada com sucesso."); } catch (IllegalArgumentException | SQLException ex) { erro(ex); } }
    private void editar() { if (idSelecionado == null) { aviso("Selecione a ocorrência para editar."); return; } try { OcorrenciaDTO dto = lerFormulario(); dto.setIdOcorrencia(idSelecionado); service.atualizar(dto); sucesso("Ocorrência atualizada com sucesso."); } catch (IllegalArgumentException | SQLException ex) { erro(ex); } }
    private void excluir() { if (idSelecionado == null) { aviso("Selecione a ocorrência para excluir."); return; } if (JOptionPane.showConfirmDialog(this, "Deseja excluir a ocorrência selecionada?", "Confirmação", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return; try { service.excluir(idSelecionado); sucesso("Ocorrência excluída com sucesso."); } catch (IllegalArgumentException | SQLException ex) { erro(ex); } }
    private void carregarDados() { try { DefaultTableModel modelo = (DefaultTableModel) tblDados.getModel(); modelo.setRowCount(0); for (OcorrenciaDTO dto : service.listarTodos()) modelo.addRow(new Object[]{dto.getIdOcorrencia(), dto.getTitulo(), dto.getIdEquipamento(), dto.getIdTipoOcorrencia(), dto.getIdStatusOcorrencia(), dto.getDataOcorrencia(), dto.getDataResolucao()}); } catch (SQLException ex) { erro(ex); } }
    private void selecionarLinha(ListSelectionEvent evento) { if (evento.getValueIsAdjusting() || tblDados.getSelectedRow() < 0) return; int linha = tblDados.convertRowIndexToModel(tblDados.getSelectedRow()); Long id = Long.valueOf(tblDados.getModel().getValueAt(linha, 0).toString()); try { preencher(service.buscarPorId(id)); idSelecionado = id; btnSalvar.setEnabled(false); boolean permitido = AuthSession.podeEscrever(); btnEditar.setEnabled(permitido); btnExcluir.setEnabled(permitido); } catch (SQLException | IllegalArgumentException ex) { erro(ex); } }
    private void preencher(OcorrenciaDTO dto) {
        ViewInputSupport.selecionar(cmbEquipamento, dto.getIdEquipamento());
        ViewInputSupport.selecionar(cmbMovimentacao, dto.getIdMovimentacao());
        ViewInputSupport.selecionar(cmbFuncionario, dto.getIdFuncionario());
        ViewInputSupport.selecionar(cmbUsuarioRegistro, dto.getIdUsuarioRegistro());
        ViewInputSupport.selecionar(cmbTipoOcorrencia, dto.getIdTipoOcorrencia());
        ViewInputSupport.selecionar(cmbStatus, dto.getIdStatusOcorrencia());
        preencher("Título", dto.getTitulo());
        preencher("Descrição", dto.getDescricao());
        preencherDataHora(calDataOcorrencia, spnHoraOcorrencia, dto.getDataOcorrencia());
        preencher("Solução", dto.getSolucao());
        preencherDataHora(calDataResolucao, spnHoraResolucao, dto.getDataResolucao());
        atualizarCamposResolucao();
    }

    private void preencher(String campo, Object valor) { ViewInputSupport.preencher(campos.get(campo), valor); }
    private void limparFormulario() {
        idSelecionado = null;
        campos.values().forEach(c -> c.setText(""));
        ViewInputSupport.selecionar(cmbEquipamento, null);
        ViewInputSupport.selecionar(cmbMovimentacao, null);
        ViewInputSupport.selecionar(cmbFuncionario, null);
        ViewInputSupport.selecionar(cmbUsuarioRegistro, null);
        ViewInputSupport.selecionar(cmbTipoOcorrencia, null);
        ViewInputSupport.selecionar(cmbStatus, null);
        calDataOcorrencia.setDate(null);
        calDataResolucao.setDate(null);
        Date agora = new Date();
        spnHoraOcorrencia.setValue(agora);
        spnHoraResolucao.setValue(agora);
        tblDados.clearSelection();
        boolean permitido = AuthSession.podeEscrever();
        btnSalvar.setEnabled(permitido);
        btnEditar.setEnabled(false);
        btnExcluir.setEnabled(false);
        atualizarCamposResolucao();
    }

    private void sucesso(String mensagem) { JOptionPane.showMessageDialog(this, mensagem); limparFormulario(); carregarDados(); }
    private void aviso(String mensagem) { JOptionPane.showMessageDialog(this, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE); }
    private void erro(Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE); }

    private void montarCampos() {
        campos.put("Título", txtCampo7);
        campos.put("Descrição", txtCampo8);
        campos.put("Solução", txtCampo10);
    }

    private void configurarSelects() {
        cmbEquipamento = new JComboBox<>(); cmbMovimentacao = new JComboBox<>(); cmbFuncionario = new JComboBox<>(); cmbUsuarioRegistro = new JComboBox<>(); cmbTipoOcorrencia = new JComboBox<>(); cmbStatus = new JComboBox<>();
        ViewInputSupport.substituirNoPainel(pnlCampos, txtCampo1, cmbEquipamento); ViewInputSupport.substituirNoPainel(pnlCampos, txtCampo2, cmbMovimentacao); ViewInputSupport.substituirNoPainel(pnlCampos, txtCampo3, cmbFuncionario); ViewInputSupport.substituirNoPainel(pnlCampos, txtCampo4, cmbUsuarioRegistro); ViewInputSupport.substituirNoPainel(pnlCampos, txtCampo5, cmbTipoOcorrencia); ViewInputSupport.substituirNoPainel(pnlCampos, txtCampo6, cmbStatus);
        try {
            for (com.mycompany.almoxarifado.Dto.EquipamentoDTO item : new com.mycompany.almoxarifado.Service.EquipamentoService().listarTodos()) cmbEquipamento.addItem(new ComboItem(item.getIdEquipamento(), item.getNome()));
            for (com.mycompany.almoxarifado.Dto.MovimentacaoDTO item : new com.mycompany.almoxarifado.Service.MovimentacaoService().listarTodos()) cmbMovimentacao.addItem(new ComboItem(item.getIdMovimentacao(), "Movimentação #" + item.getIdMovimentacao()));
            for (com.mycompany.almoxarifado.Dto.FuncionarioDTO item : new com.mycompany.almoxarifado.Service.FuncionarioService().listarTodos()) cmbFuncionario.addItem(new ComboItem(item.getIdFuncionario(), item.getNome()));
            for (com.mycompany.almoxarifado.Dto.UsuarioDTO item : new com.mycompany.almoxarifado.Service.UsuarioService().listarTodos()) cmbUsuarioRegistro.addItem(new ComboItem(item.getIdUsuario(), item.getEmail()));
            for (com.mycompany.almoxarifado.Dto.TipoOcorrenciaDTO item : new com.mycompany.almoxarifado.Service.TipoOcorrenciaService().listarTodos()) cmbTipoOcorrencia.addItem(new ComboItem(item.getIdTipoOcorrencia(), item.getNome()));
            for (com.mycompany.almoxarifado.Dto.StatusOcorrenciaDTO item : new com.mycompany.almoxarifado.Service.StatusOcorrenciaService().listarTodos()) cmbStatus.addItem(new ComboItem(item.getIdStatusOcorrencia(), item.getNome()));
            cmbEquipamento.setSelectedIndex(-1); cmbMovimentacao.setSelectedIndex(-1); cmbFuncionario.setSelectedIndex(-1); cmbUsuarioRegistro.setSelectedIndex(-1); cmbTipoOcorrencia.setSelectedIndex(-1); cmbStatus.setSelectedIndex(-1);
        } catch (SQLException ex) { erro(ex); }
    }

    private boolean statusResolvidaSelecionado() {
        Object selecionado = cmbStatus.getSelectedItem();

        return selecionado != null
                && "RESOLVIDA".equalsIgnoreCase(
                        selecionado.toString().trim()
                );
    }

    private void atualizarCamposResolucao() {
        boolean resolvida = statusResolvidaSelecionado();

        txtCampo10.setEnabled(resolvida);
        calDataResolucao.setEnabled(resolvida);
        spnHoraResolucao.setEnabled(resolvida);

        lblCampo10.setEnabled(resolvida);
        lblCampo11.setEnabled(resolvida);

        if (!resolvida) {
            txtCampo10.setText("");
            calDataResolucao.setDate(null);
        }
    }

    private void configurarDatas() {
        calDataOcorrencia = criarCalendario();
        calDataResolucao = criarCalendario();

        spnHoraOcorrencia = criarSpinnerHora();
        spnHoraResolucao = criarSpinnerHora();

        pnlDataOcorrencia = criarPainelDataHora(calDataOcorrencia, spnHoraOcorrencia);
        pnlDataResolucao = criarPainelDataHora(calDataResolucao, spnHoraResolucao);
    }

    private JDateChooser criarCalendario() {
        JDateChooser calendario = new JDateChooser();
        calendario.setDateFormatString("dd/MM/yyyy");
        return calendario;
    }

    private JSpinner criarSpinnerHora() {
        SpinnerDateModel modelo = new SpinnerDateModel(
                new Date(),
                null,
                null,
                Calendar.MINUTE
        );

        JSpinner spinner = new JSpinner(modelo);
        spinner.setEditor(new JSpinner.DateEditor(spinner, "HH:mm"));
        spinner.setPreferredSize(new Dimension(82, 32));
        return spinner;
    }

    private JPanel criarPainelDataHora(JDateChooser calendario, JSpinner hora) {
        JPanel painel = new JPanel(new BorderLayout(6, 0));
        painel.setOpaque(false);
        calendario.setPreferredSize(new Dimension(180, 32));
        painel.add(calendario, BorderLayout.CENTER);
        painel.add(hora, BorderLayout.EAST);
        return painel;
    }

    private LocalDateTime lerDataHora(
            JDateChooser calendario,
            JSpinner spinnerHora,
            String nomeCampo,
            boolean obrigatorio
    ) {
        Date dataSelecionada = calendario.getDate();

        if (dataSelecionada == null) {
            if (obrigatorio) {
                throw new IllegalArgumentException(nomeCampo + " é obrigatória.");
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
                valor.atZone(ZoneId.systemDefault()).toInstant()
        );

        calendario.setDate(data);
        spinnerHora.setValue(data);
    }

    private void ajustarLayoutFormulario() {
        pnlCampos.removeAll();
        pnlCampos.setLayout(new GridBagLayout());
        pnlCampos.setBackground(java.awt.Color.WHITE);
        pnlCampos.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 12, 12));

        java.awt.Font fonte = new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12);

        javax.swing.JLabel[] labels = {
            lblCampo1, lblCampo2, lblCampo3, lblCampo4, lblCampo5, lblCampo6,
            lblCampo7, lblCampo8, lblCampo9, lblCampo10, lblCampo11
        };

        for (javax.swing.JLabel label : labels) {
            label.setFont(fonte);
        }

        adicionarCampo(lblCampo1, cmbEquipamento, 0, 0);
        adicionarCampo(lblCampo2, cmbMovimentacao, 2, 0);

        adicionarCampo(lblCampo3, cmbFuncionario, 0, 1);
        adicionarCampo(lblCampo4, cmbUsuarioRegistro, 2, 1);

        adicionarCampo(lblCampo5, cmbTipoOcorrencia, 0, 2);
        adicionarCampo(lblCampo6, cmbStatus, 2, 2);

        adicionarCampo(lblCampo7, txtCampo7, 0, 3);
        adicionarCampo(lblCampo9, pnlDataOcorrencia, 2, 3);

        adicionarCampo(lblCampo8, txtCampo8, 0, 4);
        adicionarCampo(lblCampo10, txtCampo10, 2, 4);

        adicionarCampo(lblCampo11, pnlDataResolucao, 0, 5);

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
        pnlFormulario.add(pnlCampos, BorderLayout.CENTER);
        pnlFormulario.add(pnlAcoes, BorderLayout.SOUTH);
        pnlFormulario.setPreferredSize(new Dimension(950, 360));

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
        gbcLabel.insets = new Insets(7, 0, 7, 12);

        pnlCampos.add(label, gbcLabel);

        GridBagConstraints gbcCampo = new GridBagConstraints();
        gbcCampo.gridx = coluna + 1;
        gbcCampo.gridy = linha;
        gbcCampo.weightx = 1;
        gbcCampo.fill = GridBagConstraints.HORIZONTAL;
        gbcCampo.anchor = GridBagConstraints.WEST;
        gbcCampo.insets = new Insets(7, 0, 7, coluna == 0 ? 24 : 0);

        campo.setPreferredSize(new Dimension(250, 32));
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
        setTitle("Almoxarifado - Ocorrências");
        setMinimumSize(new java.awt.Dimension(1030, 790));
        getContentPane().setBackground(new java.awt.Color(248, 249, 250));

        jPanel1.setBackground(new java.awt.Color(248, 249, 250));
        jPanel1.setPreferredSize(new java.awt.Dimension(1000, 760));
        pnlFormulario.setBackground(java.awt.Color.WHITE);
        pnlFormulario.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(179, 179, 179), 1, true));
        pnlPesquisa.setBackground(java.awt.Color.WHITE);
        pnlPesquisa.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(179, 179, 179)));

        java.awt.Font tituloFont = new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12);
        lblTituloFormulario.setFont(tituloFont); lblTituloFormulario.setForeground(new java.awt.Color(0, 78, 174)); lblTituloFormulario.setText("Cadastro / Edição de Ocorrência");
        lblTituloPesquisa.setFont(tituloFont); lblTituloPesquisa.setForeground(new java.awt.Color(0, 78, 174)); lblTituloPesquisa.setText("Pesquisa");
        lblTituloLista.setFont(tituloFont); lblTituloLista.setForeground(new java.awt.Color(0, 78, 174)); lblTituloLista.setText("Lista de Ocorrências");
        lblBuscar.setFont(tituloFont); lblBuscar.setText("Buscar:");
        txtBuscar.setText("Digite para buscar...");
        pnlCampos.setLayout(new java.awt.GridLayout(0, 2, 12, 10));
        lblCampo1.setFont(tituloFont); lblCampo1.setText("Equipamento:"); pnlCampos.add(lblCampo1); pnlCampos.add(txtCampo1);
        lblCampo2.setFont(tituloFont); lblCampo2.setText("Movimentação:"); pnlCampos.add(lblCampo2); pnlCampos.add(txtCampo2);
        lblCampo3.setFont(tituloFont); lblCampo3.setText("Funcionário:"); pnlCampos.add(lblCampo3); pnlCampos.add(txtCampo3);
        lblCampo4.setFont(tituloFont); lblCampo4.setText("Usuário do registro:"); pnlCampos.add(lblCampo4); pnlCampos.add(txtCampo4);
        lblCampo5.setFont(tituloFont); lblCampo5.setText("Tipo de ocorrência:"); pnlCampos.add(lblCampo5); pnlCampos.add(txtCampo5);
        lblCampo6.setFont(tituloFont); lblCampo6.setText("Status:"); pnlCampos.add(lblCampo6); pnlCampos.add(txtCampo6);
        lblCampo7.setFont(tituloFont); lblCampo7.setText("Título:"); pnlCampos.add(lblCampo7); pnlCampos.add(txtCampo7);
        lblCampo8.setFont(tituloFont); lblCampo8.setText("Descrição:"); pnlCampos.add(lblCampo8); pnlCampos.add(txtCampo8);
        lblCampo9.setFont(tituloFont); lblCampo9.setText("Data da ocorrência:"); pnlCampos.add(lblCampo9); pnlCampos.add(txtCampo9);
        lblCampo10.setFont(tituloFont); lblCampo10.setText("Solução:"); pnlCampos.add(lblCampo10); pnlCampos.add(txtCampo10);
        lblCampo11.setFont(tituloFont); lblCampo11.setText("Data de resolução:"); pnlCampos.add(lblCampo11); pnlCampos.add(txtCampo11);

        btnNovo.setBackground(new java.awt.Color(0, 78, 174)); btnNovo.setFont(tituloFont); btnNovo.setForeground(java.awt.Color.WHITE); btnNovo.setText("+ Novo"); btnNovo.setPreferredSize(new java.awt.Dimension(86, 38));
        btnSalvar.setBackground(new java.awt.Color(53, 158, 77)); btnSalvar.setFont(tituloFont); btnSalvar.setForeground(java.awt.Color.WHITE); btnSalvar.setText("Salvar"); btnSalvar.setPreferredSize(new java.awt.Dimension(87, 38));
        btnEditar.setBackground(new java.awt.Color(228, 146, 10)); btnEditar.setFont(tituloFont); btnEditar.setForeground(java.awt.Color.WHITE); btnEditar.setText("Editar"); btnEditar.setPreferredSize(new java.awt.Dimension(95, 38));
        btnExcluir.setBackground(new java.awt.Color(214, 62, 50)); btnExcluir.setFont(tituloFont); btnExcluir.setForeground(java.awt.Color.WHITE); btnExcluir.setText("Excluir"); btnExcluir.setPreferredSize(new java.awt.Dimension(96, 38));

        tblDados.setModel(new DefaultTableModel(new Object[][] {}, new String[] {"ID", "Título", "Equipamento", "Tipo", "Status", "Data", "Resolução"}) { public boolean isCellEditable(int row, int column) { return false; } });
        scrollDados.setViewportView(tblDados);

        javax.swing.GroupLayout formularioLayout = new javax.swing.GroupLayout(pnlFormulario);
        pnlFormulario.setLayout(formularioLayout);
        formularioLayout.setHorizontalGroup(formularioLayout.createSequentialGroup().addGap(14).addGroup(formularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(pnlCampos, 0, 665, Short.MAX_VALUE).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, formularioLayout.createSequentialGroup().addComponent(btnNovo, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(12).addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(12).addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(8).addComponent(btnExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(14)));
        formularioLayout.setVerticalGroup(formularioLayout.createSequentialGroup().addGap(14).addComponent(pnlCampos, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(12).addGroup(formularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(btnNovo, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(btnExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(14));

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
