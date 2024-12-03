package es.uclm.delivery.dominio.controladores;

import es.uclm.delivery.dominio.entidades.*;
import es.uclm.delivery.persistencia.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GestorRepartosTest {

    private static final String USERNAME = "repartidorTest";
    private static final Long REPARTIDOR_ID = 1L;
    private static final Long PEDIDO_ID = 1L;

    @InjectMocks
    private GestorRepartos gestorRepartos;

    @Mock
    private ServicioEntregaDAO servicioEntregaDAO;

    @Mock
    private RepartidorDAO repartidorDAO;

    @Mock
    private PedidoDAO pedidoDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        configurarContextoSeguridad(USERNAME);
    }

    private void configurarContextoSeguridad(String username) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(username);
        when(authentication.isAuthenticated()).thenReturn(true);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    private Repartidor crearRepartidor() {
        Repartidor repartidor = new Repartidor();
        repartidor.setId(REPARTIDOR_ID);
        Usuario usuario = new Usuario();
        usuario.setUsername(USERNAME);
        repartidor.setUsuario(usuario);
        return repartidor;
    }

    private Pedido crearPedido() {
        Pedido pedido = new Pedido();
        pedido.setId(PEDIDO_ID);
        pedido.setEstado(EstadoPedido.RECOGIDO);
        Cliente cliente = new Cliente();
        cliente.setNombre("Cliente Test");
        cliente.setApellidos("Apellido Test");
        Restaurante restaurante = new Restaurante();
        restaurante.setNombre("Restaurante Test");
        restaurante.setDireccion("Direccion Restaurante");
        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);
        return pedido;
    }

    private Direccion crearDireccion(Cliente cliente) {
        Direccion direccion = new Direccion();
        direccion.setCalle("Calle falsa");
        direccion.setCiudad("Ciudad falsa");
        direccion.setCodigoPostal("12345");
        direccion.setId(1L);
        direccion.setCliente(cliente);
        return direccion;
    }

    // -------------------------------
    // Tests para autoasignarPedido
    // -------------------------------

    @Test
    void testAutoasignarPedido_ValidPedidoAndRepartidor() {
        Repartidor repartidor = crearRepartidor();
        Pedido pedido = crearPedido();
        ServicioEntrega servicioEntrega = new ServicioEntrega();
        servicioEntrega.setPedido(pedido);
        servicioEntrega.setEstado(EstadoPedido.PAGADO);

        when(repartidorDAO.findByUsername(USERNAME)).thenReturn(Optional.of(repartidor));
        when(servicioEntregaDAO.findByPedidoId(PEDIDO_ID)).thenReturn(Optional.of(servicioEntrega));
        when(pedidoDAO.findById(PEDIDO_ID)).thenReturn(Optional.of(pedido));

        ResponseEntity<?> response = gestorRepartos.autoasignarPedido(PEDIDO_ID);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Pedido autoasignado con éxito", response.getBody());
        verify(servicioEntregaDAO).update(servicioEntrega);
        verify(pedidoDAO).update(pedido);
    }

    @Test
    void testAutoasignarPedido_PedidoNoDisponible() {
        Repartidor repartidor = crearRepartidor();
        ServicioEntrega servicioEntrega = new ServicioEntrega();
        servicioEntrega.setEstado(EstadoPedido.ENTREGADO);

        when(repartidorDAO.findByUsername(USERNAME)).thenReturn(Optional.of(repartidor));
        when(servicioEntregaDAO.findByPedidoId(PEDIDO_ID)).thenReturn(Optional.of(servicioEntrega));

        ResponseEntity<?> response = gestorRepartos.autoasignarPedido(PEDIDO_ID);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("El pedido no está disponible para autoasignación", response.getBody());
        verify(servicioEntregaDAO, never()).update(any());
    }

    @Test
    void testAutoasignarPedido_PedidoNoEncontrado() {
        when(repartidorDAO.findByUsername(USERNAME)).thenReturn(Optional.of(new Repartidor()));
        when(servicioEntregaDAO.findByPedidoId(PEDIDO_ID)).thenReturn(Optional.empty());

        ResponseEntity<?> response = gestorRepartos.autoasignarPedido(PEDIDO_ID);

        assertEquals(404, response.getStatusCode().value());
        assertEquals("Pedido no encontrado", response.getBody());
    }

    @Test
    void testAutoasignarPedido_RepartidorNoEncontrado() {
        when(repartidorDAO.findByUsername(USERNAME)).thenReturn(Optional.empty());

        ResponseEntity<?> response = gestorRepartos.autoasignarPedido(PEDIDO_ID);

        assertEquals(404, response.getStatusCode().value());
        assertEquals("Repartidor no encontrado", response.getBody());
    }

    @Test
    void testAutoasignarPedido_ErrorInterno() {
        when(repartidorDAO.findByUsername(USERNAME)).thenThrow(RuntimeException.class);

        ResponseEntity<?> response = gestorRepartos.autoasignarPedido(PEDIDO_ID);

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Error al autoasignar el pedido", response.getBody());
    }

    // -------------------------------
    // Tests para obtenerPedidosAsignados
    // -------------------------------

    @Test
    void testObtenerPedidosAsignados_RepartidorConPedidos() {
        Repartidor repartidor = crearRepartidor();
        Pedido pedido = crearPedido();
        Direccion direccion = crearDireccion(pedido.getCliente());
        ServicioEntrega servicioEntrega = new ServicioEntrega();
        servicioEntrega.setPedido(pedido);
        servicioEntrega.setDireccion(direccion);

        when(repartidorDAO.findByUsername(USERNAME)).thenReturn(Optional.of(repartidor));
        when(servicioEntregaDAO.findByRepartidorId(REPARTIDOR_ID)).thenReturn(Collections.singletonList(servicioEntrega));

        ResponseEntity<List<Map<String, Object>>> response = gestorRepartos.obtenerPedidosAsignados();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        Map<String, Object> pedidoDetalles = response.getBody().get(0);
        assertEquals("Cliente Test Apellido Test", pedidoDetalles.get("cliente"));
        assertEquals("Restaurante Test (Direccion Restaurante)", pedidoDetalles.get("restaurante"));
        assertEquals("Calle falsa, Ciudad falsa, 12345", pedidoDetalles.get("direccion"));
        assertEquals("RECOGIDO", pedidoDetalles.get("estado"));
    }

    @Test
    void testObtenerPedidosAsignados_SinPedidos() {
        Repartidor repartidor = crearRepartidor();

        when(repartidorDAO.findByUsername(USERNAME)).thenReturn(Optional.of(repartidor));
        when(servicioEntregaDAO.findByRepartidorId(REPARTIDOR_ID)).thenReturn(Collections.emptyList());

        ResponseEntity<List<Map<String, Object>>> response = gestorRepartos.obtenerPedidosAsignados();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());
    }

    @Test
    void testObtenerPedidosAsignados_RepartidorNoEncontrado() {
        when(repartidorDAO.findByUsername(USERNAME)).thenReturn(Optional.empty());

        ResponseEntity<List<Map<String, Object>>> response = gestorRepartos.obtenerPedidosAsignados();

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    // -------------------------------
    // Tests para obtenerPerfilRepartidor
    // -------------------------------

    @Test
    void testObtenerPerfilRepartidor_Valido() {
        String username = "repartidorTest";

        Repartidor repartidor = new Repartidor();
        repartidor.setNombre("Juan");
        repartidor.setApellidos("Pérez");
        repartidor.setDni("12345678A");
        repartidor.setEficiencia(4.5);

        when(repartidorDAO.findByUsername(username)).thenReturn(Optional.of(repartidor));

        ResponseEntity<Map<String, Object>> response = gestorRepartos.obtenerPerfilRepartidor();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Juan", response.getBody().get("nombre"));
        assertEquals("Pérez", response.getBody().get("apellidos"));
        assertEquals("12345678A", response.getBody().get("dni"));
        assertEquals(4.5, response.getBody().get("valoracionMedia"));
    }

    @Test
    void testObtenerPerfilRepartidor_NoEncontrado() {
        String username = "repartidorTest";

        when(repartidorDAO.findByUsername(username)).thenReturn(Optional.empty());

        ResponseEntity<Map<String, Object>> response = gestorRepartos.obtenerPerfilRepartidor();

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }
}