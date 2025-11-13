package dominio;

import java.util.Objects;

/**
 * Representa um Objeto de Valor para coordenadas (x, y) no tabuleiro.
 * É imutável.
 */
public class Posicao {
    private final int x;
    private final int y;

    public Posicao(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Posicao mover(int deltaX, int deltaY) {
        return new Posicao(x + deltaX, y + deltaY);
    }

    public int getX() { return x; }
    public int getY() { return y; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Posicao posicao = (Posicao) obj;
        return x == posicao.x && y == posicao.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Posicao(" + x + ", " + y + ")";
    }
}
