package persistencia;

import dominio.Jogador;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JogadorDAO {
    public void salvar(Jogador jogador) throws SQLException {
        String sql = "INSERT INTO jogadores (id, nome, email) VALUES (?, ?, ?)";
        try (Connection conn = ConexaoBD.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, jogador.getId());
            stmt.setString(2, jogador.getNome());
            stmt.setString(3, jogador.getEmail());
            stmt.executeUpdate();
        }
    }

    public Jogador buscarPorId(String id) throws SQLException {
        String sql = "SELECT * FROM jogadores WHERE id = ?";
        try (Connection conn = ConexaoBD.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Jogador(
                        rs.getString("id"),
                        rs.getString("nome"),
                        rs.getString("email")
                    );
                }
            }
        }
        return null;
    }

    public List<Jogador> listarTodos() throws SQLException {
        String sql = "SELECT * FROM jogadores ORDER BY nome";
        List<Jogador> jogadores = new ArrayList<>();
        try (Connection conn = ConexaoBD.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                jogadores.add(new Jogador(
                    rs.getString("id"),
                    rs.getString("nome"),
                    rs.getString("email")
                ));
            }
        }
        return jogadores;
    }

    public boolean atualizar(Jogador jogador) throws SQLException {
        String sql = "UPDATE jogadores SET nome = ?, email = ? WHERE id = ?";
        try (Connection conn = ConexaoBD.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, jogador.getNome());
            stmt.setString(2, jogador.getEmail());
            stmt.setString(3, jogador.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deletar(String id) throws SQLException {
        String sql = "DELETE FROM jogadores WHERE id = ?";
        try (Connection conn = ConexaoBD.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
}
