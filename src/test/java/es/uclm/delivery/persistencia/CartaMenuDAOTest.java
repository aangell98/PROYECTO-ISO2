package es.uclm.delivery.persistencia;

import es.uclm.delivery.dominio.entidades.CartaMenu;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;



import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CartaMenuDAOTest {

    private CartaMenuDAO cartaMenuDAO;
    private EntityManager mockEntityManager;

    @BeforeEach
    void setUp() {
        mockEntityManager = Mockito.mock(EntityManager.class);
        cartaMenuDAO = new CartaMenuDAO();
        cartaMenuDAO.entityManager = mockEntityManager;
    }

    @Test
    void testFindAllByRestaurante_ConResultados() {
        TypedQuery<CartaMenu> mockQuery = Mockito.mock(TypedQuery.class);
        when(mockEntityManager.createQuery(anyString(), eq(CartaMenu.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("restauranteId"), any())).thenReturn(mockQuery);

        CartaMenu menu1 = new CartaMenu();
        CartaMenu menu2 = new CartaMenu();
        List<CartaMenu> menus = Arrays.asList(menu1, menu2);
        when(mockQuery.getResultList()).thenReturn(menus);

        List<CartaMenu> result = cartaMenuDAO.findAllByRestaurante(1L);

        assertEquals(2, result.size());
        assertSame(menu1, result.get(0));
        assertSame(menu2, result.get(1));
        verify(mockQuery).setParameter("restauranteId", 1L);
    }

    @Test
    void testFindAllByRestaurante_SinResultados() {
        TypedQuery<CartaMenu> mockQuery = Mockito.mock(TypedQuery.class);
        when(mockEntityManager.createQuery(anyString(), eq(CartaMenu.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("restauranteId"), any())).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(Collections.emptyList());

        List<CartaMenu> result = cartaMenuDAO.findAllByRestaurante(999L);

        assertTrue(result.isEmpty());
        verify(mockQuery).setParameter("restauranteId", 999L);
    }

    @Test
    void testFindAllByRestaurante_NullRestauranteId() {
        Exception exception = assertThrows(NullPointerException.class, () -> {
            cartaMenuDAO.findAllByRestaurante(null);
        });

        assertNotNull(exception);
    }

    @Test
    void testEliminarCartaMenuPorId_Exito() {
        CartaMenu mockMenu = Mockito.mock(CartaMenu.class);
        CartaMenuDAO spyDAO = Mockito.spy(cartaMenuDAO);

        doReturn(Optional.of(mockMenu)).when(spyDAO).findById(1L);
        doReturn(1).when(spyDAO).delete(mockMenu);

        int result = spyDAO.eliminarCartaMenuPorId(1L);

        assertEquals(1, result);
        verify(spyDAO).findById(1L);
        verify(spyDAO).delete(mockMenu);
    }

    @Test
    void testEliminarCartaMenuPorId_FalloPorIdInexistente() {
        CartaMenuDAO spyDAO = Mockito.spy(cartaMenuDAO);

        doReturn(Optional.empty()).when(spyDAO).findById(999L);

        int result = spyDAO.eliminarCartaMenuPorId(999L);

        assertEquals(0, result);
        verify(spyDAO).findById(999L);
        verify(spyDAO, never()).delete(any());
    }

    @Test
    void testEliminarCartaMenuPorId_NullMenuId() {
        CartaMenuDAO spyDAO = Mockito.spy(cartaMenuDAO);
        Mockito.doReturn(Optional.empty()).when(spyDAO).findById(null);
        int result = spyDAO.eliminarCartaMenuPorId(null);
        assertEquals(0, result);
        verify(spyDAO, never()).delete(any());
    }


    @Test
    void testEliminarCartaMenuPorId_ErrorEnDelete() {
        CartaMenu mockMenu = Mockito.mock(CartaMenu.class);
        CartaMenuDAO spyDAO = Mockito.spy(cartaMenuDAO);

        doReturn(Optional.of(mockMenu)).when(spyDAO).findById(1L);
        doThrow(new RuntimeException("Error al eliminar")).when(spyDAO).delete(mockMenu);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            spyDAO.eliminarCartaMenuPorId(1L);
        });

        assertEquals("Error al eliminar", exception.getMessage());
    }
}
