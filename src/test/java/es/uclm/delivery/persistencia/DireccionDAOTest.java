package es.uclm.delivery.persistencia;

import es.uclm.delivery.dominio.entidades.Direccion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class DireccionDAOTest {

    private static final Long PEDIDO_ID_EXISTENTE = 1L;
    private static final Long PEDIDO_ID_INEXISTENTE = 99L;
    private static final Long PEDIDO_ID_NEGATIVO = -1L;
    private static final Long PEDIDO_ID_MAXIMO = Long.MAX_VALUE;

    private DireccionDAO direccionDAO;
    private EntityManager mockEntityManager;

    @BeforeEach
    void setUp() throws Exception {
        mockEntityManager = Mockito.mock(EntityManager.class);
        direccionDAO = new DireccionDAO();

        // Configuración del EntityManager mediante reflexión
        Field entityManagerField = DireccionDAO.class.getDeclaredField("direccionEntityManager");
        entityManagerField.setAccessible(true);
        entityManagerField.set(direccionDAO, mockEntityManager);
    }

    private TypedQuery<Direccion> crearMockTypedQuery() {
        return Mockito.mock(TypedQuery.class);
    }

    @Test
    void testSave_DireccionValida() {
        Direccion direccion = new Direccion();
        direccion.setId(1L);

        doNothing().when(mockEntityManager).persist(direccion);

        assertDoesNotThrow(() -> direccionDAO.save(direccion));
        verify(mockEntityManager, times(1)).persist(direccion);
    }

    @Test
    void testSave_DireccionEsNula() {
        assertThrows(IllegalArgumentException.class, () -> direccionDAO.save(null));
        verify(mockEntityManager, never()).persist(any(Direccion.class));
    }

    @Test
    void testFindByPedidoId_PedidoIdValidoExistente() {
        Direccion mockDireccion = new Direccion();
        mockDireccion.setId(1L);

        TypedQuery<Direccion> mockQuery = crearMockTypedQuery();
        when(mockEntityManager.createQuery(anyString(), eq(Direccion.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter("pedidoId", PEDIDO_ID_EXISTENTE)).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenReturn(mockDireccion);

        Direccion result = direccionDAO.findByPedidoId(PEDIDO_ID_EXISTENTE);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testFindByPedidoId_PedidoIdValidoNoExistente() {
        TypedQuery<Direccion> mockQuery = crearMockTypedQuery();
        when(mockEntityManager.createQuery(anyString(), eq(Direccion.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter("pedidoId", PEDIDO_ID_INEXISTENTE)).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenThrow(new NoResultException());

        assertThrows(NoResultException.class, () -> direccionDAO.findByPedidoId(PEDIDO_ID_INEXISTENTE));
    }

    @Test
    void testFindByPedidoId_PedidoIdEsNulo() {
        assertThrows(NullPointerException.class, () -> direccionDAO.findByPedidoId(null));
    }

    @Test
    void testFindByPedidoId_PedidoIdEsNegativo() {
        TypedQuery<Direccion> mockQuery = crearMockTypedQuery();
        when(mockEntityManager.createQuery(anyString(), eq(Direccion.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter("pedidoId", PEDIDO_ID_NEGATIVO)).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenThrow(new NoResultException());

        assertThrows(NoResultException.class, () -> direccionDAO.findByPedidoId(PEDIDO_ID_NEGATIVO));
    }

    @Test
    void testFindByPedidoId_PedidoIdMaximo() {
        Direccion mockDireccion = new Direccion();
        mockDireccion.setId(1L);

        TypedQuery<Direccion> mockQuery = crearMockTypedQuery();
        when(mockEntityManager.createQuery(anyString(), eq(Direccion.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter("pedidoId", PEDIDO_ID_MAXIMO)).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenReturn(mockDireccion);

        Direccion result = direccionDAO.findByPedidoId(PEDIDO_ID_MAXIMO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }
}