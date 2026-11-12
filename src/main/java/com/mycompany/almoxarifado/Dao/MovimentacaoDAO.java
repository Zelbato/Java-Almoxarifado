/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.almoxarifado.Dao;

import com.mycompany.almoxarifado.Dto.MovimentacaoDTO;
import com.mycompany.almoxarifado.config.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author heito
 */
public class MovimentacaoDAO {

    // Recebe a conexão de fora porque a retirada roda na mesma transação
    // que muda a situação do equipamento (ver MovimentacaoService).
    public void cadastrar(
            Connection connection,
            MovimentacaoDTO movimentacao
    ) throws SQLException {

        String sql = """
                 INSERT INTO movimentacao (
                     id_equipamento,
                     id_funcionario,
                     id_usuario_retirada,
                     id_usuario_devolucao,
                     id_status_movimentacao,
                     data_retirada,
                     data_devolucao,
                     data_prevista_devolucao,
                     finalidade,
                     observacao_retirada,
                     observacao_devolucao
                 )
                 VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                 """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, movimentacao.getIdEquipamento());
            statement.setLong(2, movimentacao.getIdFuncionario());
            statement.setLong(3, movimentacao.getIdUsuarioRetirada());

            if (movimentacao.getIdUsuarioDevolucao() != null) {
                statement.setLong(4, movimentacao.getIdUsuarioDevolucao());
            } else {
                statement.setNull(4, java.sql.Types.BIGINT);
            }

            statement.setLong(5, movimentacao.getIdStatusMovimentacao());

            statement.setTimestamp(
                    6,
                    Timestamp.valueOf(movimentacao.getDataRetirada())
            );

            if (movimentacao.getDataDevolucao() != null) {
                statement.setTimestamp(
                        7,
                        Timestamp.valueOf(movimentacao.getDataDevolucao())
                );
            } else {
                statement.setNull(7, java.sql.Types.TIMESTAMP);
            }

            statement.setTimestamp(
                    8,
                    Timestamp.valueOf(movimentacao.getDataPrevistaDevolucao())
            );

            statement.setString(9, movimentacao.getFinalidade());
            statement.setString(10, movimentacao.getObservacaoRetirada());
            statement.setString(11, movimentacao.getObservacaoDevolucao());

