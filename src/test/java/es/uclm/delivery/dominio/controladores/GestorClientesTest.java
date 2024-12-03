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

    private static final Long RESTAURANTE_ID = 1L;
    private static final String RESTAURANTE_NOMBRE = "Favorito A";
    private static final String USERNAME = "testUser";
    private static final String CALLE = "Calle Falsa";
    private static final String CIUDAD = "Ciudad falsa";
    private static final String CODIGO_POSTAL = "12345";
    private static final Long DIRECCION_ID = 1L;

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

    private Usuario usuarioMock;
    private Cliente clienteMock;
    private Direccion direccionMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        usuarioMock = crearUsuario();
        clienteMock = crearCliente();
        direccionMock = crearDireccion();
    }

    private Usuario crearUsuario() {
        Usuario usuario = new Usuario();
        usuario.setUsername(USERNAME);
        return usuario;
    }

    private Cliente crearCliente() {
        Cliente cliente = new Cliente();
        cliente.setUsuario(usuarioMock);
        return cliente;
    }

    private Direccion crearDireccion() {
        Direccion direccion = new Direccion();
        direccion.setCalle(CALLE);
        direccion.setCiudad(CIUDAD);
        direccion.setCodigoPostal(CODIGO_POSTAL);
        direccion.setCliente(clienteMock);
        direccion.setId(DIRECCION_ID);
        return direccion;
    }

    // -------------------------------
    // Métodos relacionados con favoritos
    // -------------------------------

    @Test
    void testAgregarFavorito_ValidId() {
        gestorClientes.agregarFavorito(RESTAURANTE_ID);

        verify(iuBusqueda).marcarFavorito(RESTAURANTE_ID);
    }

    @Test
    void testAgregarFavorito_NullId() {
        gestorClientes.agregarFavorito(null);

        verify(iuBusqueda, never()).marcarFavorito(any());
    }

    @Test
    void testEliminarFavorito_ValidId() {
        gestorClientes.eliminarFavorito(RESTAURANTE_ID);

        verify(iuBusqueda).desmarcarFavorito(RESTAURANTE_ID);
    }

    @Test
    void testEliminarFavorito_NullId() {
        gestorClientes.eliminarFavorito(null);

        verify(iuBusqueda, never()).desmarcarFavorito(any());
    }

    @Test
    void testListarFavoritos_Success() {
        Restaurante favoritoMock = new Restaurante();
        favoritoMock.setNombre(RESTAURANTE_NOMBRE);
        List<Restaurante> favoritosMock = List.of(favoritoMock);
        when(iuBusqueda.listarFavoritos()).thenReturn(favoritosMock);

        List<Restaurante> result = gestorClientes.listarFavoritos();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(RESTAURANTE_NOMBRE, result.get(0).getNombre());
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
        when(principal.getName()).thenReturn(USERNAME);
        when(clienteDAO.findByUsername(USERNAME)).thenReturn(Optional.of(clienteMock));

        ResponseEntity<?> response = gestorClientes.guardarDireccion(direccionMock, principal);

        assertEquals(200, response.getStatusCode().value());
        verify(direccionDAO).save(direccionMock);
        assertEquals(clienteMock, direccionMock.getCliente());
    }

    @Test
    void testGuardarDireccion_ClienteNoEncontrado() {
        when(principal.getName()).thenReturn(USERNAME);
        when(clienteDAO.findByUsername(USERNAME)).thenReturn(Optional.empty());

        ResponseEntity<?> response = gestorClientes.guardarDireccion(direccionMock, principal);

        assertEquals(404, response.getStatusCode().value());
        assertEquals("Cliente no encontrado", response.getBody());
        verify(direccionDAO, never()).save(direccionMock);
    }

    @Test
    void testGuardarDireccion_NullDireccion() {
        when(principal.getName()).thenReturn(USERNAME);
        when(clienteDAO.findByUsername(USERNAME)).thenReturn(Optional.of(clienteMock));

        ResponseEntity<?> response = gestorClientes.guardarDireccion(null, principal);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Dirección inválida", response.getBody());
        verify(direccionDAO, never()).save(any());
    }

    @Test
    void testListarDirecciones_ValidCliente() {
        clienteMock.setDirecciones(List.of(direccionMock));
        when(principal.getName()).thenReturn(USERNAME);
        when(clienteDAO.findByUsername(USERNAME)).thenReturn(Optional.of(clienteMock));

        ResponseEntity<List<Direccion>> response = gestorClientes.listarDirecciones(principal);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(CALLE, direccionMock.getCalle());
        assertEquals(CIUDAD, direccionMock.getCiudad());
        assertEquals(CODIGO_POSTAL, direccionMock.getCodigoPostal());
        assertEquals(clienteMock, direccionMock.getCliente());
        assertEquals(DIRECCION_ID, direccionMock.getId());
    }

    @Test
    void testListarDirecciones_ClienteNoEncontrado() {
        when(principal.getName()).thenReturn(USERNAME);
        when(clienteDAO.findByUsername(USERNAME)).thenReturn(Optional.empty());

        ResponseEntity<List<Direccion>> response = gestorClientes.listarDirecciones(principal);

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    void testListarDirecciones_NoDirecciones() {
        clienteMock.setDirecciones(List.of());
        when(principal.getName()).thenReturn(USERNAME);
        when(clienteDAO.findByUsername(USERNAME)).thenReturn(Optional.of(clienteMock));

        ResponseEntity<List<Direccion>> response = gestorClientes.listarDirecciones(principal);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }
}