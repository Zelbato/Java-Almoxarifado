/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.almoxarifado.Dao;

import com.mycompany.almoxarifado.Dto.ManutencaoDTO;
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
public class ManutencaoDAO {

    public void cadastrar(ManutencaoDTO manutencao) throws SQLException {

        String sql = """
                 INSERT INTO manutencao (
                     id_equipamento,
                     id_usuario,
                     id_tipo_manutencao,
                     id_status_manutencao,
                     descricao_problema,
                     servico_realizado,
                     tecnico_responsavel,
                     data_entrada,
                     data_prevista_conclusao,
                     data_conclusao,
                     observacao
                 )
                 VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                 """;

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, manutencao.getIdEquipamento());
            statement.setLong(2, manutencao.getIdUsuario());
            statement.setLong(3, manutencao.getIdTipoManutencao());
            statement.setLong(4, manutencao.getIdStatusManutencao());

            statement.setString(5, manutencao.getDescricaoProblema());
            statement.setString(6, manutencao.getServicoRealizado());
            statement.setString(7, manutencao.getTecnicoResponsavel());

            statement.setTimestamp(
                    8,
                    Timestamp.valueOf(manutencao.getDataEntrada())
            );

            if (manutencao.getDataPrevistaConclusao() != null) {
                statement.setTimestamp(
                        9,
                        Timestamp.valueOf(manutencao.getDataPrevistaConclusao())
                );
            } else {
                statement.setNull(9, java.sql.Types.TIMESTAMP);
            }

            if (manutencao.getDataConclusao() != null) {
                statement.setTimestamp(
                        10,
                        Timestamp.valueOf(manutencao.getDataConclusao())
                );
            } else {
                statement.setNull(10, java.sql.Types.TIMESTAMP);
            }

            statement.setString(11, manutencao.getObservacao());

            statement.executeUpdate();
        }
    }

    public List<ManutencaoDTO> listarTodos() throws SQLException {

        String sql = """
                 SELECT
                     id_manutencao,
                     id_equipamento,
                     id_usuario,
                     id_tipo_manutencao,
                     id_status_manutencao,
                     descricao_problema,
                     servico_realizado,
                     tecnico_responsavel,
                     data_entrada,
                     data_prevista_conclusao,
                     data_conclusao,
                     observacao
                 FROM manutencao
                 ORDER BY id_manutencao ASC
                 """;

        List<ManutencaoDTO> manutencoes = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                manutencoes.add(converterResultSet(resultSet));
            }
        }

        return manutencoes;
    }

    public ManutencaoDTO buscarPorId(Long id) throws SQLException {

        String sql = """
                 SELECT
                     id_manutencao,
                     id_equipamento,
                     id_usuario,
                     id_tipo_manutencao,
                     id_status_manutencao,
                     descricao_problema,
                     servico_realizado,
                     tecnico_responsavel,
                     data_entrada,
                     data_prevista_conclusao,
                     data_conclusao,
                     observacao
                 FROM manutencao
                 WHERE id_manutencao = ?
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

    public void atualizar(ManutencaoDTO manutencao) throws SQLException {

        String sql = """
                 UPDATE manutencao
                 SET id_equipamento = ?,
                     id_usuario = ?,
                     id_tipo_manutencao = ?,
                     id_status_manutencao = ?,
                     descricao_problema = ?,
                     servico_realizado = ?,
                     tecnico_responsavel = ?,
                     data_entrada = ?,
                     data_prevista_conclusao = ?,
                     data_conclusao = ?,
                     observacao = ?
                 WHERE id_manutencao = ?
                 """;

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, manutencao.getIdEquipamento());
            statement.setLong(2, manutencao.getIdUsuario());
            statement.setLong(3, manutencao.getIdTipoManutencao());
            statement.setLong(4, manutencao.getIdStatusManutencao());

            statement.setString(5, manutencao.getDescricaoProblema());
            statement.setString(6, manutencao.getServicoRealizado());
            statement.setString(7, manutencao.getTecnicoResponsavel());

            statement.setTimestamp(
                    8,
                    Timestamp.valueOf(manutencao.getDataEntrada())
            );

            if (manutencao.getDataPrevistaConclusao() != null) {
                statement.setTimestamp(
                        9,
                        Timestamp.valueOf(manutencao.getDataPrevistaConclusao())
                );
            } else {
                statement.setNull(9, java.sql.Types.TIMESTAMP);
            }

            if (manutencao.getDataConclusao() != null) {
                statement.setTimestamp(
                        10,
                        Timestamp.valueOf(manutencao.getDataConclusao())
                );
            } else {
                statement.setNull(10, java.sql.Types.TIMESTAMP);
            }

            statement.setString(11, manutencao.getObservacao());
            statement.setLong(12, manutencao.getIdManutencao());

            statement.executeUpdate();
        }
    }

    public void excluir(Long id) throws SQLException {

        String sql = """
                 DELETE FROM manutencao
                 WHERE id_manutencao = ?
                 """;

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    private ManutencaoDTO converterResultSet(
            ResultSet resultSet
    ) throws SQLException {

        ManutencaoDTO manutencao = new ManutencaoDTO();

        manutencao.setIdManutencao(
                resultSet.getLong("id_manutencao")
        );

        manutencao.setIdEquipamento(
                resultSet.getLong("id_equipamento")
        );

        manutencao.setIdUsuario(
                resultSet.getLong("id_usuario")
        );

        manutencao.setIdTipoManutencao(
                resultSet.getLong("id_tipo_manutencao")
        );

        manutencao.setIdStatusManutencao(
                resultSet.getLong("id_status_manutencao")
        );

        manutencao.setDescricaoProblema(
                resultSet.getString("descricao_problema")
        );

        manutencao.setServicoRealizado(
                resultSet.getString("servico_realizado")
        );

        manutencao.setTecnicoResponsavel(
                resultSet.getString("tecnico_responsavel")
        );

        Timestamp dataEntrada
                = resultSet.getTimestamp("data_entrada");

        if (dataEntrada != null) {
            manutencao.setDataEntrada(
                    dataEntrada.toLocalDateTime()
            );
        }

        Timestamp dataPrevistaConclusao
                = resultSet.getTimestamp("data_prevista_conclusao");

        if (dataPrevistaConclusao != null) {
            manutencao.setDataPrevistaConclusao(
                    dataPrevistaConclusao.toLocalDateTime()
            );
        }

        Timestamp dataConclusao
                = resultSet.getTimestamp("data_conclusao");

        if (dataConclusao != null) {
            manutencao.setDataConclusao(
                    dataConclusao.toLocalDateTime()
            );
        }

        manutencao.setObservacao(
                resultSet.getString("observacao")
        );

        return manutencao;
    }

}
