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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Map;
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

    private Pedido crearPedidoConRestaurante() {
        Pedido pedido = new Pedido();
        Restaurante restaurante = new Restaurante();
        restaurante.setNombre(RESTAURANTE_NOMBRE);
        pedido.setRestaurante(restaurante);
        return pedido;
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

    @Test
    void buscarRestaurantes_deberiaRetornarListaDeRestaurantes() {
        String codigoPostal = "12345";
        Restaurante restauranteA = new Restaurante();
        restauranteA.setId(1L);
        restauranteA.setNombre("Restaurante A");
        Restaurante restauranteB = new Restaurante();
        restauranteB.setId(2L);
        restauranteB.setNombre("Restaurante B");
        List<Restaurante> restaurantesMock = List.of(restauranteA, restauranteB);

        when(iuBusqueda.buscarRestaurantesPorCodigoPostal(codigoPostal)).thenReturn(restaurantesMock);

        List<Restaurante> resultado = gestorClientes.buscarRestaurantes(codigoPostal);

        assertEquals(2, resultado.size());
        verify(iuBusqueda).buscarRestaurantesPorCodigoPostal(codigoPostal);
    }

    @Test
    void testBuscarRestaurantes() {
        // Arrange
        String codigoPostal = "28001";
        List<Restaurante> restaurantes = List.of(new Restaurante(), new Restaurante());
        when(iuBusqueda.buscarRestaurantesPorCodigoPostal(codigoPostal)).thenReturn(restaurantes);

        // Act
        List<Restaurante> result = gestorClientes.buscarRestaurantes(codigoPostal);

        // Assert
        assertEquals(2, result.size());
        verify(iuBusqueda, times(1)).buscarRestaurantesPorCodigoPostal(codigoPostal);
    }

    @Test
    void testAgregarFavorito() {
        // Arrange
        Long idRestaurante = 1L;

        // Act
        gestorClientes.agregarFavorito(idRestaurante);

        // Assert
        verify(iuBusqueda, times(1)).marcarFavorito(idRestaurante);
    }

    @Test
    void testEliminarFavorito() {
        // Arrange
        Long idRestaurante = 1L;

        // Act
        gestorClientes.eliminarFavorito(idRestaurante);

        // Assert
        verify(iuBusqueda, times(1)).desmarcarFavorito(idRestaurante);
    }

    @Test
    void testObtenerPedidosEnCurso() {
        // Arrange
        Cliente cliente = new Cliente(); // Simula un cliente
        cliente.setId(1L); // Asegúrate de que el cliente tiene un ID
        Pedido pedido1 = crearPedidoConRestaurante();
        Pedido pedido2 = crearPedidoConRestaurante();
        List<Pedido> pedidosEnCurso = List.of(pedido1, pedido2);
        when(iuBusqueda.obtenerClienteActual()).thenReturn(cliente);
        when(iuPedido.obtenerPedidosEnCurso(cliente.getId())).thenReturn(pedidosEnCurso);

        // Act
        ResponseEntity<Object> result = gestorClientes.obtenerPedidosEnCurso();

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof List);
        verify(iuPedido, times(1)).obtenerPedidosEnCurso(cliente.getId());
    }

    @Test
    void testConfirmarRecepcionPedidoExistente() {
        // Arrange
        Map<String, Long> payload = Map.of("idPedido", 1L);
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        Restaurante restaurante = new Restaurante();
        restaurante.setNombre(RESTAURANTE_NOMBRE);
        pedido.setRestaurante(restaurante);
        when(iuPedido.obtenerPedidoPorId(1L)).thenReturn(Optional.of(pedido));

        // Act
        ResponseEntity<Object> result = gestorClientes.confirmarRecepcion(payload);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(pedidoDAO, times(1)).update(pedido);
    }

    @Test
    void testConfirmarRecepcionPedidoNoExistente() {
        // Arrange
        Map<String, Long> payload = Map.of("idPedido", 1L);
        when(iuPedido.obtenerPedidoPorId(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Object> result = gestorClientes.confirmarRecepcion(payload);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    void testObtenerPedidosAnteriores() {
        // Arrange
        Cliente cliente = new Cliente(); // Simula un cliente
        cliente.setId(1L); // Asegúrate de que el cliente tiene un ID
        Pedido pedido1 = crearPedidoConRestaurante();
        pedido1.setFecha(new Date()); // Añadir fecha al pedido
        Pedido pedido2 = crearPedidoConRestaurante();
        pedido2.setFecha(new Date()); // Añadir fecha al pedido
        List<Pedido> pedidosAnteriores = List.of(pedido1, pedido2);
        when(iuBusqueda.obtenerClienteActual()).thenReturn(cliente);
        when(iuPedido.obtenerPedidosEntregados(cliente.getId())).thenReturn(pedidosAnteriores);

        // Act
        ResponseEntity<Object> result = gestorClientes.obtenerPedidosAnteriores();

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof List);
        List<Map<String, Object>> body = (List<Map<String, Object>>) result.getBody();
        assertEquals(2, body.size());
        assertNotNull(body.get(0).get("fecha")); // Verificar que la fecha no sea nula
        verify(iuPedido, times(1)).obtenerPedidosEntregados(cliente.getId());
    }

    @Test
    void testValorarPedido() {
        // Arrange
        Map<String, Object> payload = Map.of("idPedido", 1L, "valoracion", 4);
        Pedido pedido = new Pedido();
        Repartidor repartidor = new Repartidor();
        repartidor.setEficiencia(3.5);
        ServicioEntrega servicioEntrega = new ServicioEntrega();
        servicioEntrega.setRepartidor(repartidor);
        when(iuPedido.obtenerPedidoPorId(1L)).thenReturn(Optional.of(pedido));
        when(iuPedido.obtenerServicioEntregaPorPedido(1L)).thenReturn(Optional.of(servicioEntrega));

        // Act
        ResponseEntity<Object> result = gestorClientes.valorarPedido(payload);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(3.75, repartidor.getEficiencia(), 0.01);
        verify(repartidorDAO, times(1)).update(repartidor);
    }

    @Test
    void testListarPedidosCancelados_Exito() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        Pedido pedido1 = crearPedidoConRestaurante();
        pedido1.setEstado(EstadoPedido.CANCELADO);
        pedido1.setFecha(new Date()); // Añadir fecha al pedido
        Pedido pedido2 = crearPedidoConRestaurante();
        pedido2.setEstado(EstadoPedido.CANCELADO);
        pedido2.setFecha(new Date()); // Añadir fecha al pedido
        List<Pedido> pedidosCancelados = List.of(pedido1, pedido2);
        when(iuBusqueda.obtenerClienteActual()).thenReturn(cliente);
        when(iuPedido.obtenerPedidosCancelados(cliente.getId())).thenReturn(pedidosCancelados);

        // Act
        ResponseEntity<Object> result = gestorClientes.listarPedidosCancelados();

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof List);
        List<Map<String, Object>> body = (List<Map<String, Object>>) result.getBody();
        assertEquals(2, body.size());
        assertNotNull(body.get(0).get("fecha")); // Verificar que la fecha no sea nula
        verify(iuPedido, times(1)).obtenerPedidosCancelados(cliente.getId());
    }

    @Test
    void testListarPedidosCancelados_SinPedidos() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        when(iuBusqueda.obtenerClienteActual()).thenReturn(cliente);
        when(iuPedido.obtenerPedidosCancelados(cliente.getId())).thenReturn(List.of());

        // Act
        ResponseEntity<Object> result = gestorClientes.listarPedidosCancelados();

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof List);
        List<Map<String, Object>> body = (List<Map<String, Object>>) result.getBody();
        assertTrue(body.isEmpty());
        verify(iuPedido, times(1)).obtenerPedidosCancelados(cliente.getId());
    }

}
