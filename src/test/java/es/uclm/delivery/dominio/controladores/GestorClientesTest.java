package es.uclm.delivery.dominio.controladores;

import es.uclm.delivery.dominio.entidades.*;
import es.uclm.delivery.persistencia.ClienteDAO;
import es.uclm.delivery.persistencia.DireccionDAO;
import es.uclm.delivery.persistencia.PedidoDAO;
import es.uclm.delivery.persistencia.RepartidorDAO;
import es.uclm.delivery.presentacion.IUBusqueda;
import es.uclm.delivery.presentacion.IUPedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class GestorClientesTest {

    @Autowired
    @InjectMocks
    private GestorClientes gestorClientes;

    @Mock
    private IUBusqueda iuBusqueda;

    @Mock
    private GestorPedidos gestorPedidos;

    @Mock
    private IUPedido iuPedido;

    @Mock
    private PedidoDAO pedidoDAO;

    @Mock
    private RepartidorDAO repartidorDAO;

    @Mock
    private DireccionDAO direccionDAO;

    @Mock
    private ClienteDAO clienteDAO;

    @Mock
    private Principal principal;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // -------------------------------
    // Métodos relacionados con favoritos
    // -------------------------------

    @Test
    void testAgregarFavorito_ValidId() {
        Long idRestaurante = 1L;

        gestorClientes.agregarFavorito(idRestaurante);

        verify(iuBusqueda).marcarFavorito(idRestaurante);
    }

    @Test
    void testAgregarFavorito_NullId() {
        gestorClientes.agregarFavorito(null);

        verify(iuBusqueda, never()).marcarFavorito(any());
    }

    @Test
    void testEliminarFavorito_ValidId() {
        Long idRestaurante = 1L;

        gestorClientes.eliminarFavorito(idRestaurante);

        verify(iuBusqueda).desmarcarFavorito(idRestaurante);
    }

    @Test
    void testEliminarFavorito_NullId() {
        gestorClientes.eliminarFavorito(null);

        verify(iuBusqueda, never()).desmarcarFavorito(any());
    }

    @Test
    void testListarFavoritos_Success() {
        Restaurante favoritoMock = new Restaurante();
        favoritoMock.setNombre("Favorito A");
        List<Restaurante> favoritosMock = List.of(favoritoMock);
        when(iuBusqueda.listarFavoritos()).thenReturn(favoritosMock);

        List<Restaurante> result = gestorClientes.listarFavoritos();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Favorito A", result.get(0).getNombre());
        verify(iuBusqueda).listarFavoritos();
    }

    @Test
    void testListarFavoritos_EmptyList() {
        when(iuBusqueda.listarFavoritos()).thenReturn(List.of());

        List<Restaurante> result = gestorClientes.listarFavoritos();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(iuBusqueda).listarFavoritos();
    }

    // -------------------------------
    // Métodos relacionados con direcciones
    // -------------------------------

    @Test
    void testGuardarDireccion_ValidCliente() {
        Usuario usuarioMock = new Usuario();
        usuarioMock.setUsername("testUser");
        String username = usuarioMock.getUsername();
        Direccion direccionMock = new Direccion();
        Cliente clienteMock = new Cliente();
        clienteMock.setUsuario(usuarioMock);
        direccionMock.setCalle("Calle Falsa");
        direccionMock.setCiudad("Ciudad falsa");
        direccionMock.setCodigoPostal("12345");
        direccionMock.setCliente(clienteMock);
        direccionMock.setId(1L);
        when(principal.getName()).thenReturn(username);
        when(clienteDAO.findByUsername(username)).thenReturn(Optional.of(clienteMock));

        ResponseEntity<?> response = gestorClientes.guardarDireccion(direccionMock, principal);

        assertEquals(200, response.getStatusCode().value());
        verify(direccionDAO).save(direccionMock);
        assertEquals(clienteMock, direccionMock.getCliente());
    }

    @Test
    void testGuardarDireccion_ClienteNoEncontrado() {
        String username = "testUser";
        Direccion direccionMock = new Direccion();
        Cliente clienteMock = new Cliente();
        direccionMock.setCalle("Calle Falsa");
        direccionMock.setCiudad("Ciudad falsa");
        direccionMock.setCodigoPostal("12345");
        direccionMock.setCliente(clienteMock);
        direccionMock.setId(1L);
        when(principal.getName()).thenReturn(username);
        when(clienteDAO.findByUsername(username)).thenReturn(Optional.empty());

        ResponseEntity<?> response = gestorClientes.guardarDireccion(direccionMock, principal);

        assertEquals(404, response.getStatusCode().value());
        assertEquals("Cliente no encontrado", response.getBody());
        verify(direccionDAO, never()).save(direccionMock);
    }

    @Test
    void testGuardarDireccion_NullDireccion() {
        String username = "testUser";
        Cliente clienteMock = new Cliente();
        when(principal.getName()).thenReturn(username);
        when(clienteDAO.findByUsername(username)).thenReturn(Optional.of(clienteMock));

        ResponseEntity<?> response = gestorClientes.guardarDireccion(null, principal);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Dirección inválida", response.getBody());
        verify(direccionDAO, never()).save(any());
    }

    @Test
    void testListarDirecciones_ValidCliente() {
        String username = "testUser";
        Cliente clienteMock = new Cliente();
        Direccion direccionMock = new Direccion();
        direccionMock.setCalle("Calle Falsa");
        direccionMock.setCiudad("Ciudad falsa");
        direccionMock.setCodigoPostal("12345");
        direccionMock.setCliente(clienteMock);
        direccionMock.setId(1L);

        clienteMock.setDirecciones(List.of(direccionMock));
        when(principal.getName()).thenReturn(username);
        when(clienteDAO.findByUsername(username)).thenReturn(Optional.of(clienteMock));

        ResponseEntity<List<Direccion>> response = gestorClientes.listarDirecciones(principal);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Calle Falsa", direccionMock.getCalle());
        assertEquals("Ciudad falsa", direccionMock.getCiudad());
        assertEquals("12345", direccionMock.getCodigoPostal());
        assertEquals(clienteMock, direccionMock.getCliente());
        assertEquals(1L, direccionMock.getId());

    }

    @Test
    void testListarDirecciones_ClienteNoEncontrado() {
        String username = "testUser";
        when(principal.getName()).thenReturn(username);
        when(clienteDAO.findByUsername(username)).thenReturn(Optional.empty());

        ResponseEntity<List<Direccion>> response = gestorClientes.listarDirecciones(principal);

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    void testListarDirecciones_NoDirecciones() {
        String username = "testUser";
        Cliente clienteMock = new Cliente();
        clienteMock.setDirecciones(List.of());
        when(principal.getName()).thenReturn(username);
        when(clienteDAO.findByUsername(username)).thenReturn(Optional.of(clienteMock));

        ResponseEntity<List<Direccion>> response = gestorClientes.listarDirecciones(principal);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }
}