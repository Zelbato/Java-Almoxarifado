/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.almoxarifado.Service;

import com.mycompany.almoxarifado.Dao.LocalizacaoDAO;
import com.mycompany.almoxarifado.Dto.LocalizacaoDTO;

import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author heito
 */
public class LocalizacaoService {

    private final LocalizacaoDAO localizacaoDAO;

    public LocalizacaoService() {
        this.localizacaoDAO = new LocalizacaoDAO();
    }

    public void cadastrar(LocalizacaoDTO localizacao) throws SQLException {
        validar(localizacao);

        if (localizacaoDAO.existe(
                localizacao.getNome(),
                localizacao.getCorredor(),
                localizacao.getPrateleira()
        )) {
            throw new IllegalArgumentException(
                    "Localização já cadastrada."
            );
        }

        localizacaoDAO.cadastrar(localizacao);
    }

    public List<LocalizacaoDTO> listarTodos() throws SQLException {
        return localizacaoDAO.listarTodos();
    }

    public LocalizacaoDTO buscarPorId(Long id) throws SQLException {
        validarId(id);

        LocalizacaoDTO localizacao
                = localizacaoDAO.buscarPorId(id);

        if (localizacao == null) {
            throw new IllegalArgumentException(
                    "Localização não encontrada."
            );
        }

        return localizacao;
    }

    public void atualizar(LocalizacaoDTO localizacao) throws SQLException {
        if (localizacao == null) {
            throw new IllegalArgumentException(
                    "Localização não informada."
            );
        }

        validarId(localizacao.getIdLocalizacao());
        validar(localizacao);

        LocalizacaoDTO existente
                = localizacaoDAO.buscarPorId(
                        localizacao.getIdLocalizacao()
                );

        if (existente == null) {
            throw new IllegalArgumentException(
                    "Localização não encontrada."
            );
        }

        localizacaoDAO.atualizar(localizacao);
    }

    public void excluir(Long id) throws SQLException {
        validarId(id);

        LocalizacaoDTO localizacao
                = localizacaoDAO.buscarPorId(id);

        if (localizacao == null) {
            throw new IllegalArgumentException(
                    "Localização não encontrada."
            );
        }

        localizacaoDAO.excluir(id);
    }

    private void validar(LocalizacaoDTO localizacao) {
        if (localizacao == null) {
            throw new IllegalArgumentException(
                    "Localização não informada."
            );
        }

        if (localizacao.getNome() == null
                || localizacao.getNome().isBlank()) {

            throw new IllegalArgumentException(
                    "O nome da localização é obrigatório."
            );
        }

        if (localizacao.getNome().length() > 100) {
            throw new IllegalArgumentException(
                    "O nome da localização deve possuir no máximo 100 caracteres."
            );
        }

        if (localizacao.getCorredor() != null
                && localizacao.getCorredor() < 0) {

            throw new IllegalArgumentException(
                    "O corredor não pode ser negativo."
            );
        }

        if (localizacao.getPrateleira() != null
                && localizacao.getPrateleira() < 0) {

            throw new IllegalArgumentException(
                    "A prateleira não pode ser negativa."
            );
        }
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "ID da localização inválido."
            );
        }
    }
}
