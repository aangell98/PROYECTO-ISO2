package es.uclm.delivery.persistencia;

import es.uclm.delivery.dominio.entidades.Cliente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ClienteDAOTest {

    private static final String USERNAME_EXISTENTE = "user1";
    private static final String USERNAME_INEXISTENTE = "nonexistent";
    private static final String USERNAME_VACIO = "";
    private static final String USERNAME_ESPACIOS = "   ";
    private static final String USERNAME_MUY_LARGO = "a".repeat(256);

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

    private TypedQuery<Cliente> crearMockTypedQuery() {
        return Mockito.mock(TypedQuery.class);
    }

    @Test
    void testFindByUsername_UsuarioEncontrado() {
        Cliente mockCliente = new Cliente();
        mockCliente.setId(1L);

        TypedQuery<Cliente> mockQuery = crearMockTypedQuery();
        when(mockEntityManager.createQuery(anyString(), eq(Cliente.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter("username", USERNAME_EXISTENTE)).thenReturn(mockQuery);
        when(mockQuery.getResultStream()).thenReturn(java.util.stream.Stream.of(mockCliente));

        Optional<Cliente> result = clienteDAO.findByUsername(USERNAME_EXISTENTE);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void testFindByUsername_UsuarioNoEncontrado() {
        TypedQuery<Cliente> mockQuery = crearMockTypedQuery();
        when(mockEntityManager.createQuery(anyString(), eq(Cliente.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter("username", USERNAME_INEXISTENTE)).thenReturn(mockQuery);
        when(mockQuery.getResultStream()).thenReturn(java.util.stream.Stream.empty());

        Optional<Cliente> result = clienteDAO.findByUsername(USERNAME_INEXISTENTE);

        assertFalse(result.isPresent());
    }

    @Test
    void testFindByUsername_UsernameEsNull() {
        assertThrows(NullPointerException.class, () -> clienteDAO.findByUsername(null));
    }

    @Test
    void testFindByUsername_UsernameEsVacio() {
        TypedQuery<Cliente> mockQuery = crearMockTypedQuery();
        when(mockEntityManager.createQuery(anyString(), eq(Cliente.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter("username", USERNAME_VACIO)).thenReturn(mockQuery);
        when(mockQuery.getResultStream()).thenReturn(java.util.stream.Stream.empty());

        Optional<Cliente> result = clienteDAO.findByUsername(USERNAME_VACIO);

        assertFalse(result.isPresent());
    }

    @Test
    void testFindByUsername_UsernameEsEspacios() {
        TypedQuery<Cliente> mockQuery = crearMockTypedQuery();
        when(mockEntityManager.createQuery(anyString(), eq(Cliente.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter("username", USERNAME_ESPACIOS)).thenReturn(mockQuery);
        when(mockQuery.getResultStream()).thenReturn(java.util.stream.Stream.empty());

        Optional<Cliente> result = clienteDAO.findByUsername(USERNAME_ESPACIOS);

        assertFalse(result.isPresent());
    }

    @Test
    void testFindByUsername_UsernameMuyLargo() {
        TypedQuery<Cliente> mockQuery = crearMockTypedQuery();
        when(mockEntityManager.createQuery(anyString(), eq(Cliente.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter("username", USERNAME_MUY_LARGO)).thenReturn(mockQuery);
        when(mockQuery.getResultStream()).thenReturn(java.util.stream.Stream.empty());

        Optional<Cliente> result = clienteDAO.findByUsername(USERNAME_MUY_LARGO);

        assertFalse(result.isPresent());
    }
}