package com.mycompany.almoxarifado.Service;

import com.mycompany.almoxarifado.Dao.EquipamentoDAO;
import com.mycompany.almoxarifado.Dao.FuncionarioDAO;
import com.mycompany.almoxarifado.Dao.MovimentacaoDAO;
import com.mycompany.almoxarifado.Dao.OcorrenciaDAO;
import com.mycompany.almoxarifado.Dao.StatusOcorrenciaDAO;
import com.mycompany.almoxarifado.Dao.TipoOcorrenciaDAO;
import com.mycompany.almoxarifado.Dao.UsuarioDAO;
import com.mycompany.almoxarifado.Dto.OcorrenciaDTO;
import java.sql.SQLException;
import java.util.List;

public class OcorrenciaService {

    private final OcorrenciaDAO ocorrenciaDAO;
    private final EquipamentoDAO equipamentoDAO;
    private final MovimentacaoDAO movimentacaoDAO;
    private final FuncionarioDAO funcionarioDAO;
    private final UsuarioDAO usuarioDAO;
    private final TipoOcorrenciaDAO tipoOcorrenciaDAO;
    private final StatusOcorrenciaDAO statusOcorrenciaDAO;

    public OcorrenciaService() {
        ocorrenciaDAO = new OcorrenciaDAO();
        equipamentoDAO = new EquipamentoDAO();
        movimentacaoDAO = new MovimentacaoDAO();
        funcionarioDAO = new FuncionarioDAO();
        usuarioDAO = new UsuarioDAO();
        tipoOcorrenciaDAO = new TipoOcorrenciaDAO();
        statusOcorrenciaDAO = new StatusOcorrenciaDAO();
    }

    public void cadastrar(OcorrenciaDTO ocorrencia) throws SQLException {
        AuthSession.exigirEscrita();
        validar(ocorrencia);
        validarRelacionamentos(ocorrencia);
        ocorrenciaDAO.cadastrar(ocorrencia);
    }

    public List<OcorrenciaDTO> listarTodos() throws SQLException {
        return ocorrenciaDAO.listarTodos();
    }

    public OcorrenciaDTO buscarPorId(Long id) throws SQLException {
        validarId(id);
        OcorrenciaDTO ocorrencia = ocorrenciaDAO.buscarPorId(id);
        if (ocorrencia == null) {
            throw new IllegalArgumentException("Ocorrência não encontrada.");
        }
        return ocorrencia;
    }

    public void atualizar(OcorrenciaDTO ocorrencia) throws SQLException {
        AuthSession.exigirEscrita();
        if (ocorrencia == null) {
            throw new IllegalArgumentException("Ocorrência não informada.");
        }

        validarId(ocorrencia.getIdOcorrencia());
        validar(ocorrencia);
        validarRelacionamentos(ocorrencia);

        if (ocorrenciaDAO.buscarPorId(ocorrencia.getIdOcorrencia()) == null) {
            throw new IllegalArgumentException("Ocorrência não encontrada.");
        }

        ocorrenciaDAO.atualizar(ocorrencia);
    }

    public void excluir(Long id) throws SQLException {
        AuthSession.exigirEscrita();
        buscarPorId(id);
        ocorrenciaDAO.excluir(id);
    }

