package com.mycompany.almoxarifado.Service;

import com.mycompany.almoxarifado.Dao.UsuarioDAO;
import com.mycompany.almoxarifado.Dto.UsuarioCredenciaisDTO;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

public class AutenticacaoService {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public void autenticar(String email, String senha) throws SQLException {
        if (email == null || email.isBlank() || senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("Informe o e-mail e a senha.");
        }

        UsuarioCredenciaisDTO credenciais = usuarioDAO.buscarCredenciaisPorEmail(email.trim());
        if (credenciais == null || !credenciais.getUsuario().isAtivo()
                || !BCrypt.checkpw(senha, credenciais.getSenhaHash())) {
            throw new IllegalArgumentException("E-mail ou senha inválidos.");
        }

        AuthSession.iniciar(credenciais.getUsuario(), credenciais.getTipoUsuario());
    }
}
