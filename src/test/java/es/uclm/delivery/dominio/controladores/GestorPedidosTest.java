package es.uclm.delivery.dominio.controladores;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import es.uclm.delivery.persistencia.*;
import es.uclm.delivery.presentacion.IUBusqueda;
import es.uclm.delivery.dominio.entidades.*;
import java.util.*;

class GestorPedidosTest {
    private final IUBusqueda iuBusqueda = mock(IUBusqueda.class);
    private final CartaMenuDAO cartaMenuDAO = mock(CartaMenuDAO.class);
    private final PedidoDAO pedidoDAO = mock(PedidoDAO.class);
    private final ClienteDAO clienteDAO = mock(ClienteDAO.class);
    private final DireccionDAO direccionDAO = mock(DireccionDAO.class);
    private final ServicioEntregaDAO servicioEntregaDAO = mock(ServicioEntregaDAO.class);
    private final RepartidorDAO repartidorDAO = mock(RepartidorDAO.class);
    private final GestorPedidos gestorPedidos;

    public GestorPedidosTest() {
        this.gestorPedidos = new GestorPedidos();
        // Inject dependencies
        gestorPedidos.IUBusqueda = iuBusqueda;
        gestorPedidos.cartaMenuDAO = cartaMenuDAO;
        gestorPedidos.pedidoDAO = pedidoDAO;
        gestorPedidos.clienteDAO = clienteDAO;
        gestorPedidos.direccionDAO = direccionDAO;
        gestorPedidos.servicioEntregaDAO = servicioEntregaDAO;
        gestorPedidos.repartidorDAO = repartidorDAO;
    }

    @Test
    void testRealizarPedidoConRestauranteValido() {
        Model model = mock(Model.class);
        Carrito carrito = new Carrito();
        Long restauranteId = 1L;

        // Inicializar restaurante con cartas de menú que tienen listas de items no
        // nulas
        Restaurante restaurante = new Restaurante();
        CartaMenu carta1 = new CartaMenu();
        carta1.setItems(new ArrayList<>()); // Lista vacía para evitar NullPointerException
        CartaMenu carta2 = new CartaMenu();
        carta2.setItems(new ArrayList<>());
        restaurante.setCartasMenu(Arrays.asList(carta1, carta2));

        when(iuBusqueda.obtenerRestaurante(restauranteId)).thenReturn(restaurante);

        String resultado = gestorPedidos.realizarPedido(restauranteId, model, carrito);

        assertEquals("realizarPedido", resultado);
        assertEquals(restauranteId, carrito.getRestauranteId());
        verify(model).addAttribute(eq("restaurante"), eq(restaurante));
    }

    @Test
    void testAgregarAlCarritoConMenuValidoyPrecioIncorrecto() {
        Carrito carrito = new Carrito();
        Long menuId = 1L;
        CartaMenu menu = new CartaMenu();
        ItemMenu item1 = new ItemMenu();
        item1.setId(1L);
        ItemMenu item2 = new ItemMenu();
        item2.setId(2L);
        menu.setItems(Arrays.asList(item1, item2)); // Agrega ítems válidos
        when(cartaMenuDAO.findById(menuId)).thenReturn(Optional.of(menu));

        Map<String, Long> requestData = Map.of("id", menuId);
        ResponseEntity<?> respuesta = gestorPedidos.agregarAlCarrito(carrito, requestData);

        assertEquals(200, respuesta.getStatusCode().value());
        assertEquals(0, carrito.getItems().size());
    }

    @Test
    void testAgregarAlCarritoConMenuyPrecioValido() {
        Carrito carrito = new Carrito();
        Long menuId = 1L;
        CartaMenu menu = new CartaMenu();
        ItemMenu item1 = new ItemMenu();
        item1.setId(1L);
        item1.setPrecio(10.0); // Asegurarse de que el precio es mayor que 0
        ItemMenu item2 = new ItemMenu();
        item2.setId(2L);
        item2.setPrecio(15.0); // Asegurarse de que el precio es mayor que 0
        menu.setItems(Arrays.asList(item1, item2));

        when(cartaMenuDAO.findById(menuId)).thenReturn(Optional.of(menu));

        Map<String, Long> requestData = Map.of("id", menuId);
        ResponseEntity<?> respuesta = gestorPedidos.agregarAlCarrito(carrito, requestData);

        assertEquals(200, respuesta.getStatusCode().value());
        assertEquals(2, carrito.getItems().size());
    }

    @Test
    void testRealizarPedidoConRestauranteInvalido() {
        Model model = mock(Model.class);
        Carrito carrito = new Carrito();
        Long restauranteId = 1L;

        when(iuBusqueda.obtenerRestaurante(restauranteId)).thenReturn(null);

        assertThrows(NullPointerException.class,
                () -> gestorPedidos.realizarPedido(restauranteId, model, carrito));

        verify(model, never()).addAttribute(anyString(), any());
    }

