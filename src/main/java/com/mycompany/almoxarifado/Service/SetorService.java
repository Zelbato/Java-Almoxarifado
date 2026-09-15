/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.almoxarifado.Service;

import com.mycompany.almoxarifado.Dao.SetorDAO;
import com.mycompany.almoxarifado.Dto.SetorDTO;

import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author heito
 */
public class SetorService {

    private final SetorDAO setorDAO;

    public SetorService() {
        this.setorDAO = new SetorDAO();
    }

    public void cadastrar(SetorDTO setor) throws SQLException {
        AuthSession.exigirEscrita();
        validar(setor);

        if (setorDAO.existePorNome(setor.getNome())) {
            throw new IllegalArgumentException("Setor já cadastrado.");
        }

        setorDAO.cadastrar(setor);
    }

    public List<SetorDTO> listarTodos() throws SQLException {
        return setorDAO.listarTodos();
    }

    public SetorDTO buscarPorId(Long id) throws SQLException {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID do setor inválido");

        }

        SetorDTO setor = setorDAO.buscarPorId(id);

        if (setor == null) {
            throw new IllegalArgumentException("Setor não encontrado");
        }

        return setor;
    }

    public void atualizar(SetorDTO setor) throws SQLException {
        AuthSession.exigirEscrita();

        if (setor.getIdSetor() == null || setor.getIdSetor() <= 0) {
            throw new IllegalArgumentException("ID do setor inválido");
        }

        validar(setor);

        SetorDTO setorExistente = setorDAO.buscarPorId(setor.getIdSetor());

        if (setorExistente == null) {
            throw new IllegalArgumentException("Setor não encontrado");
        }

        if (setorDAO.existePorNomeDiferenteDoId(
                setor.getNome(),
                setor.getIdSetor()
        )) {
            throw new IllegalArgumentException("Já existe outro setor com esse nome.");
        }

        setorDAO.atualizar(setor);
    }

    public void excluir(Long id) throws SQLException {
        AuthSession.exigirEscrita();

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID do setor inválido");
        }

        SetorDTO setor = setorDAO.buscarPorId(id);

        if (setor == null) {
            throw new IllegalArgumentException("Setor não encontrado");
        }
        setorDAO.excluir(id);
    }

    private void validar(SetorDTO setor) {

        if (setor == null) {
            throw new IllegalArgumentException("Setor não Informado.");
        }

        if (setor.getNome() == null || setor.getNome().isBlank()) {
            throw new IllegalArgumentException(
                    "O nome do setor é obrigatório."
            );
        }

        if (setor.getNome().length() > 100) {
            throw new IllegalArgumentException(
                    "O nome do setor deve possuir no máximo 100 caracteres."
            );
        }

        if (setor.getDescricao() != null
                && setor.getDescricao().length() > 255) {
            throw new IllegalArgumentException(
                    "A descrição deve possuir no máximo 255 caracteres."
            );
        }
    }
}
