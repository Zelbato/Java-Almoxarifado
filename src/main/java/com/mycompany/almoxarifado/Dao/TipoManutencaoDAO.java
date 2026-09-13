/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.almoxarifado.Dao;

import com.mycompany.almoxarifado.Dto.TipoManutencaoDTO;
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
public class TipoManutencaoDAO {

    public List<TipoManutencaoDTO> listarTodos() throws SQLException {

        String sql = """
                 SELECT
                     id_tipo_manutencao,
                     nome
                 FROM tipo_manutencao
                 ORDER BY id_tipo_manutencao ASC
                 """;

        List<TipoManutencaoDTO> tiposManutencao = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                tiposManutencao.add(converterResultSet(resultSet));
            }
        }

        return tiposManutencao;
    }

    public TipoManutencaoDTO buscarPorId(Long id) throws SQLException {

        String sql = """
                 SELECT
                     id_tipo_manutencao,
                     nome
                 FROM tipo_manutencao
                 WHERE id_tipo_manutencao = ?
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

    public TipoManutencaoDTO buscarPorNome(String nome) throws SQLException {

        String sql = """
                 SELECT
                     id_tipo_manutencao,
                     nome
                 FROM tipo_manutencao
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

    private TipoManutencaoDTO converterResultSet(
            ResultSet resultSet
    ) throws SQLException {

        TipoManutencaoDTO tipoManutencao
                = new TipoManutencaoDTO();

        tipoManutencao.setIdTipoManutencao(
                resultSet.getLong("id_tipo_manutencao")
        );

        tipoManutencao.setNome(
                resultSet.getString("nome")
        );

        return tipoManutencao;
    }

}
