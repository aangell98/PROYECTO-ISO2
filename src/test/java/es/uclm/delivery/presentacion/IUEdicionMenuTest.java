package es.uclm.delivery.presentacion;

import es.uclm.delivery.dominio.entidades.CartaMenu;
import es.uclm.delivery.dominio.entidades.ItemMenu;
import es.uclm.delivery.dominio.entidades.Restaurante;
import es.uclm.delivery.persistencia.CartaMenuDAO;
import es.uclm.delivery.persistencia.ItemMenuDAO;
import es.uclm.delivery.persistencia.RestauranteDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class IUEdicionMenuTest {

    private static final Long RESTAURANTE_ID = 1L;
    private static final Long CARTA_MENU_ID = 1L;
    private static final Long ITEM_MENU_ID = 1L;
    private static final String RESTAURANTE_NOMBRE = "Restaurante A";
    private static final String RESTAURANTE_DIRECCION = "Calle Ficticia 123";
    private static final String CARTA_MENU_NOMBRE = "Carta Especial";
    private static final String CARTA_MENU_DESCRIPCION = "Descripción de la carta";
    private static final String ITEM_MENU_NOMBRE = "Plato 1";
    private static final String ITEM_MENU_DESCRIPCION = "Delicioso plato";
    private static final double ITEM_MENU_PRECIO = 15.0;

    @Mock
    private RestauranteDAO restauranteDAO;

    @Mock
    private CartaMenuDAO cartaMenuDAO;

    @Mock
    private ItemMenuDAO itemMenuDAO;

    @InjectMocks
    private IUEdicionMenu iuEdicionMenu;

    private Restaurante restaurante;
    private CartaMenu cartaMenu;
    private ItemMenu itemMenu;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        restaurante = crearMockRestaurante();
        cartaMenu = crearMockCartaMenu();
        itemMenu = crearMockItemMenu();
    }

    private Restaurante crearMockRestaurante() {
        Restaurante mockRestaurante = new Restaurante();
        mockRestaurante.setId(RESTAURANTE_ID);
        mockRestaurante.setNombre(RESTAURANTE_NOMBRE);
        mockRestaurante.setDireccion(RESTAURANTE_DIRECCION);
        return mockRestaurante;
    }

    private CartaMenu crearMockCartaMenu() {
        CartaMenu mockCartaMenu = new CartaMenu();
        mockCartaMenu.setId(CARTA_MENU_ID);
        mockCartaMenu.setNombre(CARTA_MENU_NOMBRE);
        mockCartaMenu.setDescripcion(CARTA_MENU_DESCRIPCION);
        return mockCartaMenu;
    }

    private ItemMenu crearMockItemMenu() {
        ItemMenu mockItemMenu = new ItemMenu();
        mockItemMenu.setId(ITEM_MENU_ID);
        mockItemMenu.setNombre(ITEM_MENU_NOMBRE);
        mockItemMenu.setDescripcion(ITEM_MENU_DESCRIPCION);
        mockItemMenu.setPrecio(ITEM_MENU_PRECIO);
        return mockItemMenu;
    }

    // Prueba para editar un restaurante
    @Test
    void testEditarRestaurante_Existente() {
        when(restauranteDAO.findById(RESTAURANTE_ID)).thenReturn(Optional.of(restaurante));

        boolean resultado = iuEdicionMenu.editarRestaurante(restaurante);
        assertTrue(resultado);
        verify(restauranteDAO, times(1)).update(any(Restaurante.class));
    }

    @Test
    void testEditarRestaurante_NoExistente() {
        when(restauranteDAO.findById(RESTAURANTE_ID)).thenReturn(Optional.empty());

        boolean resultado = iuEdicionMenu.editarRestaurante(restaurante);
        assertFalse(resultado);
        verify(restauranteDAO, times(0)).update(any(Restaurante.class));
    }

    @Test
    void testEditarRestaurante_Nulo() {
        boolean resultado = iuEdicionMenu.editarRestaurante(null);
        assertFalse(resultado);
        verify(restauranteDAO, times(0)).update(any(Restaurante.class));
    }

    // Prueba para editar una carta de menú
    @Test
    void testEditarCartaMenu_Existente() {
        when(cartaMenuDAO.findById(CARTA_MENU_ID)).thenReturn(Optional.of(cartaMenu));

        boolean resultado = iuEdicionMenu.editarCartaMenu(cartaMenu);
        assertTrue(resultado);
        verify(cartaMenuDAO, times(1)).update(any(CartaMenu.class));
    }

    @Test
    void testEditarCartaMenu_NoExistente() {
        when(cartaMenuDAO.findById(CARTA_MENU_ID)).thenReturn(Optional.empty());

        boolean resultado = iuEdicionMenu.editarCartaMenu(cartaMenu);
        assertFalse(resultado);
        verify(cartaMenuDAO, times(0)).update(any(CartaMenu.class));
    }

    @Test
    void testEditarCartaMenu_Nulo() {
        boolean resultado = iuEdicionMenu.editarCartaMenu(null);
        assertFalse(resultado);
        verify(cartaMenuDAO, times(0)).update(any(CartaMenu.class));
    }

    // Prueba para editar un item de menú
    @Test
    void testEditarItemMenu_Existente() {
        when(itemMenuDAO.findById(ITEM_MENU_ID)).thenReturn(Optional.of(itemMenu));

        boolean resultado = iuEdicionMenu.editarItemMenu(itemMenu);
        assertTrue(resultado);
        verify(itemMenuDAO, times(1)).update(any(ItemMenu.class));
    }

    @Test
    void testEditarItemMenu_NoExistente() {
        when(itemMenuDAO.findById(ITEM_MENU_ID)).thenReturn(Optional.empty());

        boolean resultado = iuEdicionMenu.editarItemMenu(itemMenu);
        assertFalse(resultado);
        verify(itemMenuDAO, times(0)).update(any(ItemMenu.class));
    }

    @Test
    void testEditarItemMenu_Nulo() {
        boolean resultado = iuEdicionMenu.editarItemMenu(null);
        assertFalse(resultado);
        verify(itemMenuDAO, times(0)).update(any(ItemMenu.class));
    }

    // Prueba con un item de menú con precio negativo
    @Test
    void testEditarItemMenu_PrecioNegativo() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            itemMenu.setPrecio(-10.0); // Precio negativo no válido
        });
        assertEquals("El precio no puede ser negativo", exception.getMessage());
    }
}