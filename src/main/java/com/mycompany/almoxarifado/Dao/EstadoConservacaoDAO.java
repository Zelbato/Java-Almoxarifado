/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.almoxarifado.Dao;

import com.mycompany.almoxarifado.Dto.EstadoConservacaoDTO;
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
public class EstadoConservacaoDAO {

    public List<EstadoConservacaoDTO> listarTodos() throws SQLException {

        String sql = """
                 SELECT
                     id_estado_conservacao,
                     nome
                 FROM estado_conservacao
                 ORDER BY id_estado_conservacao ASC
                 """;

        List<EstadoConservacaoDTO> estadosConservacao = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                estadosConservacao.add(converterResultSet(resultSet));
            }
        }

        return estadosConservacao;
    }

    public EstadoConservacaoDTO buscarPorId(Long id) throws SQLException {

        String sql = """
                 SELECT
                     id_estado_conservacao,
                     nome
                 FROM estado_conservacao
                 WHERE id_estado_conservacao = ?
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

    public EstadoConservacaoDTO buscarPorNome(String nome) throws SQLException {

        String sql = """
                 SELECT
                     id_estado_conservacao,
                     nome
                 FROM estado_conservacao
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

    private EstadoConservacaoDTO converterResultSet(
            ResultSet resultSet
    ) throws SQLException {

        EstadoConservacaoDTO estadoConservacao
                = new EstadoConservacaoDTO();

        estadoConservacao.setIdEstadoConservacao(
                resultSet.getLong("id_estado_conservacao")
        );

        estadoConservacao.setNome(
                resultSet.getString("nome")
        );

        return estadoConservacao;
    }

}
