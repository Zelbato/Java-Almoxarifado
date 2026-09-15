package com.mycompany.almoxarifado.View;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.math.BigDecimal;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

final class ViewInputSupport {
    private static final DateTimeFormatter DATA = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm[:ss]");

    private ViewInputSupport() { }

    static String texto(JTextField campo) {
        String valor = campo.getText().trim();
        return valor.isEmpty() ? null : valor;
    }

    static Long id(JTextField campo, String nome, boolean obrigatorio) {
        String valor = texto(campo);
        if (valor == null) {
            if (obrigatorio) throw new IllegalArgumentException(nome + " é obrigatório.");
            return null;
        }
        try {
            long id = Long.parseLong(valor);
            if (id <= 0) throw new NumberFormatException();
            return id;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(nome + " deve ser um ID numérico válido.");
        }
    }

    static Long id(JComboBox<ComboItem> campo, String nome, boolean obrigatorio) {
        ComboItem item = (ComboItem) campo.getSelectedItem();
        if (item == null) {
            if (obrigatorio) throw new IllegalArgumentException(nome + " é obrigatório.");
            return null;
        }
        return item.getId();
    }

    static void selecionar(JComboBox<ComboItem> campo, Long id) {
        if (id == null) {
            campo.setSelectedIndex(-1);
            return;
        }
        for (int indice = 0; indice < campo.getItemCount(); indice++) {
            if (id.equals(campo.getItemAt(indice).getId())) {
                campo.setSelectedIndex(indice);
                return;
            }
        }
        campo.setSelectedIndex(-1);
    }

    static void substituirNoPainel(JPanel painel, JComponent antigo, JComponent novo) {
        java.awt.Component[] componentes = painel.getComponents();
        for (int indice = 0; indice < componentes.length; indice++) {
            if (componentes[indice] == antigo) {
                painel.remove(indice);
                painel.add(novo, indice);
                painel.revalidate();
                painel.repaint();
                return;
            }
        }
        throw new IllegalStateException("Campo do formulário não foi localizado.");
    }

    static Integer inteiro(JTextField campo, String nome, boolean obrigatorio) {
        String valor = texto(campo);
        if (valor == null) {
            if (obrigatorio) throw new IllegalArgumentException(nome + " é obrigatório.");
            return null;
        }
        try {
            int numero = Integer.parseInt(valor);
            if (numero < 0) throw new NumberFormatException();
            return numero;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(nome + " deve ser um número inteiro válido.");
        }
    }

    static BigDecimal decimal(JTextField campo, String nome, boolean obrigatorio) {
        String valor = texto(campo);
        if (valor == null) {
            if (obrigatorio) throw new IllegalArgumentException(nome + " é obrigatório.");
            return null;
        }
        try {
            return new BigDecimal(valor.replace(',', '.'));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(nome + " deve ser um valor numérico válido.");
        }
    }

    static LocalDate data(JTextField campo, String nome, boolean obrigatoria) {
        String valor = texto(campo);
        if (valor == null) {
            if (obrigatoria) throw new IllegalArgumentException(nome + " é obrigatória.");
            return null;
        }
        try {
            return LocalDate.parse(valor, DATA);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(nome + " deve estar no formato yyyy-MM-dd.");
        }
    }

    static LocalDateTime dataHora(JTextField campo, String nome, boolean obrigatoria) {
        String valor = texto(campo);
        if (valor == null) {
            if (obrigatoria) throw new IllegalArgumentException(nome + " é obrigatória.");
            return null;
        }
        try {
            return LocalDateTime.parse(valor, DATA_HORA);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(nome + " deve estar no formato yyyy-MM-dd HH:mm ou yyyy-MM-dd HH:mm:ss.");
        }
    }

    static void preencher(JTextField campo, Object valor) {
        campo.setText(valor == null ? "" : valor.toString());
    }
}
