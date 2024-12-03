package es.uclm.delivery.presentacion;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

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

    private static final String USERNAME_CLIENTE = "cliente1";
    private static final String PASSWORD_CORRECTO = "password123";
    private static final String PASSWORD_INCORRECTO = "wrongpassword";
    private static final String ROLE_CLIENTE = "CLIENTE";
    private static final String ROLE_REPARTIDOR = "REPARTIDOR";
    private static final String ROLE_RESTAURANTE = "RESTAURANTE";
    private static final String ROLE_DESCONOCIDO = "UNKNOWN_ROLE";
    private static final String NOMBRE_CLIENTE = "John";
    private static final String APELLIDO_CLIENTE = "Doe";
    private static final String DNI_CLIENTE = "12345678A";
    private static final String NOMBRE_REPARTIDOR = "Pedro";
    private static final String APELLIDO_REPARTIDOR = "Gomez";
    private static final String DNI_REPARTIDOR = "87654321B";
    private static final String NOMBRE_RESTAURANTE = "Restaurante El Buen Comer";
    private static final String DIRECCION_RESTAURANTE = "Calle Ficticia 123";

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
        Usuario mockUsuario = crearMockUsuario(USERNAME_CLIENTE, PASSWORD_CORRECTO);

        // Mocking successful authentication
        Mockito.when(mockGestorLogin.autenticar(USERNAME_CLIENTE, PASSWORD_CORRECTO)).thenReturn(true);
        Mockito.when(mockUsuarioDAO.getRole(USERNAME_CLIENTE)).thenReturn(ROLE_CLIENTE);

        Model model = Mockito.mock(Model.class);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        Mockito.when(request.getSession()).thenReturn(session);

        String result = loginController.login(mockUsuario, model, request);

        assertEquals("redirect:/homeCliente", result);
    }

    @Test
    void testLoginUsuarioIncorrecto() {
        Usuario mockUsuario = crearMockUsuario(USERNAME_CLIENTE, PASSWORD_INCORRECTO);

        // Mocking failed authentication
        Mockito.when(mockGestorLogin.autenticar(USERNAME_CLIENTE, PASSWORD_INCORRECTO)).thenReturn(false);

        Model model = Mockito.mock(Model.class);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        String result = loginController.login(mockUsuario, model, request);

        assertEquals("login", result);
    }

    @Test
    void testLoginUsuarioConRolDesconocido() {
        Usuario mockUsuario = crearMockUsuario(USERNAME_CLIENTE, PASSWORD_CORRECTO);

        // Mocking successful authentication with unknown role
        Mockito.when(mockGestorLogin.autenticar(USERNAME_CLIENTE, PASSWORD_CORRECTO)).thenReturn(true);
        Mockito.when(mockUsuarioDAO.getRole(USERNAME_CLIENTE)).thenReturn(ROLE_DESCONOCIDO);

        Model model = Mockito.mock(Model.class);
        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        HttpSession mockSession = Mockito.mock(HttpSession.class);
        Mockito.when(mockRequest.getSession()).thenReturn(mockSession);

        String result = loginController.login(mockUsuario, model, mockRequest);

        assertEquals("login", result);
        Mockito.verify(mockSession, Mockito.atLeastOnce()).setAttribute(Mockito.anyString(), Mockito.any());
    }

    @Test
    void testLoginUsuarioSinPrefijoRole() {
        Usuario mockUsuario = crearMockUsuario(USERNAME_CLIENTE, PASSWORD_CORRECTO);

        // Mocking successful authentication with role not starting with "ROLE_"
        Mockito.when(mockGestorLogin.autenticar(USERNAME_CLIENTE, PASSWORD_CORRECTO)).thenReturn(true);
        Mockito.when(mockUsuarioDAO.getRole(USERNAME_CLIENTE)).thenReturn(ROLE_CLIENTE);

        Model model = Mockito.mock(Model.class);
        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        HttpSession mockSession = Mockito.mock(HttpSession.class);
        Mockito.when(mockRequest.getSession()).thenReturn(mockSession);

        String result = loginController.login(mockUsuario, model, mockRequest);

        assertEquals("redirect:/homeCliente", result);
        Mockito.verify(mockSession, Mockito.atLeastOnce()).setAttribute(Mockito.anyString(), Mockito.any());
    }

    // ---------------------------------- TEST REGISTRO CLIENTE -----------------------------------

    @Test
    void testRegistrarClienteUsuarioExistente() {
        Usuario mockUsuario = crearMockUsuario(USERNAME_CLIENTE, PASSWORD_CORRECTO);
        Cliente mockCliente = crearMockCliente();

        // Mocking registration failure due to existing username
        Mockito.when(mockGestorLogin.registrarCliente(USERNAME_CLIENTE, PASSWORD_CORRECTO, ROLE_CLIENTE, NOMBRE_CLIENTE, APELLIDO_CLIENTE, DNI_CLIENTE)).thenReturn(false);

        Model model = Mockito.mock(Model.class);
        String result = loginController.registrarCliente(mockUsuario, mockCliente, model);

        assertEquals("registroCliente", result);
    }

    // ---------------------------------- TEST REGISTRO REPARTIDOR -----------------------------------

    @Test
    void testRegistrarRepartidorUsuarioExistente() {
        Usuario mockUsuario = crearMockUsuario("repartidor1", PASSWORD_CORRECTO);
        Repartidor mockRepartidor = crearMockRepartidor();

        // Mocking registration failure due to existing username
        Mockito.when(mockGestorLogin.registrarRepartidor("repartidor1", PASSWORD_CORRECTO, ROLE_REPARTIDOR, NOMBRE_REPARTIDOR, APELLIDO_REPARTIDOR, DNI_REPARTIDOR)).thenReturn(false);

        Model model = Mockito.mock(Model.class);
        String result = loginController.registrarRepartidor(mockUsuario, mockRepartidor, model);

        assertEquals("registroRepartidor", result);
    }

    // ---------------------------------- TEST REGISTRO RESTAURANTE -----------------------------------

    @Test
    void testRegistrarRestauranteUsuarioExistente() {
        Usuario mockUsuario = crearMockUsuario("restaurante1", PASSWORD_CORRECTO);
        Restaurante mockRestaurante = crearMockRestaurante();

        // Mocking registration failure due to existing username
        Mockito.when(mockGestorLogin.registrarRestaurante("restaurante1", PASSWORD_CORRECTO, ROLE_RESTAURANTE, NOMBRE_RESTAURANTE, DIRECCION_RESTAURANTE)).thenReturn(false);

        Model model = Mockito.mock(Model.class);
        String result = loginController.registrarRestaurante(mockUsuario, mockRestaurante, model);

        assertEquals("registroRestaurante", result);
    }

    // ---------------------------------- TEST HOME -----------------------------------------------

    @Test
    void testMostrarHomeConRestaurantes() {
        Restaurante mockRestaurante1 = crearMockRestaurante();
        Restaurante mockRestaurante2 = crearMockRestaurante();
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

    // Métodos auxiliares para crear mocks

    private Usuario crearMockUsuario(String username, String password) {
        Usuario mockUsuario = Mockito.mock(Usuario.class);
        Mockito.when(mockUsuario.getUsername()).thenReturn(username);
        Mockito.when(mockUsuario.getPassword()).thenReturn(password);
        return mockUsuario;
    }

    private Cliente crearMockCliente() {
        Cliente mockCliente = Mockito.mock(Cliente.class);
        Mockito.when(mockCliente.getNombre()).thenReturn(NOMBRE_CLIENTE);
        Mockito.when(mockCliente.getApellidos()).thenReturn(APELLIDO_CLIENTE);
        Mockito.when(mockCliente.getDni()).thenReturn(DNI_CLIENTE);
        return mockCliente;
    }

    private Repartidor crearMockRepartidor() {
        Repartidor mockRepartidor = Mockito.mock(Repartidor.class);
        Mockito.when(mockRepartidor.getNombre()).thenReturn(NOMBRE_REPARTIDOR);
        Mockito.when(mockRepartidor.getApellidos()).thenReturn(APELLIDO_REPARTIDOR);
        Mockito.when(mockRepartidor.getDni()).thenReturn(DNI_REPARTIDOR);
        return mockRepartidor;
    }

    private Restaurante crearMockRestaurante() {
        Restaurante mockRestaurante = Mockito.mock(Restaurante.class);
        Mockito.when(mockRestaurante.getNombre()).thenReturn(NOMBRE_RESTAURANTE);
        Mockito.when(mockRestaurante.getDireccion()).thenReturn(DIRECCION_RESTAURANTE);
        return mockRestaurante;
    }

    // Nuevos métodos de prueba para cubrir las líneas de código especificadas en IULogin

@Test
void testShowLoginForm() {
    Model model = Mockito.mock(Model.class);

    String result = loginController.showLoginForm(model);

    assertEquals("login", result);
    verify(model).addAttribute(eq("usuario"), any(Usuario.class));
}

@Test
void testLoginUsuarioRepartidor() {
    Usuario mockUsuario = crearMockUsuario(USERNAME_CLIENTE, PASSWORD_CORRECTO);

    // Mocking successful authentication
    Mockito.when(mockGestorLogin.autenticar(USERNAME_CLIENTE, PASSWORD_CORRECTO)).thenReturn(true);
    Mockito.when(mockUsuarioDAO.getRole(USERNAME_CLIENTE)).thenReturn(ROLE_REPARTIDOR);

    Model model = Mockito.mock(Model.class);
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpSession session = Mockito.mock(HttpSession.class);
    Mockito.when(request.getSession()).thenReturn(session);

    String result = loginController.login(mockUsuario, model, request);

    assertEquals("redirect:/homeRepartidor", result);
}

@Test
void testLoginUsuarioRestaurante() {
    Usuario mockUsuario = crearMockUsuario(USERNAME_CLIENTE, PASSWORD_CORRECTO);

    // Mocking successful authentication
    Mockito.when(mockGestorLogin.autenticar(USERNAME_CLIENTE, PASSWORD_CORRECTO)).thenReturn(true);
    Mockito.when(mockUsuarioDAO.getRole(USERNAME_CLIENTE)).thenReturn(ROLE_RESTAURANTE);

    Model model = Mockito.mock(Model.class);
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpSession session = Mockito.mock(HttpSession.class);
    Mockito.when(request.getSession()).thenReturn(session);

    String result = loginController.login(mockUsuario, model, request);

    assertEquals("redirect:/homeRestaurante", result);
}

@Test
void testShowRegistroClienteForm() {
    Model model = Mockito.mock(Model.class);

    String result = loginController.showRegistroClienteForm(model);

    assertEquals("registroCliente", result);
    verify(model).addAttribute(eq("usuario"), any(Usuario.class));
}

@Test
void testRegistrarClienteExitoso() {
    Usuario mockUsuario = crearMockUsuario(USERNAME_CLIENTE, PASSWORD_CORRECTO);
    Cliente mockCliente = crearMockCliente();

    Mockito.when(mockGestorLogin.registrarCliente(USERNAME_CLIENTE, PASSWORD_CORRECTO, "CLIENTE", 
                                                  NOMBRE_CLIENTE, APELLIDO_CLIENTE, DNI_CLIENTE)).thenReturn(true);

    Model model = Mockito.mock(Model.class);
    String result = loginController.registrarCliente(mockUsuario, mockCliente, model);

    assertEquals("redirect:/login", result);
}

@Test
void testShowRegistroRepartidorForm() {
    Model model = Mockito.mock(Model.class);

    String result = loginController.showRegistroRepartidorForm(model);

    assertEquals("registroRepartidor", result);
    verify(model).addAttribute(eq("usuario"), any(Usuario.class));
}

@Test
void testRegistrarRepartidorExitoso() {
    Usuario mockUsuario = crearMockUsuario("repartidor1", PASSWORD_CORRECTO);
    Repartidor mockRepartidor = crearMockRepartidor();

    Mockito.when(mockGestorLogin.registrarRepartidor("repartidor1", PASSWORD_CORRECTO, "REPARTIDOR", 
                                                     NOMBRE_REPARTIDOR, APELLIDO_REPARTIDOR, DNI_REPARTIDOR)).thenReturn(true);

    Model model = Mockito.mock(Model.class);
    String result = loginController.registrarRepartidor(mockUsuario, mockRepartidor, model);

    assertEquals("redirect:/login", result);
}

@Test
void testShowRegistroRestauranteForm() {
    Model model = Mockito.mock(Model.class);

    String result = loginController.showRegistroRestauranteForm(model);

    assertEquals("registroRestaurante", result);
    verify(model).addAttribute(eq("usuario"), any(Usuario.class));
}

@Test
void testRegistrarRestauranteExitoso() {
    Usuario mockUsuario = crearMockUsuario("restaurante1", PASSWORD_CORRECTO);
    Restaurante mockRestaurante = crearMockRestaurante();

    Mockito.when(mockGestorLogin.registrarRestaurante("restaurante1", PASSWORD_CORRECTO, "RESTAURANTE", 
                                                      NOMBRE_RESTAURANTE, DIRECCION_RESTAURANTE)).thenReturn(true);

    Model model = Mockito.mock(Model.class);
    String result = loginController.registrarRestaurante(mockUsuario, mockRestaurante, model);

    assertEquals("redirect:/login", result);
}
}