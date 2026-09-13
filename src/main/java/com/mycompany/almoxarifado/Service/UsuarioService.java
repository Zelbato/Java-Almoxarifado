package com.mycompany.almoxarifado.Service;

import com.mycompany.almoxarifado.Dao.FuncionarioDAO;
import com.mycompany.almoxarifado.Dao.TipoUsuarioDAO;
import com.mycompany.almoxarifado.Dao.UsuarioDAO;
import com.mycompany.almoxarifado.Dto.UsuárioDTO;

import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author heito
 */
public class UsuarioService {

    private final UsuarioDAO usuarioDAO;
    private final FuncionarioDAO funcionarioDAO;
    private final TipoUsuarioDAO tipoUsuarioDAO;

    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
        this.funcionarioDAO = new FuncionarioDAO();
        this.tipoUsuarioDAO = new TipoUsuarioDAO();
    }

    public void cadastrar(UsuárioDTO usuario) throws SQLException {

        validar(usuario);
        validarFuncionario(usuario.getIdFuncionario());
        validarTipoUsuario(usuario.getIdTipoUsuario());

        if (usuarioDAO.existePorEmail(usuario.getEmail())) {
            throw new IllegalArgumentException(
                    "Já existe um usuário cadastrado com esse e-mail."
            );
        }

        if (usuarioDAO.existePorCpf(usuario.getCpf())) {
            throw new IllegalArgumentException(
                    "Já existe um usuário cadastrado com esse CPF."
            );
        }

        usuarioDAO.cadastrar(usuario);
    }

    public List<UsuárioDTO> listarTodos() throws SQLException {
        return usuarioDAO.listarTodos();
    }

    public UsuárioDTO buscarPorId(Long id) throws SQLException {

        validarId(id);

        UsuárioDTO usuario = usuarioDAO.buscarPorId(id);

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "Usuário não encontrado."
            );
        }

        return usuario;
    }

    public void atualizar(UsuárioDTO usuario) throws SQLException {

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "Usuário não informado."
            );
        }

        validarId(usuario.getIdUsuario());
        validar(usuario);
        validarFuncionario(usuario.getIdFuncionario());
        validarTipoUsuario(usuario.getIdTipoUsuario());

        UsuárioDTO usuarioExistente =
                usuarioDAO.buscarPorId(usuario.getIdUsuario());

        if (usuarioExistente == null) {
            throw new IllegalArgumentException(
                    "Usuário não encontrado."
            );
        }

        if (usuarioDAO.existePorEmailDiferenteDoId(
                usuario.getEmail(),
                usuario.getIdUsuario()
        )) {
            throw new IllegalArgumentException(
                    "Já existe outro usuário cadastrado com esse e-mail."
            );
        }

        if (usuarioDAO.existePorCpfDiferenteDoId(
                usuario.getCpf(),
                usuario.getIdUsuario()
        )) {
            throw new IllegalArgumentException(
                    "Já existe outro usuário cadastrado com esse CPF."
            );
        }

        usuarioDAO.atualizar(usuario);
    }

    public void excluir(Long id) throws SQLException {

        validarId(id);

        UsuárioDTO usuario = usuarioDAO.buscarPorId(id);

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "Usuário não encontrado."
            );
        }

        usuarioDAO.excluir(id);
    }

    private void validar(UsuárioDTO usuario) {

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "Usuário não informado."
            );
        }

        if (usuario.getIdTipoUsuario() == null
                || usuario.getIdTipoUsuario() <= 0) {

            throw new IllegalArgumentException(
                    "O tipo de usuário é obrigatório."
            );
        }

        if (usuario.getIdFuncionario() != null
                && usuario.getIdFuncionario() <= 0) {

            throw new IllegalArgumentException(
                    "ID do funcionário inválido."
            );
        }

        if (usuario.getNome() == null
                || usuario.getNome().isBlank()) {

            throw new IllegalArgumentException(
                    "O nome do usuário é obrigatório."
            );
        }

        if (usuario.getNome().length() > 150) {
            throw new IllegalArgumentException(
                    "O nome do usuário deve possuir no máximo 150 caracteres."
            );
        }

        if (usuario.getEmail() == null
                || usuario.getEmail().isBlank()) {

            throw new IllegalArgumentException(
                    "O e-mail do usuário é obrigatório."
            );
        }

        if (usuario.getEmail().length() > 150) {
            throw new IllegalArgumentException(
                    "O e-mail deve possuir no máximo 150 caracteres."
            );
        }

        if (!usuario.getEmail().matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        )) {
            throw new IllegalArgumentException(
                    "E-mail inválido."
            );
        }

        if (usuario.getSenha() == null
                || usuario.getSenha().isBlank()) {

            throw new IllegalArgumentException(
                    "A senha é obrigatória."
            );
        }

        if (usuario.getSenha().length() > 255) {
            throw new IllegalArgumentException(
                    "A senha deve possuir no máximo 255 caracteres."
            );
        }

        if (usuario.getCpf() == null
                || usuario.getCpf().isBlank()) {

            throw new IllegalArgumentException(
                    "O CPF do usuário é obrigatório."
            );
        }

        if (usuario.getCpf().length() > 14) {
            throw new IllegalArgumentException(
                    "O CPF deve possuir no máximo 14 caracteres."
            );
        }
    }

    private void validarId(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "ID do usuário inválido."
            );
        }
    }

    private void validarFuncionario(Long idFuncionario)
            throws SQLException {

        if (idFuncionario == null) {
            return;
        }

        if (funcionarioDAO.buscarPorId(idFuncionario) == null) {
            throw new IllegalArgumentException(
                    "Funcionário não encontrado."
            );
        }
    }

    private void validarTipoUsuario(Long idTipoUsuario)
            throws SQLException {

        if (tipoUsuarioDAO.buscarPorId(idTipoUsuario) == null) {
            throw new IllegalArgumentException(
                    "Tipo de usuário não encontrado."
            );
        }
    }
}