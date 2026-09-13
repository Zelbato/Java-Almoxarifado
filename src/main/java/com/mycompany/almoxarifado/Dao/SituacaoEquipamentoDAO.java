/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.almoxarifado.Dao;

import com.mycompany.almoxarifado.Dto.SituacaoEquipamentoDTO;
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
public class SituacaoEquipamentoDAO {

    public List<SituacaoEquipamentoDTO> listarTodos() throws SQLException {

        String sql = """
                 SELECT
                     id_situacao_equipamento,
                     nome
                 FROM situacao_equipamento
                 ORDER BY id_situacao_equipamento ASC
                 """;

        List<SituacaoEquipamentoDTO> situacoesEquipamento = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                situacoesEquipamento.add(converterResultSet(resultSet));
            }
        }

        return situacoesEquipamento;
    }

    public SituacaoEquipamentoDTO buscarPorId(Long id) throws SQLException {

        String sql = """
                 SELECT
                     id_situacao_equipamento,
                     nome
                 FROM situacao_equipamento
                 WHERE id_situacao_equipamento = ?
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

    public SituacaoEquipamentoDTO buscarPorNome(String nome) throws SQLException {

        String sql = """
                 SELECT
                     id_situacao_equipamento,
                     nome
                 FROM situacao_equipamento
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

    private SituacaoEquipamentoDTO converterResultSet(
            ResultSet resultSet
    ) throws SQLException {

        SituacaoEquipamentoDTO situacaoEquipamento
                = new SituacaoEquipamentoDTO();

        situacaoEquipamento.setIdSituacaoEquipamento(
                resultSet.getLong("id_situacao_equipamento")
        );

        situacaoEquipamento.setNome(
                resultSet.getString("nome")
        );

        return situacaoEquipamento;
    }

}
