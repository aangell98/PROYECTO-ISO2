package es.uclm.delivery.presentacion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import es.uclm.delivery.dominio.controladores.GestorLogin;
import es.uclm.delivery.dominio.entidades.Usuario;

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
            log.warn("Fallo de autenticación para el usuario: " + usuario.getUsername());
            return "login";
        }
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute Usuario usuario, Model model) {
        if (gestorLogin.registrar(usuario.getUsername(), usuario.getPassword())) {
            log.info("Usuario registrado: " + usuario.getUsername());
            return "redirect:/login";
        } else {
            model.addAttribute("error", "El usuario ya esxiste");
            log.warn("Intento de registro fallido. El usuario ya existe: " + usuario.getUsername());
            return "register";
        }
    }

    @GetMapping("/home")
    public String showHomePage() {
        return "home";
    }
}