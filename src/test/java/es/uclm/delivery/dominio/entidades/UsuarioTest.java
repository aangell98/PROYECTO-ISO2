package es.uclm.delivery.dominio.entidades;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UsuarioTest {

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_USER = "ROLE_USER";
    private static final String ROLE_ADMIN_WITH_PREFIX = "ROLE_ADMIN";

    private Usuario crearUsuario() {
        return new Usuario();
    }

    @Test
    void setRole_sinPrefijoRole_agregaPrefijo() {
        Usuario usuario = crearUsuario();
        usuario.setRole(ROLE_ADMIN);

        assertEquals(ROLE_ADMIN_WITH_PREFIX, usuario.getRole());
    }

    @Test
    void setRole_conPrefijoRole_mantieneValor() {
        Usuario usuario = crearUsuario();
        usuario.setRole(ROLE_USER);

        assertEquals(ROLE_USER, usuario.getRole());
    }

    @Test
    void setRole_cadenaVacia_lanzaExcepcion() {
        Usuario usuario = crearUsuario();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuario.setRole("");
        });
        assertEquals("El rol no puede estar vacío o ser nulo.", exception.getMessage());
    }

    @Test
    void setRole_nulo_lanzaExcepcion() {
        Usuario usuario = crearUsuario();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuario.setRole(null);
        });
        assertEquals("El rol no puede estar vacío o ser nulo.", exception.getMessage());
    }
}