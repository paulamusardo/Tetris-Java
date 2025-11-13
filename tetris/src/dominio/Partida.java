package dominio;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import dominio.eventos.EventoDominio;
import dominio.eventos.LinhaEliminadaEvento;
import dominio.eventos.GameOverEvento;

/**
 * Agregado Raiz (DDD). Controla o fluxo principal do jogo,
 * gerenciando o tabuleiro, a peça atual, pontuação e estado.
 */
public class Partida {
    private final String id;
    private final String jogadorId;
    private final Tabuleiro tabuleiro;
    private final SistemaPontuacao sistemaPontuacao;
    private Tetromino tetrominoAtual;
    private Tetromino proximoTetromino;
    private int totalLinhas;
    private int nivel;
    private boolean gameOver;
    private final Random random;
    private List<EventoDominio> eventos;
    
    private static final int LINHAS_POR_NIVEL = 10;

    public Partida(String jogadorId) {
        this.id = java.util.UUID.randomUUID().toString();
        this.jogadorId = jogadorId;
        this.tabuleiro = new Tabuleiro();
        this.sistemaPontuacao = new SistemaPontuacao();
        this.totalLinhas = 0;
        this.nivel = 1;
        this.gameOver = false;
        this.random = new Random();
        this.eventos = new ArrayList<>();
        this.proximoTetromino = criarTetrominoAleatorio();
        this.tetrominoAtual = criarTetrominoAleatorio();
        
        // Verifica se o primeiro tetromino já colide (game over imediato)
        if (!tabuleiro.posicaoValida(this.tetrominoAtual)) {
            this.gameOver = true;
        }
    }

    private Tetromino criarTetrominoAleatorio() {
        TipoTetromino[] tipos = TipoTetromino.values();
        TipoTetromino tipoAleatorio = tipos[random.nextInt(tipos.length)];
        // Posição inicial (centro-topo)
        return new Tetromino(tipoAleatorio, new Posicao(4, 0));
    }
    
    // Tenta mover a peça. Retorna true se moveu, false se colidiu.
    public boolean moverTetromino(int deltaX, int deltaY) {
        if (gameOver) return false;

        Tetromino copia = tetrominoAtual.criarCopia();
        copia.mover(deltaX, deltaY);

        if (tabuleiro.posicaoValida(copia)) {
            tetrominoAtual.mover(deltaX, deltaY);
            return true;
        }
        return false;
    }

    public boolean rotacionarTetromino() {
        if (gameOver) return false;
        
        Tetromino copia = tetrominoAtual.criarCopia();
        copia.rotacionar();

        if (tabuleiro.posicaoValida(copia)) {
            tetrominoAtual.rotacionar();
            return true;
        }
        return false;
    }

    /**
     * Processa um "tick" do jogo (queda automática).
     * Retorna true se a peça ainda está caindo, false se fixou.
     */
    public boolean processarQueda() {
        if (gameOver) return false;

        if (moverTetromino(0, 1)) {
            // Conseguiu mover para baixo, peça continua caindo
            return true; 
        } else {
            // Não conseguiu mover:
            // 1. Fixar tetromino atual no tabuleiro
            tabuleiro.fixarTetromino(tetrominoAtual);

            // 2. Eliminar linhas e calcular pontos
            int linhasEliminadas = tabuleiro.eliminarLinhasCompletas();
            if (linhasEliminadas > 0) {
                processarEliminacaoLinhas(linhasEliminadas);
            }

            // 3. Criar novo tetromino
            tetrominoAtual = proximoTetromino;
            proximoTetromino = criarTetrominoAleatorio();

            // 4. Verificar game over
            if (!tabuleiro.posicaoValida(tetrominoAtual)) {
                gameOver = true;
                eventos.add(new GameOverEvento(jogadorId, sistemaPontuacao.getPontuacaoTotal()));
                return false;
            }
            
            // Peça foi fixada
            return false;
        }
    }
    
    private void processarEliminacaoLinhas(int linhas) {
        totalLinhas += linhas;
        sistemaPontuacao.adicionarPontos(linhas, nivel);

        // Verificar aumento de nível
        int novoNivel = 1 + (totalLinhas / LINHAS_POR_NIVEL);
        if (novoNivel > nivel) {
            nivel = novoNivel;
        }
        eventos.add(new LinhaEliminadaEvento(linhas, nivel, sistemaPontuacao.getPontuacaoTotal()));
    }
    
    // --- Getters para Testes e UI ---
    
    public String getId() { return id; }
    public String getJogadorId() { return jogadorId; }
    public Tetromino getTetrominoAtual() { return tetrominoAtual; }
    public Tetromino getProximoTetromino() { return proximoTetromino; }
    public int getPontuacao() { return sistemaPontuacao.getPontuacaoTotal(); }
    public int getNivel() { return nivel; }
    public int getTotalLinhas() { return totalLinhas; }
    public boolean isGameOver() { return gameOver; }
    public Tabuleiro getTabuleiro() { return tabuleiro; }
    public List<EventoDominio> getEventos() { return eventos; }
    
    // --- Setters (para carregar estado salvo) ---
    public void setPontuacao(int p) { sistemaPontuacao.setPontuacaoTotal(p); }
    public void setNivel(int n) { this.nivel = n; }
    public void setTotalLinhas(int l) { this.totalLinhas = l; }
    public void setGameOver(boolean g) { this.gameOver = g; }
    public void setTetrominoAtual(Tetromino t) { this.tetrominoAtual = t; }
    public void setProximoTetromino(Tetromino t) { this.proximoTetromino = t; }
}
