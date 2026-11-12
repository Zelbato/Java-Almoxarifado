package com.mycompany.almoxarifado.Dao;

import com.mycompany.almoxarifado.Dto.DashboardDTO;
import com.mycompany.almoxarifado.config.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Consultas agregadas, somente de leitura, do painel principal.
 */
public class DashboardDAO {

    public DashboardDTO carregarIndicadores() throws SQLException {
        DashboardDTO dashboard = new DashboardDTO();

        try (Connection connection = DatabaseConnection.getConnection()) {
            carregarResumoEquipamentos(connection, dashboard);
            carregarResumoMovimentacoes(connection, dashboard);
            dashboard.setEquipamentosPorSituacao(
                    carregarEquipamentosPorSituacao(connection)
            );
            dashboard.setMovimentacoesPorMes(
                    carregarMovimentacoesPorMes(connection)
            );
            carregarDadosRelatorio(connection, dashboard);
        }

        return dashboard;
    }

    private void carregarDadosRelatorio(
            Connection connection,
            DashboardDTO dashboard
    ) throws SQLException {
        dashboard.setEquipamentosDanificados(contar(connection, """
                SELECT COUNT(*)
                FROM equipamento e
                INNER JOIN situacao_equipamento se
                    ON se.id_situacao_equipamento = e.id_situacao_equipamento
                WHERE se.nome = 'DANIFICADO'
                """));

        dashboard.setManutencoesPendentes(contar(connection, """
                SELECT COUNT(*)
                FROM manutencao m
                INNER JOIN status_manutencao sm
                    ON sm.id_status_manutencao = m.id_status_manutencao
                WHERE sm.nome IN ('AGENDADA', 'EM_ANDAMENTO')
                """));

        dashboard.setOcorrenciasAbertas(contar(connection, """
                SELECT COUNT(*)
                FROM ocorrencia o
                INNER JOIN status_ocorrencia so
                    ON so.id_status_ocorrencia = o.id_status_ocorrencia
                WHERE so.nome IN ('ABERTA', 'EM_ANALISE')
                """));

        dashboard.setEquipamentosPorCategoria(agrupar(connection, """
                SELECT c.nome, COUNT(e.id_equipamento) AS quantidade
                FROM categoria c
                LEFT JOIN equipamento e ON e.id_categoria = c.id_categoria
                GROUP BY c.id_categoria, c.nome
                ORDER BY c.nome
                """));

        dashboard.setManutencoesPorTipo(agrupar(connection, """
                SELECT t.nome, COUNT(m.id_manutencao) AS quantidade
                FROM tipo_manutencao t
                LEFT JOIN manutencao m ON m.id_tipo_manutencao = t.id_tipo_manutencao
                GROUP BY t.id_tipo_manutencao, t.nome
                ORDER BY t.id_tipo_manutencao
                """));

        dashboard.setManutencoesPorStatus(agrupar(connection, """
                SELECT s.nome, COUNT(m.id_manutencao) AS quantidade
                FROM status_manutencao s
                LEFT JOIN manutencao m ON m.id_status_manutencao = s.id_status_manutencao
                GROUP BY s.id_status_manutencao, s.nome
                ORDER BY s.id_status_manutencao
                """));

        dashboard.setOcorrenciasPorTipo(agrupar(connection, """
                SELECT t.nome, COUNT(o.id_ocorrencia) AS quantidade
                FROM tipo_ocorrencia t
                LEFT JOIN ocorrencia o ON o.id_tipo_ocorrencia = t.id_tipo_ocorrencia
                GROUP BY t.id_tipo_ocorrencia, t.nome
                ORDER BY t.id_tipo_ocorrencia
                """));

        dashboard.setOcorrenciasPorStatus(agrupar(connection, """
                SELECT s.nome, COUNT(o.id_ocorrencia) AS quantidade
                FROM status_ocorrencia s
                LEFT JOIN ocorrencia o ON o.id_status_ocorrencia = s.id_status_ocorrencia
                GROUP BY s.id_status_ocorrencia, s.nome
                ORDER BY s.id_status_ocorrencia
                """));

        // os 5 equipamentos com mais retiradas
        dashboard.setEquipamentosMaisUtilizados(agrupar(connection, """
                SELECT e.nome, COUNT(m.id_movimentacao) AS quantidade
                FROM movimentacao m
                INNER JOIN equipamento e ON e.id_equipamento = m.id_equipamento
                GROUP BY e.id_equipamento, e.nome
                ORDER BY quantidade DESC, e.nome
                LIMIT 5
                """));
    }

