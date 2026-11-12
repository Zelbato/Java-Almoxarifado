package com.mycompany.almoxarifado.Service;

import com.mycompany.almoxarifado.Dao.EquipamentoDAO;
import com.mycompany.almoxarifado.Dao.ManutencaoDAO;
import com.mycompany.almoxarifado.Dao.StatusManutencaoDAO;
import com.mycompany.almoxarifado.Dao.TipoManutencaoDAO;
import com.mycompany.almoxarifado.Dao.UsuarioDAO;
import com.mycompany.almoxarifado.Dto.ManutencaoDTO;
import com.mycompany.almoxarifado.config.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ManutencaoService {

    private final ManutencaoDAO manutencaoDAO;
    private final EquipamentoDAO equipamentoDAO;
    private final UsuarioDAO usuarioDAO;
    private final TipoManutencaoDAO tipoManutencaoDAO;
    private final StatusManutencaoDAO statusManutencaoDAO;

    public ManutencaoService() {
        manutencaoDAO = new ManutencaoDAO();
        equipamentoDAO = new EquipamentoDAO();
        usuarioDAO = new UsuarioDAO();
        tipoManutencaoDAO = new TipoManutencaoDAO();
        statusManutencaoDAO = new StatusManutencaoDAO();
    }

    public void cadastrar(ManutencaoDTO manutencao) throws SQLException {
        AuthSession.exigirEscrita();
        validar(manutencao);
        String status = validarRelacionamentos(manutencao);
        boolean emAndamento = status.equals("EM_ANDAMENTO");

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                String situacao = equipamentoDAO.buscarSituacaoParaAtualizar(connection, manutencao.getIdEquipamento());
                if (emAndamento) {
                    validarEntradaEmManutencao(situacao);
                }

                manutencaoDAO.cadastrar(connection, manutencao);

                if (emAndamento) {
                    equipamentoDAO.alterarSituacao(connection, manutencao.getIdEquipamento(), "EM_MANUTENCAO");
                }
                connection.commit();
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    public List<ManutencaoDTO> listarTodos() throws SQLException {
        AuthSession.exigirAutenticacao();
        return manutencaoDAO.listarTodos();
    }

    public ManutencaoDTO buscarPorId(Long id) throws SQLException {
        AuthSession.exigirAutenticacao();
        validarId(id);
        ManutencaoDTO manutencao = manutencaoDAO.buscarPorId(id);
        if (manutencao == null) {
            throw new IllegalArgumentException("Manutenção não encontrada.");
        }
        return manutencao;
    }

    public void atualizar(ManutencaoDTO manutencao) throws SQLException {
        AuthSession.exigirEscrita();
        if (manutencao == null) {
            throw new IllegalArgumentException("Manutenção não informada.");
        }

        validarId(manutencao.getIdManutencao());
        validar(manutencao);
        String statusNovo = validarRelacionamentos(manutencao);

        ManutencaoDTO existente = manutencaoDAO.buscarPorId(manutencao.getIdManutencao());
        if (existente == null) {
            throw new IllegalArgumentException("Manutenção não encontrada.");
        }

        String statusAnterior = statusManutencaoDAO.buscarPorId(existente.getIdStatusManutencao()).getNome();

        if ((statusAnterior.equals("CONCLUIDA") || statusAnterior.equals("CANCELADA"))
                && !statusNovo.equals(statusAnterior)) {
            throw new IllegalArgumentException("Manutenção encerrada não pode ser reaberta. Registre uma nova manutenção.");
        }
        if (!existente.getIdEquipamento().equals(manutencao.getIdEquipamento())) {
            throw new IllegalArgumentException("Não é possível trocar o equipamento de uma manutenção já registrada.");
        }

        boolean entrando = statusNovo.equals("EM_ANDAMENTO") && !statusAnterior.equals("EM_ANDAMENTO");
        boolean saindo = statusAnterior.equals("EM_ANDAMENTO") && !statusNovo.equals("EM_ANDAMENTO");

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                String situacao = equipamentoDAO.buscarSituacaoParaAtualizar(connection, manutencao.getIdEquipamento());
                if (entrando) {
                    validarEntradaEmManutencao(situacao);
                }

                manutencaoDAO.atualizar(connection, manutencao);

                if (entrando) {
                    equipamentoDAO.alterarSituacao(connection, manutencao.getIdEquipamento(), "EM_MANUTENCAO");
                } else if (saindo && "EM_MANUTENCAO".equals(situacao)) {
                    equipamentoDAO.alterarSituacao(connection, manutencao.getIdEquipamento(), "DISPONIVEL");
                }
                connection.commit();
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    public void excluir(Long id) throws SQLException {
        AuthSession.exigirChefe();
        ManutencaoDTO manutencao = buscarPorId(id);

        String status = statusManutencaoDAO.buscarPorId(manutencao.getIdStatusManutencao()).getNome();
        if (status.equals("EM_ANDAMENTO")) {
            throw new IllegalArgumentException("Conclua ou cancele a manutenção antes de excluir.");
        }
        manutencaoDAO.excluir(id);
    }

    // emprestado não pode ir pra manutenção, tem que ser devolvido antes
    private void validarEntradaEmManutencao(String situacao) {
        if (!"DISPONIVEL".equals(situacao) && !"DANIFICADO".equals(situacao)) {
            throw new IllegalArgumentException(
                    "O equipamento precisa estar DISPONIVEL ou DANIFICADO para entrar em manutenção. Situação atual: " + situacao + ".");
        }
    }

    private void validar(ManutencaoDTO manutencao) {
        if (manutencao == null) {
            throw new IllegalArgumentException("Manutenção não informada.");
        }

        validarIdObrigatorio(manutencao.getIdEquipamento(), "O equipamento");
        validarIdObrigatorio(manutencao.getIdUsuario(), "O usuário responsável");
        validarIdObrigatorio(manutencao.getIdTipoManutencao(), "O tipo de manutenção");
        validarIdObrigatorio(manutencao.getIdStatusManutencao(), "O status da manutenção");
        validarTextoObrigatorio(manutencao.getDescricaoProblema(), 500, "A descrição do problema");
        validarTextoOpcional(manutencao.getServicoRealizado(), 500, "O serviço realizado");
        validarTextoOpcional(manutencao.getTecnicoResponsavel(), 150, "O técnico responsável");
        validarTextoOpcional(manutencao.getObservacao(), 500, "A observação");

        if (manutencao.getDataEntrada() == null) {
            throw new IllegalArgumentException("A data de entrada é obrigatória.");
        }
        if (manutencao.getDataPrevistaConclusao() != null
                && manutencao.getDataPrevistaConclusao().isBefore(manutencao.getDataEntrada())) {
            throw new IllegalArgumentException("A previsão de conclusão não pode ser anterior à entrada.");
        }
        if (manutencao.getDataConclusao() != null
                && manutencao.getDataConclusao().isBefore(manutencao.getDataEntrada())) {
            throw new IllegalArgumentException("A conclusão não pode ser anterior à entrada.");
        }
    }

    private String validarRelacionamentos(ManutencaoDTO manutencao) throws SQLException {
        if (equipamentoDAO.buscarPorId(manutencao.getIdEquipamento()) == null) {
            throw new IllegalArgumentException("Equipamento não encontrado.");
        }
        if (usuarioDAO.buscarPorId(manutencao.getIdUsuario()) == null) {
            throw new IllegalArgumentException("Usuário responsável não encontrado.");
        }
        if (tipoManutencaoDAO.buscarPorId(manutencao.getIdTipoManutencao()) == null) {
            throw new IllegalArgumentException("Tipo de manutenção não encontrado.");
        }
        var status = statusManutencaoDAO.buscarPorId(manutencao.getIdStatusManutencao());

        if (status == null) {
            throw new IllegalArgumentException("Status da manutenção não encontrado.");
        }

        validarConclusaoPorStatus(manutencao, status.getNome());
        return status.getNome();
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID da manutenção inválido.");
        }
    }

    private void validarIdObrigatorio(Long id, String campo) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(campo + " é obrigatório.");
        }
    }

    private void validarTextoObrigatorio(String valor, int tamanhoMaximo, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " é obrigatória.");
        }
        validarTextoOpcional(valor, tamanhoMaximo, campo);
    }

    private void validarTextoOpcional(String valor, int tamanhoMaximo, String campo) {
        if (valor != null && valor.length() > tamanhoMaximo) {
            throw new IllegalArgumentException(campo + " deve possuir no máximo " + tamanhoMaximo + " caracteres.");
        }
    }

    private void validarConclusaoPorStatus(
            ManutencaoDTO manutencao,
            String nomeStatus
    ) {
        boolean concluida = "CONCLUIDA".equals(nomeStatus);

        if (concluida && manutencao.getDataConclusao() == null) {
            throw new IllegalArgumentException(
                    "Uma manutenção concluída exige data de conclusão."
            );
        }

        if (!concluida && manutencao.getDataConclusao() != null) {
            throw new IllegalArgumentException(
                    "A data de conclusão só pode ser informada para manutenção concluída."
            );
        }
    }
}
