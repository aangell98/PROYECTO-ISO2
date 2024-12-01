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

    private DireccionDAO direccionDAO;
    private EntityManager mockEntityManager;

    @BeforeEach
    void setUp() throws Exception {
        mockEntityManager = Mockito.mock(EntityManager.class);
        direccionDAO = new DireccionDAO();

        // Configuración del EntityManager mediante reflexión
        Field entityManagerField = DireccionDAO.class.getDeclaredField("entityManager");
        entityManagerField.setAccessible(true);
        entityManagerField.set(direccionDAO, mockEntityManager);
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
        Long pedidoId = 1L;
        Direccion mockDireccion = new Direccion();
        mockDireccion.setId(1L);

        TypedQuery<Direccion> mockQuery = Mockito.mock(TypedQuery.class);
        when(mockEntityManager.createQuery(anyString(), eq(Direccion.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter("pedidoId", pedidoId)).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenReturn(mockDireccion);

        Direccion result = direccionDAO.findByPedidoId(pedidoId);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testFindByPedidoId_PedidoIdValidoNoExistente() {
        Long pedidoId = 99L;

        TypedQuery<Direccion> mockQuery = Mockito.mock(TypedQuery.class);
        when(mockEntityManager.createQuery(anyString(), eq(Direccion.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter("pedidoId", pedidoId)).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenThrow(new NoResultException());

        assertThrows(NoResultException.class, () -> direccionDAO.findByPedidoId(pedidoId));
    }

    @Test
    void testFindByPedidoId_PedidoIdEsNulo() {
        assertThrows(java.lang.NullPointerException.class, () -> direccionDAO.findByPedidoId(null));
    }

    @Test
    void testFindByPedidoId_PedidoIdEsNegativo() {
        Long pedidoId = -1L;

        TypedQuery<Direccion> mockQuery = Mockito.mock(TypedQuery.class);
        when(mockEntityManager.createQuery(anyString(), eq(Direccion.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter("pedidoId", pedidoId)).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenThrow(new NoResultException());

        assertThrows(NoResultException.class, () -> direccionDAO.findByPedidoId(pedidoId));
    }

    @Test
    void testFindByPedidoId_PedidoIdMaximo() {
        Long pedidoId = Long.MAX_VALUE;
        Direccion mockDireccion = new Direccion();
        mockDireccion.setId(1L);

        TypedQuery<Direccion> mockQuery = Mockito.mock(TypedQuery.class);
        when(mockEntityManager.createQuery(anyString(), eq(Direccion.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter("pedidoId", pedidoId)).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenReturn(mockDireccion);

        Direccion result = direccionDAO.findByPedidoId(pedidoId);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }
}