    private long contar(Connection connection, String sql) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            return resultSet.next() ? resultSet.getLong(1) : 0;
        }
    }

    // consulta com duas colunas: nome e quantidade
    private Map<String, Integer> agrupar(Connection connection, String sql) throws SQLException {
        Map<String, Integer> resultado = new LinkedHashMap<>();

        try (PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                resultado.put(resultSet.getString(1), resultSet.getInt(2));
            }
        }

        return resultado;
    }

    private void carregarResumoEquipamentos(
            Connection connection,
            DashboardDTO dashboard
    ) throws SQLException {
        String sql = """
                     SELECT
                         COUNT(*) AS total_equipamentos,
                         COALESCE(SUM(se.nome = 'DISPONIVEL'), 0) AS disponiveis,
                         COALESCE(SUM(se.nome = 'EMPRESTADO'), 0) AS emprestados,
                         COALESCE(SUM(se.nome = 'EM_MANUTENCAO'), 0) AS em_manutencao
                     FROM equipamento e
                     LEFT JOIN situacao_equipamento se
                         ON se.id_situacao_equipamento = e.id_situacao_equipamento
                     """;

        try (PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                dashboard.setTotalEquipamentos(
                        resultSet.getLong("total_equipamentos")
                );
                dashboard.setEquipamentosDisponiveis(
                        resultSet.getLong("disponiveis")
                );
                dashboard.setEquipamentosEmprestados(
                        resultSet.getLong("emprestados")
                );
                dashboard.setEquipamentosEmManutencao(
                        resultSet.getLong("em_manutencao")
                );
            }
        }
    }

    private void carregarResumoMovimentacoes(
            Connection connection,
            DashboardDTO dashboard
    ) throws SQLException {
        String sql = """
                     SELECT
                         COALESCE(SUM(sm.nome = 'EM_ABERTO'), 0) AS em_aberto,
                         COALESCE(SUM(sm.nome = 'ATRASADA'), 0) AS atrasadas
                     FROM movimentacao m
                     INNER JOIN status_movimentacao sm
                         ON sm.id_status_movimentacao = m.id_status_movimentacao
                     """;

        try (PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                dashboard.setMovimentacoesEmAberto(
                        resultSet.getLong("em_aberto")
                );
                dashboard.setMovimentacoesAtrasadas(
                        resultSet.getLong("atrasadas")
                );
            }
        }
    }

    private Map<String, Integer> carregarEquipamentosPorSituacao(
            Connection connection
    ) throws SQLException {
        String sql = """
                     SELECT
                         se.nome,
                         COUNT(e.id_equipamento) AS quantidade
                     FROM situacao_equipamento se
                     LEFT JOIN equipamento e
                         ON e.id_situacao_equipamento = se.id_situacao_equipamento
                     GROUP BY se.id_situacao_equipamento, se.nome
                     ORDER BY se.id_situacao_equipamento
                     """;

        Map<String, Integer> indicadores = new LinkedHashMap<>();

        try (PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                indicadores.put(
                        resultSet.getString("nome"),
                        resultSet.getInt("quantidade")
                );
            }
        }

        return indicadores;
    }

    private Map<String, Integer> carregarMovimentacoesPorMes(
            Connection connection
    ) throws SQLException {
        String sql = """
                     SELECT
                         DATE_FORMAT(data_retirada, '%Y-%m') AS referencia,
                         COUNT(*) AS quantidade
                     FROM movimentacao
                     WHERE data_retirada >= DATE_SUB(
                         DATE_FORMAT(CURDATE(), '%Y-%m-01'),
                         INTERVAL 5 MONTH
                     )
                     GROUP BY DATE_FORMAT(data_retirada, '%Y-%m')
                     ORDER BY referencia
                     """;

        Map<String, Integer> indicadores = new LinkedHashMap<>();

        try (PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                indicadores.put(
                        resultSet.getString("referencia"),
                        resultSet.getInt("quantidade")
                );
            }
        }

        return indicadores;
    }
}
