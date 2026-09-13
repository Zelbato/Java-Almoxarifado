package com.mycompany.almoxarifado.Service;

import com.mycompany.almoxarifado.Dao.TipoManutencaoDAO;
import com.mycompany.almoxarifado.Dto.TipoManutencaoDTO;
import java.sql.SQLException;
import java.util.List;

public class TipoManutencaoService {

    private final TipoManutencaoDAO tipoManutencaoDAO;

    public TipoManutencaoService() {
        tipoManutencaoDAO = new TipoManutencaoDAO();
    }

    public List<TipoManutencaoDTO> listarTodos() throws SQLException {
        return tipoManutencaoDAO.listarTodos();
    }

    public TipoManutencaoDTO buscarPorId(Long id) throws SQLException {
        validarId(id);
        TipoManutencaoDTO tipo = tipoManutencaoDAO.buscarPorId(id);
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo de manutenção não encontrado.");
        }
        return tipo;
    }

    public TipoManutencaoDTO buscarPorNome(String nome) throws SQLException {
        validarNome(nome);
        TipoManutencaoDTO tipo = tipoManutencaoDAO.buscarPorNome(nome.trim());
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo de manutenção não encontrado.");
        }
        return tipo;
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID do tipo de manutenção inválido.");
        }
    }

    private void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do tipo de manutenção é obrigatório.");
        }
        if (nome.trim().length() > 50) {
            throw new IllegalArgumentException("O nome deve possuir no máximo 50 caracteres.");
        }
    }
}
