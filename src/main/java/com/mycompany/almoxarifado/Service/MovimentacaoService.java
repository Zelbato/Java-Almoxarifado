package com.mycompany.almoxarifado.Service;

import com.mycompany.almoxarifado.Dao.EquipamentoDAO;
import com.mycompany.almoxarifado.Dao.FuncionarioDAO;
import com.mycompany.almoxarifado.Dao.MovimentacaoDAO;
import com.mycompany.almoxarifado.Dao.OcorrenciaDAO;
import com.mycompany.almoxarifado.Dao.StatusMovimentacaoDAO;
import com.mycompany.almoxarifado.Dao.UsuarioDAO;
import com.mycompany.almoxarifado.Dto.MovimentacaoDTO;
import com.mycompany.almoxarifado.config.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author heito
 */
public class MovimentacaoService {

    private final MovimentacaoDAO movimentacaoDAO;
    private final EquipamentoDAO equipamentoDAO;
    private final FuncionarioDAO funcionarioDAO;
    private final UsuarioDAO usuarioDAO;
    private final StatusMovimentacaoDAO statusMovimentacaoDAO;
    private final OcorrenciaDAO ocorrenciaDAO;

    public MovimentacaoService() {
        this.movimentacaoDAO = new MovimentacaoDAO();
        this.equipamentoDAO = new EquipamentoDAO();
        this.funcionarioDAO = new FuncionarioDAO();
        this.usuarioDAO = new UsuarioDAO();
        this.statusMovimentacaoDAO = new StatusMovimentacaoDAO();
        this.ocorrenciaDAO = new OcorrenciaDAO();
    }

