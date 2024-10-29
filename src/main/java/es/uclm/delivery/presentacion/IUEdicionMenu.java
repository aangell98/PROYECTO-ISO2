package es.uclm.delivery.presentacion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.uclm.delivery.dominio.entidades.CartaMenu;
import es.uclm.delivery.dominio.entidades.Cliente;
import es.uclm.delivery.dominio.entidades.Restaurante;
import es.uclm.delivery.dominio.entidades.Usuario;
import es.uclm.delivery.persistencia.UsuarioDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import es.uclm.delivery.dominio.controladores.GestorLogin;
import es.uclm.delivery.dominio.controladores.GestorRestaurantes;

public class IUEdicionMenu {

private static final Logger log = LoggerFactory.getLogger(IULogin.class);

    @Autowired
    private GestorLogin gestorLogin;

    @Autowired
    private UsuarioDAO usuarioDAO;

    @Autowired
    private GestorRestaurantes gestorRestaurantes;

    @GetMapping("/altaMenu")
    public String mostrarFormularioAltaMenu(Model model) {
        model.addAttribute("cartaMenu", new CartaMenu());
        return "altaMenu";
    }
}