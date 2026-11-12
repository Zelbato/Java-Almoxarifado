package com.mycompany.almoxarifado.Service;

import com.mycompany.almoxarifado.Dao.FuncionarioDAO;
import com.mycompany.almoxarifado.Dao.SetorDAO;
import com.mycompany.almoxarifado.Dto.FuncionarioDTO;

import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author heito
 */
public class FuncionarioService {

    private final FuncionarioDAO funcionarioDAO;
    private final SetorDAO setorDAO;

    public FuncionarioService() {
        this.funcionarioDAO = new FuncionarioDAO();
        this.setorDAO = new SetorDAO();
    }

    public void cadastrar(FuncionarioDTO funcionario) throws SQLException {
        AuthSession.exigirChefe();

        validar(funcionario);
        validarSetor(funcionario.getIdSetor());

        if (funcionarioDAO.existePorCpf(funcionario.getCpf())) {
            throw new IllegalArgumentException(
                    "Já existe um funcionário cadastrado com esse CPF."
            );
        }

        funcionarioDAO.cadastrar(funcionario);
    }

    public List<FuncionarioDTO> listarTodos() throws SQLException {
        AuthSession.exigirAutenticacao();
        return funcionarioDAO.listarTodos();
    }

    public FuncionarioDTO buscarPorId(Long id) throws SQLException {
        AuthSession.exigirAutenticacao();

        validarId(id);

        FuncionarioDTO funcionario = funcionarioDAO.buscarPorId(id);

        if (funcionario == null) {
            throw new IllegalArgumentException(
                    "Funcionário não encontrado."
            );
        }

        return funcionario;
    }

    public void atualizar(FuncionarioDTO funcionario) throws SQLException {
        AuthSession.exigirChefe();

        if (funcionario == null) {
            throw new IllegalArgumentException(
                    "Funcionário não informado."
            );
        }

        validarId(funcionario.getIdFuncionario());
        validar(funcionario);
        validarSetor(funcionario.getIdSetor());

        FuncionarioDTO funcionarioExistente =
                funcionarioDAO.buscarPorId(funcionario.getIdFuncionario());

        if (funcionarioExistente == null) {
            throw new IllegalArgumentException(
                    "Funcionário não encontrado."
            );
        }

        if (funcionarioDAO.existePorCpfDiferenteDoId(
                funcionario.getCpf(),
                funcionario.getIdFuncionario()
        )) {
            throw new IllegalArgumentException(
                    "Já existe outro funcionário cadastrado com esse CPF."
            );
        }

        funcionarioDAO.atualizar(funcionario);
    }

    public void excluir(Long id) throws SQLException {
        AuthSession.exigirChefe();

        validarId(id);

        FuncionarioDTO funcionario = funcionarioDAO.buscarPorId(id);

        if (funcionario == null) {
            throw new IllegalArgumentException(
                    "Funcionário não encontrado."
            );
        }

        funcionarioDAO.excluir(id);
    }

    private void validar(FuncionarioDTO funcionario) {

        if (funcionario == null) {
            throw new IllegalArgumentException(
                    "Funcionário não informado."
            );
        }

        if (funcionario.getIdSetor() == null
                || funcionario.getIdSetor() <= 0) {

            throw new IllegalArgumentException(
                    "O setor do funcionário é obrigatório."
            );
        }

        if (funcionario.getNome() == null
                || funcionario.getNome().isBlank()) {

            throw new IllegalArgumentException(
                    "O nome do funcionário é obrigatório."
            );
        }

        if (funcionario.getNome().length() > 150) {
            throw new IllegalArgumentException(
                    "O nome do funcionário deve possuir no máximo 150 caracteres."
            );
        }

        if (funcionario.getCpf() == null
                || funcionario.getCpf().isBlank()) {

            throw new IllegalArgumentException(
                    "O CPF do funcionário é obrigatório."
            );
        }

        if (funcionario.getCpf().length() > 14) {
            throw new IllegalArgumentException(
                    "O CPF deve possuir no máximo 14 caracteres."
            );
        }

        if (funcionario.getTelefone() != null
                && funcionario.getTelefone().length() > 20) {

            throw new IllegalArgumentException(
                    "O telefone deve possuir no máximo 20 caracteres."
            );
        }

        if (funcionario.getEmail() != null
                && funcionario.getEmail().length() > 150) {

            throw new IllegalArgumentException(
                    "O e-mail deve possuir no máximo 150 caracteres."
            );
        }

        if (funcionario.getEmail() != null
                && !funcionario.getEmail().isBlank()
                && !funcionario.getEmail().matches(
                        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
                )) {

            throw new IllegalArgumentException(
                    "E-mail inválido."
            );
        }

        if (funcionario.getDataAdmissao() != null
                && funcionario.getDataDemissao() != null
                && funcionario.getDataDemissao()
                        .isBefore(funcionario.getDataAdmissao())) {

            throw new IllegalArgumentException(
                    "A data de demissão não pode ser anterior à data de admissão."
            );
        }
    }

    private void validarId(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "ID do funcionário inválido."
            );
        }
    }

    private void validarSetor(Long idSetor) throws SQLException {

        if (setorDAO.buscarPorId(idSetor) == null) {
            throw new IllegalArgumentException(
                    "Setor não encontrado."
            );
        }
    }
}
