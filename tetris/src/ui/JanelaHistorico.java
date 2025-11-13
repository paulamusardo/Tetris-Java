package ui;

import persistencia.HistoricoPontuacao;
import persistencia.HistoricoPontuacao.EntradaPontuacao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class JanelaHistorico extends JFrame {
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    
    public JanelaHistorico(JFrame parent) {
        super("Histórico de Pontuações");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(parent);
        
        criarInterface();
        carregarHistorico();
    }
    
    private void criarInterface() {
        setLayout(new BorderLayout());
        
        // Título
        JLabel titulo = new JLabel("Histórico de Pontuações", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(titulo, BorderLayout.NORTH);
        
        // Tabela
        String[] colunas = {"#", "Pontuação", "Nível", "Linhas", "Data/Hora"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tabela = new JTable(modeloTabela);
        tabela.setFont(new Font("Arial", Font.PLAIN, 12));
        tabela.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabela.setRowHeight(25);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Ajustar largura das colunas
        tabela.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(100);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(60);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(60);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(200);
        
        JScrollPane scrollPane = new JScrollPane(tabela);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);
        
        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout());
        
        JButton btnAtualizar = new JButton("Atualizar");
        btnAtualizar.addActionListener(e -> carregarHistorico());
        
        JButton btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> dispose());
        
        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnFechar);
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(painelBotoes, BorderLayout.SOUTH);
        
        // Mostrar record
        int record = HistoricoPontuacao.obterRecord();
        if (record > 0) {
            JLabel labelRecord = new JLabel("Record: " + record, JLabel.CENTER);
            labelRecord.setFont(new Font("Arial", Font.BOLD, 16));
            labelRecord.setForeground(new Color(255, 215, 0)); // Dourado
            labelRecord.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            add(labelRecord, BorderLayout.SOUTH);
        }
    }
    
    private void carregarHistorico() {
        modeloTabela.setRowCount(0);
        List<EntradaPontuacao> historico = HistoricoPontuacao.obterHistorico();
        
        int posicao = 1;
        for (EntradaPontuacao entrada : historico) {
            Object[] linha = {
                posicao++,
                entrada.pontuacao,
                entrada.nivel,
                entrada.linhas,
                entrada.dataHora
            };
            modeloTabela.addRow(linha);
        }
        
        if (historico.isEmpty()) {
            modeloTabela.addRow(new Object[]{"-", "-", "-", "-", "Nenhuma pontuação registrada"});
        }
    }
}

