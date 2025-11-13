package dominio.eventos;

public class GameOverEvento extends EventoDominio {
    private final String jogadorId;
    private final int pontuacaoFinal;

    public GameOverEvento(String jogadorId, int pontuacaoFinal) {
        super();
        this.jogadorId = jogadorId;
        this.pontuacaoFinal = pontuacaoFinal;
    }

    public String getJogadorId() { return jogadorId; }
    public int getPontuacaoFinal() { return pontuacaoFinal; }
}
