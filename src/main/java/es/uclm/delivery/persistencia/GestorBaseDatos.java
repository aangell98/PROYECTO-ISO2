package es.uclm.delivery.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class GestorBaseDatos {

    private Connection connection;

    public boolean conectar() {
        try {
            // Configura la conexión a la base de datos
            String url = "jdbc:mysql://localhost:8080/";
            String username = "derbyuser";
            String password = "password";
            connection = DriverManager.getConnection(url, username, password);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ResultSet ejecutarConsulta(String consulta) {
        try (Statement statement = connection.createStatement()){
            return statement.executeQuery(consulta);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int update(String sql) {
        try (Statement statement = connection.createStatement()){
            return statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void desconectar() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}