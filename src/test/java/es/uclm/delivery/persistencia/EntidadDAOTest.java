package es.uclm.delivery.persistencia;

import es.uclm.delivery.dominio.entidades.Direccion;
import org.junit.jupiter.api.*;
import org.mockito.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class EntidadDAOTest {

    private EntidadDAO<Direccion> entidadDAO;
    private EntityManager mockEntityManager;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        // Crear el mock de EntityManager
        mockEntityManager = mock(EntityManager.class);

        // Instanciamos DireccionDAO directamente
        entidadDAO = new DireccionDAO();

        // Usamos reflexión para acceder al campo "entityManager" en EntidadDAO
        java.lang.reflect.Field entityManagerField = EntidadDAO.class.getDeclaredField("entityManager");
        entityManagerField.setAccessible(true);  // Hacemos el campo accesible
        entityManagerField.set(entidadDAO, mockEntityManager);  // Inyectamos el mock en el campo
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
        when(mockEntityManager.find(Direccion.class, "1")).thenReturn(direccion);

        Optional<Direccion> result = entidadDAO.select("1");

        assertTrue(result.isPresent());
        assertEquals(direccion, result.get());
    }

    @Test
    void testSelect_IdInvalido() {
        when(mockEntityManager.find(Direccion.class, "999")).thenReturn(null);

        Optional<Direccion> result = entidadDAO.select("999");

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindById_IdValido() {
        Direccion direccion = new Direccion();
        when(mockEntityManager.find(Direccion.class, 1L)).thenReturn(direccion);

        Optional<Direccion> result = entidadDAO.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(direccion, result.get());
    }

    @Test
    void testFindById_IdInvalido() {
        when(mockEntityManager.find(Direccion.class, 999L)).thenReturn(null);

        Optional<Direccion> result = entidadDAO.findById(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindAll() {
        // Crear una lista de entidades de ejemplo que esperamos que devuelva el método findAll
        List<Direccion> direccionList = Arrays.asList(new Direccion(), new Direccion());

        // Crear un mock de TypedQuery que devuelve la lista de Direccion
        TypedQuery<Direccion> mockTypedQuery = mock(TypedQuery.class);
        when(mockTypedQuery.getResultList()).thenReturn(direccionList);

        // Crear un mock del EntityManager que devuelve el mockTypedQuery
        when(mockEntityManager.createQuery(anyString(), eq(Direccion.class))).thenReturn(mockTypedQuery);

        // Llamar al método findAll que debería usar el mockEntityManager
        List<Direccion> result = entidadDAO.findAll();

        // Verificar que el resultado no es null y que contiene las dos direcciones esperadas
        assertNotNull(result);
        assertEquals(2, result.size()); // Debería devolver dos elementos en la lista
    }
}
