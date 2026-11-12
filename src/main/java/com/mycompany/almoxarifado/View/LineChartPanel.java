package com.mycompany.almoxarifado.View;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;

/** Painel de gráfico de linha sem dependências externas. */
public class LineChartPanel extends JPanel {

    private static final Color EIXO = new Color(218, 223, 229);
    private static final Color TEXTO = new Color(108, 117, 125);
    private Map<String, Integer> dados = new LinkedHashMap<>();

    public LineChartPanel() {
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
        int esquerda = 36;
        int direita = 20;
        int topo = 24;
        int base = altura - 46;

        if (dados.isEmpty() || largura <= esquerda + direita || base <= topo) {
            desenharSemDados(g2, largura, altura);
            g2.dispose();
            return;
        }

        List<Map.Entry<String, Integer>> pontos = new ArrayList<>(
                dados.entrySet()
        );
        int maiorValor = Math.max(1, pontos.stream()
                .mapToInt(Map.Entry::getValue)
                .max()
                .orElse(0));
        int areaGrafico = largura - esquerda - direita;
        int distancia = pontos.size() == 1
                ? 0
                : areaGrafico / (pontos.size() - 1);

        g2.setColor(EIXO);
        g2.setStroke(new BasicStroke(1f));
        g2.drawLine(esquerda, base, largura - direita, base);

        g2.setColor(ViewStyle.AZUL);
        g2.setStroke(new BasicStroke(2.5f));

        int xAnterior = 0;
        int yAnterior = 0;
        for (int indice = 0; indice < pontos.size(); indice++) {
            Map.Entry<String, Integer> ponto = pontos.get(indice);
            int x = esquerda + indice * distancia;
            int y = base - Math.round(
                    ((float) ponto.getValue() / maiorValor) * (base - topo - 18)
            );

            if (indice > 0) {
                g2.drawLine(xAnterior, yAnterior, x, y);
            }

            g2.setColor(Color.WHITE);
            g2.fillOval(x - 5, y - 5, 10, 10);
            g2.setColor(ViewStyle.AZUL);
            g2.drawOval(x - 5, y - 5, 10, 10);

            g2.setColor(TEXTO);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            String valor = String.valueOf(ponto.getValue());
            g2.drawString(
                    valor,
                    x - g2.getFontMetrics().stringWidth(valor) / 2,
                    y - 10
            );

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2.drawString(
                    ponto.getKey(),
                    x - g2.getFontMetrics().stringWidth(ponto.getKey()) / 2,
                    base + 18
            );

            g2.setColor(ViewStyle.AZUL);
            xAnterior = x;
            yAnterior = y;
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
}
