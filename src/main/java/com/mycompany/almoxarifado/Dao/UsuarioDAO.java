/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.almoxarifado.Dao;

import com.mycompany.almoxarifado.Dto.UsuárioDTO;
import com.mycompany.almoxarifado.config.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author heito
 */
public class UsuarioDAO {

    public void cadastrar(UsuárioDTO usuario) throws SQLException {

        String sql = """
                 INSERT INTO usuario (
                     fk_funcionario,
                     id_tipo_usuario,
                     nome,
                     email,
                     senha,
                     cpf,
                     ativo
                 )
                 VALUES (?, ?, ?, ?, ?, ?, ?)
                 """;

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            if (usuario.getIdFuncionario() != null) {
                statement.setLong(1, usuario.getIdFuncionario());
            } else {
                statement.setNull(1, java.sql.Types.BIGINT);
            }

            statement.setLong(2, usuario.getIdTipoUsuario());
            statement.setString(3, usuario.getNome());
            statement.setString(4, usuario.getEmail());
            statement.setString(5, usuario.getSenha());
            statement.setString(6, usuario.getCpf());
            statement.setBoolean(7, usuario.isAtivo());

            statement.executeUpdate();
        }
    }

    public List<UsuárioDTO> listarTodos() throws SQLException {

        String sql = """
                 SELECT
                     id_usuario,
                     fk_funcionario,
                     id_tipo_usuario,
                     nome,
                     email,
                     senha,
                     cpf,
                     ativo
                 FROM usuario
                 ORDER BY id_usuario ASC
                 """;

        List<UsuárioDTO> usuarios = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                usuarios.add(converterResultSet(resultSet));
            }
        }

        return usuarios;
    }

    public UsuárioDTO buscarPorId(Long id) throws SQLException {

        String sql = """
                 SELECT
                     id_usuario,
                     fk_funcionario,
                     id_tipo_usuario,
                     nome,
                     email,
                     senha,
                     cpf,
                     ativo
                 FROM usuario
                 WHERE id_usuario = ?
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

    public void atualizar(UsuárioDTO usuario) throws SQLException {

        String sql = """
                 UPDATE usuario
                 SET fk_funcionario = ?,
                     id_tipo_usuario = ?,
                     nome = ?,
                     email = ?,
                     senha = ?,
                     cpf = ?,
                     ativo = ?
                 WHERE id_usuario = ?
                 """;

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            if (usuario.getIdFuncionario() != null) {
                statement.setLong(1, usuario.getIdFuncionario());
            } else {
                statement.setNull(1, java.sql.Types.BIGINT);
            }

            statement.setLong(2, usuario.getIdTipoUsuario());
            statement.setString(3, usuario.getNome());
            statement.setString(4, usuario.getEmail());
            statement.setString(5, usuario.getSenha());
            statement.setString(6, usuario.getCpf());
            statement.setBoolean(7, usuario.isAtivo());
            statement.setLong(8, usuario.getIdUsuario());

            statement.executeUpdate();
        }
    }

    public void excluir(Long id) throws SQLException {

        String sql = """
                 DELETE FROM usuario
                 WHERE id_usuario = ?
                 """;

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            statement.executeUpdate();
        }
    }

    public boolean existePorEmail(String email) throws SQLException {

        String sql = """
                 SELECT COUNT(*)
                 FROM usuario
                 WHERE LOWER(email) = LOWER(?)
                 """;

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    public boolean existePorCpf(String cpf) throws SQLException {

        String sql = """
                 SELECT COUNT(*)
                 FROM usuario
                 WHERE cpf = ?
                 """;

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, cpf);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    public boolean existePorEmailDiferenteDoId(
            String email,
            Long idUsuario
    ) throws SQLException {

        String sql = """
                 SELECT COUNT(*)
                 FROM usuario
                 WHERE LOWER(email) = LOWER(?)
                   AND id_usuario <> ?
                 """;

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email);
            statement.setLong(2, idUsuario);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    public boolean existePorCpfDiferenteDoId(
            String cpf,
            Long idUsuario
    ) throws SQLException {

        String sql = """
                 SELECT COUNT(*)
                 FROM usuario
                 WHERE cpf = ?
                   AND id_usuario <> ?
                 """;

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, cpf);
            statement.setLong(2, idUsuario);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    private UsuárioDTO converterResultSet(
            ResultSet resultSet
    ) throws SQLException {

        UsuárioDTO usuario = new UsuárioDTO();

        usuario.setIdUsuario(
                resultSet.getLong("id_usuario")
        );

        Long idFuncionario
                = resultSet.getObject("fk_funcionario", Long.class);

        usuario.setIdFuncionario(idFuncionario);

        usuario.setIdTipoUsuario(
                resultSet.getLong("id_tipo_usuario")
        );

        usuario.setNome(
                resultSet.getString("nome")
        );

        usuario.setEmail(
                resultSet.getString("email")
        );

        usuario.setSenha(
                resultSet.getString("senha")
        );

        usuario.setCpf(
                resultSet.getString("cpf")
        );

        usuario.setAtivo(
                resultSet.getBoolean("ativo")
        );

        return usuario;
    }
}
