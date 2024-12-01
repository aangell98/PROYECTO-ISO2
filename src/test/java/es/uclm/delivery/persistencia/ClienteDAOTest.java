package es.uclm.delivery.persistencia;

import es.uclm.delivery.dominio.entidades.Cliente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ClienteDAOTest {

    private ClienteDAO clienteDAO;
    private EntityManager mockEntityManager;

    @BeforeEach
    void setUp() throws Exception {
        mockEntityManager = Mockito.mock(EntityManager.class);
        clienteDAO = new ClienteDAO();

        Field entityManagerField = EntidadDAO.class.getDeclaredField("entityManager");
        entityManagerField.setAccessible(true);
        entityManagerField.set(clienteDAO, mockEntityManager); // Configurar el mock
    }

    @Test
    void testFindByUsername_UsuarioEncontrado() {
        String username = "user1";
        Cliente mockCliente = new Cliente();
        mockCliente.setId(1L);

        TypedQuery<Cliente> mockQuery = Mockito.mock(TypedQuery.class);
        when(mockEntityManager.createQuery(anyString(), eq(Cliente.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter("username", username)).thenReturn(mockQuery);
        when(mockQuery.getResultStream()).thenReturn(java.util.stream.Stream.of(mockCliente));

        Optional<Cliente> result = clienteDAO.findByUsername(username);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void testFindByUsername_UsuarioNoEncontrado() {
        String username = "nonexistent";

        TypedQuery<Cliente> mockQuery = Mockito.mock(TypedQuery.class);
        when(mockEntityManager.createQuery(anyString(), eq(Cliente.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter("username", username)).thenReturn(mockQuery);
        when(mockQuery.getResultStream()).thenReturn(java.util.stream.Stream.empty());

        Optional<Cliente> result = clienteDAO.findByUsername(username);

        assertFalse(result.isPresent());
    }

    @Test
    void testFindByUsername_UsernameEsNull() {
        assertThrows(java.lang.NullPointerException.class, () -> clienteDAO.findByUsername(null));
    }

    @Test
    void testFindByUsername_UsernameEsVacio() {
        String username = "";

        TypedQuery<Cliente> mockQuery = Mockito.mock(TypedQuery.class);
        when(mockEntityManager.createQuery(anyString(), eq(Cliente.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter("username", username)).thenReturn(mockQuery);
        when(mockQuery.getResultStream()).thenReturn(java.util.stream.Stream.empty());

        Optional<Cliente> result = clienteDAO.findByUsername(username);

        assertFalse(result.isPresent());
    }

    @Test
    void testFindByUsername_UsernameEsEspacios() {
        String username = "   ";

        TypedQuery<Cliente> mockQuery = Mockito.mock(TypedQuery.class);
        when(mockEntityManager.createQuery(anyString(), eq(Cliente.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter("username", username)).thenReturn(mockQuery);
        when(mockQuery.getResultStream()).thenReturn(java.util.stream.Stream.empty());

        Optional<Cliente> result = clienteDAO.findByUsername(username);

        assertFalse(result.isPresent());
    }

    @Test
    void testFindByUsername_UsernameMuyLargo() {
        String username = "a".repeat(256); // 256 caracteres

        TypedQuery<Cliente> mockQuery = Mockito.mock(TypedQuery.class);
        when(mockEntityManager.createQuery(anyString(), eq(Cliente.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter("username", username)).thenReturn(mockQuery);
        when(mockQuery.getResultStream()).thenReturn(java.util.stream.Stream.empty());

        Optional<Cliente> result = clienteDAO.findByUsername(username);

        assertFalse(result.isPresent());
    }
}
