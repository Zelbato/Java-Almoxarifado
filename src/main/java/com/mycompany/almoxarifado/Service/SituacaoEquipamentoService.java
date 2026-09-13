package com.mycompany.almoxarifado.Service;

import com.mycompany.almoxarifado.Dao.SituacaoEquipamentoDAO;
import com.mycompany.almoxarifado.Dto.SituacaoEquipamentoDTO;
import java.sql.SQLException;
import java.util.List;

public class SituacaoEquipamentoService {

    private final SituacaoEquipamentoDAO situacaoEquipamentoDAO;

    public SituacaoEquipamentoService() {
        situacaoEquipamentoDAO = new SituacaoEquipamentoDAO();
    }

    public List<SituacaoEquipamentoDTO> listarTodos() throws SQLException {
        return situacaoEquipamentoDAO.listarTodos();
    }

    public SituacaoEquipamentoDTO buscarPorId(Long id) throws SQLException {
        validarId(id);
        SituacaoEquipamentoDTO situacao = situacaoEquipamentoDAO.buscarPorId(id);
        if (situacao == null) {
            throw new IllegalArgumentException("Situação do equipamento não encontrada.");
        }
        return situacao;
    }

    public SituacaoEquipamentoDTO buscarPorNome(String nome) throws SQLException {
        validarNome(nome);
        SituacaoEquipamentoDTO situacao = situacaoEquipamentoDAO.buscarPorNome(nome.trim());
        if (situacao == null) {
            throw new IllegalArgumentException("Situação do equipamento não encontrada.");
        }
        return situacao;
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID da situação do equipamento inválido.");
        }
    }

    private void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome da situação do equipamento é obrigatório.");
        }
        if (nome.trim().length() > 50) {
            throw new IllegalArgumentException("O nome deve possuir no máximo 50 caracteres.");
        }
    }
}
