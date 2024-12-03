package es.uclm.delivery.presentacion;

import es.uclm.delivery.dominio.entidades.*;
import es.uclm.delivery.persistencia.ClienteDAO;
import es.uclm.delivery.persistencia.RestauranteDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IUBusquedaTest {

    private static final Long CLIENTE_ID = 1L;
    private static final Long RESTAURANTE_ID = 1L;
    private static final String USERNAME = "testUser";
    private static final String RESTAURANTE_NOMBRE = "Restaurante Test";
    private static final String RESTAURANTE_DIRECCION = "Calle Ficticia, 123";

    @InjectMocks
    private IUBusqueda iuBusqueda;

    @Mock
    private RestauranteDAO restauranteDAO;

    @Mock
    private ClienteDAO clienteDAO;

    private Cliente cliente;
    private Restaurante restaurante;
    private ClienteFavoritos favorito;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        usuario = new Usuario();
        usuario.setUsername(USERNAME);

        cliente = new Cliente();
        cliente.setId(CLIENTE_ID);
        cliente.setUsuario(usuario);
        cliente.setFavoritos(new ArrayList<>()); // Inicializar la colección favoritos

        restaurante = new Restaurante();
        restaurante.setId(RESTAURANTE_ID);
        restaurante.setNombre(RESTAURANTE_NOMBRE);
        restaurante.setDireccion(RESTAURANTE_DIRECCION);

        favorito = new ClienteFavoritos();
        favorito.setCliente(cliente);
        favorito.setRestaurante(restaurante);
        cliente.getFavoritos().add(favorito);
    }

    // Test para buscarRestaurantesPorCodigoPostal
    @Test
    void testBuscarRestaurantesPorCodigoPostal() {
        when(restauranteDAO.findByCodigoPostal("28001")).thenReturn(List.of(restaurante));

        List<Restaurante> result = iuBusqueda.buscarRestaurantesPorCodigoPostal("28001");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(RESTAURANTE_NOMBRE, result.get(0).getNombre());
    }

    // Test para marcarFavorito
    @Test
    void testMarcarFavorito() {
        when(clienteDAO.findById(CLIENTE_ID)).thenReturn(Optional.of(cliente));
        when(restauranteDAO.findById(RESTAURANTE_ID)).thenReturn(Optional.of(restaurante));
        when(clienteDAO.update(cliente)).thenReturn(1);

        iuBusqueda.marcarFavorito(RESTAURANTE_ID);

        assertTrue(cliente.getFavoritos().contains(favorito));
    }

    @Test
    void testMarcarFavorito_ClienteNoExistente() {
        when(clienteDAO.findById(CLIENTE_ID)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> iuBusqueda.marcarFavorito(RESTAURANTE_ID));
        assertEquals("Cliente no encontrado", exception.getMessage());
    }

    @Test
    void testMarcarFavorito_RestauranteNoExistente() {
        when(clienteDAO.findById(CLIENTE_ID)).thenReturn(Optional.of(cliente));
        when(restauranteDAO.findById(RESTAURANTE_ID)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> iuBusqueda.marcarFavorito(RESTAURANTE_ID));
        assertEquals("Restaurante no encontrado", exception.getMessage());
    }

    // Test para desmarcarFavorito
    @Test
    void testDesmarcarFavorito() {
        when(clienteDAO.findById(CLIENTE_ID)).thenReturn(Optional.of(cliente));
        when(clienteDAO.update(cliente)).thenReturn(1);

        iuBusqueda.desmarcarFavorito(RESTAURANTE_ID);

        assertFalse(cliente.getFavoritos().contains(favorito));
    }

    @Test
    void testDesmarcarFavorito_ClienteNoExistente() {
        when(clienteDAO.findById(CLIENTE_ID)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> iuBusqueda.desmarcarFavorito(RESTAURANTE_ID));
        assertEquals("Cliente no encontrado", exception.getMessage());
    }

    @Test
    void testDesmarcarFavorito_FavoritoNoExistente() {
        when(clienteDAO.findById(CLIENTE_ID)).thenReturn(Optional.of(cliente));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> iuBusqueda.desmarcarFavorito(2L));
        assertEquals("Favorito no encontrado", exception.getMessage());
    }

    // Test para listarFavoritos
    @Test
    void testListarFavoritos() {
        when(clienteDAO.findById(CLIENTE_ID)).thenReturn(Optional.of(cliente));

        List<Restaurante> result = iuBusqueda.listarFavoritos();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(RESTAURANTE_NOMBRE, result.get(0).getNombre());
    }

    @Test
    void testListarFavoritos_ClienteNoExistente() {
        when(clienteDAO.findById(CLIENTE_ID)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> iuBusqueda.listarFavoritos());
        assertEquals("Cliente no encontrado", exception.getMessage());
    }

    // Test para obtenerRestaurante
    @Test
    void testObtenerRestaurante() {
        when(restauranteDAO.findById(RESTAURANTE_ID)).thenReturn(Optional.of(restaurante));
        when(restauranteDAO.findCartasMenuByRestauranteId(RESTAURANTE_ID)).thenReturn(List.of(new CartaMenu(), new CartaMenu()));

        Restaurante result = iuBusqueda.obtenerRestaurante(RESTAURANTE_ID);

        assertNotNull(result);
        assertEquals(2, result.getCartasMenu().size());
    }

    @Test
    void testObtenerRestaurante_RestauranteNoExistente() {
        when(restauranteDAO.findById(RESTAURANTE_ID)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> iuBusqueda.obtenerRestaurante(RESTAURANTE_ID));
        assertEquals("Restaurante no encontrado", exception.getMessage());
    }

    // Test para obtenerRestaurantesDestacados
    @Test
    void testObtenerRestaurantesDestacados() {
        when(restauranteDAO.obtenerRestaurantesAleatorios(4)).thenReturn(List.of(restaurante, restaurante));

        List<Restaurante> result = iuBusqueda.obtenerRestaurantesDestacados();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
    }

    // Test para obtenerClienteActual
    @Test
    void testObtenerClienteActual() {
        // Simular el SecurityContext y la autenticación
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(USERNAME);

        SecurityContextHolder.setContext(securityContext);

        when(clienteDAO.findByUsername(USERNAME)).thenReturn(Optional.of(cliente));

        Cliente result = iuBusqueda.obtenerClienteActual();

        assertNotNull(result);
        assertEquals(USERNAME, result.getUsuario().getUsername());
    }

    @Test
    void testObtenerClienteActual_ClienteNoExistente() {
        // Simular el SecurityContext y la autenticación
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("nonExistentUser");

        SecurityContextHolder.setContext(securityContext);

        when(clienteDAO.findByUsername("nonExistentUser")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> iuBusqueda.obtenerClienteActual());
        assertEquals("Cliente no encontrado", exception.getMessage());
    }

    @Test
void testObtenerClienteActual_PrincipalNoUserDetails() {
    // Simular el SecurityContext y la autenticación
    SecurityContext securityContext = mock(SecurityContext.class);
    Authentication authentication = mock(Authentication.class);
    String principalName = "principalName";

    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(principalName);

    SecurityContextHolder.setContext(securityContext);

    Cliente cliente = new Cliente();
    cliente.setUsuario(new Usuario());
    cliente.getUsuario().setUsername(principalName);

    when(clienteDAO.findByUsername(principalName)).thenReturn(Optional.of(cliente));

    Cliente result = iuBusqueda.obtenerClienteActual();

    assertNotNull(result);
    assertEquals(principalName, result.getUsuario().getUsername());
}
}