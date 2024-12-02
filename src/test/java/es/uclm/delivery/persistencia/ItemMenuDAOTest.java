package es.uclm.delivery.persistencia;

import es.uclm.delivery.dominio.entidades.ItemMenu;
import es.uclm.delivery.dominio.entidades.Restaurante;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ItemMenuDAOTest {

    private ItemMenuDAO itemMenuDAO;
    private EntityManager mockEntityManager;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        // Crear el mock de EntityManager
        mockEntityManager = mock(EntityManager.class);

        // Instanciar ItemMenuDAO directamente
        itemMenuDAO = new ItemMenuDAO();

        // Usar reflexión para inyectar el mock de EntityManager en la clase DAO
        java.lang.reflect.Field entityManagerField = EntidadDAO.class.getDeclaredField("entityManager");
        entityManagerField.setAccessible(true);
        entityManagerField.set(itemMenuDAO, mockEntityManager);
    }

    @Test
    void testInsert_EntidadValida() {
        ItemMenu item = new ItemMenu();
        doNothing().when(mockEntityManager).persist(item);

        int result = itemMenuDAO.insert(item);

        assertEquals(1, result);
        verify(mockEntityManager).persist(item);
    }

    @Test
    void testInsert_EntidadNula() {
        assertThrows(IllegalArgumentException.class, () -> itemMenuDAO.insert(null));
    }

    @Test
    void testUpdate_EntidadValida() {
        ItemMenu item = new ItemMenu();
        when(mockEntityManager.merge(item)).thenReturn(item);

        int result = itemMenuDAO.update(item);

        assertEquals(1, result);
        verify(mockEntityManager).merge(item);
    }

    @Test
    void testUpdate_EntidadNula() {
        assertThrows(IllegalArgumentException.class, () -> itemMenuDAO.update(null));
    }

    @Test
    void testDelete_EntidadValida() {
        ItemMenu item = new ItemMenu();
        when(mockEntityManager.contains(item)).thenReturn(true);

        int result = itemMenuDAO.delete(item);

        assertEquals(1, result);
        verify(mockEntityManager).remove(item);
    }

    @Test
    void testDelete_EntidadNula() {
        assertThrows(IllegalArgumentException.class, () -> itemMenuDAO.delete(null));
    }

    @Test
    void testFindById_IdValido() {
        ItemMenu item = new ItemMenu();
        when(mockEntityManager.find(ItemMenu.class, 1L)).thenReturn(item);

        Optional<ItemMenu> result = itemMenuDAO.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(item, result.get());
    }

    @Test
    void testFindById_IdInvalido() {
        when(mockEntityManager.find(ItemMenu.class, 999L)).thenReturn(null);

        Optional<ItemMenu> result = itemMenuDAO.findById(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void testEliminarItemMenuPorId_EntidadExistente() {
        ItemMenu item = new ItemMenu();
        when(mockEntityManager.find(ItemMenu.class, 1L)).thenReturn(item);
        when(mockEntityManager.contains(item)).thenReturn(true);
        doNothing().when(mockEntityManager).remove(item);

        int result = itemMenuDAO.eliminarItemMenuPorId(1L);

        assertEquals(1, result);
        verify(mockEntityManager).remove(item);
    }

    @Test
    void testEliminarItemMenuPorId_EntidadNoExistente() {
        when(mockEntityManager.find(ItemMenu.class, 999L)).thenReturn(null);

        int result = itemMenuDAO.eliminarItemMenuPorId(999L);

        assertEquals(0, result);
    }

    @Test
    void testObtenerItemsPorRestaurante_ConItems() {
        Restaurante restaurante = new Restaurante();
        restaurante.setId(1L);

        ItemMenu item1 = new ItemMenu();
        item1.setRestaurante(restaurante);
        ItemMenu item2 = new ItemMenu();
        item2.setRestaurante(restaurante);

        List<ItemMenu> itemList = Arrays.asList(item1, item2);

        TypedQuery<ItemMenu> mockQuery = mock(TypedQuery.class);
        when(mockQuery.getResultList()).thenReturn(itemList);
        when(mockEntityManager.createQuery(anyString(), eq(ItemMenu.class))).thenReturn(mockQuery);

        List<ItemMenu> result = itemMenuDAO.obtenerItemsPorRestaurante(1L);

        assertEquals(2, result.size());
        assertEquals(itemList, result);
    }

    @Test
    void testObtenerItemsPorRestaurante_SinItems() {
        TypedQuery<ItemMenu> mockQuery = mock(TypedQuery.class);
        when(mockQuery.getResultList()).thenReturn(Collections.emptyList());
        when(mockEntityManager.createQuery(anyString(), eq(ItemMenu.class))).thenReturn(mockQuery);

        List<ItemMenu> result = itemMenuDAO.obtenerItemsPorRestaurante(1L);

        assertTrue(result.isEmpty());
    }
}
