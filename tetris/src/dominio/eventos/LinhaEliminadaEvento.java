package dominio.eventos;

public class LinhaEliminadaEvento extends EventoDominio {
    private final int quantidade;
    private final int novoNivel;
    private final int pontos;

    public LinhaEliminadaEvento(int quantidade, int novoNivel, int pontos) {
        super();
        this.quantidade = quantidade;
        this.novoNivel = novoNivel;
        this.pontos = pontos;
    }

    public int getQuantidade() { return quantidade; }
    public int getNovoNivel() { return novoNivel; }
    public int getPontos() { return pontos; }
}
