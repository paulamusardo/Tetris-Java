package dominio;

/**
 * Gerencia a lógica de cálculo de pontos e níveis.
 */
public class SistemaPontuacao {
    // 0, 1, 2, 3, 4 linhas
    private static final int[] PONTOS_BASE = {0, 100, 300, 500, 800};
    private int pontuacaoTotal = 0;

    public int calcularPontos(int linhasEliminadas, int nivel) {
        if (linhasEliminadas < 0 || linhasEliminadas > 4) {
            throw new IllegalArgumentException("Linhas eliminadas deve ser 0-4");
        }
        int pontosGanhos = PONTOS_BASE[linhasEliminadas] * nivel;
        this.pontuacaoTotal += pontosGanhos;
        return pontosGanhos;
    }
    
    public void adicionarPontos(int linhasEliminadas, int nivel) {
         if (linhasEliminadas < 1 || linhasEliminadas > 4) return;
         int pontos = PONTOS_BASE[linhasEliminadas] * nivel;
         pontuacaoTotal += pontos;
    }

    public int calcularNovoNivel(int totalLinhas) {
        return (totalLinhas / 10) + 1;
    }

    public long calcularVelocidade(int nivel) {
        // Velocidade diminui com o nível (em ms)
        return Math.max(50, 1000 - (nivel * 50));
    }
    
    public int getPontuacaoTotal() {
        return pontuacaoTotal;
    }

    public void setPontuacaoTotal(int pontos) {
        this.pontuacaoTotal = pontos;
    }
}
