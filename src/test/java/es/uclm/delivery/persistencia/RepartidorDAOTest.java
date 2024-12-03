package es.uclm.delivery.persistencia;

import es.uclm.delivery.dominio.entidades.Repartidor;
import org.junit.jupiter.api.*;
import org.mockito.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class RepartidorDAOTest {

    private static final Long REPARTIDOR_ID = 1L;
    private static final String USERNAME_EXISTENTE = "repartidor1";
    private static final String USERNAME_INEXISTENTE = "usuarioInexistente";

    private RepartidorDAO repartidorDAO;

    @Mock
    private EntityManager mockEntityManager;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.openMocks(this);
        repartidorDAO = new RepartidorDAO();

        // Inyectar mockEntityManager usando reflexión
        java.lang.reflect.Field entityManagerField = RepartidorDAO.class.getDeclaredField("entityManager");
        entityManagerField.setAccessible(true);
        entityManagerField.set(repartidorDAO, mockEntityManager);
    }

    private Repartidor crearRepartidor() {
        Repartidor repartidor = new Repartidor();
        repartidor.setId(REPARTIDOR_ID);
        return repartidor;
    }

    private TypedQuery<Repartidor> crearMockTypedQuery() {
        return mock(TypedQuery.class);
    }

    private Query crearMockQuery() {
        return mock(Query.class);
    }

    @Test
    void testFindAllIds_ExistenRepartidores() {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        Query mockQuery = crearMockQuery();
        when(mockEntityManager.createNativeQuery(anyString())).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(ids);

        List<Long> result = repartidorDAO.findAllIds();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains(1L));
    }

    @Test
    void testFindAllIds_NoExistenRepartidores() {
        Query mockQuery = crearMockQuery();
        when(mockEntityManager.createNativeQuery(anyString())).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(Collections.emptyList());

        List<Long> result = repartidorDAO.findAllIds();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByUsername_UsuarioExistente() {
        Repartidor repartidor = crearRepartidor();
        TypedQuery<Repartidor> mockQuery = crearMockTypedQuery();
        when(mockEntityManager.createQuery(anyString(), eq(Repartidor.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("username"), anyString())).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(Collections.singletonList(repartidor));

        Optional<Repartidor> result = repartidorDAO.findByUsername(USERNAME_EXISTENTE);

        assertTrue(result.isPresent());
        assertEquals(REPARTIDOR_ID, result.get().getId());
    }

    @Test
    void testFindByUsername_UsuarioNoExistente() {
        TypedQuery<Repartidor> mockQuery = crearMockTypedQuery();
        when(mockEntityManager.createQuery(anyString(), eq(Repartidor.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("username"), anyString())).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(Collections.emptyList());

        Optional<Repartidor> result = repartidorDAO.findByUsername(USERNAME_INEXISTENTE);

        assertFalse(result.isPresent());
    }

    @Test
    void testFindByUsername_ParametroNulo() {
        assertThrows(IllegalArgumentException.class, () -> repartidorDAO.findByUsername(null));
    }
}