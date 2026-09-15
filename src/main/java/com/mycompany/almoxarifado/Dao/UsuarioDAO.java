/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.almoxarifado.Dao;

import com.mycompany.almoxarifado.Dto.UsuarioDTO;
import com.mycompany.almoxarifado.Dto.UsuarioCredenciaisDTO;
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

    public void cadastrar(UsuarioDTO usuario, String senhaHash) throws SQLException {

        String sql = """
                 INSERT INTO usuario (
                     fk_funcionario,
                     id_tipo_usuario,
                     nome,
                     email,
                     senha,
                     cpf,
                     data_nascimento,
                     ativo
                 )
                 VALUES (?, ?, ?, ?, ?, ?, ?, ?)
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
            statement.setString(5, senhaHash);
            statement.setString(6, usuario.getCpf());

            if (usuario.getDataNascimento() != null) {
                statement.setDate(7, Date.valueOf(usuario.getDataNascimento()));
            } else {
                statement.setNull(7, java.sql.Types.DATE);
            }

            statement.setBoolean(8, usuario.isAtivo());

            statement.executeUpdate();
        }
    }

    public List<UsuarioDTO> listarTodos() throws SQLException {

        String sql = """
                 SELECT
                     id_usuario,
                     fk_funcionario,
                     id_tipo_usuario,
                     nome,
                     email,
                     cpf,
                     data_nascimento,
                     ativo
                 FROM usuario
                 ORDER BY id_usuario ASC
                 """;

        List<UsuarioDTO> usuarios = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                usuarios.add(converterResultSet(resultSet));
            }
        }

        return usuarios;
    }

    public UsuarioDTO buscarPorId(Long id) throws SQLException {

        String sql = """
                 SELECT
                     id_usuario,
                     fk_funcionario,
                     id_tipo_usuario,
                     nome,
                     email,
                     cpf,
                     data_nascimento,
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

    public UsuarioCredenciaisDTO buscarCredenciaisPorEmail(String email) throws SQLException {
        String sql = """
                 SELECT u.id_usuario, u.fk_funcionario, u.id_tipo_usuario,
                        u.nome, u.email, u.cpf, u.data_nascimento, u.ativo,
                        u.senha, t.nome AS tipo_usuario
                 FROM usuario u
                 INNER JOIN tipo_usuario t ON t.id_tipo_usuario = u.id_tipo_usuario
                 WHERE LOWER(u.email) = LOWER(?)
                 LIMIT 1
                 """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) return null;

                UsuarioDTO usuario = new UsuarioDTO();
                usuario.setIdUsuario(resultSet.getLong("id_usuario"));
                usuario.setIdFuncionario(resultSet.getObject("fk_funcionario", Long.class));
                usuario.setIdTipoUsuario(resultSet.getLong("id_tipo_usuario"));
                usuario.setNome(resultSet.getString("nome"));
                usuario.setEmail(resultSet.getString("email"));
                usuario.setCpf(resultSet.getString("cpf"));
                Date dataNascimento = resultSet.getDate("data_nascimento");
                if (dataNascimento != null) usuario.setDataNascimento(dataNascimento.toLocalDate());
                usuario.setAtivo(resultSet.getBoolean("ativo"));
                return new UsuarioCredenciaisDTO(usuario, resultSet.getString("senha"), resultSet.getString("tipo_usuario"));
            }
        }
    }

    public void atualizar(UsuarioDTO usuario) throws SQLException {

        String sql = """
                 UPDATE usuario
                 SET fk_funcionario = ?,
                     id_tipo_usuario = ?,
                     nome = ?,
                     email = ?,
                     cpf = ?,
                     data_nascimento = ?,
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
            statement.setString(5, usuario.getCpf());

            if (usuario.getDataNascimento() != null) {
                statement.setDate(6, Date.valueOf(usuario.getDataNascimento()));
            } else {
                statement.setNull(6, java.sql.Types.DATE);
            }

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

    public void atualizarSenha(Long idUsuario, String senhaHash) throws SQLException {
        String sql = """
                 UPDATE usuario
                 SET senha = ?
                 WHERE id_usuario = ?
                 """;

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, senhaHash);
            statement.setLong(2, idUsuario);
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

    private UsuarioDTO converterResultSet(
            ResultSet resultSet
    ) throws SQLException {

        UsuarioDTO usuario = new UsuarioDTO();

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

        usuario.setCpf(
                resultSet.getString("cpf")
        );

        Date dataNascimento = resultSet.getDate("data_nascimento");

        if (dataNascimento != null) {
            usuario.setDataNascimento(dataNascimento.toLocalDate());
        }

        usuario.setAtivo(
                resultSet.getBoolean("ativo")
        );

        return usuario;
    }
}
