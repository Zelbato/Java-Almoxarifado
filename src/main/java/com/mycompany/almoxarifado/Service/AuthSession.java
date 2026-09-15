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

    public static synchronized boolean podeEscrever() {
        if (!estaAutenticado()) return false;
        return switch (tipoUsuario) {
            case "ADMINISTRADOR", "ALMOXARIFADO_CHEFE", "ALMOXARIFE", "GERENTE" -> true;
            default -> false;
        };
    }

    public static synchronized void exigirAutenticacao() {
        if (!estaAutenticado()) throw new SecurityException("É necessário fazer login para acessar o sistema.");
    }

    public static synchronized void exigirEscrita() {
        exigirAutenticacao();
        if (!podeEscrever()) throw new SecurityException("Seu perfil possui acesso somente para visualização.");
    }
}
