package dominio;

import java.util.Arrays;

/**
 * Representa o tabuleiro do jogo, a grade e as regras de colisão e eliminação.
 */
public class Tabuleiro {
    private static final int LARGURA = 10;
    private static final int ALTURA = 20;
    private final boolean[][] grid;

    public Tabuleiro() {
        this.grid = new boolean[ALTURA][LARGURA];
    }

    public boolean posicaoValida(Tetromino tetromino) {
        boolean[][] forma = tetromino.getForma();
        Posicao pos = tetromino.getPosicao();

        for (int i = 0; i < forma.length; i++) {
            for (int j = 0; j < forma[i].length; j++) {
                if (forma[i][j]) {
                    int x = pos.getX() + j;
                    int y = pos.getY() + i;

                    // 1. Verifica limites das bordas
                    if (x < 0 || x >= LARGURA || y >= ALTURA) {
                        return false;
                    }
                    // 2. Verifica colisão com peças existentes (ignora topo negativo)
                    if (y >= 0 && grid[y][x]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void fixarTetromino(Tetromino tetromino) {
        boolean[][] forma = tetromino.getForma();
        Posicao pos = tetromino.getPosicao();

        for (int i = 0; i < forma.length; i++) {
            for (int j = 0; j < forma[i].length; j++) {
                if (forma[i][j]) {
                    int x = pos.getX() + j;
                    int y = pos.getY() + i;
                    if (y >= 0 && y < ALTURA && x >= 0 && x < LARGURA) {
                        grid[y][x] = true;
                    }
                }
            }
        }
    }

    public int eliminarLinhasCompletas() {
        int linhasEliminadas = 0;
        for (int y = ALTURA - 1; y >= 0; y--) {
            if (linhaCompleta(y)) {
                eliminarLinha(y);
                linhasEliminadas++;
                y++; // Verifica a mesma linha novamente (agora com o conteúdo de cima)
            }
        }
        return linhasEliminadas;
    }

    private boolean linhaCompleta(int y) {
        for (int x = 0; x < LARGURA; x++) {
            if (!grid[y][x]) {
                return false;
            }
        }
        return true;
    }

    private void eliminarLinha(int linhaParaEliminar) {
        // Mover todas as linhas acima para baixo
        for (int y = linhaParaEliminar; y > 0; y--) {
            System.arraycopy(grid[y - 1], 0, grid[y], 0, LARGURA);
        }
        // Limpar linha superior
        Arrays.fill(grid[0], false);
    }
    
    // --- Métodos auxiliares para testes e serialização ---
    
    public boolean[][] getGrid() {
        boolean[][] copia = new boolean[ALTURA][LARGURA];
        for (int i = 0; i < ALTURA; i++) {
            System.arraycopy(grid[i], 0, copia[i], 0, LARGURA);
        }
        return copia;
    }

    public void setGrid(boolean[][] novoGrid) {
        for (int i = 0; i < ALTURA && i < novoGrid.length; i++) {
            System.arraycopy(novoGrid[i], 0, grid[i], 0, LARGURA);
        }
    }
    
    public void definirBloco(int x, int y, boolean valor) {
        if (x >= 0 && x < LARGURA && y >= 0 && y < ALTURA) {
            grid[y][x] = valor;
        }
    }

    public boolean temBloco(int x, int y) {
        if (x >= 0 && x < LARGURA && y >= 0 && y < ALTURA) {
            return grid[y][x];
        }
        return false;
    }
    
    // Método para verificar se há algum bloco (usado em testes)
    public boolean temBlocosFixos() {
        for (int y = 0; y < ALTURA; y++) {
            for (int x = 0; x < LARGURA; x++) {
                if (grid[y][x]) {
                    return true;
                }
            }
        }
        return false;
    }
    
    // Método para preencher linha (usado em testes)
    public void preencherLinha(int y) {
        for (int x = 0; x < LARGURA; x++) {
            definirBloco(x, y, true);
        }
    }

    public static int getLargura() { return LARGURA; }
    public static int getAltura() { return ALTURA; }
}
