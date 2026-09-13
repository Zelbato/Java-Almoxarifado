/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.almoxarifado.Dao;

import com.mycompany.almoxarifado.Dto.TipoUsuarioDTO;
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
public class TipoUsuarioDAO {

    public List<TipoUsuarioDTO> listarTodos() throws SQLException {

        String sql = """
                 SELECT
                     id_tipo_usuario,
                     nome
                 FROM tipo_usuario
                 ORDER BY id_tipo_usuario ASC
                 """;

        List<TipoUsuarioDTO> tiposUsuario = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                tiposUsuario.add(converterResultSet(resultSet));
            }
        }

        return tiposUsuario;
    }

    public TipoUsuarioDTO buscarPorId(Long id) throws SQLException {

        String sql = """
                 SELECT
                     id_tipo_usuario,
                     nome
                 FROM tipo_usuario
                 WHERE id_tipo_usuario = ?
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

    public TipoUsuarioDTO buscarPorNome(String nome) throws SQLException {

        String sql = """
                 SELECT
                     id_tipo_usuario,
                     nome
                 FROM tipo_usuario
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

    private TipoUsuarioDTO converterResultSet(
            ResultSet resultSet
    ) throws SQLException {

        TipoUsuarioDTO tipoUsuario = new TipoUsuarioDTO();

        tipoUsuario.setIdTipoUsuario(
                resultSet.getLong("id_tipo_usuario")
        );

        tipoUsuario.setNome(
                resultSet.getString("nome")
        );

        return tipoUsuario;
    }

}
