/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.almoxarifado.Dao;

import com.mycompany.almoxarifado.Dto.FuncionarioDTO;
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
public class FuncionarioDAO {

    public void cadastrar(FuncionarioDTO funcionario) throws SQLException {

        String sql = """
                 INSERT INTO funcionario (
                     fk_setor,
                     nome,
                     cpf,
                     telefone,
                     data_admissao,
                     email,
                     data_demissao
                 )
                 VALUES (?, ?, ?, ?, ?, ?, ?)
                 """;

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, funcionario.getIdSetor());
            statement.setString(2, funcionario.getNome());
            statement.setString(3, funcionario.getCpf());
            statement.setString(4, funcionario.getTelefone());

            if (funcionario.getData_admissao() != null) {
                statement.setDate(
                        5,
                        Date.valueOf(funcionario.getData_admissao())
                );
            } else {
                statement.setNull(
                        5,
                        java.sql.Types.DATE
                );
            }

            statement.setString(6, funcionario.getEmail());

            if (funcionario.getData_demissao() != null) {
                statement.setDate(
                        7,
                        Date.valueOf(funcionario.getData_demissao())
                );
            } else {
                statement.setNull(
                        7,
                        java.sql.Types.DATE
                );
            }

            statement.executeUpdate();
        }
    }

    public List<FuncionarioDTO> listarTodos() throws SQLException {

        String sql = """
                 SELECT
                     id_funcionario,
                     fk_setor,
                     nome,
                     cpf,
                     telefone,
                     data_admissao,
                     email,
                     data_demissao
                 FROM funcionario
                 ORDER BY id_funcionario ASC
                 """;

        List<FuncionarioDTO> funcionarios = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                FuncionarioDTO funcionario
                        = converterResultSet(resultSet);

                funcionarios.add(funcionario);
            }
        }

        return funcionarios;
    }

    public FuncionarioDTO buscarPorId(Long id) throws SQLException {

        String sql = """
                 SELECT
                     id_funcionario,
                     fk_setor,
                     nome,
                     cpf,
                     telefone,
                     data_admissao,
                     email,
                     data_demissao
                 FROM funcionario
                 WHERE id_funcionario = ?
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

    public void atualizar(FuncionarioDTO funcionario) throws SQLException {

        String sql = """
                 UPDATE funcionario
                 SET fk_setor = ?,
                     nome = ?,
                     cpf = ?,
                     telefone = ?,
                     data_admissao = ?,
                     email = ?,
                     data_demissao = ?
                 WHERE id_funcionario = ?
                 """;

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, funcionario.getIdSetor());
            statement.setString(2, funcionario.getNome());
            statement.setString(3, funcionario.getCpf());
            statement.setString(4, funcionario.getTelefone());

            if (funcionario.getData_admissao() != null) {
                statement.setDate(
                        5,
                        Date.valueOf(funcionario.getData_admissao())
                );
            } else {
                statement.setNull(
                        5,
                        java.sql.Types.DATE
                );
            }

            statement.setString(6, funcionario.getEmail());

            if (funcionario.getData_demissao() != null) {
                statement.setDate(
                        7,
                        Date.valueOf(funcionario.getData_demissao())
                );
            } else {
                statement.setNull(
                        7,
                        java.sql.Types.DATE
                );
            }

            statement.setLong(
                    8,
                    funcionario.getIdFuncionario()
            );

            statement.executeUpdate();
        }
    }

    public void excluir(Long id) throws SQLException {

        String sql = """
                 DELETE FROM funcionario
                 WHERE id_funcionario = ?
                 """;

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            statement.executeUpdate();
        }
    }

    public boolean existePorCpf(String cpf) throws SQLException {

        String sql = """
                 SELECT COUNT(*)
                 FROM funcionario
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

    public boolean existePorCpfDiferenteDoId(
            String cpf,
            Long idFuncionario
    ) throws SQLException {

        String sql = """
                 SELECT COUNT(*)
                 FROM funcionario
                 WHERE cpf = ?
                 AND id_funcionario <> ?
                 """;

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, cpf);
            statement.setLong(2, idFuncionario);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    private FuncionarioDTO converterResultSet(
            ResultSet resultSet
    ) throws SQLException {

        FuncionarioDTO funcionario = new FuncionarioDTO();

        funcionario.setIdFuncionario(
                resultSet.getLong("id_funcionario")
        );

        funcionario.setIdSetor(
                resultSet.getLong("fk_setor")
        );

        funcionario.setNome(
                resultSet.getString("nome")
        );

        funcionario.setCpf(
                resultSet.getString("cpf")
        );

        funcionario.setTelefone(
                resultSet.getString("telefone")
        );

        Date dataAdmissao
                = resultSet.getDate("data_admissao");

        if (dataAdmissao != null) {
            funcionario.setData_admissao(
                    dataAdmissao.toLocalDate()
            );
        }

        funcionario.setEmail(
                resultSet.getString("email")
        );

        Date dataDemissao
                = resultSet.getDate("data_demissao");

        if (dataDemissao != null) {
            funcionario.setData_demissao(
                    dataDemissao.toLocalDate()
            );
        }

        return funcionario;
    }
}