            statement.executeUpdate();
        }
    }

    public List<MovimentacaoDTO> listarTodos() throws SQLException {

        String sql = """
                 SELECT
                     id_movimentacao,
                     id_equipamento,
                     id_funcionario,
                     id_usuario_retirada,
                     id_usuario_devolucao,
                     id_status_movimentacao,
                     data_retirada,
                     data_devolucao,
                     data_prevista_devolucao,
                     finalidade,
                     observacao_retirada,
                     observacao_devolucao
                 FROM movimentacao
                 ORDER BY id_movimentacao ASC
                 """;

        List<MovimentacaoDTO> movimentacoes = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                movimentacoes.add(converterResultSet(resultSet));
            }
        }

        return movimentacoes;
    }

    public MovimentacaoDTO buscarPorId(Long id) throws SQLException {

        String sql = """
                 SELECT
                     id_movimentacao,
                     id_equipamento,
                     id_funcionario,
                     id_usuario_retirada,
                     id_usuario_devolucao,
                     id_status_movimentacao,
                     data_retirada,
                     data_devolucao,
                     data_prevista_devolucao,
                     finalidade,
                     observacao_retirada,
                     observacao_devolucao
                 FROM movimentacao
                 WHERE id_movimentacao = ?
                 """;

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return converterResultSet(resultSet);
                }
            }
        }

        return null;
    }

    public void atualizar(
            Connection connection,
            MovimentacaoDTO movimentacao
    ) throws SQLException {

        String sql = """
                 UPDATE movimentacao
                 SET id_equipamento = ?,
                     id_funcionario = ?,
                     id_usuario_retirada = ?,
                     id_usuario_devolucao = ?,
                     id_status_movimentacao = ?,
                     data_retirada = ?,
                     data_devolucao = ?,
                     data_prevista_devolucao = ?,
                     finalidade = ?,
                     observacao_retirada = ?,
                     observacao_devolucao = ?
                 WHERE id_movimentacao = ?
                 """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, movimentacao.getIdEquipamento());
            statement.setLong(2, movimentacao.getIdFuncionario());
            statement.setLong(3, movimentacao.getIdUsuarioRetirada());

            if (movimentacao.getIdUsuarioDevolucao() != null) {
                statement.setLong(4, movimentacao.getIdUsuarioDevolucao());
            } else {
                statement.setNull(4, java.sql.Types.BIGINT);
            }

            statement.setLong(5, movimentacao.getIdStatusMovimentacao());

            statement.setTimestamp(
                    6,
                    Timestamp.valueOf(movimentacao.getDataRetirada())
            );

            if (movimentacao.getDataDevolucao() != null) {
                statement.setTimestamp(
                        7,
                        Timestamp.valueOf(movimentacao.getDataDevolucao())
                );
            } else {
                statement.setNull(7, java.sql.Types.TIMESTAMP);
            }

            statement.setTimestamp(
                    8,
                    Timestamp.valueOf(movimentacao.getDataPrevistaDevolucao())
            );

            statement.setString(9, movimentacao.getFinalidade());
            statement.setString(10, movimentacao.getObservacaoRetirada());
            statement.setString(11, movimentacao.getObservacaoDevolucao());

            statement.setLong(12, movimentacao.getIdMovimentacao());

            statement.executeUpdate();
        }
    }

    // Não existe excluir: o histórico das retiradas não pode ser apagado.
    // Para anular uma retirada o status vira CANCELADA.

    /**
     * Acerta o status pelo prazo: EM_ABERTO vencida vira ATRASADA e,
     * se o prazo foi prorrogado, ATRASADA volta para EM_ABERTO.
     */
    public void atualizarAtrasadas() throws SQLException {

        String sqlAtrasar = """
                 UPDATE movimentacao
                 SET id_status_movimentacao = (
                     SELECT id_status_movimentacao FROM status_movimentacao
                     WHERE nome = 'ATRASADA'
                 )
                 WHERE id_status_movimentacao = (
                     SELECT id_status_movimentacao FROM status_movimentacao
                     WHERE nome = 'EM_ABERTO'
                 )
                 AND data_prevista_devolucao < NOW()
                 """;

        String sqlReabrir = """
                 UPDATE movimentacao
                 SET id_status_movimentacao = (
                     SELECT id_status_movimentacao FROM status_movimentacao
                     WHERE nome = 'EM_ABERTO'
                 )
                 WHERE id_status_movimentacao = (
                     SELECT id_status_movimentacao FROM status_movimentacao
                     WHERE nome = 'ATRASADA'
                 )
                 AND data_prevista_devolucao >= NOW()
                 """;

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement atrasar = connection.prepareStatement(sqlAtrasar); PreparedStatement reabrir = connection.prepareStatement(sqlReabrir)) {

            atrasar.executeUpdate();
            reabrir.executeUpdate();
        }
    }

    private MovimentacaoDTO converterResultSet(
            ResultSet resultSet
    ) throws SQLException {

        MovimentacaoDTO movimentacao = new MovimentacaoDTO();

        movimentacao.setIdMovimentacao(
                resultSet.getLong("id_movimentacao")
        );

        movimentacao.setIdEquipamento(
                resultSet.getLong("id_equipamento")
        );

        movimentacao.setIdFuncionario(
                resultSet.getLong("id_funcionario")
        );

        movimentacao.setIdUsuarioRetirada(
                resultSet.getLong("id_usuario_retirada")
        );

        Long idUsuarioDevolucao
                = resultSet.getObject(
                        "id_usuario_devolucao",
                        Long.class
                );

        movimentacao.setIdUsuarioDevolucao(
                idUsuarioDevolucao
        );

        movimentacao.setIdStatusMovimentacao(
                resultSet.getLong("id_status_movimentacao")
        );

        Timestamp dataRetirada
                = resultSet.getTimestamp("data_retirada");

        if (dataRetirada != null) {
            movimentacao.setDataRetirada(
                    dataRetirada.toLocalDateTime()
            );
        }

        Timestamp dataDevolucao
                = resultSet.getTimestamp("data_devolucao");

        if (dataDevolucao != null) {
            movimentacao.setDataDevolucao(
                    dataDevolucao.toLocalDateTime()
            );
        }

        Timestamp dataPrevistaDevolucao
                = resultSet.getTimestamp(
                        "data_prevista_devolucao"
                );

        if (dataPrevistaDevolucao != null) {
            movimentacao.setDataPrevistaDevolucao(
                    dataPrevistaDevolucao.toLocalDateTime()
            );
        }

        movimentacao.setFinalidade(
                resultSet.getString("finalidade")
        );

        movimentacao.setObservacaoRetirada(
                resultSet.getString("observacao_retirada")
        );

        movimentacao.setObservacaoDevolucao(
                resultSet.getString("observacao_devolucao")
        );

        return movimentacao;
    }
}
