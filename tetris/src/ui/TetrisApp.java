package ui;

import dominio.Partida;
import dominio.Tabuleiro;
import dominio.Tetromino;
import dominio.Posicao;
import dominio.TipoTetromino;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JColorChooser;
import javax.swing.Timer;
import persistencia.HistoricoPontuacao;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class TetrisApp extends JFrame {
    private Partida partida;
    private JogoPanel painel;
    private Timer timer;
    private boolean pausado = false;
    private Color corTemaFundo = new Color(20, 22, 28); // Tema padrão escuro

    public TetrisApp() {
        super("Tetris - POO");
        this.partida = new Partida("jogador-local");
        this.painel = new JogoPanel(partida, this);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        criarMenu();
        setContentPane(painel);
        pack();
        setLocationRelativeTo(null);
        setFocusable(true);
        painel.setFocusable(true);
        painel.requestFocusInWindow();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_P:
                        // Pausar/Despausar
                        pausado = !pausado;
                        if (pausado) {
                            timer.stop();
                        } else {
                            timer.start();
                        }
                        painel.repaint();
                        return;
                    case KeyEvent.VK_R:
                        // Reiniciar jogo
                        reiniciarJogo();
                        return;
                }
                
                if (partida.isGameOver() || pausado) return;
                
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        partida.moverTetromino(-1, 0);
                        break;
                    case KeyEvent.VK_RIGHT:
                        partida.moverTetromino(1, 0);
                        break;
                    case KeyEvent.VK_DOWN:
                        // queda suave
                        partida.moverTetromino(0, 1);
                        break;
                    case KeyEvent.VK_UP:
                        partida.rotacionarTetromino();
                        break;
                    case KeyEvent.VK_SPACE:
                        // hard drop: desce até não poder e fixa
                        while (partida.moverTetromino(0, 1)) { /* loop até travar */ }
                        partida.processarQueda();
                        break;
                }
                painel.repaint();
            }
        });

        iniciarLoop();
    }
    
    private void criarMenu() {
        JMenuBar menuBar = new JMenuBar();
        
        // Menu Histórico
        JMenu menuHistorico = new JMenu("Histórico");
        JMenuItem itemVerHistorico = new JMenuItem("Ver Histórico");
        itemVerHistorico.addActionListener(e -> mostrarHistorico());
        JMenuItem itemLimparHistorico = new JMenuItem("Limpar Histórico");
        itemLimparHistorico.addActionListener(e -> {
            HistoricoPontuacao.limparHistorico();
            painel.repaint();
        });
        menuHistorico.add(itemVerHistorico);
        menuHistorico.addSeparator();
        menuHistorico.add(itemLimparHistorico);
        
        // Menu Controles
        JMenu menuControles = new JMenu("Controles");
        JMenuItem itemControles = new JMenuItem("Ver Controles");
        itemControles.addActionListener(e -> mostrarControles());
        menuControles.add(itemControles);
        
        // Menu Tema
        JMenu menuTema = new JMenu("Tema");
        JMenuItem itemTemaEscuro = new JMenuItem("Escuro (Padrão)");
        itemTemaEscuro.addActionListener(e -> alterarTema(new Color(20, 22, 28)));
        JMenuItem itemTemaRosa = new JMenuItem("Rosa");
        itemTemaRosa.addActionListener(e -> alterarTema(new Color(255, 192, 203)));
        JMenuItem itemTemaAzul = new JMenuItem("Azul");
        itemTemaAzul.addActionListener(e -> alterarTema(new Color(135, 206, 250)));
        JMenuItem itemTemaVerde = new JMenuItem("Verde");
        itemTemaVerde.addActionListener(e -> alterarTema(new Color(144, 238, 144)));
        JMenuItem itemTemaRoxo = new JMenuItem("Roxo");
        itemTemaRoxo.addActionListener(e -> alterarTema(new Color(221, 160, 221)));
        JMenuItem itemTemaAmarelo = new JMenuItem("Amarelo");
        itemTemaAmarelo.addActionListener(e -> alterarTema(new Color(255, 255, 224)));
        JMenuItem itemTemaPersonalizado = new JMenuItem("Personalizado...");
        itemTemaPersonalizado.addActionListener(e -> escolherCorPersonalizada());
        menuTema.add(itemTemaEscuro);
        menuTema.addSeparator();
        menuTema.add(itemTemaRosa);
        menuTema.add(itemTemaAzul);
        menuTema.add(itemTemaVerde);
        menuTema.add(itemTemaRoxo);
        menuTema.add(itemTemaAmarelo);
        menuTema.addSeparator();
        menuTema.add(itemTemaPersonalizado);
        
        menuBar.add(menuHistorico);
        menuBar.add(menuControles);
        menuBar.add(menuTema);
        setJMenuBar(menuBar);
    }
    
    private void mostrarControles() {
        String controles = "CONTROLES DO TECLADO:\n\n" +
                          "← →     Mover peça\n" +
                          "↑        Rotacionar\n" +
                          "↓        Descer\n" +
                          "Espaço   Hard Drop (queda rápida)\n" +
                          "P        Pausar/Despausar\n" +
                          "R        Reiniciar jogo";
        JOptionPane.showMessageDialog(this, controles, "Controles", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void alterarTema(Color corFundo) {
        this.corTemaFundo = corFundo;
        if (painel != null) {
            painel.setCorTemaFundo(corFundo);
            painel.repaint();
        }
    }
    
    private void escolherCorPersonalizada() {
        Color corEscolhida = JColorChooser.showDialog(
            this,
            "Escolher Cor do Tema",
            corTemaFundo
        );
        if (corEscolhida != null) {
            alterarTema(corEscolhida);
        }
    }
    
    /**
     * Calcula a cor do texto baseada na luminosidade do fundo.
     * Retorna branco para fundos escuros e preto para fundos claros.
     */
    public static Color calcularCorTexto(Color corFundo) {
        // Calcular luminosidade usando fórmula padrão
        double luminosidade = (0.299 * corFundo.getRed() + 
                               0.587 * corFundo.getGreen() + 
                               0.114 * corFundo.getBlue()) / 255.0;
        
        // Se a luminosidade for menor que 0.5, usar texto branco, senão preto
        if (luminosidade < 0.5) {
            return Color.WHITE;
        } else {
            return Color.BLACK;
        }
    }
    
    private void mostrarHistorico() {
        new JanelaHistorico(this).setVisible(true);
    }

    private void iniciarLoop() {
        int delayMs = calcularDelayMs(partida.getNivel());
        timer = new Timer(delayMs, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!pausado && !partida.isGameOver()) {
                    boolean estavaGameOver = partida.isGameOver();
                    partida.processarQueda();
                    
                    // Se acabou de entrar em game over, salvar pontuação
                    if (!estavaGameOver && partida.isGameOver()) {
                        salvarPontuacao();
                    }
                    
                    // Ajusta a velocidade quando nível mudar
                    int novoDelay = calcularDelayMs(partida.getNivel());
                    if (novoDelay != timer.getDelay()) {
                        timer.setDelay(novoDelay);
                    }
                    painel.repaint();
                } else {
                    painel.repaint();
                }
            }
        });
        timer.start();
    }
    
    private void salvarPontuacao() {
        int pontuacao = partida.getPontuacao();
        int nivel = partida.getNivel();
        int linhas = partida.getTotalLinhas();
        int recordAnterior = HistoricoPontuacao.obterRecord();
        HistoricoPontuacao.salvarPontuacao(pontuacao, nivel, linhas);
        
        // Verificar se bateu o record
        int novoRecord = HistoricoPontuacao.obterRecord();
        if (novoRecord > recordAnterior && pontuacao == novoRecord) {
            mostrarMensagemRecord(pontuacao);
        }
    }
    
    private void mostrarMensagemRecord(int pontuacao) {
        JOptionPane.showMessageDialog(
            this,
            "🎉 NOVO RECORD! 🎉\n\nPontuação: " + pontuacao,
            "Parabéns!",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private int calcularDelayMs(int nivel) {
        // Velocidade aumenta com o nível (mínimo 80ms)
        int base = 700; // nível 1
        int passo = 60;
        int delay = Math.max(80, base - (nivel - 1) * passo);
        return delay;
    }
    
    public void reiniciarJogo() {
        timer.stop();
        this.partida = new Partida("jogador-local");
        this.painel = new JogoPanel(partida, this);
        setContentPane(painel);
        pack();
        pausado = false;
        painel.setFocusable(true);
        painel.requestFocusInWindow();
        iniciarLoop();
        painel.repaint();
    }
    
    public void pausarDespausar() {
        pausado = !pausado;
        if (pausado) {
            timer.stop();
        } else {
            timer.start();
        }
        painel.repaint();
    }
    
    public boolean isPausado() {
        return pausado;
    }
    
    public Partida getPartida() {
        return partida;
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new TetrisApp().setVisible(true));
    }

    private class JogoPanel extends JPanel {
        private static final int TAM_BLOCO = 30;
        private static final int MARGEM = 16;
        private Partida partida;
        private TetrisApp app;
        private Color corTemaFundo = new Color(20, 22, 28);
        private Color corTexto = Color.WHITE;

        public JogoPanel(Partida partida, TetrisApp app) {
            this.partida = partida;
            this.app = app;
            this.corTemaFundo = app.corTemaFundo;
            this.corTexto = TetrisApp.calcularCorTexto(corTemaFundo);
            int larguraPx = MARGEM * 2 + Tabuleiro.getLargura() * TAM_BLOCO + 220; // área lateral info aumentada
            // Altura mínima para acomodar os botões: calcular posição do último botão + altura
            int record = HistoricoPontuacao.obterRecord();
            int linhasInfo = record > 0 ? 4 : 3;
            int yBotoesCalc = MARGEM + 10 + 35 * linhasInfo + 130 + 20; // Posição dos botões
            int alturaMinima = yBotoesCalc + (30 + 10) * 6; // yBotoes + altura de todos os botões
            int alturaTabuleiro = MARGEM * 2 + Tabuleiro.getAltura() * TAM_BLOCO;
            int alturaPx = Math.max(alturaMinima, alturaTabuleiro); // Usar a maior altura
            setPreferredSize(new Dimension(larguraPx, alturaPx));
            setBackground(corTemaFundo);
            setLayout(null); // Layout absoluto para desenho customizado
            setFocusable(true);
            
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    // P e R sempre funcionam, mesmo em game over ou pausado
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_P:
                            app.pausarDespausar();
                            return;
                        case KeyEvent.VK_R:
                            app.reiniciarJogo();
                            requestFocusInWindow(); // Garantir foco após reiniciar
                            return;
                    }
                    
                    if (app.isPausado() || partida.isGameOver()) {
                        return;
                    }
                    
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_LEFT:
                            partida.moverTetromino(-1, 0);
                            repaint();
                            break;
                        case KeyEvent.VK_RIGHT:
                            partida.moverTetromino(1, 0);
                            repaint();
                            break;
                        case KeyEvent.VK_DOWN:
                            partida.moverTetromino(0, 1);
                            repaint();
                            break;
                        case KeyEvent.VK_UP:
                            partida.rotacionarTetromino();
                            repaint();
                            break;
                        case KeyEvent.VK_SPACE:
                            while (partida.moverTetromino(0, 1)) { /* loop até travar */ }
                            partida.processarQueda();
                            repaint();
                            break;
                    }
                }
            });
            
            criarBotoes();
        }
        
        private void criarBotoes() {
            int xBotoes = MARGEM + Tabuleiro.getLargura() * TAM_BLOCO + 20;
            // Posicionar botões abaixo da prévia da próxima peça
            int yInfo = MARGEM + 10;
            int espacamentoLinha = 35;
            int record = HistoricoPontuacao.obterRecord();
            int linhasInfo = record > 0 ? 4 : 3; // Se tem record, são 4 linhas de info
            // Prévia termina aproximadamente em: yInfo + espacamentoLinha * linhasInfo + 30 + 20 + 80 = yInfo + espacamentoLinha * linhasInfo + 130
            int yBotoes = yInfo + espacamentoLinha * linhasInfo + 130 + 20; // Abaixo da prévia
            int larguraBotao = 90;
            int alturaBotao = 30;
            int espacamento = 10;
            
            // Botões de movimento
            JButton btnEsquerda = criarBotao("Esquerda", xBotoes, yBotoes, larguraBotao, alturaBotao);
            btnEsquerda.addActionListener(e -> {
                if (!app.isPausado() && !partida.isGameOver()) {
                    partida.moverTetromino(-1, 0);
                    repaint();
                }
            });
            
            JButton btnDireita = criarBotao("Direita", xBotoes + larguraBotao + espacamento, yBotoes, larguraBotao, alturaBotao);
            btnDireita.addActionListener(e -> {
                if (!app.isPausado() && !partida.isGameOver()) {
                    partida.moverTetromino(1, 0);
                    repaint();
                }
            });
            
            JButton btnRotacionar = criarBotao("Rotacionar", xBotoes, yBotoes + alturaBotao + espacamento, larguraBotao * 2 + espacamento, alturaBotao);
            btnRotacionar.addActionListener(e -> {
                if (!app.isPausado() && !partida.isGameOver()) {
                    partida.rotacionarTetromino();
                    repaint();
                }
            });
            
            JButton btnDescer = criarBotao("Descer", xBotoes, yBotoes + (alturaBotao + espacamento) * 2, larguraBotao * 2 + espacamento, alturaBotao);
            btnDescer.addActionListener(e -> {
                if (!app.isPausado() && !partida.isGameOver()) {
                    partida.moverTetromino(0, 1);
                    repaint();
                }
            });
            
            JButton btnHardDrop = criarBotao("Hard Drop", xBotoes, yBotoes + (alturaBotao + espacamento) * 3, larguraBotao * 2 + espacamento, alturaBotao);
            btnHardDrop.addActionListener(e -> {
                if (!app.isPausado() && !partida.isGameOver()) {
                    while (partida.moverTetromino(0, 1)) { /* loop até travar */ }
                    partida.processarQueda();
                    repaint();
                }
            });
            
            // Botões de controle
            JButton btnPausar = criarBotao("Pausar", xBotoes, yBotoes + (alturaBotao + espacamento) * 4, larguraBotao * 2 + espacamento, alturaBotao);
            btnPausar.addActionListener(e -> app.pausarDespausar());
            
            JButton btnReiniciar = criarBotao("Reiniciar", xBotoes, yBotoes + (alturaBotao + espacamento) * 5, larguraBotao * 2 + espacamento, alturaBotao);
            btnReiniciar.addActionListener(e -> app.reiniciarJogo());
            
            add(btnEsquerda);
            add(btnDireita);
            add(btnRotacionar);
            add(btnDescer);
            add(btnHardDrop);
            add(btnPausar);
            add(btnReiniciar);
        }
        
        private JButton criarBotao(String texto, int x, int y, int largura, int altura) {
            JButton btn = new JButton(texto);
            btn.setBounds(x, y, largura, altura);
            btn.setFont(new Font("Arial", Font.BOLD, 12));
            btn.setBackground(new Color(60, 64, 72));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorderPainted(true);
            return btn;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            desenharGrade(g2);
            desenharFixos(g2);
            desenharAtual(g2);
            desenharProximaPeca(g2);
            desenharHUD(g2);

            if (partida.isGameOver()) {
                desenharGameOver(g2);
            }
            
            if (pausado) {
                desenharPausa(g2);
            }
        }

        private void desenharGrade(Graphics2D g2) {
            int x0 = MARGEM;
            int y0 = MARGEM;
            int w = Tabuleiro.getLargura() * TAM_BLOCO;
            int h = Tabuleiro.getAltura() * TAM_BLOCO;

            g2.setColor(new Color(40, 44, 52));
            g2.fillRect(x0, y0, w, h);

            g2.setColor(new Color(60, 64, 72));
            for (int y = 0; y <= Tabuleiro.getAltura(); y++) {
                int yy = y0 + y * TAM_BLOCO;
                g2.drawLine(x0, yy, x0 + w, yy);
            }
            for (int x = 0; x <= Tabuleiro.getLargura(); x++) {
                int xx = x0 + x * TAM_BLOCO;
                g2.drawLine(xx, y0, xx, y0 + h);
            }
        }

        private void desenharFixos(Graphics2D g2) {
            boolean[][] grid = partida.getTabuleiro().getGrid();
            for (int y = 0; y < grid.length; y++) {
                for (int x = 0; x < grid[y].length; x++) {
                    if (grid[y][x]) {
                        // Usar cor baseada na posição para variedade visual
                        Color cor = obterCorFixa(x, y);
                        desenharBloco(g2, x, y, cor);
                    }
                }
            }
        }

        private void desenharAtual(Graphics2D g2) {
            Tetromino t = partida.getTetrominoAtual();
            if (t == null) return;
            boolean[][] forma = t.getForma();
            Posicao p = t.getPosicao();
            Color cor = obterCorParaTipo(t.getTipo());
            for (int i = 0; i < forma.length; i++) {
                for (int j = 0; j < forma[i].length; j++) {
                    if (forma[i][j]) {
                        int x = p.getX() + j;
                        int y = p.getY() + i;
                        if (y >= 0) { // ignora parte acima do topo
                            desenharBloco(g2, x, y, cor);
                        }
                    }
                }
            }
        }

        private void desenharBloco(Graphics2D g2, int x, int y, Color cor) {
            int px = MARGEM + x * TAM_BLOCO;
            int py = MARGEM + y * TAM_BLOCO;
            g2.setColor(cor);
            g2.fillRect(px + 1, py + 1, TAM_BLOCO - 2, TAM_BLOCO - 2);
            g2.setColor(cor.brighter());
            g2.drawRect(px + 1, py + 1, TAM_BLOCO - 2, TAM_BLOCO - 2);
        }

        private void desenharProximaPeca(Graphics2D g2) {
            Tetromino proxima = partida.getProximoTetromino();
            if (proxima == null) return;
            
            int xInfo = MARGEM + Tabuleiro.getLargura() * TAM_BLOCO + 20;
            int yInfo = MARGEM + 10;
            int espacamentoLinha = 35;
            int record = HistoricoPontuacao.obterRecord();
            int linhasInfo = record > 0 ? 4 : 3; // Se tem record, são 4 linhas de info
            // Posicionar logo após as informações, mais próximo
            int yPreview = yInfo + espacamentoLinha * linhasInfo + 30;
            int tamBlocoPreview = 20;
            
            g2.setColor(corTexto);
            g2.setFont(getFont().deriveFont(Font.BOLD, 14f));
            g2.drawString("Próxima:", xInfo, yPreview);
            
            boolean[][] forma = proxima.getForma();
            int offsetX = xInfo;
            int offsetY = yPreview + 20;
            
            // Centralizar a peça
            int larguraPeca = forma[0].length;
            offsetX += (4 - larguraPeca) * tamBlocoPreview / 2;
            
            Color cor = obterCorParaTipo(proxima.getTipo());
            for (int i = 0; i < forma.length; i++) {
                for (int j = 0; j < forma[i].length; j++) {
                    if (forma[i][j]) {
                        int px = offsetX + j * tamBlocoPreview;
                        int py = offsetY + i * tamBlocoPreview;
                        g2.setColor(cor);
                        g2.fillRect(px + 1, py + 1, tamBlocoPreview - 2, tamBlocoPreview - 2);
                        g2.setColor(cor.brighter());
                        g2.drawRect(px + 1, py + 1, tamBlocoPreview - 2, tamBlocoPreview - 2);
                    }
                }
            }
        }
        
        private void desenharHUD(Graphics2D g2) {
            int xInfo = MARGEM + Tabuleiro.getLargura() * TAM_BLOCO + 20;
            int yInfo = MARGEM + 10;
            int espacamentoLinha = 35; // Mais espaçamento entre linhas
            
            g2.setColor(corTexto);
            g2.setFont(getFont().deriveFont(Font.BOLD, 18f));
            g2.drawString("Pontuação: " + partida.getPontuacao(), xInfo, yInfo);
            
            // Mostrar record se houver
            int record = HistoricoPontuacao.obterRecord();
            if (record > 0) {
                // Record sempre em dourado para destacar, mas ajustar se fundo for muito claro
                Color corRecord = new Color(255, 215, 0);
                if (corTexto == Color.BLACK) {
                    // Se o texto é preto (fundo claro), usar dourado mais escuro
                    corRecord = new Color(184, 134, 11); // Dourado escuro
                }
                g2.setColor(corRecord);
                g2.drawString("Record: " + record, xInfo, yInfo + espacamentoLinha);
                g2.setColor(corTexto);
                g2.drawString("Nível: " + partida.getNivel(), xInfo, yInfo + espacamentoLinha * 2);
                g2.drawString("Linhas: " + partida.getTotalLinhas(), xInfo, yInfo + espacamentoLinha * 3);
            } else {
                g2.drawString("Nível: " + partida.getNivel(), xInfo, yInfo + espacamentoLinha);
                g2.drawString("Linhas: " + partida.getTotalLinhas(), xInfo, yInfo + espacamentoLinha * 2);
            }

        }
        
        private void desenharPausa(Graphics2D g2) {
            g2.setColor(new Color(0, 0, 0, 180));
            int w = Tabuleiro.getLargura() * TAM_BLOCO;
            int h = Tabuleiro.getAltura() * TAM_BLOCO;
            g2.fillRect(MARGEM, MARGEM, w, h);

            g2.setColor(Color.WHITE);
            g2.setFont(getFont().deriveFont(Font.BOLD, 36f));
            String texto = "PAUSADO";
            java.awt.FontMetrics fm = g2.getFontMetrics();
            int textoWidth = fm.stringWidth(texto);
            g2.drawString(texto, MARGEM + (w - textoWidth) / 2, MARGEM + h / 2);
        }
        

        private void desenharGameOver(Graphics2D g2) {
            // Overlay semi-transparente (sempre escuro para contraste)
            g2.setColor(new Color(0, 0, 0, 160));
            int w = Tabuleiro.getLargura() * TAM_BLOCO;
            int h = Tabuleiro.getAltura() * TAM_BLOCO;
            g2.fillRect(MARGEM, MARGEM, w, h);

            g2.setColor(Color.WHITE); // Sempre branco no overlay escuro
            g2.setFont(getFont().deriveFont(Font.BOLD, 36f));
            g2.drawString("GAME OVER", MARGEM + 24, MARGEM + h / 2);
        }

        private Color obterCorParaTipo(TipoTetromino tipo) {
            switch (tipo) {
                case I: return new Color(0x00F5FF); // Ciano
                case O: return new Color(0xFFFF00); // Amarelo
                case T: return new Color(0xA000F0); // Roxo
                case S: return new Color(0x00FF00); // Verde
                case Z: return new Color(0xFF0000); // Vermelho
                case J: return new Color(0x0000FF); // Azul
                case L: return new Color(0xFFA500); // Laranja
                case BIG: return new Color(0xFF1493); // Rosa choque para o bloco grande
                default: return new Color(0xFFFFFF); // Branco
            }
        }
        
        private Color obterCorFixa(int x, int y) {
            // Usar cores mais escuras para peças fixas, variando por posição
            int hash = (x * 7 + y * 13) % 7;
            Color[] coresFixas = {
                new Color(0x008B8B), // Ciano escuro
                new Color(0x8B8B00), // Amarelo escuro
                new Color(0x6A0080), // Roxo escuro
                new Color(0x008B00), // Verde escuro
                new Color(0x8B0000), // Vermelho escuro
                new Color(0x00008B), // Azul escuro
                new Color(0x8B5A00)  // Laranja escuro
            };
            return coresFixas[hash];
        }
        
        public void setCorTemaFundo(Color cor) {
            this.corTemaFundo = cor;
            this.corTexto = TetrisApp.calcularCorTexto(cor);
            setBackground(cor);
        }
    }
}


