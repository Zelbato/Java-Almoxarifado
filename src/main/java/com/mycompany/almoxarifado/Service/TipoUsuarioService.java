package com.mycompany.almoxarifado.Service;

import com.mycompany.almoxarifado.Dao.TipoUsuarioDAO;
import com.mycompany.almoxarifado.Dto.TipoUsuarioDTO;
import java.sql.SQLException;
import java.util.List;

public class TipoUsuarioService {

    private final TipoUsuarioDAO tipoUsuarioDAO;

    public TipoUsuarioService() {
        tipoUsuarioDAO = new TipoUsuarioDAO();
    }

    public List<TipoUsuarioDTO> listarTodos() throws SQLException {
        return tipoUsuarioDAO.listarTodos();
    }

    public TipoUsuarioDTO buscarPorId(Long id) throws SQLException {
        validarId(id);
        TipoUsuarioDTO tipo = tipoUsuarioDAO.buscarPorId(id);
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo de usuário não encontrado.");
        }
        return tipo;
    }

    public TipoUsuarioDTO buscarPorNome(String nome) throws SQLException {
        validarNome(nome);
        TipoUsuarioDTO tipo = tipoUsuarioDAO.buscarPorNome(nome.trim());
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo de usuário não encontrado.");
        }
        return tipo;
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID do tipo de usuário inválido.");
        }
    }

    private void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do tipo de usuário é obrigatório.");
        }
        if (nome.trim().length() > 50) {
            throw new IllegalArgumentException("O nome deve possuir no máximo 50 caracteres.");
        }
    }
}
