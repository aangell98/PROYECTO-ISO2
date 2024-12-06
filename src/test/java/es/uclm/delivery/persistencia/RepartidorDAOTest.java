package es.uclm.delivery.persistencia;

import es.uclm.delivery.dominio.entidades.Repartidor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.Query;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class RepartidorDAOTest {

    @InjectMocks
    private RepartidorDAO repartidorDAO;

    @Mock
    private EntityManager mockEntityManager;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.openMocks(this);
        repartidorDAO = new RepartidorDAO();

        // Inyectar mockEntityManager usando reflexión
        java.lang.reflect.Field entityManagerField = RepartidorDAO.class.getDeclaredField("repartidorEntityManager");
        entityManagerField.setAccessible(true);
        entityManagerField.set(repartidorDAO, mockEntityManager);
    }

    private Repartidor crearRepartidor() {
        return new Repartidor();
    }

    private TypedQuery<Repartidor> crearMockQuery() {
        return mock(TypedQuery.class);
    }

    @Test
    void testFindAllIds() {
        List<Long> ids = List.of(1L, 2L, 3L);
        Query mockQuery = mock(Query.class);
        when(mockEntityManager.createNativeQuery(anyString())).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(ids);

        List<Long> result = repartidorDAO.findAllIds();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(ids, result);
    }

    @Test
    void testFindByUsername_UsernameValido() {
        Repartidor repartidor = crearRepartidor();
        TypedQuery<Repartidor> mockQuery = crearMockQuery();
        when(mockEntityManager.createQuery(anyString(), eq(Repartidor.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(anyString(), any())).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(List.of(repartidor));

        Optional<Repartidor> result = repartidorDAO.findByUsername("username");

        assertTrue(result.isPresent());
        assertEquals(repartidor, result.get());
    }

    @Test
    void testFindByUsername_UsernameInvalido() {
        TypedQuery<Repartidor> mockQuery = crearMockQuery();
        when(mockEntityManager.createQuery(anyString(), eq(Repartidor.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(anyString(), any())).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(Collections.emptyList());

        Optional<Repartidor> result = repartidorDAO.findByUsername("username");

        assertFalse(result.isPresent());
    }

    @Test
    void testFindByUsername_UsernameEsNulo() {
        assertThrows(IllegalArgumentException.class, () -> repartidorDAO.findByUsername(null));
    }
}