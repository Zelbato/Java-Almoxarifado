/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.almoxarifado.Dao;

import com.mycompany.almoxarifado.Dto.OcorrenciaDTO;
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
public class OcorrenciaDAO {

    public void cadastrar(OcorrenciaDTO ocorrencia) throws SQLException {

        String sql = """
                 INSERT INTO ocorrencia (
                     id_equipamento,
                     id_movimentacao,
                     id_funcionario,
                     id_usuario_registro,
                     id_tipo_ocorrencia,
                     id_status_ocorrencia,
                     titulo,
                     descricao,
                     data_ocorrencia,
                     solucao,
                     data_resolucao
                 )
                 VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                 """;

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, ocorrencia.getIdEquipamento());

            if (ocorrencia.getIdMovimentacao() != null) {
                statement.setLong(2, ocorrencia.getIdMovimentacao());
            } else {
                statement.setNull(2, java.sql.Types.BIGINT);
            }

            if (ocorrencia.getIdFuncionario() != null) {
                statement.setLong(3, ocorrencia.getIdFuncionario());
            } else {
                statement.setNull(3, java.sql.Types.BIGINT);
            }

            statement.setLong(4, ocorrencia.getIdUsuarioRegistro());
            statement.setLong(5, ocorrencia.getIdTipoOcorrencia());
            statement.setLong(6, ocorrencia.getIdStatusOcorrencia());

            statement.setString(7, ocorrencia.getTitulo());
            statement.setString(8, ocorrencia.getDescricao());

            statement.setTimestamp(
                    9,
                    Timestamp.valueOf(ocorrencia.getDataOcorrencia())
            );

            statement.setString(10, ocorrencia.getSolucao());

            if (ocorrencia.getDataResolucao() != null) {
                statement.setTimestamp(
                        11,
                        Timestamp.valueOf(ocorrencia.getDataResolucao())
                );
            } else {
                statement.setNull(11, java.sql.Types.TIMESTAMP);
            }

