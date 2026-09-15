package com.mycompany.almoxarifado.View;

/** Item de uma lista de seleção, preservando o ID que será gravado no banco. */
final class ComboItem {
    private final Long id;
    private final String descricao;

    ComboItem(Long id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
