package es.uclm.delivery.presentacion;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import es.uclm.delivery.dominio.entidades.*;
import es.uclm.delivery.persistencia.PedidoDAO;
import es.uclm.delivery.persistencia.ServicioEntregaDAO;

class IUPedidoTest {

    private static final Long CLIENTE_ID = 1L;
    private static final Long PEDIDO_ID = 1L;

    @Mock
    private PedidoDAO pedidoDAO;

    @Mock
    private ServicioEntregaDAO servicioEntregaDAO;

    @InjectMocks
    private IUPedido iuPedido;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testObtenerPedidosEnCurso_ClienteConPedidos() {
        List<Pedido> pedidosEnCurso = Arrays.asList(new Pedido(), new Pedido());
        when(pedidoDAO.findPedidosEnCurso(CLIENTE_ID)).thenReturn(pedidosEnCurso);

        List<Pedido> result = iuPedido.obtenerPedidosEnCurso(CLIENTE_ID);

        assertEquals(pedidosEnCurso, result);
    }

    @Test
    void testObtenerPedidosEnCurso_ClienteSinPedidos() {
        when(pedidoDAO.findPedidosEnCurso(CLIENTE_ID)).thenReturn(Collections.emptyList());

        List<Pedido> result = iuPedido.obtenerPedidosEnCurso(CLIENTE_ID);

        assertTrue(result.isEmpty());
    }

    @Test
    void testObtenerPedidosEnCurso_ClienteIdNulo() {
        assertThrows(IllegalArgumentException.class, () -> iuPedido.obtenerPedidosEnCurso(null));
    }

    @Test
    void testObtenerPedidoPorId_Existe() {
        Pedido pedido = new Pedido();
        when(pedidoDAO.buscarporid(PEDIDO_ID)).thenReturn(Optional.of(pedido));

        Optional<Pedido> result = iuPedido.obtenerPedidoPorId(PEDIDO_ID);

        assertTrue(result.isPresent());
        assertEquals(pedido, result.get());
    }

    @Test
    void testObtenerPedidoPorId_NoExiste() {
        when(pedidoDAO.buscarporid(PEDIDO_ID)).thenReturn(Optional.empty());

        Optional<Pedido> result = iuPedido.obtenerPedidoPorId(PEDIDO_ID);

        assertTrue(result.isEmpty());
    }

    @Test
    void testObtenerPedidoPorId_IdNulo() {
        assertThrows(IllegalArgumentException.class, () -> iuPedido.obtenerPedidoPorId(null));
    }

    @Test
    void testObtenerServicioEntregaPorPedido_Existe() {
        ServicioEntrega servicio = new ServicioEntrega();
        when(servicioEntregaDAO.findByPedidoId(PEDIDO_ID)).thenReturn(Optional.of(servicio));

        Optional<ServicioEntrega> result = iuPedido.obtenerServicioEntregaPorPedido(PEDIDO_ID);

        assertTrue(result.isPresent());
        assertEquals(servicio, result.get());
    }

    @Test
    void testObtenerServicioEntregaPorPedido_NoExiste() {
        when(servicioEntregaDAO.findByPedidoId(PEDIDO_ID)).thenReturn(Optional.empty());

        Optional<ServicioEntrega> result = iuPedido.obtenerServicioEntregaPorPedido(PEDIDO_ID);

        assertTrue(result.isEmpty());
    }

    @Test
    void testObtenerServicioEntregaPorPedido_IdNulo() {
        assertThrows(IllegalArgumentException.class, () -> iuPedido.obtenerServicioEntregaPorPedido(null));
    }

    @Test
    void testMarcarPedidoComoEntregado_Exito() {
        Pedido pedido = new Pedido();
        pedido.setEstado(EstadoPedido.PAGADO);
        when(pedidoDAO.findById(PEDIDO_ID)).thenReturn(Optional.of(pedido));

        iuPedido.marcarPedidoComoEntregado(PEDIDO_ID);

        assertEquals(EstadoPedido.ENTREGADO, pedido.getEstado());
        verify(pedidoDAO).update(pedido);
    }

    @Test
    void testMarcarPedidoComoEntregado_PedidoNoExiste() {
        when(pedidoDAO.findById(PEDIDO_ID)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> iuPedido.marcarPedidoComoEntregado(PEDIDO_ID));
    }

    @Test
    void testMarcarPedidoComoEntregado_IdNulo() {
        assertThrows(IllegalArgumentException.class, () -> iuPedido.marcarPedidoComoEntregado(null));
    }

    @Test
    void testObtenerPedidosEntregados_ClienteConPedidos() {
        List<Pedido> pedidosEntregados = Arrays.asList(new Pedido(), new Pedido());
        when(pedidoDAO.findPedidosEntregados(CLIENTE_ID)).thenReturn(pedidosEntregados);

        List<Pedido> result = iuPedido.obtenerPedidosEntregados(CLIENTE_ID);

        assertEquals(pedidosEntregados, result);
    }

    @Test
    void testObtenerPedidosEntregados_ClienteSinPedidos() {
        when(pedidoDAO.findPedidosEntregados(CLIENTE_ID)).thenReturn(Collections.emptyList());

        List<Pedido> result = iuPedido.obtenerPedidosEntregados(CLIENTE_ID);

        assertTrue(result.isEmpty());
    }

    @Test
    void testObtenerPedidosEntregados_ClienteIdNulo() {
        assertThrows(IllegalArgumentException.class, () -> iuPedido.obtenerPedidosEntregados(null));
    }
}