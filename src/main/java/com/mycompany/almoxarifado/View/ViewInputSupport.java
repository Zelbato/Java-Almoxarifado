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
import com.toedter.calendar.JDateChooser;

final class ViewInputSupport {
    private static final DateTimeFormatter DATA_ISO = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DATA_BR = DateTimeFormatter.ofPattern("dd/MM/uuuu");
    private static final DateTimeFormatter DATA_HORA_ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm[:ss]");
    private static final DateTimeFormatter DATA_HORA_BR = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm[:ss]");

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

    // campos de data + hora (JDateChooser e JSpinner) usados nas manutenções e ocorrências
    static void configurarHora(javax.swing.JSpinner... spinners) {
        for (javax.swing.JSpinner spinner : spinners) {
            spinner.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(), null, null, java.util.Calendar.MINUTE));
            spinner.setEditor(new javax.swing.JSpinner.DateEditor(spinner, "HH:mm"));
        }
    }

    static LocalDateTime dataHora(JDateChooser data, javax.swing.JSpinner hora, String nome, boolean obrigatoria) {
        if (data.getDate() == null) {
            if (obrigatoria) throw new IllegalArgumentException(nome + " é obrigatória.");
            return null;
        }
        java.time.ZoneId zona = java.time.ZoneId.systemDefault();
        LocalDate dia = data.getDate().toInstant().atZone(zona).toLocalDate();
        java.time.LocalTime horario = ((java.util.Date) hora.getValue()).toInstant().atZone(zona).toLocalTime().withSecond(0).withNano(0);
        return LocalDateTime.of(dia, horario);
    }

    static void selecionar(JDateChooser data, javax.swing.JSpinner hora, LocalDateTime valor) {
        if (valor == null) {
            data.setDate(null);
            hora.setValue(new java.util.Date());
            return;
        }
        java.util.Date convertido = java.util.Date.from(valor.atZone(java.time.ZoneId.systemDefault()).toInstant());
        data.setDate(convertido);
        hora.setValue(convertido);
    }

    // datas na tabela no formato brasileiro
    static String formatar(LocalDate data) {
        return data == null ? "" : DATA_BR.format(data);
    }

    static String formatar(LocalDateTime dataHora) {
        return dataHora == null ? "" : DATA_HORA_BR.format(dataHora);
    }

    // usado nas tabelas para mostrar o nome em vez do id
    static String nomeDoItem(JComboBox<ComboItem> campo, Long id) {
        for (int indice = 0; indice < campo.getItemCount(); indice++) {
            if (campo.getItemAt(indice).getId().equals(id)) {
                return campo.getItemAt(indice).toString();
            }
        }
        return String.valueOf(id);
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
            return tentarData(valor);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(nome + " deve estar no formato dd/MM/yyyy.");
        }
    }

    static LocalDate data(JDateChooser campo, String nome, boolean obrigatoria) {
        java.util.Date valor = campo.getDate();
        if (valor == null) {
            if (obrigatoria) throw new IllegalArgumentException(nome + " é obrigatória.");
            return null;
        }
        return valor.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }

    static void selecionar(JDateChooser campo, LocalDate data) {
        campo.setDate(data == null ? null : java.util.Date.from(data.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()));
    }

    static LocalDateTime dataHora(JTextField campo, String nome, boolean obrigatoria) {
        String valor = texto(campo);
        if (valor == null) {
            if (obrigatoria) throw new IllegalArgumentException(nome + " é obrigatória.");
            return null;
        }
        try {
            return tentarDataHora(valor);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(nome + " deve estar no formato dd/MM/yyyy HH:mm ou dd/MM/yyyy HH:mm:ss.");
        }
    }

    static void preencher(JTextField campo, Object valor) {
        if (valor instanceof LocalDate data) {
            campo.setText(DATA_BR.format(data));
        } else if (valor instanceof LocalDateTime dataHora) {
            campo.setText(DATA_HORA_BR.format(dataHora));
        } else {
            campo.setText(valor == null ? "" : valor.toString());
        }
    }

    private static LocalDate tentarData(String valor) {
        try {
            return LocalDate.parse(valor, DATA_BR);
        } catch (DateTimeParseException ex) {
            return LocalDate.parse(valor, DATA_ISO);
        }
    }

    private static LocalDateTime tentarDataHora(String valor) {
        try {
            return LocalDateTime.parse(valor, DATA_HORA_BR);
        } catch (DateTimeParseException ex) {
            return LocalDateTime.parse(valor, DATA_HORA_ISO);
        }
    }
}
