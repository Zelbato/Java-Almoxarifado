/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.almoxarifado.Dao;

import com.mycompany.almoxarifado.Dto.TipoOcorrenciaDTO;
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
public class TipoOcorrenciaDAO {

    public List<TipoOcorrenciaDTO> listarTodos() throws SQLException {

        String sql = """
                 SELECT
                     id_tipo_ocorrencia,
                     nome
                 FROM tipo_ocorrencia
                 ORDER BY id_tipo_ocorrencia ASC
                 """;

        List<TipoOcorrenciaDTO> tiposOcorrencia = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                tiposOcorrencia.add(converterResultSet(resultSet));
            }
        }

        return tiposOcorrencia;
    }

    public TipoOcorrenciaDTO buscarPorId(Long id) throws SQLException {

        String sql = """
                 SELECT
                     id_tipo_ocorrencia,
                     nome
                 FROM tipo_ocorrencia
                 WHERE id_tipo_ocorrencia = ?
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

    public TipoOcorrenciaDTO buscarPorNome(String nome) throws SQLException {

        String sql = """
                 SELECT
                     id_tipo_ocorrencia,
                     nome
                 FROM tipo_ocorrencia
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

    private TipoOcorrenciaDTO converterResultSet(
            ResultSet resultSet
    ) throws SQLException {

        TipoOcorrenciaDTO tipoOcorrencia
                = new TipoOcorrenciaDTO();

        tipoOcorrencia.setIdTipoOcorrencia(
                resultSet.getLong("id_tipo_ocorrencia")
        );

        tipoOcorrencia.setNome(
                resultSet.getString("nome")
        );

        return tipoOcorrencia;
    }

}
