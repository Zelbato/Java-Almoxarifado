/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.almoxarifado.Dao;

import com.mycompany.almoxarifado.Dto.LocalizacaoDTO;
import com.mycompany.almoxarifado.config.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author heito
 */
public class LocalizacaoDAO {

    public void cadastrar(LocalizacaoDTO localizacao) throws SQLException {
        String sql = """
                     INSERT INTO localizacao (
                         nome,
                         corredor,
                         prateleira,
                         ativo
                     )
                     VALUES (?, ?, ?, ?)
                     """;

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, localizacao.getNome());

            if (localizacao.getCorredor() != null) {
                statement.setInt(2, localizacao.getCorredor());
            } else {
                statement.setNull(2, java.sql.Types.INTEGER);
            }

            if (localizacao.getPrateleira() != null) {
                statement.setInt(3, localizacao.getPrateleira());
            } else {
                statement.setNull(3, java.sql.Types.INTEGER);
            }

            statement.setBoolean(4, localizacao.isAtivo());

            statement.executeUpdate();
        }
    }

    public List<LocalizacaoDTO> listarTodos() throws SQLException {
        String sql = """
                     SELECT
                         id_localizacao,
                         nome,
                         corredor,
                         prateleira,
                         ativo
                     FROM localizacao
                     ORDER BY id_localizacao ASC
                     """;

        List<LocalizacaoDTO> localizacoes = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                LocalizacaoDTO localizacao = new LocalizacaoDTO();

                localizacao.setIdLocalizacao(
                        resultSet.getLong("id_localizacao")
                );

                localizacao.setNome(
                        resultSet.getString("nome")
                );

                int corredor = resultSet.getInt("corredor");

                if (resultSet.wasNull()) {
                    localizacao.setCorredor(null);
                } else {
                    localizacao.setCorredor(corredor);
                }

                int prateleira = resultSet.getInt("prateleira");

                if (resultSet.wasNull()) {
                    localizacao.setPrateleira(null);
                } else {
                    localizacao.setPrateleira(prateleira);
                }

                localizacao.setAtivo(
                        resultSet.getBoolean("ativo")
                );

                localizacoes.add(localizacao);
            }
        }

        return localizacoes;
    }

    public LocalizacaoDTO buscarPorId(Long id) throws SQLException {
        String sql = """
                     SELECT
                         id_localizacao,
                         nome,
                         corredor,
                         prateleira,
                         ativo
                     FROM localizacao
                     WHERE id_localizacao = ?
                     """;

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    LocalizacaoDTO localizacao = new LocalizacaoDTO();

                    localizacao.setIdLocalizacao(
                            resultSet.getLong("id_localizacao")
                    );

                    localizacao.setNome(
                            resultSet.getString("nome")
                    );

                    int corredor = resultSet.getInt("corredor");

                    if (resultSet.wasNull()) {
                        localizacao.setCorredor(null);
                    } else {
                        localizacao.setCorredor(corredor);
                    }

                    int prateleira = resultSet.getInt("prateleira");

                    if (resultSet.wasNull()) {
                        localizacao.setPrateleira(null);
                    } else {
                        localizacao.setPrateleira(prateleira);
                    }

                    localizacao.setAtivo(
                            resultSet.getBoolean("ativo")
                    );

                    return localizacao;
                }
            }
        }

        return null;
    }

    public void atualizar(LocalizacaoDTO localizacao) throws SQLException {
        String sql = """
                     UPDATE localizacao
                     SET nome = ?,
                         corredor = ?,
                         prateleira = ?,
                         ativo = ?
                     WHERE id_localizacao = ?
                     """;

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, localizacao.getNome());

            if (localizacao.getCorredor() != null) {
                statement.setInt(2, localizacao.getCorredor());
            } else {
                statement.setNull(2, java.sql.Types.INTEGER);
            }

            if (localizacao.getPrateleira() != null) {
                statement.setInt(3, localizacao.getPrateleira());
            } else {
                statement.setNull(3, java.sql.Types.INTEGER);
            }

            statement.setBoolean(4, localizacao.isAtivo());
            statement.setLong(5, localizacao.getIdLocalizacao());

            statement.executeUpdate();
        }
    }

    public void excluir(Long id) throws SQLException {
        String sql = """
                     DELETE FROM localizacao
                     WHERE id_localizacao = ?
                     """;

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            statement.executeUpdate();
        }
    }

    public boolean existe(
            String nome,
            Integer corredor,
            Integer prateleira
    ) throws SQLException {

        String sql = """
                     SELECT COUNT(*)
                     FROM localizacao
                     WHERE LOWER(nome) = LOWER(?)
                       AND (
                           corredor = ?
                           OR (corredor IS NULL AND ? IS NULL)
                       )
                       AND (
                           prateleira = ?
                           OR (prateleira IS NULL AND ? IS NULL)
                       )
                     """;

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nome);

            if (corredor != null) {
                statement.setInt(2, corredor);
                statement.setInt(3, corredor);
            } else {
                statement.setNull(2, java.sql.Types.INTEGER);
                statement.setNull(3, java.sql.Types.INTEGER);
            }

            if (prateleira != null) {
                statement.setInt(4, prateleira);
                statement.setInt(5, prateleira);
            } else {
                statement.setNull(4, java.sql.Types.INTEGER);
                statement.setNull(5, java.sql.Types.INTEGER);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }

        return false;
    }
}
