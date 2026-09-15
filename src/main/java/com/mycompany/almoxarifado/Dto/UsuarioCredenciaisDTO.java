package com.mycompany.almoxarifado.Dto;

/** Dados internos usados somente durante a autenticação. */
public final class UsuarioCredenciaisDTO {
    private final UsuarioDTO usuario;
    private final String senhaHash;
    private final String tipoUsuario;

    public UsuarioCredenciaisDTO(UsuarioDTO usuario, String senhaHash, String tipoUsuario) {
        this.usuario = usuario;
        this.senhaHash = senhaHash;
        this.tipoUsuario = tipoUsuario;
    }

    public UsuarioDTO getUsuario() { return usuario; }
    public String getSenhaHash() { return senhaHash; }
    public String getTipoUsuario() { return tipoUsuario; }
}
