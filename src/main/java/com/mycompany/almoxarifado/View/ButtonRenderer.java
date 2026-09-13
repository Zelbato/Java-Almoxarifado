package com.mycompany.almoxarifado.View;

import java.awt.Component;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

public class ButtonRenderer extends JLabel implements TableCellRenderer {

    private final ImageIcon icon;

    public ButtonRenderer() {
        ImageIcon original = new ImageIcon(
                getClass().getResource("/icons/trash.png")
        );

        Image imagem = original.getImage().getScaledInstance(
                18,
                18,
                Image.SCALE_SMOOTH
        );

        icon = new ImageIcon(imagem);

        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);
        setOpaque(false);
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column
    ) {
        setIcon(icon);
        setText("");

        return this;
    }
}
