package es.uclm.delivery.dominio.entidades;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;


//ESTO ES UN EJEMPLO DE FUNCIONAMIENTO DE UN TEST UNITARIO

public class ClienteTest {

    static Cliente cliente = new Cliente();

    @BeforeAll
    public static void setUp() {
        cliente.setNombre("Juan");
        cliente.setApellidos("Perez");
        cliente.setDni("12345678A");
    }

    @Test
    void testGetNombre() {

        String expected = "Juan";
        String actual = cliente.getNombre();
        assertEquals(expected, actual);

    }
}