    public void cadastrar(MovimentacaoDTO movimentacao) throws SQLException {
        AuthSession.exigirEscrita();

        validar(movimentacao);
        String status = validarRelacionamentos(movimentacao);

        if (!status.equals("EM_ABERTO") && !status.equals("ATRASADA")) {
            throw new IllegalArgumentException(
                    "Uma nova retirada deve ser registrada com status EM_ABERTO."
            );
        }

        if (!equipamentoDAO.buscarPorId(movimentacao.getIdEquipamento()).isAtivo()) {
            throw new IllegalArgumentException(
                    "Equipamento inativo não pode ser retirado."
            );
        }

        // retirada + equipamento EMPRESTADO precisam acontecer juntos,
        // se um falhar o outro é desfeito
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                String situacao = equipamentoDAO.buscarSituacaoParaAtualizar(
                        connection,
                        movimentacao.getIdEquipamento()
                );

                if (!"DISPONIVEL".equals(situacao)) {
                    throw new IllegalArgumentException(
                            "O equipamento não está disponível para retirada. "
                            + "Situação atual: " + situacao + "."
                    );
                }

                movimentacaoDAO.cadastrar(connection, movimentacao);
                equipamentoDAO.alterarSituacao(
                        connection,
                        movimentacao.getIdEquipamento(),
                        "EMPRESTADO"
                );

                connection.commit();
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    public List<MovimentacaoDTO> listarTodos() throws SQLException {
        AuthSession.exigirAutenticacao();
        movimentacaoDAO.atualizarAtrasadas();
        return movimentacaoDAO.listarTodos();
    }

    public MovimentacaoDTO buscarPorId(Long id) throws SQLException {
        AuthSession.exigirAutenticacao();
        validarId(id);

        MovimentacaoDTO movimentacao = movimentacaoDAO.buscarPorId(id);

        if (movimentacao == null) {
            throw new IllegalArgumentException(
                    "Movimentação não encontrada."
            );
        }

        return movimentacao;
    }

    public void atualizar(MovimentacaoDTO movimentacao) throws SQLException {
        AuthSession.exigirEscrita();

        if (movimentacao == null) {
            throw new IllegalArgumentException(
                    "Movimentação não informada."
            );
        }

        validarId(movimentacao.getIdMovimentacao());
        validar(movimentacao);
        String statusNovo = validarRelacionamentos(movimentacao);

        MovimentacaoDTO movimentacaoExistente =
                movimentacaoDAO.buscarPorId(
                        movimentacao.getIdMovimentacao()
                );

        if (movimentacaoExistente == null) {
            throw new IllegalArgumentException(
                    "Movimentação não encontrada."
            );
        }

        String statusAnterior = statusMovimentacaoDAO
                .buscarPorId(movimentacaoExistente.getIdStatusMovimentacao())
                .getNome();

        if (statusAnterior.equals("DEVOLVIDA")
                || statusAnterior.equals("CANCELADA")) {
            throw new IllegalArgumentException(
                    "Esta movimentação já foi encerrada e não pode mais ser alterada."
            );
        }

        if (!movimentacaoExistente.getIdEquipamento()
                .equals(movimentacao.getIdEquipamento())) {
            throw new IllegalArgumentException(
                    "Não é possível trocar o equipamento de uma retirada. "
                    + "Cancele esta movimentação e registre uma nova."
            );
        }

        boolean encerrando = statusNovo.equals("DEVOLVIDA")
                || statusNovo.equals("CANCELADA");

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                String situacao = equipamentoDAO.buscarSituacaoParaAtualizar(
                        connection,
                        movimentacao.getIdEquipamento()
                );

                movimentacaoDAO.atualizar(connection, movimentacao);

                // só mexe no equipamento se ele ainda está como EMPRESTADO,
                // pra não sobrescrever uma situação colocada pelo chefe
                if (encerrando && "EMPRESTADO".equals(situacao)) {
                    String situacaoFinal = "DISPONIVEL";

                    if (statusNovo.equals("DEVOLVIDA")
                            && ocorrenciaDAO.existeDanoAbertoNaMovimentacao(
                                    connection,
                                    movimentacao.getIdMovimentacao()
                            )) {
                        situacaoFinal = "DANIFICADO";
                    }

                    equipamentoDAO.alterarSituacao(
                            connection,
                            movimentacao.getIdEquipamento(),
                            situacaoFinal
                    );
                }

                connection.commit();
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    public void excluir(Long id) throws SQLException {
        AuthSession.exigirEscrita();

        buscarPorId(id);

        // o histórico da retirada nunca é apagado
        throw new IllegalArgumentException(
                "Movimentações não podem ser excluídas para manter o histórico. "
                + "Para anular a retirada, altere o status para CANCELADA."
        );
    }

    private void validar(MovimentacaoDTO movimentacao) {
        if (movimentacao == null) {
            throw new IllegalArgumentException(
                    "Movimentação não informada."
            );
        }

        if (movimentacao.getIdEquipamento() == null
                || movimentacao.getIdEquipamento() <= 0) {
            throw new IllegalArgumentException(
                    "O equipamento é obrigatório."
            );
        }

        if (movimentacao.getIdFuncionario() == null
                || movimentacao.getIdFuncionario() <= 0) {
            throw new IllegalArgumentException(
                    "O funcionário é obrigatório."
            );
        }

        if (movimentacao.getIdUsuarioRetirada() == null
                || movimentacao.getIdUsuarioRetirada() <= 0) {
            throw new IllegalArgumentException(
                    "O usuário responsável pela retirada é obrigatório."
            );
        }

        if (movimentacao.getIdUsuarioDevolucao() != null
                && movimentacao.getIdUsuarioDevolucao() <= 0) {
            throw new IllegalArgumentException(
                    "ID do usuário responsável pela devolução inválido."
            );
        }

        if (movimentacao.getIdStatusMovimentacao() == null
                || movimentacao.getIdStatusMovimentacao() <= 0) {
            throw new IllegalArgumentException(
                    "O status da movimentação é obrigatório."
            );
        }

        if (movimentacao.getDataRetirada() == null) {
            throw new IllegalArgumentException(
                    "A data de retirada é obrigatória."
            );
        }

        if (movimentacao.getDataPrevistaDevolucao() == null) {
            throw new IllegalArgumentException(
                    "A data prevista de devolução é obrigatória."
            );
        }

        if (movimentacao.getDataPrevistaDevolucao()
                .isBefore(movimentacao.getDataRetirada())) {
            throw new IllegalArgumentException(
                    "A data prevista de devolução não pode ser anterior à data de retirada."
            );
        }

        if (movimentacao.getDataDevolucao() != null
                && movimentacao.getDataDevolucao()
                        .isBefore(movimentacao.getDataRetirada())) {
            throw new IllegalArgumentException(
                    "A data de devolução não pode ser anterior à data de retirada."
            );
        }

        if (movimentacao.getFinalidade() != null
                && movimentacao.getFinalidade().length() > 255) {
            throw new IllegalArgumentException(
                    "A finalidade deve possuir no máximo 255 caracteres."
            );
        }

        if (movimentacao.getObservacaoRetirada() != null
                && movimentacao.getObservacaoRetirada().length() > 500) {
            throw new IllegalArgumentException(
                    "A observação da retirada deve possuir no máximo 500 caracteres."
            );
        }

        if (movimentacao.getObservacaoDevolucao() != null
                && movimentacao.getObservacaoDevolucao().length() > 500) {
            throw new IllegalArgumentException(
                    "A observação da devolução deve possuir no máximo 500 caracteres."
            );
        }
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "ID da movimentação inválido."
            );
        }
    }

