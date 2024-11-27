package es.uclm.delivery.dominio.controladores;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import es.uclm.delivery.dominio.entidades.Cliente;
import es.uclm.delivery.dominio.entidades.Direccion;
import es.uclm.delivery.dominio.entidades.Usuario;
import es.uclm.delivery.persistencia.ClienteDAO;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

    class GestorClientesTest {
     @Mock
    private ClienteDAO clienteDAO;

    @InjectMocks
    private GestorClientes gestorClientes;

    @Mock
    private Principal principal;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListarDireccionesClienteEncontrado() {

        Usuario usuario = new Usuario();
        usuario.setUsername("usuarioEjemplo");
        String nombre = usuario.getUsername();
        // Simulamos el Principal
        when(principal.getName()).thenReturn(null);

        // Creamos datos simulados
        Cliente clienteMock = new Cliente();
        clienteMock.setUsuario(usuario);

        Direccion direccion1 = new Direccion();
        direccion1.setCalle("Calle Falsa 123");
        Direccion direccion2 = new Direccion();
        direccion2.setCalle("Avenida Siempre Viva 742");

        clienteMock.setDirecciones(List.of(direccion1, direccion2));

        // Simulamos el clienteDAO
        when(clienteDAO.findByUsername(nombre)).thenReturn(Optional.of(clienteMock));

        // Ejecutamos el método
        ResponseEntity<List<Direccion>> response = gestorClientes.listarDirecciones(principal);

        // Verificamos la respuesta
        assertNotNull(response.getBody());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Calle Falsa 123", response.getBody().get(0).getCalle());
        assertEquals("Avenida Siempre Viva 742", response.getBody().get(1).getCalle());
    }

    @Test
    void testListarDireccionesClienteNoEncontrado() {
        // Simulamos el Principal
        when(principal.getName()).thenReturn("usuarioNoExistente");

        // Simulamos que el clienteDAO no encuentra al usuario
        when(clienteDAO.findByUsername("usuarioNoExistente")).thenReturn(Optional.empty());

        // Ejecutamos el método
        ResponseEntity<List<Direccion>> response = gestorClientes.listarDirecciones(principal);

        // Verificamos la respuesta
        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }
}
