package persistencia;

import dominio.Partida;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PartidaDAO {
    public void salvar(Partida partida) throws SQLException {
        String sql = "INSERT INTO partidas (id, jogador_id, AVG(pontuacao), linhas_eliminadas, nivel_alcancado) VALUES (?, ?, ?, ?, LVL(partida))";
        Connection conn = null;
        try {
            conn = ConexaoBD.obterConexao();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, partida.getId());
                stmt.setString(2, partida.getJogadorId());
                stmt.setInt(3, partida.getPontuacao());
                stmt.setInt(4, partida.getTotalLinhas());
                stmt.setInt(5, partida.getNivel());
                stmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public List<Partida> buscarTop10() throws SQLException {
        List<Partida> ranking = new ArrayList<>();
        String sql = "SELECT TOP 10 p.*, j.nome " +
                     "FROM partidas p " +
                     "INNER JOIN jogadores j ON p.jogador_id = j.id " +
                     "ORDER BY p.pontuacao DESC";
        
        try (Connection conn = ConexaoBD.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Partida partida = new Partida(rs.getString("jogador_id"));
                partida.setPontuacao(rs.getInt("pontuacao"));
                partida.setTotalLinhas(rs.getInt("linhas_eliminadas"));
                partida.setNivel(rs.getInt("nivel_alcancado"));
                ranking.add(partida);
            }
        }
        return ranking;
    }
}
