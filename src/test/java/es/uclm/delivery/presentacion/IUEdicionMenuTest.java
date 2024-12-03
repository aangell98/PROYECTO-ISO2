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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class IUEdicionMenuTest {

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
        // Inicializar los objetos para las pruebas
        restaurante = new Restaurante();
        restaurante.setId(1L);
        restaurante.setNombre("Restaurante A");
        restaurante.setDireccion("Calle Ficticia 123");

        cartaMenu = new CartaMenu();
        cartaMenu.setId(1L);
        cartaMenu.setNombre("Carta Especial");
        cartaMenu.setDescripcion("Descripción de la carta");

        itemMenu = new ItemMenu();
        itemMenu.setId(1L);
        itemMenu.setNombre("Plato 1");
        itemMenu.setDescripcion("Delicioso plato");
        itemMenu.setPrecio(15.0);
    }

    // Prueba para editar un restaurante
    @Test
    void testEditarRestaurante_Existente() {
        when(restauranteDAO.findById(restaurante.getId())).thenReturn(Optional.of(restaurante));

        boolean resultado = iuEdicionMenu.editarRestaurante(restaurante);
        assertTrue(resultado);
        verify(restauranteDAO, times(1)).update(any(Restaurante.class));
    }

    @Test
    void testEditarRestaurante_NoExistente() {
        when(restauranteDAO.findById(restaurante.getId())).thenReturn(Optional.empty());

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
        when(cartaMenuDAO.findById(cartaMenu.getId())).thenReturn(Optional.of(cartaMenu));

        boolean resultado = iuEdicionMenu.editarCartaMenu(cartaMenu);
        assertTrue(resultado);
        verify(cartaMenuDAO, times(1)).update(any(CartaMenu.class));
    }

    @Test
    void testEditarCartaMenu_NoExistente() {
        when(cartaMenuDAO.findById(cartaMenu.getId())).thenReturn(Optional.empty());

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
        when(itemMenuDAO.findById(itemMenu.getId())).thenReturn(Optional.of(itemMenu));

        boolean resultado = iuEdicionMenu.editarItemMenu(itemMenu);
        assertTrue(resultado);
        verify(itemMenuDAO, times(1)).update(any(ItemMenu.class));
    }

    @Test
    void testEditarItemMenu_NoExistente() {
        when(itemMenuDAO.findById(itemMenu.getId())).thenReturn(Optional.empty());

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

    // Pruebas con errores al intentar actualizar en base de datos

    @Test
    void testEditarRestaurante_ErrorActualizacion() {
        when(restauranteDAO.findById(restaurante.getId())).thenReturn(Optional.of(restaurante));
        doThrow(new RuntimeException("Error al actualizar")).when(restauranteDAO).update(any(Restaurante.class));

        boolean resultado = iuEdicionMenu.editarRestaurante(restaurante);
        assertFalse(resultado);
    }

    @Test
    void testEditarCartaMenu_ErrorActualizacion() {
        when(cartaMenuDAO.findById(cartaMenu.getId())).thenReturn(Optional.of(cartaMenu));
        doThrow(new RuntimeException("Error al actualizar")).when(cartaMenuDAO).update(any(CartaMenu.class));

        boolean resultado = iuEdicionMenu.editarCartaMenu(cartaMenu);
        assertFalse(resultado);
    }

    @Test
    void testEditarItemMenu_ErrorActualizacion() {
        when(itemMenuDAO.findById(itemMenu.getId())).thenReturn(Optional.of(itemMenu));
        doThrow(new RuntimeException("Error al actualizar")).when(itemMenuDAO).update(any(ItemMenu.class));

        boolean resultado = iuEdicionMenu.editarItemMenu(itemMenu);
        assertFalse(resultado);
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