    private void validar(OcorrenciaDTO ocorrencia) {
        if (ocorrencia == null) {
            throw new IllegalArgumentException("Ocorrência não informada.");
        }

        validarIdObrigatorio(ocorrencia.getIdEquipamento(), "O equipamento");
        validarIdOpcional(ocorrencia.getIdMovimentacao(), "ID da movimentação");
        validarIdOpcional(ocorrencia.getIdFuncionario(), "ID do funcionário");
        validarIdObrigatorio(ocorrencia.getIdUsuarioRegistro(), "O usuário responsável pelo registro");
        validarIdObrigatorio(ocorrencia.getIdTipoOcorrencia(), "O tipo de ocorrência");
        validarIdObrigatorio(ocorrencia.getIdStatusOcorrencia(), "O status da ocorrência");
        validarTextoObrigatorio(ocorrencia.getTitulo(), 150, "O título");
        validarTextoObrigatorio(ocorrencia.getDescricao(), 500, "A descrição");
        validarTextoOpcional(ocorrencia.getSolucao(), 500, "A solução");

        if (ocorrencia.getDataOcorrencia() == null) {
            throw new IllegalArgumentException("A data da ocorrência é obrigatória.");
        }
        if (ocorrencia.getDataResolucao() != null
                && ocorrencia.getDataResolucao().isBefore(ocorrencia.getDataOcorrencia())) {
            throw new IllegalArgumentException("A data de resolução não pode ser anterior à ocorrência.");
        }
        if (ocorrencia.getDataResolucao() != null
                && (ocorrencia.getSolucao() == null || ocorrencia.getSolucao().isBlank())) {
            throw new IllegalArgumentException("Informe a solução ao registrar a resolução.");
        }
        if (ocorrencia.getSolucao() != null && !ocorrencia.getSolucao().isBlank()
                && ocorrencia.getDataResolucao() == null) {
            throw new IllegalArgumentException("Informe a data de resolução ao registrar a solução.");
        }
    }

    private void validarRelacionamentos(OcorrenciaDTO ocorrencia) throws SQLException {
        if (equipamentoDAO.buscarPorId(ocorrencia.getIdEquipamento()) == null) {
            throw new IllegalArgumentException("Equipamento não encontrado.");
        }
        if (ocorrencia.getIdMovimentacao() != null
                && movimentacaoDAO.buscarPorId(ocorrencia.getIdMovimentacao()) == null) {
            throw new IllegalArgumentException("Movimentação não encontrada.");
        }
        if (ocorrencia.getIdFuncionario() != null
                && funcionarioDAO.buscarPorId(ocorrencia.getIdFuncionario()) == null) {
            throw new IllegalArgumentException("Funcionário não encontrado.");
        }
        if (usuarioDAO.buscarPorId(ocorrencia.getIdUsuarioRegistro()) == null) {
            throw new IllegalArgumentException("Usuário responsável pelo registro não encontrado.");
        }
        if (tipoOcorrenciaDAO.buscarPorId(ocorrencia.getIdTipoOcorrencia()) == null) {
            throw new IllegalArgumentException("Tipo de ocorrência não encontrado.");
        }
        var status = statusOcorrenciaDAO.buscarPorId(ocorrencia.getIdStatusOcorrencia());

        if (status == null) {
            throw new IllegalArgumentException("Status da ocorrência não encontrado.");
        }

        validarResolucaoPorStatus(ocorrencia, status.getNome());
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID da ocorrência inválido.");
        }
    }

    private void validarIdObrigatorio(Long id, String campo) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(campo + " é obrigatório.");
        }
    }

    private void validarIdOpcional(Long id, String campo) {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException(campo + " inválido.");
        }
    }

    private void validarTextoObrigatorio(String valor, int tamanhoMaximo, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " é obrigatório.");
        }
        validarTextoOpcional(valor, tamanhoMaximo, campo);
    }

    private void validarTextoOpcional(String valor, int tamanhoMaximo, String campo) {
        if (valor != null && valor.length() > tamanhoMaximo) {
            throw new IllegalArgumentException(campo + " deve possuir no máximo " + tamanhoMaximo + " caracteres.");
        }
    }

    private void validarResolucaoPorStatus(
            OcorrenciaDTO ocorrencia,
            String nomeStatus
    ) {
        boolean resolvida = "RESOLVIDA".equals(nomeStatus);
        boolean possuiResolucao = ocorrencia.getDataResolucao() != null
                && ocorrencia.getSolucao() != null
                && !ocorrencia.getSolucao().isBlank();

        if (resolvida && !possuiResolucao) {
            throw new IllegalArgumentException(
                    "Uma ocorrência resolvida exige solução e data de resolução."
            );
        }

        if (!resolvida && possuiResolucao) {
            throw new IllegalArgumentException(
                    "Solução e data de resolução só podem ser informadas para ocorrência resolvida."
            );
        }
    }
}
