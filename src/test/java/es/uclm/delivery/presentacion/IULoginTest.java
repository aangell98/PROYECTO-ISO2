package es.uclm.delivery.presentacion;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.ui.Model;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import es.uclm.delivery.dominio.entidades.*;
import es.uclm.delivery.dominio.controladores.*;
import es.uclm.delivery.persistencia.*;

import java.util.*;

class IULoginTest {

    private IULogin loginController;
    private GestorLogin mockGestorLogin;
    private UsuarioDAO mockUsuarioDAO;
    private RestauranteDAO mockRestauranteDAO;
    private ServicioEntregaDAO mockServicioEntregaDAO;

    @BeforeEach
    void setUp() {
        loginController = new IULogin();
        mockGestorLogin = Mockito.mock(GestorLogin.class);
        mockUsuarioDAO = Mockito.mock(UsuarioDAO.class);
        mockRestauranteDAO = Mockito.mock(RestauranteDAO.class);
        mockServicioEntregaDAO = Mockito.mock(ServicioEntregaDAO.class);

        // Inyectar las dependencias simuladas
        loginController.gestorLogin = mockGestorLogin;
        loginController.usuarioDAO = mockUsuarioDAO;
        loginController.restauranteDAO = mockRestauranteDAO;
        loginController.servicioEntregaDAO = mockServicioEntregaDAO;
    }

    // ------------------------------------- TEST LOGIN ------------------------------------------

    @Test
    void testLoginUsuarioCorrecto() {
        Usuario mockUsuario = Mockito.mock(Usuario.class);
        Mockito.when(mockUsuario.getUsername()).thenReturn("cliente1");
        Mockito.when(mockUsuario.getPassword()).thenReturn("password123");

        // Mocking successful authentication
        Mockito.when(mockGestorLogin.autenticar("cliente1", "password123")).thenReturn(true);
        Mockito.when(mockUsuarioDAO.getRole("cliente1")).thenReturn("CLIENTE");

        Model model = Mockito.mock(Model.class);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        Mockito.when(request.getSession()).thenReturn(session);

        String result = loginController.login(mockUsuario, model, request);

        assertEquals("redirect:/homeCliente", result);
    }

    @Test
    void testLoginUsuarioIncorrecto() {
        Usuario mockUsuario = Mockito.mock(Usuario.class);
        Mockito.when(mockUsuario.getUsername()).thenReturn("cliente1");
        Mockito.when(mockUsuario.getPassword()).thenReturn("wrongpassword");

        // Mocking failed authentication
        Mockito.when(mockGestorLogin.autenticar("cliente1", "wrongpassword")).thenReturn(false);

        Model model = Mockito.mock(Model.class);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        String result = loginController.login(mockUsuario, model, request);

        assertEquals("login", result);
    }

    @Test
    void testLoginUsuarioConRolDesconocido() {
        // Mock de Usuario
        Usuario mockUsuario = Mockito.mock(Usuario.class);
        Mockito.when(mockUsuario.getUsername()).thenReturn("cliente1");
        Mockito.when(mockUsuario.getPassword()).thenReturn("password123");

        // Mocking successful authentication with unknown role
        Mockito.when(mockGestorLogin.autenticar("cliente1", "password123")).thenReturn(true);
        Mockito.when(mockUsuarioDAO.getRole("cliente1")).thenReturn("UNKNOWN_ROLE");

        // Mock de Model
        Model model = Mockito.mock(Model.class);
        
        // Mock de HttpServletRequest y HttpSession
        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        HttpSession mockSession = Mockito.mock(HttpSession.class);
        Mockito.when(mockRequest.getSession()).thenReturn(mockSession);

        // Ejecutamos el método de login
        String result = loginController.login(mockUsuario, model, mockRequest);

        // Comprobamos que el resultado es el esperado para un rol desconocido
        assertEquals("login", result);
        
        // Verificamos que la sesión ha sido alterada aunque el rol sea desconocido
        Mockito.verify(mockSession, Mockito.atLeastOnce()).setAttribute(Mockito.anyString(), Mockito.any());
    }

    @Test
    void testLoginUsuarioSinPrefijoRole() {
        // Mock de Usuario
        Usuario mockUsuario = Mockito.mock(Usuario.class);
        Mockito.when(mockUsuario.getUsername()).thenReturn("cliente1");
        Mockito.when(mockUsuario.getPassword()).thenReturn("password123");

        // Mocking successful authentication with role not starting with "ROLE_"
        Mockito.when(mockGestorLogin.autenticar("cliente1", "password123")).thenReturn(true);
        Mockito.when(mockUsuarioDAO.getRole("cliente1")).thenReturn("CLIENTE");

        // Mock de Model
        Model model = Mockito.mock(Model.class);
        
        // Mock de HttpServletRequest
        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        
        // Mock de HttpSession
        HttpSession mockSession = Mockito.mock(HttpSession.class);
        Mockito.when(mockRequest.getSession()).thenReturn(mockSession);

        // Ejecutamos el método de login
        String result = loginController.login(mockUsuario, model, mockRequest);

        // Comprobamos que el resultado es el esperado para un usuario sin prefijo en el rol
        assertEquals("redirect:/homeCliente", result);

        // Verificamos que la sesión haya sido configurada correctamente
        Mockito.verify(mockSession, Mockito.atLeastOnce()).setAttribute(Mockito.anyString(), Mockito.any());
    }


