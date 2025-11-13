package dominio;

import java.util.Random;

/**
 * Padrão Factory para criar peças aleatórias.
 */
public class TetrominoFactory {
    private static final Random random = new Random();
    
    public static Tetromino createRandomPiece() {
        TipoTetromino[] tipos = TipoTetromino.values();
        TipoTetromino tipoAleatorio = tipos[random.nextInt(tipos.length)];
        return new Tetromino(tipoAleatorio, new Posicao(4, 0));
    }
}
