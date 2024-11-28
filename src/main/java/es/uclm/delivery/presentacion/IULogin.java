package es.uclm.delivery.presentacion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import es.uclm.delivery.dominio.entidades.Cliente;
import es.uclm.delivery.dominio.entidades.EstadoPedido;
import es.uclm.delivery.dominio.entidades.Repartidor;
import es.uclm.delivery.dominio.entidades.Restaurante;
import es.uclm.delivery.dominio.entidades.ServicioEntrega;
import es.uclm.delivery.dominio.entidades.Usuario;
import es.uclm.delivery.persistencia.UsuarioDAO;
import es.uclm.delivery.persistencia.RestauranteDAO;
import es.uclm.delivery.persistencia.ServicioEntregaDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import es.uclm.delivery.dominio.controladores.GestorLogin;

@Controller
public class IULogin {
    private static final Logger log = LoggerFactory.getLogger(IULogin.class);

    @Autowired
    private GestorLogin gestorLogin;

    @Autowired
    private UsuarioDAO usuarioDAO;

    @Autowired
    private RestauranteDAO restauranteDAO;

    @Autowired
    private ServicioEntregaDAO servicioEntregaDAO;

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute Usuario usuario, Model model, HttpServletRequest request) {
        if (gestorLogin.autenticar(usuario.getUsername(), usuario.getPassword())) {
            log.info("Usuario autenticado: {}", usuario.getUsername());
            String role = usuarioDAO.getRole(usuario.getUsername());
            log.info("Rol del usuario: {}", role);

            // Asegúrate de que el rol tiene el prefijo "ROLE_"
            if (!role.startsWith("ROLE_")) {
                role = "ROLE_" + role;
            }

            // Manejar la sesión
            HttpSession session = request.getSession();
            session.setAttribute("username", usuario.getUsername());
            session.setAttribute("role", role);
            session.setMaxInactiveInterval(30 * 60); // La sesión expira en 30 minutos de inactividad
            log.info("Sesión creada: {}", session.getId());
            log.info("Atributos de la sesión: username={}, role={}", 
                     session.getAttribute("username"), session.getAttribute("role"));

            // Redirigir según el rol del usuario
            if (role.equals("ROLE_CLIENTE")) {
                return "redirect:/homeCliente";
            } else if (role.equals("ROLE_REPARTIDOR")) {
                return "redirect:/homeRepartidor";
            } else if (role.equals("ROLE_RESTAURANTE")) {
                return "redirect:/homeRestaurante";
            } else {
                model.addAttribute("error", "Rol desconocido");
                return "login";
            }
        } else {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
            return "login";
        }
    }

    @GetMapping("/registroCliente")
    public String showRegistroClienteForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registroCliente";
    }

    @PostMapping("/registroCliente")
    public String registrarCliente(@ModelAttribute Usuario usuario, @ModelAttribute Cliente cliente, Model model) {
        if (gestorLogin.registrarCliente(usuario.getUsername(), usuario.getPassword(), "CLIENTE", 
                                         cliente.getNombre(), cliente.getApellidos(), cliente.getDni())) {
            log.info("Cliente registrado: {}", usuario.getUsername());
            return "redirect:/login";
        } else {
            model.addAttribute("error", "El usuario ya existe");
            return "registroCliente";
        }
    }

    @GetMapping("/registroRepartidor")
    public String showRegistroRepartidorForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registroRepartidor";
    }

    @PostMapping("/registroRepartidor")
    public String registrarRepartidor(@ModelAttribute Usuario usuario, @ModelAttribute Repartidor repartidor, Model model) {
        if (gestorLogin.registrarRepartidor(usuario.getUsername(), usuario.getPassword(), "REPARTIDOR", 
                                            repartidor.getNombre(), repartidor.getApellidos(), repartidor.getDni())) {
            log.info("Repartidor registrado: {}", usuario.getUsername());
            return "redirect:/login";
        } else {
            model.addAttribute("error", "El usuario ya existe");
            return "registroRepartidor";
        }
    }

    @GetMapping("/registroRestaurante")
    public String showRegistroRestauranteForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registroRestaurante";
    }

    @PostMapping("/registroRestaurante")
    public String registrarRestaurante(@ModelAttribute Usuario usuario, @ModelAttribute Restaurante restaurante, Model model) {
        if (gestorLogin.registrarRestaurante(usuario.getUsername(), usuario.getPassword(), "RESTAURANTE", 
                                             restaurante.getNombre(), restaurante.getDireccion())) {
            log.info("Restaurante registrado: {}", usuario.getUsername());
            return "redirect:/login";
        } else {
            model.addAttribute("error", "El usuario ya existe");
            return "registroRestaurante";
        }
    }

    @GetMapping("/home")
    public String mostrarHome(Model model) {
        List<Restaurante> todosLosRestaurantes = restauranteDAO.findAll();
        List<Restaurante> restaurantesDestacados = new ArrayList<>();

        if (!todosLosRestaurantes.isEmpty()) {
            Collections.shuffle(todosLosRestaurantes);
            int maxDestacados = Math.min(4, todosLosRestaurantes.size());
            for (int i = 0; i < maxDestacados; i++) {
                restaurantesDestacados.add(todosLosRestaurantes.get(i));
            }
        }

        model.addAttribute("restaurantesDestacados", restaurantesDestacados);
        return "home";
    }

    @GetMapping("/homeCliente")
    public String homeCliente() {
        return "homeCliente";
    }

    @GetMapping("/homeRepartidor")
    public String homeRepartidor(Model model) {
        List<ServicioEntrega> pedidosPendientes = servicioEntregaDAO.findByEstado(EstadoPedido.PAGADO);
        model.addAttribute("pedidosPendientes", pedidosPendientes);
        return "homeRepartidor";
    }
}
