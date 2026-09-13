/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.almoxarifado.Dao;

import com.mycompany.almoxarifado.Dto.StatusManutencaoDTO;
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
public class StatusManutencaoDAO {

    public List<StatusManutencaoDTO> listarTodos() throws SQLException {

        String sql = """
                 SELECT
                     id_status_manutencao,
                     nome
                 FROM status_manutencao
                 ORDER BY id_status_manutencao ASC
                 """;

        List<StatusManutencaoDTO> statusManutencoes = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                statusManutencoes.add(converterResultSet(resultSet));
            }
        }

        return statusManutencoes;
    }

    public StatusManutencaoDTO buscarPorId(Long id) throws SQLException {

        String sql = """
                 SELECT
                     id_status_manutencao,
                     nome
                 FROM status_manutencao
                 WHERE id_status_manutencao = ?
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

    public StatusManutencaoDTO buscarPorNome(String nome) throws SQLException {

        String sql = """
                 SELECT
                     id_status_manutencao,
                     nome
                 FROM status_manutencao
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

    private StatusManutencaoDTO converterResultSet(
            ResultSet resultSet
    ) throws SQLException {

        StatusManutencaoDTO statusManutencao
                = new StatusManutencaoDTO();

        statusManutencao.setIdStatusManutencao(
                resultSet.getLong("id_status_manutencao")
        );

        statusManutencao.setNome(
                resultSet.getString("nome")
        );

        return statusManutencao;
    }

}
