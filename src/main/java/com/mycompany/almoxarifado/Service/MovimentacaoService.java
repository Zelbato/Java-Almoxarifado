package com.mycompany.almoxarifado.Service;

import com.mycompany.almoxarifado.Dao.EquipamentoDAO;
import com.mycompany.almoxarifado.Dao.FuncionarioDAO;
import com.mycompany.almoxarifado.Dao.MovimentacaoDAO;
import com.mycompany.almoxarifado.Dao.StatusMovimentacaoDAO;
import com.mycompany.almoxarifado.Dao.UsuarioDAO;
import com.mycompany.almoxarifado.Dto.MovimentacaoDTO;

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

    public MovimentacaoService() {
        this.movimentacaoDAO = new MovimentacaoDAO();
        this.equipamentoDAO = new EquipamentoDAO();
        this.funcionarioDAO = new FuncionarioDAO();
        this.usuarioDAO = new UsuarioDAO();
        this.statusMovimentacaoDAO = new StatusMovimentacaoDAO();
    }

    public void cadastrar(MovimentacaoDTO movimentacao) throws SQLException {
        AuthSession.exigirEscrita();

        validar(movimentacao);
        validarRelacionamentos(movimentacao);

        movimentacaoDAO.cadastrar(movimentacao);
    }

    public List<MovimentacaoDTO> listarTodos() throws SQLException {
        return movimentacaoDAO.listarTodos();
    }

    public MovimentacaoDTO buscarPorId(Long id) throws SQLException {

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
        validarRelacionamentos(movimentacao);

        MovimentacaoDTO movimentacaoExistente =
                movimentacaoDAO.buscarPorId(
                        movimentacao.getIdMovimentacao()
                );

        if (movimentacaoExistente == null) {
            throw new IllegalArgumentException(
                    "Movimentação não encontrada."
            );
        }

        movimentacaoDAO.atualizar(movimentacao);
    }

    public void excluir(Long id) throws SQLException {
        AuthSession.exigirEscrita();

        validarId(id);

        MovimentacaoDTO movimentacao =
                movimentacaoDAO.buscarPorId(id);

        if (movimentacao == null) {
            throw new IllegalArgumentException(
                    "Movimentação não encontrada."
            );
        }

        movimentacaoDAO.excluir(id);
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

        if (movimentacao.getDataDevolucao() != null
                && movimentacao.getIdUsuarioDevolucao() == null) {

            throw new IllegalArgumentException(
                    "Informe o usuário responsável pela devolução."
            );
        }

        if (movimentacao.getIdUsuarioDevolucao() != null
                && movimentacao.getDataDevolucao() == null) {

            throw new IllegalArgumentException(
                    "Informe a data de devolução."
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

    private void validarRelacionamentos(
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

        if (movimentacao.getIdUsuarioDevolucao() != null
                && usuarioDAO.buscarPorId(
                        movimentacao.getIdUsuarioDevolucao()
                ) == null) {

            throw new IllegalArgumentException(
                    "Usuário responsável pela devolução não encontrado."
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
    }

    private void validarDevolucaoPorStatus(
            MovimentacaoDTO movimentacao,
            String nomeStatus
    ) {
        boolean devolvida = "DEVOLVIDA".equals(nomeStatus);
        boolean possuiDadosDevolucao = movimentacao.getDataDevolucao() != null
                && movimentacao.getIdUsuarioDevolucao() != null;

        if (devolvida && !possuiDadosDevolucao) {
            throw new IllegalArgumentException(
                    "Uma movimentação devolvida exige data e usuário da devolução."
            );
        }

        if (!devolvida && possuiDadosDevolucao) {
            throw new IllegalArgumentException(
                    "Dados de devolução só podem ser informados para movimentações devolvidas."
            );
        }
    }
}
