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

    private static final Long RESTAURANTE_ID_EXISTENTE = 1L;
    private static final Long RESTAURANTE_ID_INEXISTENTE = 999L;
    private static final Long CARTA_MENU_ID_EXISTENTE = 1L;
    private static final Long CARTA_MENU_ID_INEXISTENTE = 999L;

    private CartaMenuDAO cartaMenuDAO;
    private EntityManager mockEntityManager;

    @BeforeEach
    void setUp() {
        mockEntityManager = Mockito.mock(EntityManager.class);
        cartaMenuDAO = new CartaMenuDAO();
        cartaMenuDAO.entityManager = mockEntityManager;
    }

    private TypedQuery<CartaMenu> crearMockTypedQuery() {
        return Mockito.mock(TypedQuery.class);
    }

    @Test
    void testFindAllByRestaurante_ConResultados() {
        TypedQuery<CartaMenu> mockQuery = crearMockTypedQuery();
        when(mockEntityManager.createQuery(anyString(), eq(CartaMenu.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("restauranteId"), any())).thenReturn(mockQuery);

        CartaMenu menu1 = new CartaMenu();
        CartaMenu menu2 = new CartaMenu();
        List<CartaMenu> menus = Arrays.asList(menu1, menu2);
        when(mockQuery.getResultList()).thenReturn(menus);

        List<CartaMenu> result = cartaMenuDAO.findAllByRestaurante(RESTAURANTE_ID_EXISTENTE);

        assertEquals(2, result.size());
        assertSame(menu1, result.get(0));
        assertSame(menu2, result.get(1));
        verify(mockQuery).setParameter("restauranteId", RESTAURANTE_ID_EXISTENTE);
    }

    @Test
    void testFindAllByRestaurante_SinResultados() {
        TypedQuery<CartaMenu> mockQuery = crearMockTypedQuery();
        when(mockEntityManager.createQuery(anyString(), eq(CartaMenu.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("restauranteId"), any())).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(Collections.emptyList());

        List<CartaMenu> result = cartaMenuDAO.findAllByRestaurante(RESTAURANTE_ID_INEXISTENTE);

        assertTrue(result.isEmpty());
        verify(mockQuery).setParameter("restauranteId", RESTAURANTE_ID_INEXISTENTE);
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

        doReturn(Optional.of(mockMenu)).when(spyDAO).findById(CARTA_MENU_ID_EXISTENTE);
        doReturn(1).when(spyDAO).delete(mockMenu);

        int result = spyDAO.eliminarCartaMenuPorId(CARTA_MENU_ID_EXISTENTE);

        assertEquals(1, result);
        verify(spyDAO).findById(CARTA_MENU_ID_EXISTENTE);
        verify(spyDAO).delete(mockMenu);
    }

    @Test
    void testEliminarCartaMenuPorId_FalloPorIdInexistente() {
        CartaMenuDAO spyDAO = Mockito.spy(cartaMenuDAO);

        doReturn(Optional.empty()).when(spyDAO).findById(CARTA_MENU_ID_INEXISTENTE);

        int result = spyDAO.eliminarCartaMenuPorId(CARTA_MENU_ID_INEXISTENTE);

        assertEquals(0, result);
        verify(spyDAO).findById(CARTA_MENU_ID_INEXISTENTE);
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

        doReturn(Optional.of(mockMenu)).when(spyDAO).findById(CARTA_MENU_ID_EXISTENTE);
        doThrow(new RuntimeException("Error al eliminar")).when(spyDAO).delete(mockMenu);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            spyDAO.eliminarCartaMenuPorId(CARTA_MENU_ID_EXISTENTE);
        });

        assertEquals("Error al eliminar", exception.getMessage());
    }
}