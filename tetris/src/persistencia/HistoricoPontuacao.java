package persistencia;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Gerencia o armazenamento local de pontuações usando arquivo JSON simples.
 */
public class HistoricoPontuacao {
    private static final String ARQUIVO_HISTORICO = "tetris_historico.txt";
    private static final String ARQUIVO_RECORD = "tetris_record.txt";
    
    /**
     * Representa uma entrada de pontuação no histórico.
     */
    public static class EntradaPontuacao {
        public final int pontuacao;
        public final int nivel;
        public final int linhas;
        public final String dataHora;
        
        public EntradaPontuacao(int pontuacao, int nivel, int linhas, String dataHora) {
            this.pontuacao = pontuacao;
            this.nivel = nivel;
            this.linhas = linhas;
            this.dataHora = dataHora;
        }
        
        @Override
        public String toString() {
            return pontuacao + "|" + nivel + "|" + linhas + "|" + dataHora;
        }
        
        public static EntradaPontuacao fromString(String linha) {
            String[] partes = linha.split("\\|");
            if (partes.length == 4) {
                return new EntradaPontuacao(
                    Integer.parseInt(partes[0]),
                    Integer.parseInt(partes[1]),
                    Integer.parseInt(partes[2]),
                    partes[3]
                );
            }
            return null;
        }
    }
    
    /**
     * Salva uma pontuação no histórico.
     */
    public static void salvarPontuacao(int pontuacao, int nivel, int linhas) {
        try {
            LocalDateTime agora = LocalDateTime.now();
            String dataHora = agora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            EntradaPontuacao entrada = new EntradaPontuacao(pontuacao, nivel, linhas, dataHora);
            
            // Salvar no histórico
            Path arquivo = Paths.get(ARQUIVO_HISTORICO);
            List<String> linhasArquivo = new ArrayList<>();
            if (Files.exists(arquivo)) {
                linhasArquivo = Files.readAllLines(arquivo);
            }
            linhasArquivo.add(entrada.toString());
            Files.write(arquivo, linhasArquivo, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            
            // Verificar e atualizar record
            int recordAtual = obterRecord();
            if (pontuacao > recordAtual) {
                Files.write(Paths.get(ARQUIVO_RECORD), 
                    String.valueOf(pontuacao).getBytes(), 
                    StandardOpenOption.CREATE, 
                    StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar pontuação: " + e.getMessage());
        }
    }
    
    /**
     * Obtém o record de pontuação.
     */
    public static int obterRecord() {
        try {
            Path arquivo = Paths.get(ARQUIVO_RECORD);
            if (Files.exists(arquivo)) {
                String conteudo = new String(Files.readAllBytes(arquivo)).trim();
                return Integer.parseInt(conteudo);
            }
        } catch (IOException | NumberFormatException e) {
            // Ignorar erros, retornar 0
        }
        return 0;
    }
    
    /**
     * Obtém todas as pontuações ordenadas por pontuação (maior primeiro).
     */
    public static List<EntradaPontuacao> obterHistorico() {
        List<EntradaPontuacao> historico = new ArrayList<>();
        try {
            Path arquivo = Paths.get(ARQUIVO_HISTORICO);
            if (Files.exists(arquivo)) {
                List<String> linhas = Files.readAllLines(arquivo);
                for (String linha : linhas) {
                    EntradaPontuacao entrada = EntradaPontuacao.fromString(linha);
                    if (entrada != null) {
                        historico.add(entrada);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler histórico: " + e.getMessage());
        }
        
        // Ordenar por pontuação (maior primeiro)
        return historico.stream()
            .sorted((a, b) -> Integer.compare(b.pontuacao, a.pontuacao))
            .collect(Collectors.toList());
    }
    
    /**
     * Obtém as top 10 pontuações.
     */
    public static List<EntradaPontuacao> obterTop10() {
        List<EntradaPontuacao> historico = obterHistorico();
        return historico.stream()
            .limit(10)
            .collect(Collectors.toList());
    }
    
    /**
     * Limpa todo o histórico.
     */
    public static void limparHistorico() {
        try {
            Files.deleteIfExists(Paths.get(ARQUIVO_HISTORICO));
            Files.deleteIfExists(Paths.get(ARQUIVO_RECORD));
        } catch (IOException e) {
            System.err.println("Erro ao limpar histórico: " + e.getMessage());
        }
    }
}

