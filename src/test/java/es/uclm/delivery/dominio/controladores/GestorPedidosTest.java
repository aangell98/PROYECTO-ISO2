package es.uclm.delivery.dominio.controladores;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.http.HttpStatus;
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
    private final PagoDAO pagoDAO = mock(PagoDAO.class);
    private final GestorPedidos gestorPedidos;

    public GestorPedidosTest() {
        this.gestorPedidos = new GestorPedidos();
        // Inject dependencies
        gestorPedidos.iuBusqueda = iuBusqueda;
        gestorPedidos.cartaMenuDAO = cartaMenuDAO;
        gestorPedidos.pedidoDAO = pedidoDAO;
        gestorPedidos.clienteDAO = clienteDAO;
        gestorPedidos.direccionDAO = direccionDAO;
        gestorPedidos.servicioEntregaDAO = servicioEntregaDAO;
        gestorPedidos.repartidorDAO = repartidorDAO;
        gestorPedidos.pagoDAO = pagoDAO;
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
        verify(model).addAttribute("restaurante", restaurante);
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

    @Test
    void testConfirmarPedidoDireccionIdInvalido() {
        Carrito carrito = new Carrito();
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("direccionId", "abc"); // ID inválido
        requestData.put("metodoPago", "TARJETA");
        requestData.put("pagoInfo", Map.of("detalle", "info"));

        ResponseEntity<?> response = gestorPedidos.confirmarPedido(carrito, requestData);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("ID de dirección inválido", response.getBody());
    }

    @Test
    void testConfirmarPedidoDireccionNoEncontrada() {
        Carrito carrito = new Carrito();
        carrito.setRestauranteId(1L);
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("direccionId", "1");
        requestData.put("metodoPago", "TARJETA");
        requestData.put("pagoInfo", Map.of("detalle", "info"));

        Cliente cliente = new Cliente();
        cliente.setUsuario(new Usuario());
        cliente.getUsuario().setUsername("testUser");

        when(iuBusqueda.obtenerClienteActual()).thenReturn(cliente);
        when(clienteDAO.findByUsername("testUser")).thenReturn(Optional.of(cliente));
        when(direccionDAO.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = gestorPedidos.confirmarPedido(carrito, requestData);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Dirección no encontrada", response.getBody());
    }

    @Test
    void testConfirmarPedidoClienteNoEncontrado() {
        Carrito carrito = new Carrito();
        carrito.setRestauranteId(1L);
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("direccionId", "1");
        requestData.put("metodoPago", "TARJETA");
        requestData.put("pagoInfo", Map.of("detalle", "info"));

        Cliente cliente = new Cliente();
        cliente.setUsuario(new Usuario());
        cliente.getUsuario().setUsername("testUser");

        when(iuBusqueda.obtenerClienteActual()).thenReturn(cliente);
        when(clienteDAO.findByUsername("testUser")).thenReturn(Optional.empty());

        ResponseEntity<?> response = gestorPedidos.confirmarPedido(carrito, requestData);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Cliente no encontrado", response.getBody());
    }

    @Test
    void testConfirmarPedidoErrorInterno() {
        Carrito carrito = new Carrito();
        carrito.setRestauranteId(1L);
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("direccionId", "1");
        requestData.put("metodoPago", "TARJETA");
        requestData.put("pagoInfo", Map.of("detalle", "info"));

        Cliente cliente = new Cliente();
        cliente.setUsuario(new Usuario());
        cliente.getUsuario().setUsername("testUser");

        Direccion direccion = new Direccion();
        direccion.setId(1L);

        when(iuBusqueda.obtenerClienteActual()).thenReturn(cliente);
        when(clienteDAO.findByUsername("testUser")).thenReturn(Optional.of(cliente));
        when(direccionDAO.findById(1L)).thenReturn(Optional.of(direccion));
        when(iuBusqueda.obtenerRestaurante(1L)).thenReturn(new Restaurante());
        doThrow(new RuntimeException("Error interno")).when(pedidoDAO).insert(any(Pedido.class));

        ResponseEntity<?> response = gestorPedidos.confirmarPedido(carrito, requestData);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error al confirmar el pedido", response.getBody());
    }

    @Test
void testConfirmarPedido_DireccionInvalida() {
    // Arrange
    Map<String, Object> requestData = Map.of(
        "direccionId", "invalid_id",  // Dirección inválida
        "metodoPago", "CREDIT_CARD",
        "pagoInfo", Map.of("numeroTarjeta", "1234567890")
    );
    Carrito carrito = new Carrito();
    
    // Act
    ResponseEntity<Object> result = gestorPedidos.confirmarPedido(carrito, requestData);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    assertEquals("ID de dirección inválido", result.getBody());
}

@Test
void testConfirmarPedido_FaltanParametros() {
    // Arrange
    Map<String, Object> requestData = Map.of(
        "direccionId", 1L,  // Dirección válida
        "metodoPago", "CREDIT_CARD"  // Pago sin datos
    );
    Carrito carrito = new Carrito();

    // Act
    ResponseEntity<Object> result = gestorPedidos.confirmarPedido(carrito, requestData);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    assertEquals("Dirección, método de pago y datos de pago son requeridos", result.getBody());
}

@Test
void testConfirmarPedido_ClienteNoEncontrado() {
    // Arrange
    Map<String, Object> requestData = Map.of(
        "direccionId", 1L,
        "metodoPago", "CREDIT_CARD",
        "pagoInfo", Map.of("numeroTarjeta", "1234567890")
    );
    Carrito carrito = new Carrito();
    when(iuBusqueda.obtenerClienteActual()).thenReturn(new Cliente());  // Cliente simulado

    // Simulamos que no se encuentra el cliente en la base de datos
    when(clienteDAO.findByUsername(anyString())).thenReturn(Optional.empty());

    // Act
    ResponseEntity<Object> result = gestorPedidos.confirmarPedido(carrito, requestData);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    assertEquals("Cliente no encontrado", result.getBody());
}

@Test
void testConfirmarPedido_Exito() {
    // Arrange
    Map<String, Object> requestData = Map.of(
        "direccionId", "1",
        "metodoPago", "CREDIT_CARD",
        "pagoInfo", Map.of("numeroTarjeta", "1234567890")
    );
    Carrito carrito = new Carrito();
    carrito.setRestauranteId(1L);

    // Simulamos que el cliente y la dirección existen
    Cliente cliente = new Cliente();
    cliente.setUsuario(new Usuario());
    cliente.getUsuario().setUsername("testUser");
    Direccion direccion = new Direccion();
    Restaurante restaurante = new Restaurante();
    restaurante.setId(1L);

    when(iuBusqueda.obtenerClienteActual()).thenReturn(cliente);
    when(clienteDAO.findByUsername("testUser")).thenReturn(Optional.of(cliente));
    when(direccionDAO.findById(1L)).thenReturn(Optional.of(direccion));
    when(iuBusqueda.obtenerRestaurante(1L)).thenReturn(restaurante);

    // Act
    ResponseEntity<Object> result = gestorPedidos.confirmarPedido(carrito, requestData);

    // Assert
    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertEquals("Pedido confirmado y servicio de entrega en proceso", result.getBody());
    // Verificar que los métodos insert han sido llamados
    verify(pedidoDAO, times(1)).insert(any(Pedido.class));
    verify(pagoDAO, times(1)).insert(any(Pago.class));
    verify(servicioEntregaDAO, times(1)).insert(any(ServicioEntrega.class));
}

@Test
void testCancelarPedido_Exito() {
    // Arrange
    Long pedidoId = 1L;
    Pedido pedido = new Pedido();
    pedido.setId(pedidoId);
    pedido.setEstado(EstadoPedido.PAGADO);

    when(pedidoDAO.findById(pedidoId)).thenReturn(Optional.of(pedido));

    // Act
    ResponseEntity<Object> response = gestorPedidos.cancelarPedido(Map.of("idPedido", pedidoId));

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Pedido cancelado con éxito", response.getBody());
    verify(pedidoDAO, times(1)).update(pedido);
    assertEquals(EstadoPedido.CANCELADO, pedido.getEstado());
}

@Test
void testCancelarPedido_PedidoNoEncontrado() {
    // Arrange
    Long pedidoId = 1L;
    when(pedidoDAO.findById(pedidoId)).thenReturn(Optional.empty());

    // Act
    ResponseEntity<Object> response = gestorPedidos.cancelarPedido(Map.of("idPedido", pedidoId));

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Pedido no encontrado", response.getBody());
}

@Test
void testCancelarPedido_EstadoInvalido() {
    // Arrange
    Long pedidoId = 1L;
    Pedido pedido = new Pedido();
    pedido.setId(pedidoId);
    pedido.setEstado(EstadoPedido.ENTREGADO);

    when(pedidoDAO.findById(pedidoId)).thenReturn(Optional.of(pedido));

    // Act
    ResponseEntity<Object> response = gestorPedidos.cancelarPedido(Map.of("idPedido", pedidoId));

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("No se puede cancelar el pedido en el estado actual", response.getBody());
}


}
