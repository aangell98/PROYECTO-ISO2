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

import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
        verify(redirectAttributes).addFlashAttribute("error", "ID de menú no proporcionado.");
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

    // -----------------------------------------
    // Pruebas para editarCartaMenu
    // -----------------------------------------
    @Test
    void editarCartaMenu_desasociarPlatos_exito() {
        CartaMenu cartaMenu = new CartaMenu();
        cartaMenu.setId(MENU_ID);
        cartaMenu.setNombre("Nuevo Nombre");
        cartaMenu.setDescripcion("Nueva Descripción");

        ItemMenu item1 = crearItemMenu();
        item1.setId(1L);
        ItemMenu item2 = crearItemMenu();
        item2.setId(2L);

        cartaMenu.setItems(Arrays.asList(item1, item2));

        when(cartaMenuDAO.findById(MENU_ID)).thenReturn(Optional.of(cartaMenu));
        when(itemMenuDAO.findAllById(Arrays.asList(1L))).thenReturn(Collections.singletonList(item1));

        String result = gestorRestaurantes.editarCartaMenu(cartaMenu, Collections.singletonList(1L));

        assertEquals("redirect:/homeRestaurante", result);
        verify(cartaMenuDAO).update(cartaMenu);
        assertEquals(1, cartaMenu.getItems().size());
        assertTrue(cartaMenu.getItems().contains(item1));
        assertFalse(cartaMenu.getItems().contains(item2));
    }

    @Test
    void editarCartaMenu_actualizarMenu_exito() {
        CartaMenu cartaMenu = new CartaMenu();
        cartaMenu.setId(MENU_ID);
        cartaMenu.setNombre("Nuevo Nombre");
        cartaMenu.setDescripcion("Nueva Descripción");

        when(cartaMenuDAO.findById(MENU_ID)).thenReturn(Optional.of(cartaMenu));
        when(itemMenuDAO.findAllById(Collections.emptyList())).thenReturn(Collections.emptyList());

        String result = gestorRestaurantes.editarCartaMenu(cartaMenu, Collections.emptyList());

        assertEquals("redirect:/homeRestaurante", result);
        verify(cartaMenuDAO).update(cartaMenu);
        assertEquals("Nuevo Nombre", cartaMenu.getNombre());
        assertEquals("Nueva Descripción", cartaMenu.getDescripcion());
    }

    // -----------------------------------------
    // Pruebas para editarItemMenu
    // -----------------------------------------
    @Test
    void editarItemMenu_actualizarPlato_exito() {
        ItemMenu itemMenu = crearItemMenu();
        itemMenu.setNombre("Nuevo Nombre");
        itemMenu.setDescripcion("Nueva Descripción");
        itemMenu.setPrecio(15.0);

        when(itemMenuDAO.findById(PLATO_ID)).thenReturn(Optional.of(itemMenu));

        String result = gestorRestaurantes.editarItemMenu(itemMenu, redirectAttributes);

        assertEquals("redirect:/homeRestaurante", result);
        verify(itemMenuDAO).update(itemMenu);
        assertEquals("Nuevo Nombre", itemMenu.getNombre());
        assertEquals("Nueva Descripción", itemMenu.getDescripcion());
        assertEquals(15.0, itemMenu.getPrecio());
    }

    @Test
    void testEliminarRestaurante() {
        Long restauranteId = 1L;
        Restaurante restauranteMock = new Restaurante();
        restauranteMock.setId(restauranteId);

        when(restauranteDAO.findById(restauranteId)).thenReturn(Optional.of(restauranteMock));

        String result = gestorRestaurantes.eliminarRestaurante(restauranteId, mock(Model.class));

        assertEquals("redirect:/homeRestaurante", result);
        verify(restauranteDAO).delete(restauranteMock);
    }
    // Nuevos métodos de prueba para cubrir más código en GestorRestaurantes

    @Test
    void testCrearRestaurante() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("usuario1");
        Usuario usuarioMock = new Usuario();
        usuarioMock.setUsername("usuario1");
        Restaurante restauranteMock = new Restaurante();
        restauranteMock.setNombre("Restaurante Prueba");
        restauranteMock.setDireccion("Calle Falsa 123");

        when(usuarioDAO.encontrarUser("usuario1")).thenReturn(Optional.of(usuarioMock));
        when(restauranteDAO.findByUsuario(usuarioMock)).thenReturn(Optional.of(restauranteMock));

        Restaurante nuevoRestaurante = new Restaurante();
        nuevoRestaurante.setNombre("Nuevo Restaurante");
        nuevoRestaurante.setDireccion("Nueva Dirección");

        String result = gestorRestaurantes.crearRestaurante(nuevoRestaurante, principal);

        assertEquals("redirect:/homeRestaurante", result);
        verify(restauranteDAO).update(restauranteMock);
        assertEquals("Nuevo Restaurante", restauranteMock.getNombre());
        assertEquals("Nueva Dirección", restauranteMock.getDireccion());
    }

    @Test
    void testCrearRestauranteUsuarioNoEncontrado() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("usuario1");

        when(usuarioDAO.encontrarUser("usuario1")).thenReturn(Optional.empty());

        Restaurante nuevoRestaurante = new Restaurante();
        nuevoRestaurante.setNombre("Nuevo Restaurante");
        nuevoRestaurante.setDireccion("Nueva Dirección");

        String result = gestorRestaurantes.crearRestaurante(nuevoRestaurante, principal);

        assertEquals("redirect:/homeRestaurante", result);
        verify(restauranteDAO, never()).update(any());
    }

    @Test
    void testCrearCartaMenu() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("usuario1");
        Usuario usuarioMock = new Usuario();
        usuarioMock.setUsername("usuario1");
        Restaurante restauranteMock = new Restaurante();
        restauranteMock.setId(RESTAURANTE_ID);

        when(usuarioDAO.encontrarUser("usuario1")).thenReturn(Optional.of(usuarioMock));
        when(restauranteDAO.findByUsuario(usuarioMock)).thenReturn(Optional.of(restauranteMock));

        CartaMenu cartaMenu = new CartaMenu();
        cartaMenu.setNombre("Nueva Carta");
        List<Long> itemsIds = Arrays.asList(1L, 2L);
        ItemMenu item1 = new ItemMenu();
        item1.setId(1L);
        ItemMenu item2 = new ItemMenu();
        item2.setId(2L);

        when(itemMenuDAO.findAllById(itemsIds)).thenReturn(Arrays.asList(item1, item2));

        String result = gestorRestaurantes.crearCartaMenu(cartaMenu, itemsIds, principal);

        assertEquals("redirect:/homeRestaurante", result);
        verify(cartaMenuDAO).insert(cartaMenu);
        assertEquals(2, cartaMenu.getItems().size());
    }

    @Test
    void testCrearCartaMenuUsuarioNoEncontrado() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("usuario1");

        when(usuarioDAO.encontrarUser("usuario1")).thenReturn(Optional.empty());

        CartaMenu cartaMenu = new CartaMenu();
        cartaMenu.setNombre("Nueva Carta");
        List<Long> itemsIds = Arrays.asList(1L, 2L);

        String result = gestorRestaurantes.crearCartaMenu(cartaMenu, itemsIds, principal);

        assertEquals("redirect:/homeRestaurante", result);
        verify(cartaMenuDAO, never()).insert(any());
    }

    @Test
    void testCrearCartaMenuRestauranteNoEncontrado() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("usuario1");
        Usuario usuarioMock = new Usuario();
        usuarioMock.setUsername("usuario1");

        when(usuarioDAO.encontrarUser("usuario1")).thenReturn(Optional.of(usuarioMock));
        when(restauranteDAO.findByUsuario(usuarioMock)).thenReturn(Optional.empty());

        CartaMenu cartaMenu = new CartaMenu();
        cartaMenu.setNombre("Nueva Carta");
        List<Long> itemsIds = Arrays.asList(1L, 2L);

        String result = gestorRestaurantes.crearCartaMenu(cartaMenu, itemsIds, principal);

        assertEquals("redirect:/homeRestaurante", result);
        verify(cartaMenuDAO, never()).insert(any());
    }

    @Test
