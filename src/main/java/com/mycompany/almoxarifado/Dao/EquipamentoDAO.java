/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.almoxarifado.Dao;

import com.mycompany.almoxarifado.Dto.EquipamentoDTO;
import com.mycompany.almoxarifado.config.DatabaseConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author heito
 */
public class EquipamentoDAO {

    public void cadastrar(EquipamentoDTO equipamento) throws SQLException {

        String sql = """
                 INSERT INTO equipamento (
                     id_localizacao,
                     id_categoria,
                     id_estado_conservacao,
                     id_situacao_equipamento,
                     nome,
                     valor,
                     descricao,
                     ativo,
                     marca,
                     modelo,
                     numero_patrimonio,
                     numero_serie,
                     data_aquisicao
                 )
                 VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                 """;

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, equipamento.getIdLocalizacao());
            statement.setLong(2, equipamento.getIdCategoria());
            statement.setLong(3, equipamento.getIdEstadoConservacao());
            statement.setLong(4, equipamento.getIdSituacaoEquipamento());

            statement.setString(5, equipamento.getNome());

            if (equipamento.getValor() != null) {
                statement.setBigDecimal(6, equipamento.getValor());
            } else {
                statement.setNull(6, java.sql.Types.DECIMAL);
            }

            statement.setString(7, equipamento.getDescricao());
            statement.setBoolean(8, equipamento.isAtivo());
            statement.setString(9, equipamento.getMarca());
            statement.setString(10, equipamento.getModelo());
            statement.setString(11, equipamento.getNumeroPatrimonio());
            statement.setString(12, equipamento.getNumeroSerie());

            if (equipamento.getDataAquisicao() != null) {
                statement.setDate(
                        13,
                        Date.valueOf(equipamento.getDataAquisicao())
                );
            } else {
                statement.setNull(13, java.sql.Types.DATE);
            }

            statement.executeUpdate();
        }
    }

    public List<EquipamentoDTO> listarTodos() throws SQLException {

        String sql = """
                 SELECT
                     id_equipamento,
                     id_localizacao,
                     id_categoria,
                     id_estado_conservacao,
                     id_situacao_equipamento,
                     nome,
                     valor,
                     descricao,
                     ativo,
                     marca,
                     modelo,
                     numero_patrimonio,
                     numero_serie,
                     data_aquisicao,
                     data_cadastro
                 FROM equipamento
                 ORDER BY id_equipamento ASC
                 """;

        List<EquipamentoDTO> equipamentos = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                equipamentos.add(converterResultSet(resultSet));
            }
        }

        return equipamentos;
    }

    public EquipamentoDTO buscarPorId(Long id) throws SQLException {

        String sql = """
                 SELECT
                     id_equipamento,
                     id_localizacao,
                     id_categoria,
                     id_estado_conservacao,
                     id_situacao_equipamento,
                     nome,
                     valor,
                     descricao,
                     ativo,
                     marca,
                     modelo,
                     numero_patrimonio,
                     numero_serie,
                     data_aquisicao,
                     data_cadastro
                 FROM equipamento
                 WHERE id_equipamento = ?
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

    public void atualizar(EquipamentoDTO equipamento) throws SQLException {

        String sql = """
                 UPDATE equipamento
                 SET id_localizacao = ?,
                     id_categoria = ?,
                     id_estado_conservacao = ?,
                     id_situacao_equipamento = ?,
                     nome = ?,
                     valor = ?,
                     descricao = ?,
                     ativo = ?,
                     marca = ?,
                     modelo = ?,
                     numero_patrimonio = ?,
                     numero_serie = ?,
                     data_aquisicao = ?
                 WHERE id_equipamento = ?
                 """;

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, equipamento.getIdLocalizacao());
            statement.setLong(2, equipamento.getIdCategoria());
            statement.setLong(3, equipamento.getIdEstadoConservacao());
            statement.setLong(4, equipamento.getIdSituacaoEquipamento());

            statement.setString(5, equipamento.getNome());

            if (equipamento.getValor() != null) {
                statement.setBigDecimal(6, equipamento.getValor());
            } else {
                statement.setNull(6, java.sql.Types.DECIMAL);
            }

            statement.setString(7, equipamento.getDescricao());
            statement.setBoolean(8, equipamento.isAtivo());
            statement.setString(9, equipamento.getMarca());
            statement.setString(10, equipamento.getModelo());
            statement.setString(11, equipamento.getNumeroPatrimonio());
            statement.setString(12, equipamento.getNumeroSerie());

            if (equipamento.getDataAquisicao() != null) {
                statement.setDate(
                        13,
                        Date.valueOf(equipamento.getDataAquisicao())
                );
            } else {
                statement.setNull(13, java.sql.Types.DATE);
            }

            statement.setLong(14, equipamento.getIdEquipamento());

            statement.executeUpdate();
        }
    }

    public void excluir(Long id) throws SQLException {

        String sql = """
                 DELETE FROM equipamento
                 WHERE id_equipamento = ?
                 """;

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    public boolean existePorNumeroPatrimonio(
            String numeroPatrimonio
    ) throws SQLException {

        String sql = """
                 SELECT COUNT(*)
                 FROM equipamento
                 WHERE numero_patrimonio = ?
                 """;

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, numeroPatrimonio);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    public boolean existePorNumeroPatrimonioDiferenteDoId(
            String numeroPatrimonio,
            Long idEquipamento
    ) throws SQLException {

        String sql = """
                 SELECT COUNT(*)
                 FROM equipamento
                 WHERE numero_patrimonio = ?
                   AND id_equipamento <> ?
                 """;

        try (
                Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, numeroPatrimonio);
            statement.setLong(2, idEquipamento);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    private EquipamentoDTO converterResultSet(
            ResultSet resultSet
    ) throws SQLException {

        EquipamentoDTO equipamento = new EquipamentoDTO();

        equipamento.setIdEquipamento(
                resultSet.getLong("id_equipamento")
        );

        equipamento.setIdLocalizacao(
                resultSet.getLong("id_localizacao")
        );

        equipamento.setIdCategoria(
                resultSet.getLong("id_categoria")
        );

        equipamento.setIdEstadoConservacao(
                resultSet.getLong("id_estado_conservacao")
        );

        equipamento.setIdSituacaoEquipamento(
                resultSet.getLong("id_situacao_equipamento")
        );

        equipamento.setNome(
                resultSet.getString("nome")
        );

        equipamento.setValor(
                resultSet.getBigDecimal("valor")
        );

        equipamento.setDescricao(
                resultSet.getString("descricao")
        );

        equipamento.setAtivo(
                resultSet.getBoolean("ativo")
        );

        equipamento.setMarca(
                resultSet.getString("marca")
        );

        equipamento.setModelo(
                resultSet.getString("modelo")
        );

        equipamento.setNumeroPatrimonio(
                resultSet.getString("numero_patrimonio")
        );

        equipamento.setNumeroSerie(
                resultSet.getString("numero_serie")
        );

        Date dataAquisicao
                = resultSet.getDate("data_aquisicao");

        if (dataAquisicao != null) {
            equipamento.setDataAquisicao(
                    dataAquisicao.toLocalDate()
            );
        }

        Timestamp dataCadastro
                = resultSet.getTimestamp("data_cadastro");

        if (dataCadastro != null) {
            equipamento.setDataCadastro(
                    dataCadastro.toLocalDateTime()
            );
        }

        return equipamento;
    }
}
