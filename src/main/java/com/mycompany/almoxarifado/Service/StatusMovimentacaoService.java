package com.mycompany.almoxarifado.Service;

import com.mycompany.almoxarifado.Dao.StatusMovimentacaoDAO;
import com.mycompany.almoxarifado.Dto.StatusMovimentacaoDTO;
import java.sql.SQLException;
import java.util.List;

public class StatusMovimentacaoService {

    private final StatusMovimentacaoDAO statusMovimentacaoDAO;

    public StatusMovimentacaoService() {
        statusMovimentacaoDAO = new StatusMovimentacaoDAO();
    }

    public List<StatusMovimentacaoDTO> listarTodos() throws SQLException {
        return statusMovimentacaoDAO.listarTodos();
    }

    public StatusMovimentacaoDTO buscarPorId(Long id) throws SQLException {
        validarId(id);
        StatusMovimentacaoDTO status = statusMovimentacaoDAO.buscarPorId(id);
        if (status == null) {
            throw new IllegalArgumentException("Status da movimentação não encontrado.");
        }
        return status;
    }

    public StatusMovimentacaoDTO buscarPorNome(String nome) throws SQLException {
        validarNome(nome);
        StatusMovimentacaoDTO status = statusMovimentacaoDAO.buscarPorNome(nome.trim());
        if (status == null) {
            throw new IllegalArgumentException("Status da movimentação não encontrado.");
        }
        return status;
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID do status da movimentação inválido.");
        }
    }

    private void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do status da movimentação é obrigatório.");
        }
        if (nome.trim().length() > 50) {
            throw new IllegalArgumentException("O nome deve possuir no máximo 50 caracteres.");
        }
    }
}
