package com.mycompany.almoxarifado.Service;

import com.mycompany.almoxarifado.Dto.UsuarioDTO;

/** Sessão do usuário autenticado durante a execução do aplicativo Swing. */
public final class AuthSession {
    private static UsuarioDTO usuario;
    private static String tipoUsuario;

    private AuthSession() { }

    public static synchronized void iniciar(UsuarioDTO novoUsuario, String novoTipoUsuario) {
        if (novoUsuario == null || novoTipoUsuario == null || novoTipoUsuario.isBlank()) {
            throw new IllegalArgumentException("Dados de sessão inválidos.");
        }
        usuario = novoUsuario;
        tipoUsuario = novoTipoUsuario.trim().toUpperCase();
    }

    public static synchronized void sair() {
        usuario = null;
        tipoUsuario = null;
    }

    public static synchronized boolean estaAutenticado() { return usuario != null; }
    public static synchronized UsuarioDTO usuario() { return usuario; }
    public static synchronized String tipoUsuario() { return tipoUsuario; }

    public static synchronized boolean ehChefe() {
        return estaAutenticado() && "ALMOXARIFADO_CHEFE".equals(tipoUsuario);
    }

    public static synchronized boolean ehJunior() {
        return estaAutenticado() && "ALMOXARIFADO_JUNIOR".equals(tipoUsuario);
    }

    // Chefe e Júnior registram operações (retirada, devolução, manutenção, ocorrência)
    public static synchronized boolean podeEscrever() {
        return ehChefe() || ehJunior();
    }

    // As telas já mostram a mensagem de IllegalArgumentException, por isso
    // o bloqueio de permissão usa ela também.
    public static synchronized void exigirAutenticacao() {
        if (!estaAutenticado()) throw new IllegalArgumentException("É necessário fazer login para acessar o sistema.");
    }

    public static synchronized void exigirEscrita() {
        exigirAutenticacao();
        if (!podeEscrever()) throw new IllegalArgumentException("Seu perfil possui acesso somente para visualização.");
    }

    public static synchronized void exigirChefe() {
        exigirAutenticacao();
        if (!ehChefe()) throw new IllegalArgumentException("Somente o Almoxarifado Chefe pode realizar esta ação.");
    }
}