            statement.executeUpdate();
        }
    }

    public List<OcorrenciaDTO> listarTodos() throws SQLException {

        String sql = """
                 SELECT
                     id_ocorrencia,
                     id_equipamento,
                     id_movimentacao,
                     id_funcionario,
                     id_usuario_registro,
                     id_tipo_ocorrencia,
                     id_status_ocorrencia,
                     titulo,
                     descricao,
                     data_ocorrencia,
                     solucao,
                     data_resolucao
                 FROM ocorrencia
                 ORDER BY id_ocorrencia ASC
                 """;

        List<OcorrenciaDTO> ocorrencias = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                ocorrencias.add(converterResultSet(resultSet));
            }
        }

        return ocorrencias;
    }

    public OcorrenciaDTO buscarPorId(Long id) throws SQLException {

        String sql = """
                 SELECT
                     id_ocorrencia,
                     id_equipamento,
                     id_movimentacao,
                     id_funcionario,
                     id_usuario_registro,
                     id_tipo_ocorrencia,
                     id_status_ocorrencia,
                     titulo,
                     descricao,
                     data_ocorrencia,
                     solucao,
                     data_resolucao
                 FROM ocorrencia
                 WHERE id_ocorrencia = ?
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

    public void atualizar(OcorrenciaDTO ocorrencia) throws SQLException {

        String sql = """
                 UPDATE ocorrencia
                 SET id_equipamento = ?,
                     id_movimentacao = ?,
                     id_funcionario = ?,
                     id_usuario_registro = ?,
                     id_tipo_ocorrencia = ?,
                     id_status_ocorrencia = ?,
                     titulo = ?,
                     descricao = ?,
                     data_ocorrencia = ?,
                     solucao = ?,
                     data_resolucao = ?
                 WHERE id_ocorrencia = ?
                 """;

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, ocorrencia.getIdEquipamento());

            if (ocorrencia.getIdMovimentacao() != null) {
                statement.setLong(2, ocorrencia.getIdMovimentacao());
            } else {
                statement.setNull(2, java.sql.Types.BIGINT);
            }

            if (ocorrencia.getIdFuncionario() != null) {
                statement.setLong(3, ocorrencia.getIdFuncionario());
            } else {
                statement.setNull(3, java.sql.Types.BIGINT);
            }

            statement.setLong(4, ocorrencia.getIdUsuarioRegistro());
            statement.setLong(5, ocorrencia.getIdTipoOcorrencia());
            statement.setLong(6, ocorrencia.getIdStatusOcorrencia());

            statement.setString(7, ocorrencia.getTitulo());
            statement.setString(8, ocorrencia.getDescricao());

            statement.setTimestamp(
                    9,
                    Timestamp.valueOf(ocorrencia.getDataOcorrencia())
            );

            statement.setString(10, ocorrencia.getSolucao());

            if (ocorrencia.getDataResolucao() != null) {
                statement.setTimestamp(
                        11,
                        Timestamp.valueOf(ocorrencia.getDataResolucao())
                );
            } else {
                statement.setNull(11, java.sql.Types.TIMESTAMP);
            }

            statement.setLong(12, ocorrencia.getIdOcorrencia());

            statement.executeUpdate();
        }
    }

    public void excluir(Long id) throws SQLException {

        String sql = """
                 DELETE FROM ocorrencia
                 WHERE id_ocorrencia = ?
                 """;

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    /**
     * Usado na devolução: se a retirada tem ocorrência de DANO ou DEFEITO
     * ainda não resolvida, o equipamento volta como DANIFICADO.
     */
    public boolean existeDanoAbertoNaMovimentacao(
            Connection connection,
            Long idMovimentacao
    ) throws SQLException {

        String sql = """
                 SELECT COUNT(*)
                 FROM ocorrencia o
                 INNER JOIN tipo_ocorrencia t
                     ON t.id_tipo_ocorrencia = o.id_tipo_ocorrencia
                 INNER JOIN status_ocorrencia s
                     ON s.id_status_ocorrencia = o.id_status_ocorrencia
                 WHERE o.id_movimentacao = ?
                 AND t.nome IN ('DANO', 'DEFEITO')
                 AND s.nome IN ('ABERTA', 'EM_ANALISE')
                 """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, idMovimentacao);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() && resultSet.getLong(1) > 0;
            }
        }
    }

    private OcorrenciaDTO converterResultSet(
            ResultSet resultSet
    ) throws SQLException {

        OcorrenciaDTO ocorrencia = new OcorrenciaDTO();

        ocorrencia.setIdOcorrencia(
                resultSet.getLong("id_ocorrencia")
        );

        ocorrencia.setIdEquipamento(
                resultSet.getLong("id_equipamento")
        );

        Long idMovimentacao
                = resultSet.getObject("id_movimentacao", Long.class);

        ocorrencia.setIdMovimentacao(idMovimentacao);

        Long idFuncionario
                = resultSet.getObject("id_funcionario", Long.class);

        ocorrencia.setIdFuncionario(idFuncionario);

        ocorrencia.setIdUsuarioRegistro(
                resultSet.getLong("id_usuario_registro")
        );

        ocorrencia.setIdTipoOcorrencia(
                resultSet.getLong("id_tipo_ocorrencia")
        );

        ocorrencia.setIdStatusOcorrencia(
                resultSet.getLong("id_status_ocorrencia")
        );

        ocorrencia.setTitulo(
                resultSet.getString("titulo")
        );

        ocorrencia.setDescricao(
                resultSet.getString("descricao")
        );

        Timestamp dataOcorrencia
                = resultSet.getTimestamp("data_ocorrencia");

        if (dataOcorrencia != null) {
            ocorrencia.setDataOcorrencia(
                    dataOcorrencia.toLocalDateTime()
            );
        }

        ocorrencia.setSolucao(
                resultSet.getString("solucao")
        );

        Timestamp dataResolucao
                = resultSet.getTimestamp("data_resolucao");

        if (dataResolucao != null) {
            ocorrencia.setDataResolucao(
                    dataResolucao.toLocalDateTime()
            );
        }

        return ocorrencia;
    }

}
