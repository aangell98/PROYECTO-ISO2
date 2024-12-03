package es.uclm.delivery.presentacion;

import es.uclm.delivery.dominio.entidades.*;
import es.uclm.delivery.persistencia.ClienteDAO;
import es.uclm.delivery.persistencia.RestauranteDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IUBusquedaTest {

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
        usuario.setUsername("testUser");

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setUsuario(usuario);
        cliente.setFavoritos(new ArrayList<>()); // Inicializar la colección favoritos

        restaurante = new Restaurante();
        restaurante.setId(1L);
        restaurante.setNombre("Restaurante Test");
        restaurante.setDireccion("Calle Ficticia, 123");

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
        assertEquals("Restaurante Test", result.get(0).getNombre());
    }

    // Test para marcarFavorito
    @Test
    void testMarcarFavorito() {
        when(clienteDAO.findById(1L)).thenReturn(Optional.of(cliente));
        when(restauranteDAO.findById(1L)).thenReturn(Optional.of(restaurante));
        when(clienteDAO.update(cliente)).thenReturn(1);

        iuBusqueda.marcarFavorito(1L);

        assertTrue(cliente.getFavoritos().contains(favorito));
    }

    @Test
    void testMarcarFavorito_ClienteNoExistente() {
        when(clienteDAO.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> iuBusqueda.marcarFavorito(1L));
        assertEquals("Cliente no encontrado", exception.getMessage());
    }

    @Test
    void testMarcarFavorito_RestauranteNoExistente() {
        when(clienteDAO.findById(1L)).thenReturn(Optional.of(cliente));
        when(restauranteDAO.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> iuBusqueda.marcarFavorito(1L));
        assertEquals("Restaurante no encontrado", exception.getMessage());
    }

    // Test para desmarcarFavorito
    @Test
    void testDesmarcarFavorito() {
        when(clienteDAO.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteDAO.update(cliente)).thenReturn(1);

        iuBusqueda.desmarcarFavorito(1L);

        assertFalse(cliente.getFavoritos().contains(favorito));
    }

    @Test
    void testDesmarcarFavorito_ClienteNoExistente() {
        when(clienteDAO.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> iuBusqueda.desmarcarFavorito(1L));
        assertEquals("Cliente no encontrado", exception.getMessage());
    }

    @Test
    void testDesmarcarFavorito_FavoritoNoExistente() {
        when(clienteDAO.findById(1L)).thenReturn(Optional.of(cliente));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> iuBusqueda.desmarcarFavorito(2L));
        assertEquals("Favorito no encontrado", exception.getMessage());
    }

    // Test para listarFavoritos
    @Test
    void testListarFavoritos() {
        when(clienteDAO.findById(1L)).thenReturn(Optional.of(cliente));

        List<Restaurante> result = iuBusqueda.listarFavoritos();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Restaurante Test", result.get(0).getNombre());
    }

    @Test
    void testListarFavoritos_ClienteNoExistente() {
        when(clienteDAO.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> iuBusqueda.listarFavoritos());
        assertEquals("Cliente no encontrado", exception.getMessage());
    }

    // Test para obtenerRestaurante
    @Test
    void testObtenerRestaurante() {
        when(restauranteDAO.findById(1L)).thenReturn(Optional.of(restaurante));
        when(restauranteDAO.findCartasMenuByRestauranteId(1L)).thenReturn(List.of(new CartaMenu(), new CartaMenu()));

        Restaurante result = iuBusqueda.obtenerRestaurante(1L);

        assertNotNull(result);
        assertEquals(2, result.getCartasMenu().size());
    }

    @Test
    void testObtenerRestaurante_RestauranteNoExistente() {
        when(restauranteDAO.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> iuBusqueda.obtenerRestaurante(1L));
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
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testUser");
        SecurityContextHolder.getContext().setAuthentication(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(userDetails, null));

        when(clienteDAO.findByUsername("testUser")).thenReturn(Optional.of(cliente));

        Cliente result = iuBusqueda.obtenerClienteActual();

        assertNotNull(result);
        assertEquals("testUser", result.getUsuario().getUsername());
    }

    @Test
    void testObtenerClienteActual_ClienteNoExistente() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("nonExistentUser");
        SecurityContextHolder.getContext().setAuthentication(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(userDetails, null));

        when(clienteDAO.findByUsername("nonExistentUser")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> iuBusqueda.obtenerClienteActual());
        assertEquals("Cliente no encontrado", exception.getMessage());
    }
}