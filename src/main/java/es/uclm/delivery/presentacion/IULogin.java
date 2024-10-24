package es.uclm.delivery.presentacion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import es.uclm.delivery.dominio.entidades.Usuario;
import es.uclm.delivery.persistencia.UsuarioDAO;
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


    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute Usuario usuario, Model model, HttpServletRequest request) {
        if (gestorLogin.autenticar(usuario.getUsername(), usuario.getPassword())) {
            log.info("Usuario autenticado: " + usuario.getUsername());
            String role = usuarioDAO.getRole(usuario.getUsername());
            log.info("rol del usuario " + role);

            // Asegúrate que el rol tiene el prefijo "ROLE_"
            if (!role.startsWith("ROLE_")) {
                role = "ROLE_" + role;
            }
            
            // Manejar la sesión
            HttpSession session = request.getSession();
            session.setAttribute("username", usuario.getUsername());
            session.setAttribute("role", role);
            session.setMaxInactiveInterval(30 * 60); // La sesión expira en 30 minutos de inactividad

            log.info("Sesión creada: " + session.getId());
            log.info("Atributos de la sesión: username=" + session.getAttribute("username") + ", role=" + session.getAttribute("role"));
            
            
            if (role.equals("CLIENTE")) {
                return "redirect:/homeCliente";
            } else if (role.equals("REPARTIDOR")) {
                return "redirect:/homeRepartidor";
            } else if (role.equals("RESTAURANTE")) {
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
    public String registrarCliente(@ModelAttribute Usuario usuario, Model model) {
        if (gestorLogin.registrar(usuario.getUsername(), usuario.getPassword(), "CLIENTE")) {
            log.info("Cliente registrado: " + usuario.getUsername());
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
    public String registrarRepartidor(@ModelAttribute Usuario usuario, Model model) {
        if (gestorLogin.registrar(usuario.getUsername(), usuario.getPassword(), "REPARTIDOR")) {
            log.info("Repartidor registrado: " + usuario.getUsername());
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
    public String registrarRestaurante(@ModelAttribute Usuario usuario, Model model) {
        if (gestorLogin.registrar(usuario.getUsername(), usuario.getPassword(), "RESTAURANTE")) {
            log.info("Restaurante registrado: " + usuario.getUsername());
            return "redirect:/login";
        } else {
            model.addAttribute("error", "El usuario ya existe");
            return "registroRestaurante";
        }
    }

    @GetMapping("/home")
    public String showHomePage() {
        return "home";
    }

    @GetMapping("/homeCliente")
    public String homeCliente() {
        return "homeCliente"; 
    }

    @GetMapping("/homeRepartidor")
    public String homeRepartidor() {
        return "homeRepartidor"; 
    }

    @GetMapping("/homeRestaurante")
    public String homeRestaurante() {
        return "homeRestaurante"; 
    }
}