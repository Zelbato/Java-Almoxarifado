package com.mycompany.almoxarifado.Service;

import com.mycompany.almoxarifado.Dao.TipoOcorrenciaDAO;
import com.mycompany.almoxarifado.Dto.TipoOcorrenciaDTO;
import java.sql.SQLException;
import java.util.List;

public class TipoOcorrenciaService {

    private final TipoOcorrenciaDAO tipoOcorrenciaDAO;

    public TipoOcorrenciaService() {
        tipoOcorrenciaDAO = new TipoOcorrenciaDAO();
    }

    public List<TipoOcorrenciaDTO> listarTodos() throws SQLException {
        return tipoOcorrenciaDAO.listarTodos();
    }

    public TipoOcorrenciaDTO buscarPorId(Long id) throws SQLException {
        validarId(id);
        TipoOcorrenciaDTO tipo = tipoOcorrenciaDAO.buscarPorId(id);
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo de ocorrência não encontrado.");
        }
        return tipo;
    }

    public TipoOcorrenciaDTO buscarPorNome(String nome) throws SQLException {
        validarNome(nome);
        TipoOcorrenciaDTO tipo = tipoOcorrenciaDAO.buscarPorNome(nome.trim());
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo de ocorrência não encontrado.");
        }
        return tipo;
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID do tipo de ocorrência inválido.");
        }
    }

    private void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do tipo de ocorrência é obrigatório.");
        }
        if (nome.trim().length() > 50) {
            throw new IllegalArgumentException("O nome deve possuir no máximo 50 caracteres.");
        }
    }
}
