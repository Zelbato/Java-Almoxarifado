package com.mycompany.almoxarifado.Service;

import com.mycompany.almoxarifado.Dao.CategoriaDAO;
import com.mycompany.almoxarifado.Dao.EquipamentoDAO;
import com.mycompany.almoxarifado.Dao.EstadoConservacaoDAO;
import com.mycompany.almoxarifado.Dao.LocalizacaoDAO;
import com.mycompany.almoxarifado.Dao.SituacaoEquipamentoDAO;
import com.mycompany.almoxarifado.Dto.EquipamentoDTO;

import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author heito
 */
public class EquipamentoService {

    private final EquipamentoDAO equipamentoDAO;
    private final CategoriaDAO categoriaDAO;
    private final LocalizacaoDAO localizacaoDAO;
    private final EstadoConservacaoDAO estadoConservacaoDAO;
    private final SituacaoEquipamentoDAO situacaoEquipamentoDAO;

    public EquipamentoService() {
        this.equipamentoDAO = new EquipamentoDAO();
        this.categoriaDAO = new CategoriaDAO();
        this.localizacaoDAO = new LocalizacaoDAO();
        this.estadoConservacaoDAO = new EstadoConservacaoDAO();
        this.situacaoEquipamentoDAO = new SituacaoEquipamentoDAO();
    }

    public void cadastrar(EquipamentoDTO equipamento) throws SQLException {

        validar(equipamento);
        validarRelacionamentos(equipamento);

        if (equipamento.getNumeroPatrimonio() != null
                && !equipamento.getNumeroPatrimonio().isBlank()
                && equipamentoDAO.existePorNumeroPatrimonio(
                        equipamento.getNumeroPatrimonio()
                )) {

            throw new IllegalArgumentException(
                    "Já existe um equipamento cadastrado com esse número de patrimônio."
            );
        }

        equipamentoDAO.cadastrar(equipamento);
    }

    public List<EquipamentoDTO> listarTodos() throws SQLException {
        return equipamentoDAO.listarTodos();
    }

    public EquipamentoDTO buscarPorId(Long id) throws SQLException {

        validarId(id);

        EquipamentoDTO equipamento = equipamentoDAO.buscarPorId(id);

        if (equipamento == null) {
            throw new IllegalArgumentException(
                    "Equipamento não encontrado."
            );
        }

        return equipamento;
    }

    public void atualizar(EquipamentoDTO equipamento) throws SQLException {

        if (equipamento == null) {
            throw new IllegalArgumentException(
                    "Equipamento não informado."
            );
        }

        validarId(equipamento.getIdEquipamento());
        validar(equipamento);
        validarRelacionamentos(equipamento);

        EquipamentoDTO equipamentoExistente =
                equipamentoDAO.buscarPorId(equipamento.getIdEquipamento());

        if (equipamentoExistente == null) {
            throw new IllegalArgumentException(
                    "Equipamento não encontrado."
            );
        }

        if (equipamento.getNumeroPatrimonio() != null
                && !equipamento.getNumeroPatrimonio().isBlank()
                && equipamentoDAO.existePorNumeroPatrimonioDiferenteDoId(
                        equipamento.getNumeroPatrimonio(),
                        equipamento.getIdEquipamento()
                )) {

            throw new IllegalArgumentException(
                    "Já existe outro equipamento cadastrado com esse número de patrimônio."
            );
        }

        equipamentoDAO.atualizar(equipamento);
    }

    public void excluir(Long id) throws SQLException {

        validarId(id);

        EquipamentoDTO equipamento = equipamentoDAO.buscarPorId(id);

        if (equipamento == null) {
            throw new IllegalArgumentException(
                    "Equipamento não encontrado."
            );
        }

        equipamentoDAO.excluir(id);
    }

    private void validar(EquipamentoDTO equipamento) {

        if (equipamento == null) {
            throw new IllegalArgumentException(
                    "Equipamento não informado."
            );
        }

        if (equipamento.getIdCategoria() == null
                || equipamento.getIdCategoria() <= 0) {

            throw new IllegalArgumentException(
                    "A categoria do equipamento é obrigatória."
            );
        }

        if (equipamento.getIdLocalizacao() == null
                || equipamento.getIdLocalizacao() <= 0) {

            throw new IllegalArgumentException(
                    "A localização do equipamento é obrigatória."
            );
        }

        if (equipamento.getIdEstadoConservacao() == null
                || equipamento.getIdEstadoConservacao() <= 0) {

            throw new IllegalArgumentException(
                    "O estado de conservação do equipamento é obrigatório."
            );
        }

        if (equipamento.getIdSituacaoEquipamento() == null
                || equipamento.getIdSituacaoEquipamento() <= 0) {

            throw new IllegalArgumentException(
                    "A situação do equipamento é obrigatória."
            );
        }

        if (equipamento.getNome() == null
                || equipamento.getNome().isBlank()) {

            throw new IllegalArgumentException(
                    "O nome do equipamento é obrigatório."
            );
        }

        if (equipamento.getNome().length() > 150) {
            throw new IllegalArgumentException(
                    "O nome do equipamento deve possuir no máximo 150 caracteres."
            );
        }

        if (equipamento.getValor() != null
                && equipamento.getValor().signum() < 0) {

            throw new IllegalArgumentException(
                    "O valor do equipamento não pode ser negativo."
            );
        }

        if (equipamento.getDescricao() != null
                && equipamento.getDescricao().length() > 255) {

            throw new IllegalArgumentException(
                    "A descrição deve possuir no máximo 255 caracteres."
            );
        }

        if (equipamento.getMarca() != null
                && equipamento.getMarca().length() > 100) {

            throw new IllegalArgumentException(
                    "A marca deve possuir no máximo 100 caracteres."
            );
        }

        if (equipamento.getModelo() != null
                && equipamento.getModelo().length() > 100) {

            throw new IllegalArgumentException(
                    "O modelo deve possuir no máximo 100 caracteres."
            );
        }

        if (equipamento.getNumeroPatrimonio() != null
                && equipamento.getNumeroPatrimonio().length() > 100) {

            throw new IllegalArgumentException(
                    "O número de patrimônio deve possuir no máximo 100 caracteres."
            );
        }

        if (equipamento.getNumeroSerie() != null
                && equipamento.getNumeroSerie().length() > 100) {

            throw new IllegalArgumentException(
                    "O número de série deve possuir no máximo 100 caracteres."
            );
        }
    }

    private void validarId(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "ID do equipamento inválido."
            );
        }
    }

    private void validarRelacionamentos(
            EquipamentoDTO equipamento
    ) throws SQLException {

        if (categoriaDAO.buscarPorId(
                equipamento.getIdCategoria()
        ) == null) {

            throw new IllegalArgumentException(
                    "Categoria não encontrada."
            );
        }

        if (localizacaoDAO.buscarPorId(
                equipamento.getIdLocalizacao()
        ) == null) {

            throw new IllegalArgumentException(
                    "Localização não encontrada."
            );
        }

        if (estadoConservacaoDAO.buscarPorId(
                equipamento.getIdEstadoConservacao()
        ) == null) {

            throw new IllegalArgumentException(
                    "Estado de conservação não encontrado."
            );
        }

        if (situacaoEquipamentoDAO.buscarPorId(
                equipamento.getIdSituacaoEquipamento()
        ) == null) {

            throw new IllegalArgumentException(
                    "Situação do equipamento não encontrada."
            );
        }
    }
}