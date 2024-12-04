package es.uclm.delivery.persistencia;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
class GestorBaseDatosTest {

    private GestorBaseDatos gestorBaseDatos;
    private Connection mockConnection;
    private Statement mockStatement;
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() {
        gestorBaseDatos = new GestorBaseDatos();
        mockConnection = mock(Connection.class); // Mock de la conexión
        
        // Simulamos que DriverManager.getConnection devuelve la conexión mock
        try {
            when(DriverManager.getConnection(anyString(), anyString(), anyString())).thenReturn(mockConnection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testConectar_Exito() {
        // Simulamos que la conexión es exitosa, lo que significa que devuelve true
        boolean result = gestorBaseDatos.conectar();
        
        // Verificamos que la conexión haya sido exitosa
        assertTrue(result);
    }

    @Test
    void testConectar_Fallo() throws SQLException {
        // Simulamos que DriverManager lanza una excepción al intentar obtener la conexión
        when(DriverManager.getConnection(anyString(), anyString(), anyString())).thenThrow(SQLException.class);
        
        // Ejecutamos la conexión y verificamos que se maneje el fallo correctamente
        boolean result = gestorBaseDatos.conectar();
        assertFalse(result);
    }

    @Test
    void testEjecutarConsulta_Exito() throws SQLException {
        // Simulamos que la conexión y la consulta funcionan correctamente
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        
        // Ejecutamos la consulta
        ResultSet resultSet = gestorBaseDatos.ejecutarConsulta("SELECT * FROM tabla");
        
        // Verificamos que la consulta haya devuelto el ResultSet simulado
        assertNotNull(resultSet);
    }

    @Test
    void testEjecutarConsulta_Fallo() throws SQLException {
        // Simulamos que la conexión o la consulta lanzan una excepción
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenThrow(SQLException.class);
        
        // Ejecutamos la consulta y verificamos que se maneje el fallo correctamente
        ResultSet resultSet = gestorBaseDatos.ejecutarConsulta("SELECT * FROM tabla");
        assertNull(resultSet);
    }

    @Test
    void testInsert_Exito() throws SQLException {
        // Simulamos que la conexión y la inserción funcionan correctamente
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeUpdate(anyString())).thenReturn(1);
        
        // Ejecutamos la inserción
        int result = gestorBaseDatos.update("INSERT INTO tabla VALUES (1, 'test')");
        
        // Verificamos que el resultado sea 1, lo que indica que la inserción fue exitosa
        assertEquals(1, result);
    }

    @Test
    void testInsert_Fallo() throws SQLException {
        // Simulamos que la conexión o la inserción lanzan una excepción
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeUpdate(anyString())).thenThrow(SQLException.class);
        
        // Ejecutamos la inserción y verificamos que el resultado sea -1, indicando un fallo
        int result = gestorBaseDatos.update("INSERT INTO tabla VALUES (1, 'test')");
        assertEquals(-1, result);
    }

    @Test
    void testUpdate_Exito() throws SQLException {
        // Simulamos que la conexión y la actualización funcionan correctamente
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeUpdate(anyString())).thenReturn(1);
        
        // Ejecutamos la actualización
        int result = gestorBaseDatos.update("UPDATE tabla SET nombre = 'test' WHERE id = 1");
        
        // Verificamos que el resultado sea 1, lo que indica que la actualización fue exitosa
        assertEquals(1, result);
    }

    @Test
    void testUpdate_Fallo() throws SQLException {
        // Simulamos que la conexión o la actualización lanzan una excepción
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeUpdate(anyString())).thenThrow(SQLException.class);
        
        // Ejecutamos la actualización y verificamos que el resultado sea -1, indicando un fallo
        int result = gestorBaseDatos.update("UPDATE tabla SET nombre = 'test' WHERE id = 1");
        assertEquals(-1, result);
    }

    @Test
    void testDesconectar() throws SQLException {
        // Simulamos el comportamiento del método close() para que no haga nada
        doNothing().when(mockConnection).close();

        gestorBaseDatos.conectar();
        gestorBaseDatos.desconectar();

        // Verificamos que el método close() se haya llamado para cerrar la conexión
        verify(mockConnection, times(1)).close();
    }
}
