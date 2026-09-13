/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.almoxarifado.Dao;

import com.mycompany.almoxarifado.Dto.StatusOcorrenciaDTO;
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
public class StatusOcorrenciaDAO {

    public List<StatusOcorrenciaDTO> listarTodos() throws SQLException {

        String sql = """
                 SELECT
                     id_status_ocorrencia,
                     nome
                 FROM status_ocorrencia
                 ORDER BY id_status_ocorrencia ASC
                 """;

        List<StatusOcorrenciaDTO> statusOcorrencias = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                statusOcorrencias.add(converterResultSet(resultSet));
            }
        }

        return statusOcorrencias;
    }

    public StatusOcorrenciaDTO buscarPorId(Long id) throws SQLException {

        String sql = """
                 SELECT
                     id_status_ocorrencia,
                     nome
                 FROM status_ocorrencia
                 WHERE id_status_ocorrencia = ?
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

    public StatusOcorrenciaDTO buscarPorNome(String nome) throws SQLException {

        String sql = """
                 SELECT
                     id_status_ocorrencia,
                     nome
                 FROM status_ocorrencia
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

    private StatusOcorrenciaDTO converterResultSet(
            ResultSet resultSet
    ) throws SQLException {

        StatusOcorrenciaDTO statusOcorrencia
                = new StatusOcorrenciaDTO();

        statusOcorrencia.setIdStatusOcorrencia(
                resultSet.getLong("id_status_ocorrencia")
        );

        statusOcorrencia.setNome(
                resultSet.getString("nome")
        );

        return statusOcorrencia;
    }
}
