package com.mycompany.almoxarifado.View;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JPanel;

/** Painel de gráfico de barras sem dependências externas. */
public class BarChartPanel extends JPanel {

    private static final Color EIXO = new Color(218, 223, 229);
    private static final Color TEXTO = new Color(108, 117, 125);
    private static final Color[] CORES = {
        ViewStyle.AZUL,
        ViewStyle.VERDE,
        ViewStyle.LARANJA,
        ViewStyle.VERMELHO,
        new Color(111, 66, 193),
        new Color(23, 162, 184)
    };

    private Map<String, Integer> dados = new LinkedHashMap<>();

    public BarChartPanel() {
        setOpaque(true);
        setBackground(Color.WHITE);
    }

    void setDados(Map<String, Integer> dados) {
        this.dados = new LinkedHashMap<>(dados);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        int largura = getWidth();
        int altura = getHeight();
        int esquerda = 34;
        int direita = 18;
        int topo = 24;
        int base = altura - 46;

        if (dados.isEmpty() || largura <= esquerda + direita || base <= topo) {
            desenharSemDados(g2, largura, altura);
            g2.dispose();
            return;
        }

        int maiorValor = Math.max(1, dados.values().stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0));
        int areaGrafico = largura - esquerda - direita;
        int espaco = Math.max(6, areaGrafico / (dados.size() * 5));
        int larguraBarra = Math.max(
                16,
                (areaGrafico - espaco * (dados.size() + 1)) / dados.size()
        );

        if (larguraBarra * dados.size() + espaco * (dados.size() + 1) > areaGrafico) {
            larguraBarra = Math.max(8, (areaGrafico - espaco) / dados.size());
        }

        g2.setColor(EIXO);
        g2.setStroke(new BasicStroke(1f));
        g2.drawLine(esquerda, base, largura - direita, base);

        FontMetrics rotulo = g2.getFontMetrics(
                new Font("Segoe UI", Font.PLAIN, 10)
        );
        int indice = 0;
        int x = esquerda + espaco;

        for (Map.Entry<String, Integer> entrada : dados.entrySet()) {
            int valor = entrada.getValue();
            int alturaBarra = Math.round(
                    ((float) valor / maiorValor) * (base - topo - 18)
            );
            int y = base - alturaBarra;

            g2.setColor(CORES[indice % CORES.length]);
            g2.fillRoundRect(x, y, larguraBarra, alturaBarra, 8, 8);

            g2.setColor(TEXTO);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            String textoValor = String.valueOf(valor);
            int larguraValor = g2.getFontMetrics().stringWidth(textoValor);
            g2.drawString(textoValor, x + (larguraBarra - larguraValor) / 2, y - 6);

            String textoRotulo = abreviar(entrada.getKey(), 13);
            // corta mais o rótulo quando as barras são estreitas, pra não encavalar
            while (textoRotulo.length() > 3
                    && rotulo.stringWidth(textoRotulo) > larguraBarra + espaco - 4) {
                textoRotulo = textoRotulo.substring(0, textoRotulo.length() - 2) + "…";
            }
            int larguraRotulo = rotulo.stringWidth(textoRotulo);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2.drawString(
                    textoRotulo,
                    x + (larguraBarra - larguraRotulo) / 2,
                    base + 18
            );

            x += larguraBarra + espaco;
            indice++;
        }

        g2.dispose();
    }

    private void desenharSemDados(Graphics2D g2, int largura, int altura) {
        g2.setColor(TEXTO);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        String texto = "Nenhum dado disponível.";
        int x = (largura - g2.getFontMetrics().stringWidth(texto)) / 2;
        int y = altura / 2;
        g2.drawString(texto, x, y);
    }

    private String abreviar(String texto, int tamanhoMaximo) {
        String rotulo = texto.replace('_', ' ');
        return rotulo.length() <= tamanhoMaximo
                ? rotulo
                : rotulo.substring(0, tamanhoMaximo - 1) + "…";
    }
}
