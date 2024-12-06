package es.uclm.delivery.dominio.controladores;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import es.uclm.delivery.dominio.entidades.Cliente;
import es.uclm.delivery.dominio.entidades.Repartidor;
import es.uclm.delivery.dominio.entidades.Restaurante;
import es.uclm.delivery.dominio.entidades.Usuario;
import es.uclm.delivery.dominio.excepciones.CifradoException;
import es.uclm.delivery.persistencia.ClienteDAO;
import es.uclm.delivery.persistencia.RepartidorDAO;
import es.uclm.delivery.persistencia.RestauranteDAO;
import es.uclm.delivery.persistencia.UsuarioDAO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;

class GestorLoginTest {

    private static final String USERNAME = "testUser";
    private static final String PASSWORD = "password123";
    private static final String WRONG_PASSWORD = "wrongPassword";
    private static final String NON_EXISTENT_USER = "nonExistentUser";
    private static final String NEW_USER = "newUser";
    private static final String EXISTING_USER = "existingUser";
    private static final String CLIENT_ROLE = "CLIENT";
    private static final String USER_ROLE = "USER";
    private static final String REPARTIDOR_ROLE = "REPARTIDOR";
    private static final String RESTAURANTE_ROLE = "RESTAURANTE";
    private static final String CLIENT_NAME = "John";
    private static final String CLIENT_SURNAME = "Doe";
    private static final String CLIENT_DNI = "12345678A";
    private static final String REPARTIDOR_NAME = "Jane";
    private static final String REPARTIDOR_SURNAME = "Smith";
    private static final String REPARTIDOR_DNI = "87654321B";
    private static final String RESTAURANTE_NAME = "Good Food";
    private static final String RESTAURANTE_ADDRESS = "123 Street";

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

    private Usuario crearUsuario(String username, String password) {
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(gestorLogin.cifrarPassword(password));
        return usuario;
    }

    @Test
    void testAutenticarUsuarioCorrecto() {
        Usuario mockUsuario = crearUsuario(USERNAME, PASSWORD);

        when(usuarioDAO.select(USERNAME)).thenReturn(Optional.of(mockUsuario));

        assertTrue(gestorLogin.autenticar(USERNAME, PASSWORD));
    }

    @Test
    void testAutenticarUsuarioIncorrecto() {
        Usuario mockUsuario = crearUsuario(USERNAME, PASSWORD);

        when(usuarioDAO.select(USERNAME)).thenReturn(Optional.of(mockUsuario));

        assertFalse(gestorLogin.autenticar(USERNAME, WRONG_PASSWORD));
    }

    @Test
    void testAutenticarUsuarioNoExiste() {
        when(usuarioDAO.select(NON_EXISTENT_USER)).thenReturn(Optional.empty());

        assertFalse(gestorLogin.autenticar(NON_EXISTENT_USER, PASSWORD));
    }

    @Test
    void testRegistrarUsuarioExitoso() {
        when(usuarioDAO.select(NEW_USER)).thenReturn(Optional.empty());

        assertTrue(gestorLogin.registrar(NEW_USER, PASSWORD, USER_ROLE));

        verify(usuarioDAO).insert(any(Usuario.class));
    }

    @Test
    void testRegistrarUsuarioDuplicado() {
        when(usuarioDAO.select(EXISTING_USER)).thenReturn(Optional.of(new Usuario()));

        assertFalse(gestorLogin.registrar(EXISTING_USER, PASSWORD, USER_ROLE));
    }

    @Test
    void testRegistrarClienteExitoso() {
        when(usuarioDAO.select(NEW_USER)).thenReturn(Optional.empty());

        assertTrue(gestorLogin.registrarCliente(NEW_USER, PASSWORD, CLIENT_ROLE, CLIENT_NAME, CLIENT_SURNAME, CLIENT_DNI));

        verify(usuarioDAO).insert(any(Usuario.class));
        verify(clienteDAO).insert(any(Cliente.class));
    }

    @Test
    void testRegistrarClienteDuplicado() {
        when(usuarioDAO.select(EXISTING_USER)).thenReturn(Optional.of(new Usuario()));

        assertFalse(gestorLogin.registrarCliente(EXISTING_USER, PASSWORD, CLIENT_ROLE, CLIENT_NAME, CLIENT_SURNAME, CLIENT_DNI));
    }

    @Test
    void testRegistrarRepartidorExitoso() {
        when(usuarioDAO.select(NEW_USER)).thenReturn(Optional.empty());

        assertTrue(gestorLogin.registrarRepartidor(NEW_USER, PASSWORD, REPARTIDOR_ROLE, REPARTIDOR_NAME, REPARTIDOR_SURNAME, REPARTIDOR_DNI));

        verify(usuarioDAO).insert(any(Usuario.class));
        verify(repartidorDAO).insert(any(Repartidor.class));
    }

    @Test
    void testRegistrarRestauranteExitoso() {
        when(usuarioDAO.select(NEW_USER)).thenReturn(Optional.empty());

        assertTrue(gestorLogin.registrarRestaurante(NEW_USER, PASSWORD, RESTAURANTE_ROLE, RESTAURANTE_NAME, RESTAURANTE_ADDRESS));

        verify(usuarioDAO).insert(any(Usuario.class));
        verify(restauranteDAO).insert(any(Restaurante.class));
    }

    @Test
    void testRegistrarRestauranteDuplicado() {
        when(usuarioDAO.select(EXISTING_USER)).thenReturn(Optional.of(new Usuario()));

        assertFalse(gestorLogin.registrarRestaurante(EXISTING_USER, PASSWORD, RESTAURANTE_ROLE, RESTAURANTE_NAME, RESTAURANTE_ADDRESS));
    }

     @Test
    void testCifrarPassword_LanzaCifradoException() {
        // Arrange
        GestorLogin gestorLoginWithException = new GestorLogin() {
            @Override
            public String cifrarPassword(String password) {
                throw new CifradoException("Error al cifrar la contraseña", new NoSuchAlgorithmException());
            }
        };

        // Act & Assert
        CifradoException exception = assertThrows(CifradoException.class, () -> {
            gestorLoginWithException.cifrarPassword("password123");
        });

        assertEquals("Error al cifrar la contraseña", exception.getMessage());
    }
}