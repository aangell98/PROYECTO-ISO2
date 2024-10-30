package es.uclm.delivery.dominio.controladores;

import es.uclm.delivery.dominio.entidades.*;
import es.uclm.delivery.persistencia.*;
import es.uclm.delivery.presentacion.IUEdicionMenu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class GestorRestaurantes {

    @Autowired
    private UsuarioDAO usuarioDAO;

    @Autowired
    private RestauranteDAO restauranteDAO;

    @Autowired
    private CartaMenuDAO cartaMenuDAO;

    @Autowired
    private ItemMenuDAO itemMenuDAO;

    @Autowired
    private IUEdicionMenu iuEdicionMenu;

    @PostMapping("/editarRestaurante")
public String editarRestaurante(@ModelAttribute Restaurante restaurante, Model model) {
    Optional<Restaurante> restauranteExistente = restauranteDAO.findById(restaurante.getId());
    if (restauranteExistente.isPresent()) {
        Restaurante original = restauranteExistente.get();
        original.setNombre(restaurante.getNombre());
        original.setDireccion(restaurante.getDireccion());
        restauranteDAO.update(original);
        model.addAttribute("message", "Restaurante actualizado exitosamente.");
    } else {
        model.addAttribute("error", "Error: Restaurante no encontrado.");
    }
    return "redirect:/homeRestaurante";
}

    @PostMapping("/editarCartaMenu")
    public String editarCartaMenu(@ModelAttribute CartaMenu cartaMenu, @RequestParam List<Long> itemsIds) {
        Optional<CartaMenu> cartaExistente = cartaMenuDAO.findById(cartaMenu.getId());
        if (cartaExistente.isPresent()) {
            CartaMenu original = cartaExistente.get();
            original.setNombre(cartaMenu.getNombre());
            original.setDescripcion(cartaMenu.getDescripcion());
            original.setItems(itemMenuDAO.findAllById(itemsIds));
            cartaMenuDAO.update(original);
        }
        return "redirect:/homeRestaurante";
    }

    @PostMapping("/editarItemMenu")
    public String editarItemMenu(@ModelAttribute ItemMenu itemMenu) {
        Optional<ItemMenu> itemExistente = itemMenuDAO.findById(itemMenu.getId());
        if (itemExistente.isPresent()) {
            ItemMenu original = itemExistente.get();
            original.setNombre(itemMenu.getNombre());
            original.setDescripcion(itemMenu.getDescripcion());
            original.setPrecio(itemMenu.getPrecio());
            itemMenuDAO.update(original);
        }
        return "redirect:/homeRestaurante";
    }

    @GetMapping("/homeRestaurante")
    public String showHomeRestaurante(Model model, Principal principal) {
        Optional<Usuario> usuarioOpt = usuarioDAO.encontrarUser(principal.getName());
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            Optional<Restaurante> restauranteOpt = restauranteDAO.findByUsuario(usuario);

            if (restauranteOpt.isEmpty()) {
                model.addAttribute("restaurante", new Restaurante());
                model.addAttribute("isRestauranteRegistrado", false);
            } else {
                Restaurante restaurante = restauranteOpt.get();
                model.addAttribute("restaurante", restaurante);
                model.addAttribute("isRestauranteRegistrado", true);
                model.addAttribute("items", itemMenuDAO.findAll());
                model.addAttribute("menus", cartaMenuDAO.findAllByRestaurante(restaurante.getId()));
                model.addAttribute("cartaMenu", new CartaMenu());
                model.addAttribute("itemMenu", new ItemMenu());
            }
        }
        return "homeRestaurante";
    }

    @PostMapping("/crearRestaurante")
    public String crearRestaurante(@ModelAttribute Restaurante restaurante, Principal principal) {
        Optional<Usuario> usuarioOpt = usuarioDAO.encontrarUser(principal.getName());
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            restaurante.setUsuario(usuario);
            restauranteDAO.insert(restaurante);
        }
        return "redirect:/homeRestaurante";
    }

    @PostMapping("/crearItemMenu")
    public String crearItemMenu(@ModelAttribute ItemMenu itemMenu) {
        itemMenuDAO.insert(itemMenu);
        return "redirect:/homeRestaurante";
    }

    @PostMapping("/crearCartaMenu")
    public String crearCartaMenu(@ModelAttribute CartaMenu cartaMenu, @RequestParam List<Long> itemsIds,
            Principal principal) {
        Optional<Usuario> usuarioOpt = usuarioDAO.encontrarUser(principal.getName());
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            Optional<Restaurante> restauranteOpt = restauranteDAO.findByUsuario(usuario);
            if (restauranteOpt.isPresent()) {
                cartaMenu.setRestaurante(restauranteOpt.get());
                List<ItemMenu> items = itemMenuDAO.findAllById(itemsIds);
                cartaMenu.setItems(items);
                cartaMenuDAO.insert(cartaMenu);
            }
        }
        return "redirect:/homeRestaurante";
    }
}
