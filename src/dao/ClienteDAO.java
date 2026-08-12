package dao;

import model.Cliente;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ClienteDAO {

    private static final String URL = "jdbc:mysql://localhost:3306/banco_bom?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Guarda a lista completa de clientes no MySQL, 
     * mantendo compatibilidade total com o ClienteController.
     */
    public void guardar(ArrayList<Cliente> clientes) {
        String deleteSql = "DELETE FROM cliente";
        String insertSql = "INSERT INTO cliente (id, nome, data_nasc, num_contas, saldo_total) VALUES (?, ?, ?, ?, ?)";
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement()) {
            
            // Desativa o auto-commit para garantir segurança na transação
            conn.setAutoCommit(false);
            
            // 1. Limpa a tabela (equivalente a sobrescrever o ficheiro antigo)
            stmt.executeUpdate(deleteSql);
            
            // 2. Insere todos os clientes atualizados da lista
            try (PreparedStatement pstm = conn.prepareStatement(insertSql)) {
                for (Cliente cliente : clientes) {
                    pstm.setString(1, cliente.getId());
                    pstm.setString(2, cliente.getNome());
                    
                    String dataFormatada = cliente.getDataNascimento() != null ? 
                                           sdf.format(cliente.getDataNascimento()) : "2000-01-01";
                    pstm.setString(3, dataFormatada);
                    
                    pstm.setInt(4, cliente.getContas().size());
                    pstm.setDouble(5, cliente.getSaldoTotal());
                    
                    pstm.executeUpdate();
                }
            }
            
            // Confirma as alterações no banco
            conn.commit();
            System.out.println("Base de dados MySQL sincronizada com sucesso!");
            
        } catch (SQLException e) {
            System.err.println("Erro ao guardar lista de clientes no MySQL: " + e.getMessage());
        }
    }

    /**
     * Carrega a lista de clientes do MySQL para o Controller.
     */
    public ArrayList<Cliente> carregar() {
        ArrayList<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM cliente";

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Cliente cliente = new Cliente(
                    rs.getString("nome"), 
                    rs.getString("id"), 
                    rs.getDate("data_nasc")
                );
                
                clientes.add(cliente);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao carregar clientes do MySQL: " + e.getMessage());
        }

        return clientes;
    }
}
/**
import model.Cliente;

import java.io.*;
import java.util.ArrayList;

/**
 * DAO (Data Access Object) — responsavel pela persistencia
 * da lista de clientes em ficheiro de objectos (.dat).
 *
 * Nao contem logica de negocio — apenas leitura e escrita.
 */
/*public class ClienteDAO {

    private static final String FICHEIRO = "clientes.dat";

    /**
     * Guarda toda a lista de clientes no ficheiro.
     * Sobrescreve qualquer conteudo anterior.
     */
    
    /*
    public void guardar(ArrayList<Cliente> clientes) {
        try (ObjectOutputStream oos =
                new ObjectOutputStream(new FileOutputStream(FICHEIRO))) {
            oos.writeObject(clientes);
        } catch (IOException e) {
            System.err.println("Erro ao guardar clientes: " + e.getMessage());
        }
    }

    /**
     * Carrega a lista de clientes do ficheiro.
     * Se o ficheiro ainda nao existir, retorna lista vazia.
     */
    
    /*
    @SuppressWarnings("unchecked")
    public ArrayList<Cliente> carregar() {
        File f = new File(FICHEIRO);
        if (!f.exists()) return new ArrayList<>();
        try (ObjectInputStream ois =
                new ObjectInputStream(new FileInputStream(FICHEIRO))) {
            return (ArrayList<Cliente>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar clientes: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
*/