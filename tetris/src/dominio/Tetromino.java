package dominio;

/**
 * Representa a entidade Tetromino (a peça do jogo).
 * Controla seu tipo, posição e rotação.
 */
public class Tetromino {
    private final TipoTetromino tipo;
    private Posicao posicao;
    private int rotacao;

    public Tetromino(TipoTetromino tipo, Posicao posicao) {
        this.tipo = tipo;
        this.posicao = posicao;
        this.rotacao = 0;
    }

    public void mover(int deltaX, int deltaY) {
        this.posicao = posicao.mover(deltaX, deltaY);
    }

    public void rotacionar() {
        this.rotacao = (rotacao + 1) % 4; // Assume 4 rotações
    }

    public boolean[][] getForma() {
        return tipo.getForma(rotacao);
    }

    public Posicao getPosicao() { return posicao; }
    public TipoTetromino getTipo() { return tipo; }
    public int getRotacao() { return rotacao; }
    
    // Método para clonagem usado nos testes de rotação
    public Tetromino criarCopia() {
        Tetromino copia = new Tetromino(this.tipo, this.posicao);
        copia.rotacao = this.rotacao;
        return copia;
    }

    @Override
    public String toString() {
        return "Tetromino{" + tipo + ", " + posicao + ", rot=" + rotacao + "}";
    }
}