void testEliminarNombreDireccionRestaurante() {
    Principal principal = mock(Principal.class);
    when(principal.getName()).thenReturn("usuario1");
    Usuario usuarioMock = new Usuario();
    usuarioMock.setUsername("usuario1");
    Restaurante restauranteMock = new Restaurante();
    restauranteMock.setNombre("Restaurante Prueba");
    restauranteMock.setDireccion("Calle Falsa 123");

    when(usuarioDAO.encontrarUser("usuario1")).thenReturn(Optional.of(usuarioMock));
    when(restauranteDAO.findByUsuario(usuarioMock)).thenReturn(Optional.of(restauranteMock));

    String result = gestorRestaurantes.eliminarNombreDireccionRestaurante(principal);

    assertEquals("redirect:/homeRestaurante", result);
    assertNull(restauranteMock.getNombre());
    assertNull(restauranteMock.getDireccion());
    verify(restauranteDAO).update(restauranteMock);
}

@Test
void testEliminarNombreDireccionRestauranteUsuarioNoEncontrado() {
    Principal principal = mock(Principal.class);
    when(principal.getName()).thenReturn("usuario1");

    when(usuarioDAO.encontrarUser("usuario1")).thenReturn(Optional.empty());

    String result = gestorRestaurantes.eliminarNombreDireccionRestaurante(principal);

    assertEquals("redirect:/homeRestaurante", result);
    verify(restauranteDAO, never()).update(any());
}

@Test
void testEliminarNombreDireccionRestauranteRestauranteNoEncontrado() {
    Principal principal = mock(Principal.class);
    when(principal.getName()).thenReturn("usuario1");
    Usuario usuarioMock = new Usuario();
    usuarioMock.setUsername("usuario1");

    when(usuarioDAO.encontrarUser("usuario1")).thenReturn(Optional.of(usuarioMock));
    when(restauranteDAO.findByUsuario(usuarioMock)).thenReturn(Optional.empty());

    String result = gestorRestaurantes.eliminarNombreDireccionRestaurante(principal);

    assertEquals("redirect:/homeRestaurante", result);
    verify(restauranteDAO, never()).update(any());
}
}