    // ---------------------------------- TEST REGISTRO CLIENTE -----------------------------------

    @Test
    void testRegistrarClienteUsuarioExistente() {
        Usuario mockUsuario = Mockito.mock(Usuario.class);
        Cliente mockCliente = Mockito.mock(Cliente.class);
        Mockito.when(mockUsuario.getUsername()).thenReturn("cliente1");

        // Mocking registration failure due to existing username
        Mockito.when(mockGestorLogin.registrarCliente("cliente1", "password123", "CLIENTE", "John", "Doe", "12345678A")).thenReturn(false);

        Model model = Mockito.mock(Model.class);
        String result = loginController.registrarCliente(mockUsuario, mockCliente, model);

        assertEquals("registroCliente", result);
    }

    // ---------------------------------- TEST REGISTRO REPARTIDOR -----------------------------------


    @Test
    void testRegistrarRepartidorUsuarioExistente() {
        Usuario mockUsuario = Mockito.mock(Usuario.class);
        Repartidor mockRepartidor = Mockito.mock(Repartidor.class);
        Mockito.when(mockUsuario.getUsername()).thenReturn("repartidor1");

        // Mocking registration failure due to existing username
        Mockito.when(mockGestorLogin.registrarRepartidor("repartidor1", "password123", "REPARTIDOR", "Pedro", "Gomez", "87654321B")).thenReturn(false);

        Model model = Mockito.mock(Model.class);
        String result = loginController.registrarRepartidor(mockUsuario, mockRepartidor, model);

        assertEquals("registroRepartidor", result);
    }

    // ---------------------------------- TEST REGISTRO RESTAURANTE -----------------------------------

    @Test
    void testRegistrarRestauranteUsuarioExistente() {
        Usuario mockUsuario = Mockito.mock(Usuario.class);
        Restaurante mockRestaurante = Mockito.mock(Restaurante.class);
        Mockito.when(mockUsuario.getUsername()).thenReturn("restaurante1");

        // Mocking registration failure due to existing username
        Mockito.when(mockGestorLogin.registrarRestaurante("restaurante1", "password123", "RESTAURANTE", "Restaurante El Buen Comer", "Calle Ficticia 123")).thenReturn(false);

        Model model = Mockito.mock(Model.class);
        String result = loginController.registrarRestaurante(mockUsuario, mockRestaurante, model);

        assertEquals("registroRestaurante", result);
    }

    // ---------------------------------- TEST HOME -----------------------------------------------

    @Test
    void testMostrarHomeConRestaurantes() {
        Restaurante mockRestaurante1 = Mockito.mock(Restaurante.class);
        Mockito.when(mockRestaurante1.getNombre()).thenReturn("Restaurante 1");
        Mockito.when(mockRestaurante1.getDireccion()).thenReturn("Calle 1");
        Restaurante mockRestaurante2 = Mockito.mock(Restaurante.class);
        Mockito.when(mockRestaurante2.getNombre()).thenReturn("Restaurante 1");
        Mockito.when(mockRestaurante2.getDireccion()).thenReturn("Calle 1");
        List<Restaurante> mockRestaurantes = Arrays.asList(mockRestaurante1, mockRestaurante2);
        
        Mockito.when(mockRestauranteDAO.findAll()).thenReturn(mockRestaurantes);

        Model model = Mockito.mock(Model.class);
        String result = loginController.mostrarHome(model);

        assertEquals("home", result);
        Mockito.verify(model).addAttribute(Mockito.eq("restaurantesDestacados"), Mockito.anyList());
    }

    @Test
    void testMostrarHomeSinRestaurantes() {
        List<Restaurante> mockRestaurantes = Collections.emptyList();
        
        Mockito.when(mockRestauranteDAO.findAll()).thenReturn(mockRestaurantes);

        Model model = Mockito.mock(Model.class);
        String result = loginController.mostrarHome(model);

        assertEquals("home", result);
        Mockito.verify(model).addAttribute(Mockito.eq("restaurantesDestacados"), Mockito.anyList());
    }

    // ---------------------------------- TEST HOME REPARTIDOR ---------------------------------------

    @Test
    void testHomeRepartidorConPedidosPendientes() {
        ServicioEntrega mockPedido = Mockito.mock(ServicioEntrega.class);
        List<ServicioEntrega> mockPedidosPendientes = Collections.singletonList(mockPedido);

        Mockito.when(mockServicioEntregaDAO.findByEstado(EstadoPedido.PAGADO)).thenReturn(mockPedidosPendientes);

        Model model = Mockito.mock(Model.class);
        String result = loginController.homeRepartidor(model);

        assertEquals("homeRepartidor", result);
        Mockito.verify(model).addAttribute(Mockito.eq("pedidosPendientes"), Mockito.anyList());
    }

    @Test
    void testHomeRepartidorSinPedidosPendientes() {
        List<ServicioEntrega> mockPedidosPendientes = Collections.emptyList();

        Mockito.when(mockServicioEntregaDAO.findByEstado(EstadoPedido.PAGADO)).thenReturn(mockPedidosPendientes);

        Model model = Mockito.mock(Model.class);
        String result = loginController.homeRepartidor(model);

        assertEquals("homeRepartidor", result);
        Mockito.verify(model).addAttribute(Mockito.eq("pedidosPendientes"), Mockito.anyList());
    }
}