package com.mycompany.almoxarifado.Service;

import com.mycompany.almoxarifado.Dao.StatusOcorrenciaDAO;
import com.mycompany.almoxarifado.Dto.StatusOcorrenciaDTO;
import java.sql.SQLException;
import java.util.List;

public class StatusOcorrenciaService {

    private final StatusOcorrenciaDAO statusOcorrenciaDAO;

    public StatusOcorrenciaService() {
        statusOcorrenciaDAO = new StatusOcorrenciaDAO();
    }

    public List<StatusOcorrenciaDTO> listarTodos() throws SQLException {
        return statusOcorrenciaDAO.listarTodos();
    }

    public StatusOcorrenciaDTO buscarPorId(Long id) throws SQLException {
        validarId(id);
        StatusOcorrenciaDTO status = statusOcorrenciaDAO.buscarPorId(id);
        if (status == null) {
            throw new IllegalArgumentException("Status da ocorrência não encontrado.");
        }
        return status;
    }

    public StatusOcorrenciaDTO buscarPorNome(String nome) throws SQLException {
        validarNome(nome);
        StatusOcorrenciaDTO status = statusOcorrenciaDAO.buscarPorNome(nome.trim());
        if (status == null) {
            throw new IllegalArgumentException("Status da ocorrência não encontrado.");
        }
        return status;
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID do status da ocorrência inválido.");
        }
    }

    private void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do status da ocorrência é obrigatório.");
        }
        if (nome.trim().length() > 50) {
            throw new IllegalArgumentException("O nome deve possuir no máximo 50 caracteres.");
        }
    }
}
