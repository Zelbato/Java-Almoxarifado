/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.almoxarifado.Dao;

import com.mycompany.almoxarifado.Dto.StatusMovimentacaoDTO;
import com.mycompany.almoxarifado.config.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author heito
 */
public class StatusMovimentacaoDAO {

    public List<StatusMovimentacaoDTO> listarTodos() throws SQLException {

        String sql = """
                 SELECT
                     id_status_movimentacao,
                     nome
                 FROM status_movimentacao
                 ORDER BY id_status_movimentacao ASC
                 """;

        List<StatusMovimentacaoDTO> statusMovimentacoes = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                statusMovimentacoes.add(converterResultSet(resultSet));
            }
        }

        return statusMovimentacoes;
    }

    public StatusMovimentacaoDTO buscarPorId(Long id) throws SQLException {

        String sql = """
                 SELECT
                     id_status_movimentacao,
                     nome
                 FROM status_movimentacao
                 WHERE id_status_movimentacao = ?
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

    public StatusMovimentacaoDTO buscarPorNome(String nome) throws SQLException {

        String sql = """
                 SELECT
                     id_status_movimentacao,
                     nome
                 FROM status_movimentacao
                 WHERE nome = ?
                 """;

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, nome);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return converterResultSet(resultSet);
                }
            }
        }

        return null;
    }

    private StatusMovimentacaoDTO converterResultSet(
            ResultSet resultSet
    ) throws SQLException {

        StatusMovimentacaoDTO statusMovimentacao
                = new StatusMovimentacaoDTO();

        statusMovimentacao.setIdStatusMovimentacao(
                resultSet.getLong("id_status_movimentacao")
        );

        statusMovimentacao.setNome(
                resultSet.getString("nome")
        );

        return statusMovimentacao;
    }

}
