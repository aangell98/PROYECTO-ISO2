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

    @PostMapping("/eliminarNombreDireccionRestaurante")
    public String eliminarNombreDireccionRestaurante(Principal principal) {
        Optional<Usuario> usuarioOpt = usuarioDAO.encontrarUser(principal.getName());
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            Optional<Restaurante> restauranteOpt = restauranteDAO.findByUsuario(usuario);
            
            if (restauranteOpt.isPresent()) {
                Restaurante restaurante = restauranteOpt.get();
                restaurante.setNombre(null);
                restaurante.setDireccion(null);
                restauranteDAO.update(restaurante);
            }
        }
        return "redirect:/homeRestaurante";
    }

    @PostMapping("/eliminarCartaMenu")
    public String eliminarCartaMenu(@RequestParam(name = "menuId", required = true) Long menuId, Model model) {
        Optional<CartaMenu> cartaMenuOpt = cartaMenuDAO.findById(menuId);
        if (cartaMenuOpt.isPresent()) {
            CartaMenu cartaMenu = cartaMenuOpt.get();
            cartaMenu.setItems(null);  // Desvincula los platos del menú antes de eliminarlo
            cartaMenuDAO.delete(cartaMenu);
            model.addAttribute("message", "Menú eliminado exitosamente.");
        } else {
            model.addAttribute("error", "Error: Menú no encontrado.");
        }
        return "redirect:/homeRestaurante";
    }

    @PostMapping("/eliminarItemMenu")
    public String eliminarItemMenu(@RequestParam(name = "itemId", required = true) Long itemId, Model model) {
        Optional<ItemMenu> itemMenuOpt = itemMenuDAO.findById(itemId);
        if (itemMenuOpt.isPresent()) {
            ItemMenu itemMenu = itemMenuOpt.get();
            itemMenuDAO.delete(itemMenu);
            model.addAttribute("message", "Item eliminado exitosamente.");
        } else {
            model.addAttribute("error", "Error: Item no encontrado.");
        }
        return "redirect:/homeRestaurante";
    }

    @PostMapping("/eliminarRestaurante")
    public String eliminarRestaurante(@RequestParam(name = "restauranteId", required = true) Long restauranteId, Model model) {
        Optional<Restaurante> restauranteOpt = restauranteDAO.findById(restauranteId);
        if (restauranteOpt.isPresent()) {
            Restaurante restaurante = restauranteOpt.get();
            restauranteDAO.delete(restaurante);
            model.addAttribute("message", "Restaurante eliminado exitosamente.");
        } else {
            model.addAttribute("error", "Error: Restaurante no encontrado.");
        }
        return "redirect:/homeRestaurante";
    }

    @GetMapping("/homeRestaurante")
    public String showHomeRestaurante(Model model, Principal principal) {
        Optional<Usuario> usuarioOpt = usuarioDAO.encontrarUser(principal.getName());
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            Optional<Restaurante> restauranteOpt = restauranteDAO.findByUsuario(usuario);
            
            List<CartaMenu> menus = restauranteOpt
                    .map(restaurante -> cartaMenuDAO.findAllByRestauranteId(restaurante.getId()))
                    .orElse(List.of());

            // Agregar log para verificar los valores de menus
            menus.forEach(menu -> System.out.println("Menu encontrado: " + (menu != null ? menu.getId() : "null")));

            if (restauranteOpt.isEmpty() || restauranteOpt.get().getNombre() == null) {
                model.addAttribute("restaurante", new Restaurante());
                model.addAttribute("isRestauranteRegistrado", false);
            } else {
                Restaurante restaurante = restauranteOpt.get();
                model.addAttribute("restaurante", restaurante);
                model.addAttribute("isRestauranteRegistrado", true);
                model.addAttribute("items", itemMenuDAO.findAll());
                model.addAttribute("menus", menus);
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
            Optional<Restaurante> restauranteOpt = restauranteDAO.findByUsuario(usuario);
            
            if (restauranteOpt.isPresent()) {
                // Actualizar el restaurante existente
                Restaurante restauranteExistente = restauranteOpt.get();
                restauranteExistente.setNombre(restaurante.getNombre());
                restauranteExistente.setDireccion(restaurante.getDireccion());
                restauranteDAO.update(restauranteExistente);
            } else {
                // Crear un nuevo restaurante
                restaurante.setUsuario(usuario);
                restauranteDAO.insert(restaurante);
            }
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