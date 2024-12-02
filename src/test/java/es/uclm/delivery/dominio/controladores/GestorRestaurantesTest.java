package es.uclm.delivery.dominio.controladores;

import es.uclm.delivery.dominio.entidades.*;
import es.uclm.delivery.persistencia.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GestorRestaurantesTest {

    private static final Long MENU_ID = 1L;
    private static final Long PLATO_ID = 1L;
    private static final Long RESTAURANTE_ID = 1L;

    @InjectMocks
    private GestorRestaurantes gestorRestaurantes;

    @Mock
    private UsuarioDAO usuarioDAO;

    @Mock
    private RestauranteDAO restauranteDAO;

    @Mock
    private CartaMenuDAO cartaMenuDAO;

    @Mock
    private ItemMenuDAO itemMenuDAO;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private Model model;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Restaurante crearRestaurante() {
        Restaurante restaurante = new Restaurante();
        restaurante.setId(RESTAURANTE_ID);
        restaurante.setNombre("Nuevo Nombre");
        restaurante.setDireccion("Nueva Dirección");
        return restaurante;
    }

    private ItemMenu crearItemMenu() {
        ItemMenu item = new ItemMenu();
        item.setId(PLATO_ID);
        item.setNombre("Plato Test");
        item.setDescripcion("Descripción Test");
        item.setPrecio(10.0);
        return item;
    }

    // -----------------------------------------
    // Pruebas para eliminarMenu
    // -----------------------------------------
    @Test
    void eliminarMenu_menuExiste_exito() {
        when(cartaMenuDAO.eliminarCartaMenuPorId(MENU_ID)).thenReturn(1);

        String result = gestorRestaurantes.eliminarMenu(MENU_ID, redirectAttributes);

        assertEquals("redirect:/homeRestaurante", result);
        verify(cartaMenuDAO, times(1)).eliminarCartaMenuPorId(MENU_ID);
        verify(redirectAttributes).addFlashAttribute("mensaje", "Menú eliminado con éxito.");
    }

    @Test
    void eliminarMenu_menuNoExiste_error() {
        when(cartaMenuDAO.eliminarCartaMenuPorId(MENU_ID)).thenReturn(0);

        String result = gestorRestaurantes.eliminarMenu(MENU_ID, redirectAttributes);

        assertEquals("redirect:/homeRestaurante", result);
        verify(cartaMenuDAO, times(1)).eliminarCartaMenuPorId(MENU_ID);
        verify(redirectAttributes).addFlashAttribute("error", "Error al eliminar el menú.");
    }

    @Test
    void eliminarMenu_menuIdInvalido_error() {
        Long menuId = null;

        String result = gestorRestaurantes.eliminarMenu(menuId, redirectAttributes);

        assertEquals("redirect:/homeRestaurante", result);
        verify(cartaMenuDAO, never()).eliminarCartaMenuPorId(any());
        verify(redirectAttributes).addFlashAttribute("error", "Error al eliminar el menú.");
    }

    // -----------------------------------------
    // Pruebas para eliminarItemMenu
    // -----------------------------------------
    @Test
    void eliminarItemMenu_itemExiste_exito() {
        ItemMenu item = crearItemMenu();
        when(itemMenuDAO.findById(PLATO_ID)).thenReturn(Optional.of(item));

        String result = gestorRestaurantes.eliminarItemMenu(PLATO_ID, model);

        assertEquals("redirect:/homeRestaurante", result);
        verify(itemMenuDAO, times(1)).delete(item);
        verify(model).addAttribute("successMessage", "Plato eliminado correctamente.");
    }

    @Test
    void eliminarItemMenu_itemNoExiste_error() {
        when(itemMenuDAO.findById(PLATO_ID)).thenReturn(Optional.empty());

        String result = gestorRestaurantes.eliminarItemMenu(PLATO_ID, model);

        assertEquals("redirect:/homeRestaurante", result);
        verify(itemMenuDAO, never()).delete(any());
        verify(model).addAttribute("errorMessage", "El plato no existe.");
    }

    @Test
    void eliminarItemMenu_itemAsociadoAMenu_error() {
        ItemMenu item = crearItemMenu();
        when(itemMenuDAO.findById(PLATO_ID)).thenReturn(Optional.of(item));
        doThrow(new RuntimeException("Clave foránea")).when(itemMenuDAO).delete(item);

        String result = gestorRestaurantes.eliminarItemMenu(PLATO_ID, model);

        assertEquals("redirect:/homeRestaurante", result);
        verify(model).addAttribute("errorMessage", 
            "No se puede eliminar el plato porque está asociado a uno o más menús.");
    }

    // -----------------------------------------
    // Pruebas para editarRestaurante
    // -----------------------------------------
    @Test
    void editarRestaurante_restauranteExiste_exito() {
        Restaurante restaurante = crearRestaurante();

        Restaurante original = new Restaurante();
        when(restauranteDAO.findById(RESTAURANTE_ID)).thenReturn(Optional.of(original));

        String result = gestorRestaurantes.editarRestaurante(restaurante, model);

        assertEquals("redirect:/homeRestaurante", result);
        verify(restauranteDAO).update(original);
        assertEquals("Nuevo Nombre", original.getNombre());
        assertEquals("Nueva Dirección", original.getDireccion());
    }

    @Test
    void editarRestaurante_restauranteNoExiste_error() {
        Restaurante restaurante = crearRestaurante();
        when(restauranteDAO.findById(RESTAURANTE_ID)).thenReturn(Optional.empty());

        String result = gestorRestaurantes.editarRestaurante(restaurante, model);

        assertEquals("redirect:/homeRestaurante", result);
        verify(model).addAttribute("error", "Error: Restaurante no encontrado.");
        verify(restauranteDAO, never()).update(any());
    }
}