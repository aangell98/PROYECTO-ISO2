package es.uclm.delivery.persistencia;

import es.uclm.delivery.dominio.entidades.EstadoPedido;
import es.uclm.delivery.dominio.entidades.Pedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PedidoDAOTest {

    private static final Long VALID_ID = 1L;
    private static final Long INVALID_ID = 999L;
    private static final Long CLIENTE_ID = 1L;
    private static final EstadoPedido ESTADO_ENTREGADO = EstadoPedido.ENTREGADO;
    private static final EstadoPedido ESTADO_PAGADO = EstadoPedido.PAGADO;

    @InjectMocks
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

    private Pedido crearPedido() {
        return new Pedido();
    }

    private TypedQuery<Pedido> crearMockQuery() {
        return mock(TypedQuery.class);
    }

    @Test
    void testSave_PedidoValido() {
        Pedido pedido = crearPedido();
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
        List<Pedido> pedidos = Arrays.asList(crearPedido(), crearPedido());
        TypedQuery<Pedido> mockQuery = crearMockQuery();
        when(mockEntityManager.createQuery(any(String.class), eq(Pedido.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("clienteId"), any(Long.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("estadoEntregado"), eq(ESTADO_ENTREGADO))).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(pedidos);

        List<Pedido> result = pedidoDAO.findPedidosEnCurso(CLIENTE_ID);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testFindPedidosEnCurso_ClienteSinPedidos() {
        TypedQuery<Pedido> mockQuery = crearMockQuery();
        when(mockEntityManager.createQuery(any(String.class), eq(Pedido.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("clienteId"), any(Long.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("estadoEntregado"), eq(ESTADO_ENTREGADO))).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(Collections.emptyList());

        List<Pedido> result = pedidoDAO.findPedidosEnCurso(CLIENTE_ID);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testBuscarPorId_IdValido() {
        Pedido pedido = crearPedido();
        when(mockEntityManager.find(Pedido.class, VALID_ID)).thenReturn(pedido);

        Optional<Pedido> result = pedidoDAO.buscarporid(VALID_ID);

        assertTrue(result.isPresent());
        assertEquals(pedido, result.get());
    }

    @Test
    void testBuscarPorId_IdInvalido() {
        when(mockEntityManager.find(Pedido.class, INVALID_ID)).thenReturn(null);

        Optional<Pedido> result = pedidoDAO.buscarporid(INVALID_ID);

        assertFalse(result.isPresent());
    }

    @Test
    void testFindPedidosEntregados_ClienteConPedidos() {
        List<Pedido> pedidos = Arrays.asList(crearPedido(), crearPedido());
        TypedQuery<Pedido> mockQuery = crearMockQuery();
        when(mockEntityManager.createQuery(any(String.class), eq(Pedido.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("clienteId"), any(Long.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("estadoEntregado"), eq(ESTADO_ENTREGADO))).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(pedidos);

        List<Pedido> result = pedidoDAO.findPedidosEntregados(CLIENTE_ID);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testFindPedidosEntregados_ClienteSinPedidos() {
        TypedQuery<Pedido> mockQuery = crearMockQuery();
        when(mockEntityManager.createQuery(any(String.class), eq(Pedido.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("clienteId"), any(Long.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("estadoEntregado"), eq(ESTADO_ENTREGADO))).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(Collections.emptyList());

        List<Pedido> result = pedidoDAO.findPedidosEntregados(CLIENTE_ID);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindPedidosPagados_ExistenPedidos() {
        List<Pedido> pedidos = Arrays.asList(crearPedido(), crearPedido());
        TypedQuery<Pedido> mockQuery = crearMockQuery();
        when(mockEntityManager.createQuery(any(String.class), eq(Pedido.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("estadoPagado"), eq(ESTADO_PAGADO))).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(pedidos);

        List<Pedido> result = pedidoDAO.findPedidosPagados();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testFindPedidosPagados_NoExistenPedidos() {
        TypedQuery<Pedido> mockQuery = crearMockQuery();
        when(mockEntityManager.createQuery(any(String.class), eq(Pedido.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("estadoPagado"), eq(ESTADO_PAGADO))).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(Collections.emptyList());

        List<Pedido> result = pedidoDAO.findPedidosPagados();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}