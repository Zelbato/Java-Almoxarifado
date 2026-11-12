package com.mycompany.almoxarifado.Service;

import com.mycompany.almoxarifado.Dao.DashboardDAO;
import com.mycompany.almoxarifado.Dao.MovimentacaoDAO;
import com.mycompany.almoxarifado.Dto.DashboardDTO;
import java.sql.SQLException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Regras de apresentação dos indicadores do Dashboard.
 */
public class DashboardService {

    private static final DateTimeFormatter CHAVE_MES
            = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final DateTimeFormatter ROTULO_MES
            = DateTimeFormatter.ofPattern(
                    "MMM/yy",
                    Locale.forLanguageTag("pt-BR")
            );

    private final DashboardDAO dashboardDAO;

    public DashboardService() {
        dashboardDAO = new DashboardDAO();
    }

    public DashboardDTO carregarIndicadores() throws SQLException {
        AuthSession.exigirAutenticacao();

        // garante que as atrasadas já estão marcadas antes de contar
        new MovimentacaoDAO().atualizarAtrasadas();

        DashboardDTO dashboard = dashboardDAO.carregarIndicadores();
        dashboard.setMovimentacoesPorMes(
                preencherUltimosSeisMeses(
                        dashboard.getMovimentacoesPorMes()
                )
        );

        return dashboard;
    }

    private Map<String, Integer> preencherUltimosSeisMeses(
            Map<String, Integer> movimentacoesPorMes
    ) {
        Map<String, Integer> meses = new LinkedHashMap<>();
        YearMonth mesAtual = YearMonth.now();

        for (int deslocamento = 5; deslocamento >= 0; deslocamento--) {
            YearMonth mes = mesAtual.minusMonths(deslocamento);
            meses.put(
                    ROTULO_MES.format(mes),
                    movimentacoesPorMes.getOrDefault(
                            CHAVE_MES.format(mes),
                            0
                    )
            );
        }

        return meses;
    }
}