    // devolve o nome do status escolhido, usado no cadastrar/atualizar
    private String validarRelacionamentos(
            MovimentacaoDTO movimentacao
    ) throws SQLException {
        if (equipamentoDAO.buscarPorId(
                movimentacao.getIdEquipamento()
        ) == null) {
            throw new IllegalArgumentException(
                    "Equipamento não encontrado."
            );
        }

        if (funcionarioDAO.buscarPorId(
                movimentacao.getIdFuncionario()
        ) == null) {
            throw new IllegalArgumentException(
                    "Funcionário não encontrado."
            );
        }

        if (usuarioDAO.buscarPorId(
                movimentacao.getIdUsuarioRetirada()
        ) == null) {
            throw new IllegalArgumentException(
                    "Usuário responsável pela retirada não encontrado."
            );
        }

        var status = statusMovimentacaoDAO.buscarPorId(
                movimentacao.getIdStatusMovimentacao()
        );

        if (status == null) {
            throw new IllegalArgumentException(
                    "Status da movimentação não encontrado."
            );
        }

        validarDevolucaoPorStatus(movimentacao, status.getNome());

        if (movimentacao.getIdUsuarioDevolucao() != null
                && usuarioDAO.buscarPorId(
                        movimentacao.getIdUsuarioDevolucao()
                ) == null) {
            throw new IllegalArgumentException(
                    "Usuário responsável pela devolução não encontrado."
            );
        }

        return status.getNome().trim().toUpperCase();
    }

    private void validarDevolucaoPorStatus(
            MovimentacaoDTO movimentacao,
            String nomeStatus
    ) {
        boolean devolvida = "DEVOLVIDA".equalsIgnoreCase(
                nomeStatus == null ? "" : nomeStatus.trim()
        );

        boolean possuiUsuarioDevolucao =
                movimentacao.getIdUsuarioDevolucao() != null;

        boolean possuiDataDevolucao =
                movimentacao.getDataDevolucao() != null;

        if (devolvida) {
            if (!possuiUsuarioDevolucao || !possuiDataDevolucao) {
                throw new IllegalArgumentException(
                        "Uma movimentação devolvida exige data e usuário da devolução."
                );
            }
            return;
        }

        if (possuiUsuarioDevolucao || possuiDataDevolucao) {
            throw new IllegalArgumentException(
                    "Usuário e data de devolução só podem ser informados quando o status for DEVOLVIDA."
            );
        }
    }
}
