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
import es.uclm.delivery.dominio.controladores.GestorLogin;

@Controller
public class IULogin {

    private static final Logger log = LoggerFactory.getLogger(IULogin.class);

    @Autowired
    private GestorLogin gestorLogin;

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute Usuario usuario, Model model) {
        if (gestorLogin.autenticar(usuario.getUsername(), usuario.getPassword())) {
            log.info("Usuario autenticado: " + usuario.getUsername());
            return "redirect:/home";
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
}