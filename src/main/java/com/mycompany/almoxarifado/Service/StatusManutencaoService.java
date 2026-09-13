package com.mycompany.almoxarifado.Service;

import com.mycompany.almoxarifado.Dao.StatusManutencaoDAO;
import com.mycompany.almoxarifado.Dto.StatusManutencaoDTO;
import java.sql.SQLException;
import java.util.List;

public class StatusManutencaoService {

    private final StatusManutencaoDAO statusManutencaoDAO;

    public StatusManutencaoService() {
        statusManutencaoDAO = new StatusManutencaoDAO();
    }

    public List<StatusManutencaoDTO> listarTodos() throws SQLException {
        return statusManutencaoDAO.listarTodos();
    }

    public StatusManutencaoDTO buscarPorId(Long id) throws SQLException {
        validarId(id);
        StatusManutencaoDTO status = statusManutencaoDAO.buscarPorId(id);
        if (status == null) {
            throw new IllegalArgumentException("Status da manutenção não encontrado.");
        }
        return status;
    }

    public StatusManutencaoDTO buscarPorNome(String nome) throws SQLException {
        validarNome(nome);
        StatusManutencaoDTO status = statusManutencaoDAO.buscarPorNome(nome.trim());
        if (status == null) {
            throw new IllegalArgumentException("Status da manutenção não encontrado.");
        }
        return status;
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID do status da manutenção inválido.");
        }
    }

    private void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do status da manutenção é obrigatório.");
        }
        if (nome.trim().length() > 50) {
            throw new IllegalArgumentException("O nome deve possuir no máximo 50 caracteres.");
        }
    }
}
