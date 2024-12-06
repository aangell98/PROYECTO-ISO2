package es.uclm.delivery.persistencia;

import es.uclm.delivery.dominio.entidades.Direccion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class EntidadDAOTest {

    private static final Long ID_VALIDO = 1L;
    private static final Long ID_INVALIDO = 999L;
    private static final String ID_VALIDO_STRING = "1";
    private static final String ID_INVALIDO_STRING = "999";

    private EntidadDAO<Direccion> entidadDAO;
    private EntityManager mockEntityManager;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        mockEntityManager = mock(EntityManager.class);
        entidadDAO = new DireccionDAO();

        Field entityManagerField = EntidadDAO.class.getDeclaredField("entityManager");
        entityManagerField.setAccessible(true);
        entityManagerField.set(entidadDAO, mockEntityManager);
    }

    private TypedQuery<Direccion> crearMockTypedQuery() {
        return Mockito.mock(TypedQuery.class);
    }

    @Test
    void testInsert_EntidadValida() {
        Direccion direccion = new Direccion();
        doNothing().when(mockEntityManager).persist(direccion);

        int result = entidadDAO.insert(direccion);

        assertEquals(1, result);
        verify(mockEntityManager).persist(direccion);
    }

    @Test
    void testInsert_EntidadNula() {
        assertThrows(IllegalArgumentException.class, () -> entidadDAO.insert(null));
    }

    @Test
    void testUpdate_EntidadValida() {
        Direccion direccion = new Direccion();
        when(mockEntityManager.merge(direccion)).thenReturn(direccion);

        int result = entidadDAO.update(direccion);

        assertEquals(1, result);
        verify(mockEntityManager).merge(direccion);
    }

    @Test
    void testUpdate_EntidadNula() {
        assertThrows(IllegalArgumentException.class, () -> entidadDAO.update(null));
    }

    @Test
    void testDelete_EntidadValida() {
        Direccion direccion = new Direccion();
        when(mockEntityManager.contains(direccion)).thenReturn(true);

        int result = entidadDAO.delete(direccion);

        assertEquals(1, result);
        verify(mockEntityManager).remove(direccion);
    }

    @Test
    void testDelete_EntidadNula() {
        assertThrows(IllegalArgumentException.class, () -> entidadDAO.delete(null));
    }

    @Test
    void testSelect_IdValido() {
        Direccion direccion = new Direccion();
        when(mockEntityManager.find(Direccion.class, ID_VALIDO_STRING)).thenReturn(direccion);

        Optional<Direccion> result = entidadDAO.select(ID_VALIDO_STRING);

        assertTrue(result.isPresent());
        assertEquals(direccion, result.get());
    }

    @Test
    void testSelect_IdInvalido() {
        when(mockEntityManager.find(Direccion.class, ID_INVALIDO_STRING)).thenReturn(null);

        Optional<Direccion> result = entidadDAO.select(ID_INVALIDO_STRING);

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindById_IdValido() {
        Direccion direccion = new Direccion();
        when(mockEntityManager.find(Direccion.class, ID_VALIDO)).thenReturn(direccion);

        Optional<Direccion> result = entidadDAO.findById(ID_VALIDO);

        assertTrue(result.isPresent());
        assertEquals(direccion, result.get());
    }

    @Test
    void testFindById_IdInvalido() {
        when(mockEntityManager.find(Direccion.class, ID_INVALIDO)).thenReturn(null);

        Optional<Direccion> result = entidadDAO.findById(ID_INVALIDO);

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindAll() {
        List<Direccion> direccionList = Arrays.asList(new Direccion(), new Direccion());

        TypedQuery<Direccion> mockTypedQuery = crearMockTypedQuery();
        when(mockTypedQuery.getResultList()).thenReturn(direccionList);

        when(mockEntityManager.createQuery(anyString(), eq(Direccion.class))).thenReturn(mockTypedQuery);

        List<Direccion> result = entidadDAO.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
    }
}