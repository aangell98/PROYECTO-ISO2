package es.uclm.delivery.persistencia;

import es.uclm.delivery.dominio.entidades.EstadoPedido;
import es.uclm.delivery.dominio.entidades.Pedido;
import org.junit.jupiter.api.*;
import org.mockito.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PedidoDAOTest {

    private PedidoDAO pedidoDAO;

    @Mock
    private EntityManager mockEntityManager;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.openMocks(this);
        pedidoDAO = new PedidoDAO();

        // Inyectar mockEntityManager usando reflexión
        java.lang.reflect.Field entityManagerField = PedidoDAO.class.getDeclaredField("entityManager");
        entityManagerField.setAccessible(true);
        entityManagerField.set(pedidoDAO, mockEntityManager);
    }

    @Test
    void testSave_PedidoValido() {
        Pedido pedido = new Pedido();
        doNothing().when(mockEntityManager).persist(pedido);

        pedidoDAO.save(pedido);

        verify(mockEntityManager).persist(pedido);
    }

    @Test
    void testSave_PedidoNulo() {
        assertThrows(IllegalArgumentException.class, () -> pedidoDAO.save(null));
    }

    @Test
    void testFindPedidosEnCurso_ClienteConPedidos() {
        List<Pedido> pedidos = Arrays.asList(new Pedido(), new Pedido());
        TypedQuery<Pedido> mockQuery = mock(TypedQuery.class);
        when(mockEntityManager.createQuery(any(String.class), eq(Pedido.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("clienteId"), any(Long.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("estadoEntregado"), eq(EstadoPedido.ENTREGADO))).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(pedidos);

        List<Pedido> result = pedidoDAO.findPedidosEnCurso(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testFindPedidosEnCurso_ClienteSinPedidos() {
        TypedQuery<Pedido> mockQuery = mock(TypedQuery.class);
        when(mockEntityManager.createQuery(any(String.class), eq(Pedido.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("clienteId"), any(Long.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("estadoEntregado"), eq(EstadoPedido.ENTREGADO))).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(Collections.emptyList());

        List<Pedido> result = pedidoDAO.findPedidosEnCurso(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testBuscarPorId_IdValido() {
        Pedido pedido = new Pedido();
        when(mockEntityManager.find(Pedido.class, 1L)).thenReturn(pedido);

        Optional<Pedido> result = pedidoDAO.buscarporid(1L);

        assertTrue(result.isPresent());
        assertEquals(pedido, result.get());
    }

    @Test
    void testBuscarPorId_IdInvalido() {
        when(mockEntityManager.find(Pedido.class, 999L)).thenReturn(null);

        Optional<Pedido> result = pedidoDAO.buscarporid(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void testFindPedidosEntregados_ClienteConPedidos() {
        List<Pedido> pedidos = Arrays.asList(new Pedido(), new Pedido());
        TypedQuery<Pedido> mockQuery = mock(TypedQuery.class);
        when(mockEntityManager.createQuery(any(String.class), eq(Pedido.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("clienteId"), any(Long.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("estadoEntregado"), eq(EstadoPedido.ENTREGADO))).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(pedidos);

        List<Pedido> result = pedidoDAO.findPedidosEntregados(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testFindPedidosEntregados_ClienteSinPedidos() {
        TypedQuery<Pedido> mockQuery = mock(TypedQuery.class);
        when(mockEntityManager.createQuery(any(String.class), eq(Pedido.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("clienteId"), any(Long.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("estadoEntregado"), eq(EstadoPedido.ENTREGADO))).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(Collections.emptyList());

        List<Pedido> result = pedidoDAO.findPedidosEntregados(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindPedidosPagados_ExistenPedidos() {
        List<Pedido> pedidos = Arrays.asList(new Pedido(), new Pedido());
        TypedQuery<Pedido> mockQuery = mock(TypedQuery.class);
        when(mockEntityManager.createQuery(any(String.class), eq(Pedido.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("estadoPagado"), eq(EstadoPedido.PAGADO))).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(pedidos);

        List<Pedido> result = pedidoDAO.findPedidosPagados();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testFindPedidosPagados_NoExistenPedidos() {
        TypedQuery<Pedido> mockQuery = mock(TypedQuery.class);
        when(mockEntityManager.createQuery(any(String.class), eq(Pedido.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("estadoPagado"), eq(EstadoPedido.PAGADO))).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(Collections.emptyList());

        List<Pedido> result = pedidoDAO.findPedidosPagados();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
