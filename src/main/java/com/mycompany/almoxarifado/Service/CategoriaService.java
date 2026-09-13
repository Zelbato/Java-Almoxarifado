/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.almoxarifado.Service;

import com.mycompany.almoxarifado.Dao.CategoriaDAO;
import com.mycompany.almoxarifado.Dto.CategoriaDTO;

import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author heito
 */
public class CategoriaService {

    private final CategoriaDAO categoriaDAO;

    public CategoriaService() {
        this.categoriaDAO = new CategoriaDAO();
    }

    public void cadastrar(CategoriaDTO categoria) throws SQLException {
        validar(categoria);

        if (categoriaDAO.existePorNome(categoria.getNome())) {
            throw new IllegalArgumentException("Categoria já cadastrada.");
        }

        categoriaDAO.cadastrar(categoria);
    }

    public List<CategoriaDTO> listarTodos() throws SQLException {
        return categoriaDAO.listarTodos();
    }

    public CategoriaDTO buscarPorId(Long id) throws SQLException {
        validarId(id);

        CategoriaDTO categoria = categoriaDAO.buscarPorId(id);

        if (categoria == null) {
            throw new IllegalArgumentException("Categoria não encontrada.");
        }

        return categoria;
    }

    public void atualizar(CategoriaDTO categoria) throws SQLException {
        if (categoria == null) {
            throw new IllegalArgumentException("Categoria não informada.");
        }

        validarId(categoria.getIdCategoria());
        validar(categoria);

        CategoriaDTO categoriaExistente
                = categoriaDAO.buscarPorId(categoria.getIdCategoria());

        if (categoriaExistente == null) {
            throw new IllegalArgumentException("Categoria não encontrada.");
        }

        if (categoriaDAO.existePorNomeDiferenteDoId(
                categoria.getNome(),
                categoria.getIdCategoria()
        )) {
            throw new IllegalArgumentException(
                    "Já existe outra categoria com esse nome."
            );
        }

        categoriaDAO.atualizar(categoria);
    }

    public void excluir(Long id) throws SQLException {
        validarId(id);

        CategoriaDTO categoria = categoriaDAO.buscarPorId(id);

        if (categoria == null) {
            throw new IllegalArgumentException("Categoria não encontrada.");
        }

        categoriaDAO.excluir(id);
    }

    private void validar(CategoriaDTO categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("Categoria não informada.");
        }

        if (categoria.getNome() == null || categoria.getNome().isBlank()) {
            throw new IllegalArgumentException(
                    "O nome da categoria é obrigatório."
            );
        }

        if (categoria.getNome().length() > 100) {
            throw new IllegalArgumentException(
                    "O nome da categoria deve possuir no máximo 100 caracteres."
            );
        }

        if (categoria.getDescricao() != null
                && categoria.getDescricao().length() > 255) {
            throw new IllegalArgumentException(
                    "A descrição deve possuir no máximo 255 caracteres."
            );
        }
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "ID da categoria inválido."
            );
        }
    }
}
