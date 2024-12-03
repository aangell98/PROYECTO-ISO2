package es.uclm.delivery.persistencia;

import es.uclm.delivery.dominio.entidades.EstadoPedido;
import es.uclm.delivery.dominio.entidades.ServicioEntrega;
import org.junit.jupiter.api.*;
import org.mockito.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ServicioEntregaDAOTest {

    private static final Long SERVICIO_ENTREGA_ID = 1L;
    private static final Long PEDIDO_ID_EXISTENTE = 1L;
    private static final Long PEDIDO_ID_INEXISTENTE = 999L;
    private static final Long REPARTIDOR_ID = 1L;
    private static final EstadoPedido ESTADO_PAGADO = EstadoPedido.PAGADO;

    private ServicioEntregaDAO servicioEntregaDAO;

    @Mock
    private EntityManager mockEntityManager;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.openMocks(this);
        servicioEntregaDAO = new ServicioEntregaDAO();

        // Inyectar mockEntityManager usando reflexión
        java.lang.reflect.Field entityManagerField = ServicioEntregaDAO.class.getSuperclass().getDeclaredField("entityManager");
        entityManagerField.setAccessible(true);
        entityManagerField.set(servicioEntregaDAO, mockEntityManager);
    }

    private ServicioEntrega crearServicioEntrega() {
        ServicioEntrega servicio = new ServicioEntrega();
        servicio.setId(SERVICIO_ENTREGA_ID);
        return servicio;
    }

    private TypedQuery<ServicioEntrega> crearMockTypedQuery() {
        return mock(TypedQuery.class);
    }

    @Test
    void testFindByPedidoId_PedidoExistente() {
        ServicioEntrega servicio = crearServicioEntrega();
        TypedQuery<ServicioEntrega> mockQuery = crearMockTypedQuery();
        when(mockEntityManager.createQuery(anyString(), eq(ServicioEntrega.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter("pedidoID", PEDIDO_ID_EXISTENTE)).thenReturn(mockQuery);
        when(mockQuery.getResultStream()).thenReturn(Stream.of(servicio));

        Optional<ServicioEntrega> result = servicioEntregaDAO.findByPedidoId(PEDIDO_ID_EXISTENTE);

        assertTrue(result.isPresent());
        assertEquals(servicio, result.get());
    }

    @Test
    void testFindByPedidoId_PedidoNoExistente() {
        TypedQuery<ServicioEntrega> mockQuery = crearMockTypedQuery();
        when(mockEntityManager.createQuery(anyString(), eq(ServicioEntrega.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter("pedidoID", PEDIDO_ID_EXISTENTE)).thenReturn(mockQuery);
        when(mockQuery.getResultStream()).thenReturn(Stream.empty());

        Optional<ServicioEntrega> result = servicioEntregaDAO.findByPedidoId(PEDIDO_ID_INEXISTENTE);

        assertFalse(result.isPresent());
    }

    @Test
    void testFindByEstado_ExistenServicios() {
        List<ServicioEntrega> servicios = Arrays.asList(crearServicioEntrega(), crearServicioEntrega());
        TypedQuery<ServicioEntrega> mockQuery = crearMockTypedQuery();
        when(mockEntityManager.createQuery(anyString(), eq(ServicioEntrega.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter("estado", ESTADO_PAGADO)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(servicios);

        List<ServicioEntrega> result = servicioEntregaDAO.findByEstado(ESTADO_PAGADO);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testFindByEstado_NoExistenServicios() {
        TypedQuery<ServicioEntrega> mockQuery = crearMockTypedQuery();
        when(mockEntityManager.createQuery(anyString(), eq(ServicioEntrega.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter("estado", ESTADO_PAGADO)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(Collections.emptyList());

        List<ServicioEntrega> result = servicioEntregaDAO.findByEstado(ESTADO_PAGADO);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByRepartidorId_ExistenServicios() {
        List<ServicioEntrega> servicios = Arrays.asList(crearServicioEntrega(), crearServicioEntrega());
        TypedQuery<ServicioEntrega> mockQuery = crearMockTypedQuery();
        when(mockEntityManager.createQuery(anyString(), eq(ServicioEntrega.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter("repartidorID", REPARTIDOR_ID)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(servicios);

        List<ServicioEntrega> result = servicioEntregaDAO.findByRepartidorId(REPARTIDOR_ID);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testFindByRepartidorId_NoExistenServicios() {
        TypedQuery<ServicioEntrega> mockQuery = crearMockTypedQuery();
        when(mockEntityManager.createQuery(anyString(), eq(ServicioEntrega.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter("repartidorID", REPARTIDOR_ID)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(Collections.emptyList());

        List<ServicioEntrega> result = servicioEntregaDAO.findByRepartidorId(REPARTIDOR_ID);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}