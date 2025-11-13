package persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBD {
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=tetrisdb;encrypt=true;trustServerCertificate=true";
    private static final String USUARIO = "paulamusardo";
    private static final String SENHA = "Cri$tynn@0605";

    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver SQL Server não encontrado. Certifique-se de que o arquivo mssql-jdbc-13.2.1.jre11.jar está no classpath.", e);
        }
    }

    public static Connection obterConexao() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    public static void main(String[] args) {
        try (Connection conn = obterConexao()) {
            System.out.println("Conexão estabelecida com sucesso!");
            System.out.println("Database: " + conn.getCatalog());
        } catch (SQLException e) {
            System.err.println("Erro ao conectar: " + e.getMessage());
        }
    }
}