    @Test
    void testEliminarDelCarritoConItemExistente() {
        Carrito carrito = new Carrito();
        ItemMenu item = new ItemMenu();
        item.setId(1L);
        carrito.agregarItem(item);

        ResponseEntity<?> respuesta = gestorPedidos.eliminarDelCarrito(carrito, 1L);

        assertEquals(200, respuesta.getStatusCode().value());
        assertTrue(carrito.getItems().isEmpty());
    }

    @Test
    void testEliminarDelCarritoConItemInexistente() {
        Carrito carrito = new Carrito();
        ItemMenu item = new ItemMenu();
        item.setId(1L);
        item.setPrecio(10.0); // Asegurarse de que el precio es mayor que 0
        carrito.agregarItem(item);

        ResponseEntity<?> respuesta = gestorPedidos.eliminarDelCarrito(carrito, 99L);

        assertEquals(200, respuesta.getStatusCode().value());
        assertEquals(1, carrito.getItems().size()); // No elimina nada
    }

    @Test
    void testLimpiarCarrito() {
        Carrito carrito = new Carrito();
        carrito.agregarItem(new ItemMenu());
        carrito.agregarItem(new ItemMenu());

        SessionStatus sessionStatus = mock(SessionStatus.class);
        ResponseEntity<?> respuesta = gestorPedidos.limpiarCarrito(carrito);

        assertEquals(200, respuesta.getStatusCode().value());
        assertTrue(carrito.getItems().isEmpty());
    }

    @Test
    void testObtenerPedidosPagados() {
        Pedido pedido1 = new Pedido();
        pedido1.setId(1L);
        pedido1.setEstado(EstadoPedido.PAGADO);

        Pedido pedido2 = new Pedido();
        pedido2.setId(2L);
        pedido2.setEstado(EstadoPedido.PAGADO);

        when(pedidoDAO.findPedidosPagados()).thenReturn(List.of(pedido1, pedido2));

        ResponseEntity<List<Map<String, Object>>> respuesta = gestorPedidos.obtenerPedidosPagados();

        assertEquals(200, respuesta.getStatusCode().value());
        assertEquals(2, respuesta.getBody().size());
        assertEquals(1L, respuesta.getBody().get(0).get("id"));
        assertEquals("PAGADO", respuesta.getBody().get(0).get("estado"));
    }

    @Test
    void testObtenerPedidosAsignadosARepartidorValido() {
        // Mock del contexto de seguridad
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        // Configurar el contexto de seguridad para devolver un nombre de usuario válido
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("repartidor1");
        SecurityContextHolder.setContext(securityContext); // Establecer el contexto de seguridad mock

        // Crear el repartidor y simular el pedido asignado
        Repartidor repartidor = new Repartidor();
        repartidor.setId(1L);

        Cliente cliente = new Cliente();
        cliente.setNombre("Cliente1");
        cliente.setId(1L);

        Restaurante restaurante = new Restaurante();
        restaurante.setNombre("Restaurante1");
        restaurante.setId(1L);

        Pedido pedido1 = new Pedido();
        pedido1.setId(1L);
        pedido1.setEstado(EstadoPedido.RECOGIDO);
        pedido1.setCliente(cliente); // Asociar un cliente válido al pedido
        pedido1.setRestaurante(restaurante); // Asociar un restaurante válido al pedido

        ServicioEntrega servicio1 = new ServicioEntrega();
        servicio1.setPedido(pedido1);

        // Configurar los mocks
        when(repartidorDAO.findByUsername("repartidor1")).thenReturn(Optional.of(repartidor));
        when(servicioEntregaDAO.findByRepartidorId(1L)).thenReturn(List.of(servicio1));

        // Llamar al método y verificar el resultado
        ResponseEntity<List<Map<String, Object>>> respuesta = gestorPedidos.obtenerPedidosAsignados();

        assertEquals(200, respuesta.getStatusCode().value());
        assertEquals(1, respuesta.getBody().size());
        assertEquals(1L, respuesta.getBody().get(0).get("id"));
        assertEquals("RECOGIDO", respuesta.getBody().get(0).get("estado"));
    }

    @Test
    void testObtenerPedidosAsignadosARepartidorInvalido() {
        // Mock del contexto de seguridad
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        // Configurar el contexto de seguridad para devolver un nombre de usuario no
        // válido
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("repartidorInexistente");
        SecurityContextHolder.setContext(securityContext); // Establecer el contexto de seguridad mock

        // Simular que no se encuentra el repartidor en el DAO
        when(repartidorDAO.findByUsername("repartidorInexistente")).thenReturn(Optional.empty());

        // Llamar al método y verificar el resultado
        ResponseEntity<List<Map<String, Object>>> respuesta = gestorPedidos.obtenerPedidosAsignados();

        assertEquals(200, respuesta.getStatusCode().value());
        assertNull(respuesta.getBody());
    }
}