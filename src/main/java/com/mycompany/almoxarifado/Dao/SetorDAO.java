/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.almoxarifado.Dao;

import com.mycompany.almoxarifado.config.DatabaseConnection;
import com.mycompany.almoxarifado.Dto.SetorDTO;

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
public class SetorDAO {

    public void cadastrar(SetorDTO setor) throws SQLException {
        String sql = """
                     INSERT INTO setor (nome, descricao, ativo)
                     VALUES (?,?,?)
                    """;

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, setor.getNome());
            statement.setString(2, setor.getDescricao());
            statement.setBoolean(3, setor.isAtivo());

            statement.executeUpdate();
        }
    }

    public List<SetorDTO> listarTodos() throws SQLException {
        String sql = """
                     SELECT id_setor, nome, descricao, ativo
                                  FROM setor
                                  ORDER BY id_setor ASC
                     """;

        List<SetorDTO> setores = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                SetorDTO setor = new SetorDTO();

                setor.setIdSetor(
                        resultSet.getLong("id_setor")
                );

                setor.setNome(
                        resultSet.getString("nome")
                );

                setor.setDescricao(
                        resultSet.getString("descricao")
                );

                setor.setAtivo(
                        resultSet.getBoolean("ativo")
                );

                setores.add(setor);
            }
        }
        return setores;
    }

    public SetorDTO buscarPorId(Long id) throws SQLException {
        String sql = """
                     SELECT id_setor, nome, descricao, ativo
                     FROM setor
                     WHERE id_setor = ?
                     """;

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    SetorDTO setor = new SetorDTO();

                    setor.setIdSetor(
                            resultSet.getLong("id_setor")
                    );

                    setor.setNome(
                            resultSet.getString("nome")
                    );

                    setor.setDescricao(
                            resultSet.getString("descricao")
                    );

                    setor.setAtivo(
                            resultSet.getBoolean("ativo")
                    );

                    return setor;
                }
            }
        }
        return null;
    }

    public void atualizar(SetorDTO setor) throws SQLException {

        String sql = """
            UPDATE setor
            SET nome = ?,
                descricao = ?,
                ativo = ?
            WHERE id_setor = ?
            """;

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, setor.getNome());
            statement.setString(2, setor.getDescricao());
            statement.setBoolean(3, setor.isAtivo());
            statement.setLong(4, setor.getIdSetor());

            statement.executeUpdate();
        }
    }

    public void excluir(Long id) throws SQLException {

        String sql = """
            DELETE FROM setor
            WHERE id_setor = ?
            """;

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            statement.executeUpdate();
        }

    }

    public boolean existePorNome(String nome) throws SQLException {
        String sql = """
                 SELECT COUNT(*) 
                 FROM setor
                 WHERE LOWER(nome) = LOWER(?)
                 """;

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nome);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }

        return false;
    }

}
