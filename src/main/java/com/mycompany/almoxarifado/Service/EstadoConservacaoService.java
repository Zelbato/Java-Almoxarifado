package com.mycompany.almoxarifado.Service;

import com.mycompany.almoxarifado.Dao.EstadoConservacaoDAO;
import com.mycompany.almoxarifado.Dto.EstadoConservacaoDTO;
import java.sql.SQLException;
import java.util.List;

public class EstadoConservacaoService {

    private final EstadoConservacaoDAO estadoConservacaoDAO;

    public EstadoConservacaoService() {
        estadoConservacaoDAO = new EstadoConservacaoDAO();
    }

    public List<EstadoConservacaoDTO> listarTodos() throws SQLException {
        return estadoConservacaoDAO.listarTodos();
    }

    public EstadoConservacaoDTO buscarPorId(Long id) throws SQLException {
        validarId(id);
        EstadoConservacaoDTO estado = estadoConservacaoDAO.buscarPorId(id);
        if (estado == null) {
            throw new IllegalArgumentException("Estado de conservação não encontrado.");
        }
        return estado;
    }

    public EstadoConservacaoDTO buscarPorNome(String nome) throws SQLException {
        validarNome(nome);
        EstadoConservacaoDTO estado = estadoConservacaoDAO.buscarPorNome(nome.trim());
        if (estado == null) {
            throw new IllegalArgumentException("Estado de conservação não encontrado.");
        }
        return estado;
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID do estado de conservação inválido.");
        }
    }

    private void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do estado de conservação é obrigatório.");
        }
        if (nome.trim().length() > 50) {
            throw new IllegalArgumentException("O nome deve possuir no máximo 50 caracteres.");
        }
    }
}
