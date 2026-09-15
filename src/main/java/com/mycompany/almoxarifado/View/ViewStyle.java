package com.mycompany.almoxarifado.View;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JLabel;

/** Cores e dimensões compartilhadas pelas JFrame Forms do almoxarifado. */
final class ViewStyle {
    static final Color AZUL = new Color(0, 78, 174);
    static final Color VERDE = new Color(53, 158, 77);
    static final Color LARANJA = new Color(228, 146, 10);
    static final Color VERMELHO = new Color(214, 62, 50);
    static final Font FONTE_BOTAO = new Font("Segoe UI", Font.BOLD, 12);

    private ViewStyle() { }

    static void botao(JButton botao, Color cor, int largura) {
        botao.setBackground(cor);
        botao.setForeground(Color.WHITE);
        botao.setFont(FONTE_BOTAO);
        botao.setPreferredSize(new Dimension(largura, 38));
        botao.setFocusPainted(false);
        botao.setContentAreaFilled(true);
        botao.setOpaque(true);
    }

    static void botoesCrud(JButton novo, JButton salvar, JButton editar, JButton excluir) {
        botao(novo, AZUL, 86);
        botao(salvar, VERDE, 87);
        botao(editar, LARANJA, 95);
        botao(excluir, VERMELHO, 96);
    }

    static void titulo(JLabel titulo) {
        titulo.setFont(FONTE_BOTAO);
        titulo.setForeground(AZUL);
    }
}
