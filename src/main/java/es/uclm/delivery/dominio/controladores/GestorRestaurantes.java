package es.uclm.delivery.dominio.controladores;

import es.uclm.delivery.dominio.entidades.*;
import es.uclm.delivery.persistencia.*;
import es.uclm.delivery.presentacion.IUEdicionMenu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @PostMapping("/eliminarCartaMenu")
    public String eliminarMenu(@RequestParam("menuId") Long menuId, RedirectAttributes redirectAttributes) {
        int resultado = cartaMenuDAO.eliminarCartaMenuPorId(menuId);
        if (resultado == 1) {
            redirectAttributes.addFlashAttribute("mensaje", "Menú eliminado con éxito.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el menú.");
        }
        return "redirect:/homeRestaurante";
    }

    @PostMapping("/eliminarItemMenu")
    public String eliminarItemMenu(@RequestParam Long platoId, Model model) {
        ItemMenu itemMenu = itemMenuDAO.findById(platoId).orElse(null);
        if (itemMenu != null) {
            try {
                int result = itemMenuDAO.delete(itemMenu);
                if (result == 1) {
                    // Éxito, redirigir o mostrar mensaje
                    model.addAttribute("successMessage", "Plato eliminado correctamente.");
                } else {
                    // Error en la eliminación
                    model.addAttribute("errorMessage", "No se pudo eliminar el plato.");
                }
            } catch (Exception e) {
                // Manejo de la excepción
                model.addAttribute("errorMessage",
                        "No se puede eliminar el plato porque está asociado a uno o más menús.");
                e.printStackTrace(); // O puedes registrar el error
            }
        } else {
            model.addAttribute("errorMessage", "El plato no existe.");
        }
        return "redirect:/homeRestaurante"; // O la vista que desees
    }

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

    @PostMapping("/eliminarRestaurante")
    public String eliminarRestaurante(@RequestParam(name = "restauranteId", required = true) Long restauranteId,
            Model model) {
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

            List<CartaMenu> menus = restauranteOpt
                    .map(restaurante -> cartaMenuDAO.findAllByRestaurante(restaurante.getId()))
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
