/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.almoxarifado.Dao;

import com.mycompany.almoxarifado.Dto.CategoriaDTO;
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
public class CategoriaDAO {

    public void cadastrar(CategoriaDTO categoria) throws SQLException {
        String sql = """
                     INSERT INTO categoria (nome, descricao, ativo)
                     VALUES (?, ?, ?)
                     """;

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, categoria.getNome());
            statement.setString(2, categoria.getDescricao());
            statement.setBoolean(3, categoria.isAtivo());

            statement.executeUpdate();
        }
    }

    public List<CategoriaDTO> listarTodos() throws SQLException {
        String sql = """
                     SELECT id_categoria, nome, descricao, ativo
                     FROM categoria
                     ORDER BY id_categoria ASC
                     """;

        List<CategoriaDTO> categorias = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                CategoriaDTO categoria = new CategoriaDTO();

                categoria.setIdCategoria(
                        resultSet.getLong("id_categoria")
                );

                categoria.setNome(
                        resultSet.getString("nome")
                );

                categoria.setDescricao(
                        resultSet.getString("descricao")
                );

                categoria.setAtivo(
                        resultSet.getBoolean("ativo")
                );

                categorias.add(categoria);
            }
        }

        return categorias;
    }

    public CategoriaDTO buscarPorId(Long id) throws SQLException {
        String sql = """
                     SELECT id_categoria, nome, descricao, ativo
                     FROM categoria
                     WHERE id_categoria = ?
                     """;

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    CategoriaDTO categoria = new CategoriaDTO();

                    categoria.setIdCategoria(
                            resultSet.getLong("id_categoria")
                    );

                    categoria.setNome(
                            resultSet.getString("nome")
                    );

                    categoria.setDescricao(
                            resultSet.getString("descricao")
                    );

                    categoria.setAtivo(
                            resultSet.getBoolean("ativo")
                    );

                    return categoria;
                }
            }
        }

        return null;
    }

    public void atualizar(CategoriaDTO categoria) throws SQLException {
        String sql = """
                     UPDATE categoria
                     SET nome = ?,
                         descricao = ?,
                         ativo = ?
                     WHERE id_categoria = ?
                     """;

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, categoria.getNome());
            statement.setString(2, categoria.getDescricao());
            statement.setBoolean(3, categoria.isAtivo());
            statement.setLong(4, categoria.getIdCategoria());

            statement.executeUpdate();
        }
    }

    public void excluir(Long id) throws SQLException {
        String sql = """
                     DELETE FROM categoria
                     WHERE id_categoria = ?
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
                     FROM categoria
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

    public boolean existePorNomeDiferenteDoId(
            String nome,
            Long idCategoria
    ) throws SQLException {
        String sql = """
                     SELECT COUNT(*)
                     FROM categoria
                     WHERE LOWER(nome) = LOWER(?)
                       AND id_categoria <> ?
                     """;

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nome);
            statement.setLong(2, idCategoria);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        }
    }
}
