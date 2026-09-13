package com.mycompany.almoxarifado.Service;

import com.mycompany.almoxarifado.Dao.EquipamentoDAO;
import com.mycompany.almoxarifado.Dao.ManutencaoDAO;
import com.mycompany.almoxarifado.Dao.StatusManutencaoDAO;
import com.mycompany.almoxarifado.Dao.TipoManutencaoDAO;
import com.mycompany.almoxarifado.Dao.UsuarioDAO;
import com.mycompany.almoxarifado.Dto.ManutencaoDTO;
import java.sql.SQLException;
import java.util.List;

public class ManutencaoService {

    private final ManutencaoDAO manutencaoDAO;
    private final EquipamentoDAO equipamentoDAO;
    private final UsuarioDAO usuarioDAO;
    private final TipoManutencaoDAO tipoManutencaoDAO;
    private final StatusManutencaoDAO statusManutencaoDAO;

    public ManutencaoService() {
        manutencaoDAO = new ManutencaoDAO();
        equipamentoDAO = new EquipamentoDAO();
        usuarioDAO = new UsuarioDAO();
        tipoManutencaoDAO = new TipoManutencaoDAO();
        statusManutencaoDAO = new StatusManutencaoDAO();
    }

    public void cadastrar(ManutencaoDTO manutencao) throws SQLException {
        validar(manutencao);
        validarRelacionamentos(manutencao);
        manutencaoDAO.cadastrar(manutencao);
    }

    public List<ManutencaoDTO> listarTodos() throws SQLException {
        return manutencaoDAO.listarTodos();
    }

    public ManutencaoDTO buscarPorId(Long id) throws SQLException {
        validarId(id);
        ManutencaoDTO manutencao = manutencaoDAO.buscarPorId(id);
        if (manutencao == null) {
            throw new IllegalArgumentException("Manutenção não encontrada.");
        }
        return manutencao;
    }

    public void atualizar(ManutencaoDTO manutencao) throws SQLException {
        if (manutencao == null) {
            throw new IllegalArgumentException("Manutenção não informada.");
        }

        validarId(manutencao.getIdManutencao());
        validar(manutencao);
        validarRelacionamentos(manutencao);

        if (manutencaoDAO.buscarPorId(manutencao.getIdManutencao()) == null) {
            throw new IllegalArgumentException("Manutenção não encontrada.");
        }

        manutencaoDAO.atualizar(manutencao);
    }

    public void excluir(Long id) throws SQLException {
        buscarPorId(id);
        manutencaoDAO.excluir(id);
    }

    private void validar(ManutencaoDTO manutencao) {
        if (manutencao == null) {
            throw new IllegalArgumentException("Manutenção não informada.");
        }

        validarIdObrigatorio(manutencao.getIdEquipamento(), "O equipamento");
        validarIdObrigatorio(manutencao.getIdUsuario(), "O usuário responsável");
        validarIdObrigatorio(manutencao.getIdTipoManutencao(), "O tipo de manutenção");
        validarIdObrigatorio(manutencao.getIdStatusManutencao(), "O status da manutenção");
        validarTextoObrigatorio(manutencao.getDescricaoProblema(), 500, "A descrição do problema");
        validarTextoOpcional(manutencao.getServicoRealizado(), 500, "O serviço realizado");
        validarTextoOpcional(manutencao.getTecnicoResponsavel(), 150, "O técnico responsável");
        validarTextoOpcional(manutencao.getObservacao(), 500, "A observação");

        if (manutencao.getDataEntrada() == null) {
            throw new IllegalArgumentException("A data de entrada é obrigatória.");
        }
        if (manutencao.getDataPrevistaConclusao() != null
                && manutencao.getDataPrevistaConclusao().isBefore(manutencao.getDataEntrada())) {
            throw new IllegalArgumentException("A previsão de conclusão não pode ser anterior à entrada.");
        }
        if (manutencao.getDataConclusao() != null
                && manutencao.getDataConclusao().isBefore(manutencao.getDataEntrada())) {
            throw new IllegalArgumentException("A conclusão não pode ser anterior à entrada.");
        }
    }

    private void validarRelacionamentos(ManutencaoDTO manutencao) throws SQLException {
        if (equipamentoDAO.buscarPorId(manutencao.getIdEquipamento()) == null) {
            throw new IllegalArgumentException("Equipamento não encontrado.");
        }
        if (usuarioDAO.buscarPorId(manutencao.getIdUsuario()) == null) {
            throw new IllegalArgumentException("Usuário responsável não encontrado.");
        }
        if (tipoManutencaoDAO.buscarPorId(manutencao.getIdTipoManutencao()) == null) {
            throw new IllegalArgumentException("Tipo de manutenção não encontrado.");
        }
        var status = statusManutencaoDAO.buscarPorId(manutencao.getIdStatusManutencao());

        if (status == null) {
            throw new IllegalArgumentException("Status da manutenção não encontrado.");
        }

        validarConclusaoPorStatus(manutencao, status.getNome());
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID da manutenção inválido.");
        }
    }

    private void validarIdObrigatorio(Long id, String campo) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(campo + " é obrigatório.");
        }
    }

    private void validarTextoObrigatorio(String valor, int tamanhoMaximo, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " é obrigatória.");
        }
        validarTextoOpcional(valor, tamanhoMaximo, campo);
    }

    private void validarTextoOpcional(String valor, int tamanhoMaximo, String campo) {
        if (valor != null && valor.length() > tamanhoMaximo) {
            throw new IllegalArgumentException(campo + " deve possuir no máximo " + tamanhoMaximo + " caracteres.");
        }
    }

    private void validarConclusaoPorStatus(
            ManutencaoDTO manutencao,
            String nomeStatus
    ) {
        boolean concluida = "CONCLUIDA".equals(nomeStatus);

        if (concluida && manutencao.getDataConclusao() == null) {
            throw new IllegalArgumentException(
                    "Uma manutenção concluída exige data de conclusão."
            );
        }

        if (!concluida && manutencao.getDataConclusao() != null) {
            throw new IllegalArgumentException(
                    "A data de conclusão só pode ser informada para manutenção concluída."
            );
        }
    }
}
