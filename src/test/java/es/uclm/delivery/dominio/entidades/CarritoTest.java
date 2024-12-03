package es.uclm.delivery.dominio.entidades;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class CarritoTest {

    // Test para agregar un ítem válido
    @Test
    void testAgregarItem() {
        Carrito carrito = new Carrito();
        ItemMenu item1 = org.mockito.Mockito.mock(ItemMenu.class);
        org.mockito.Mockito.when(item1.getId()).thenReturn(1L);
        org.mockito.Mockito.when(item1.getNombre()).thenReturn("Item 1");
        org.mockito.Mockito.when(item1.getPrecio()).thenReturn(10.0);

        carrito.agregarItem(item1);

        assertEquals(1, carrito.getItems().size());
        assertEquals(10.0, carrito.getPrecioTotal());
    }

    // Test para vaciar el carrito
    @Test
    void testVaciar() {
        Carrito carrito = new Carrito();
        ItemMenu item1 = org.mockito.Mockito.mock(ItemMenu.class);
        org.mockito.Mockito.when(item1.getId()).thenReturn(1L);
        org.mockito.Mockito.when(item1.getNombre()).thenReturn("Item 1");
        org.mockito.Mockito.when(item1.getPrecio()).thenReturn(10.0);
        carrito.agregarItem(item1);
        assertFalse(carrito.getItems().isEmpty());

        carrito.vaciar();
        assertTrue(carrito.getItems().isEmpty());
        assertEquals(0.0, carrito.getPrecioTotal());
    }

    // Test para vaciar carrito ya vacío
    @Test
    void testVaciarCarritoVacio() {
        Carrito carrito = new Carrito();
        carrito.vaciar();  // Carrito ya vacío, no debería causar problemas
        assertTrue(carrito.getItems().isEmpty());
        assertEquals(0.0, carrito.getPrecioTotal());
    }

    // Test para agregar ítem con precio negativo (caso desfavorable)
    @Test
    void testAgregarItemConPrecioInvalido() {
        Carrito carrito = new Carrito();

        // Intentar agregar un ítem con precio negativo
        ItemMenu itemInvalido1 = org.mockito.Mockito.mock(ItemMenu.class);
        org.mockito.Mockito.when(itemInvalido1.getId()).thenReturn(1L);
        org.mockito.Mockito.when(itemInvalido1.getNombre()).thenReturn("Item Inválido 1");
        org.mockito.Mockito.when(itemInvalido1.getPrecio()).thenReturn(-10.0);
        
        carrito.agregarItem(itemInvalido1);
        assertEquals(0, carrito.getItems().size());  // No debería haberse agregado
    }


    // Test para eliminar un ítem existente y no existente
    @Test
    void testEliminarItem() {
        Carrito carrito = new Carrito();
        
        // Mock para item1
        ItemMenu item1 = org.mockito.Mockito.mock(ItemMenu.class);
        org.mockito.Mockito.when(item1.getId()).thenReturn(1L);
        org.mockito.Mockito.when(item1.getNombre()).thenReturn("Item 1");
        org.mockito.Mockito.when(item1.getPrecio()).thenReturn(10.0);
        
        // Mock para item2 (usando item2 en vez de item1)
        ItemMenu item2 = org.mockito.Mockito.mock(ItemMenu.class);
        org.mockito.Mockito.when(item2.getId()).thenReturn(2L);
        org.mockito.Mockito.when(item2.getNombre()).thenReturn("Item 2");
        org.mockito.Mockito.when(item2.getPrecio()).thenReturn(15.0);
        
        // Agregar los ítems al carrito
        carrito.agregarItem(item1);
        carrito.agregarItem(item2);

        // Eliminar el primer ítem
        carrito.eliminarItem(1L);
        assertEquals(1, carrito.getItems().size());
        assertEquals(15.0, carrito.getPrecioTotal());
        assertEquals("Item 2", carrito.getItems().get(0).getNombre());

        // Eliminar el último ítem
        carrito.eliminarItem(2L);
        assertEquals(0, carrito.getItems().size());  // Carrito vacío
        assertEquals(0.0, carrito.getPrecioTotal());

        // Intentar eliminar un ítem que no existe
        carrito.eliminarItem(3L);  // ID no existe
        assertEquals(0, carrito.getItems().size()); // No cambia nada
    }


    // Test para actualizar el precio total después de agregar y eliminar ítems
    @Test
    void testActualizarPrecioTotal() {
        Carrito carrito = new Carrito();
        assertEquals(0.0, carrito.getPrecioTotal());

        // Mock para item1
        ItemMenu item1 = org.mockito.Mockito.mock(ItemMenu.class);
        org.mockito.Mockito.when(item1.getId()).thenReturn(1L);
        org.mockito.Mockito.when(item1.getNombre()).thenReturn("Item 1");
        org.mockito.Mockito.when(item1.getPrecio()).thenReturn(10.0);
        carrito.agregarItem(item1);
        assertEquals(10.0, carrito.getPrecioTotal());

        // Mock para item2 (debe usar item2 en lugar de item1)
        ItemMenu item2 = org.mockito.Mockito.mock(ItemMenu.class);
        org.mockito.Mockito.when(item2.getId()).thenReturn(2L);
        org.mockito.Mockito.when(item2.getNombre()).thenReturn("Item 2");
        org.mockito.Mockito.when(item2.getPrecio()).thenReturn(15.0);
        carrito.agregarItem(item2);
        assertEquals(25.0, carrito.getPrecioTotal());

        carrito.eliminarItem(1L);
        assertEquals(15.0, carrito.getPrecioTotal());
    }


    // Test para verificar actualización de precio después de múltiples eliminaciones y adiciones
    @Test
    void testActualizarPrecioTotalConMúltiplesEliminacionesYAdiciones() {
        Carrito carrito = new Carrito();

        // Mock para item1
        ItemMenu item1 = org.mockito.Mockito.mock(ItemMenu.class);
        org.mockito.Mockito.when(item1.getId()).thenReturn(1L);
        org.mockito.Mockito.when(item1.getNombre()).thenReturn("Item 1");
        org.mockito.Mockito.when(item1.getPrecio()).thenReturn(10.0);

        // Mock para item2
        ItemMenu item2 = org.mockito.Mockito.mock(ItemMenu.class);
        org.mockito.Mockito.when(item2.getId()).thenReturn(2L);
        org.mockito.Mockito.when(item2.getNombre()).thenReturn("Item 2");
        org.mockito.Mockito.when(item2.getPrecio()).thenReturn(15.0);

        // Mock para item3
        ItemMenu item3 = org.mockito.Mockito.mock(ItemMenu.class);
        org.mockito.Mockito.when(item3.getId()).thenReturn(3L);
        org.mockito.Mockito.when(item3.getNombre()).thenReturn("Item 3");
        org.mockito.Mockito.when(item3.getPrecio()).thenReturn(25.0);

        // Agregar ítems al carrito
        carrito.agregarItem(item1);
        carrito.agregarItem(item2);
        carrito.agregarItem(item3);
        assertEquals(50.0, carrito.getPrecioTotal()); // 10 + 15 + 25

        // Eliminar el primer ítem
        carrito.eliminarItem(1L);
        assertEquals(40.0, carrito.getPrecioTotal()); // 15 + 25

        // Eliminar el segundo ítem
        carrito.eliminarItem(2L);
        assertEquals(25.0, carrito.getPrecioTotal()); // Solo el último ítem

        // Volver a agregar el primer ítem
        carrito.agregarItem(item1);
        assertEquals(35.0, carrito.getPrecioTotal()); // 10 + 25
    }
}