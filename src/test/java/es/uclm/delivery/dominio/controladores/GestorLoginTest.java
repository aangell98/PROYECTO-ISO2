package es.uclm.delivery.dominio.controladores;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import es.uclm.delivery.dominio.entidades.Cliente;
import es.uclm.delivery.dominio.entidades.Repartidor;
import es.uclm.delivery.dominio.entidades.Restaurante;
import es.uclm.delivery.dominio.entidades.Usuario;
import es.uclm.delivery.persistencia.ClienteDAO;
import es.uclm.delivery.persistencia.RepartidorDAO;
import es.uclm.delivery.persistencia.RestauranteDAO;
import es.uclm.delivery.persistencia.UsuarioDAO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

class GestorLoginTest {

    @InjectMocks
    private GestorLogin gestorLogin;

    @Mock
    private UsuarioDAO usuarioDAO;

    @Mock
    private ClienteDAO clienteDAO;

    @Mock
    private RepartidorDAO repartidorDAO;

    @Mock
    private RestauranteDAO restauranteDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAutenticarUsuarioCorrecto() {
        Usuario mockUsuario = new Usuario();
        mockUsuario.setUsername("testUser");
        mockUsuario.setPassword(gestorLogin.cifrarPassword("password123"));

        when(usuarioDAO.select("testUser")).thenReturn(Optional.of(mockUsuario));

        assertTrue(gestorLogin.autenticar("testUser", "password123"));
    }

    @Test
    void testAutenticarUsuarioIncorrecto() {
        Usuario mockUsuario = new Usuario();
        mockUsuario.setUsername("testUser");
        mockUsuario.setPassword(gestorLogin.cifrarPassword("password123"));

        when(usuarioDAO.select("testUser")).thenReturn(Optional.of(mockUsuario));

        assertFalse(gestorLogin.autenticar("testUser", "wrongPassword"));
    }

    @Test
    void testAutenticarUsuarioNoExiste() {
        when(usuarioDAO.select("nonExistentUser")).thenReturn(Optional.empty());

        assertFalse(gestorLogin.autenticar("nonExistentUser", "password"));
    }

    @Test
    void testRegistrarUsuarioExitoso() {
        when(usuarioDAO.select("newUser")).thenReturn(Optional.empty());

        assertTrue(gestorLogin.registrar("newUser", "password123", "USER"));

        verify(usuarioDAO).insert(any(Usuario.class));
    }

    @Test
    void testRegistrarUsuarioDuplicado() {
        when(usuarioDAO.select("existingUser")).thenReturn(Optional.of(new Usuario()));

        assertFalse(gestorLogin.registrar("existingUser", "password123", "USER"));
    }

    @Test
    void testRegistrarClienteExitoso() {
        when(usuarioDAO.select("newClient")).thenReturn(Optional.empty());

        assertTrue(gestorLogin.registrarCliente("newClient", "password123", "CLIENT", "John", "Doe", "12345678A"));

        verify(usuarioDAO).insert(any(Usuario.class));
        verify(clienteDAO).insert(any(Cliente.class));
    }

    @Test
    void testRegistrarClienteDuplicado() {
        when(usuarioDAO.select("existingClient")).thenReturn(Optional.of(new Usuario()));

        assertFalse(gestorLogin.registrarCliente("existingClient", "password123", "CLIENT", "John", "Doe", "12345678A"));
    }

    @Test
    void testRegistrarRepartidorExitoso() {
        when(usuarioDAO.select("newRepartidor")).thenReturn(Optional.empty());

        assertTrue(gestorLogin.registrarRepartidor("newRepartidor", "password123", "REPARTIDOR", "Jane", "Smith", "87654321B"));

        verify(usuarioDAO).insert(any(Usuario.class));
        verify(repartidorDAO).insert(any(Repartidor.class));
    }

    @Test
    void testRegistrarRestauranteExitoso() {
        when(usuarioDAO.select("newRestaurant")).thenReturn(Optional.empty());

        assertTrue(gestorLogin.registrarRestaurante("newRestaurant", "password123", "RESTAURANTE", "Good Food", "123 Street"));

        verify(usuarioDAO).insert(any(Usuario.class));
        verify(restauranteDAO).insert(any(Restaurante.class));
    }

    @Test
    void testRegistrarRestauranteDuplicado() {
        when(usuarioDAO.select("existingRestaurant")).thenReturn(Optional.of(new Usuario()));

        assertFalse(gestorLogin.registrarRestaurante("existingRestaurant", "password123", "RESTAURANTE", "Good Food", "123 Street"));
    }
}
