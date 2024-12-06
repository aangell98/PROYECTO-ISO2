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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class GestorRestaurantes {
    private static final String ERROR = "error";
    private static final String REDIRECT_HOME_RESTAURANT = "redirect:/homeRestaurante";
    private static final String ERROR_MENSAJE = "errorMessage";
    private static final String SUCCESS_MESSAGE = "successMessage";

    @Autowired
    private UsuarioDAO usuarioDAO;

    @Autowired
    private RestauranteDAO restauranteDAO;

    @Autowired
    private CartaMenuDAO cartaMenuDAO;

    @Autowired
    private ItemMenuDAO itemMenuDAO;

    @PostMapping("/eliminarCartaMenu")
    public String eliminarMenu(@RequestParam("menuId") Long menuId, RedirectAttributes redirectAttributes) {
        if (menuId == null) {
            redirectAttributes.addFlashAttribute(ERROR, "ID de menú no proporcionado.");
            return REDIRECT_HOME_RESTAURANT;
        }

        int resultado = cartaMenuDAO.eliminarCartaMenuPorId(menuId);
        if (resultado == 1) {
            redirectAttributes.addFlashAttribute("mensaje", "Menú eliminado con éxito.");
            return REDIRECT_HOME_RESTAURANT;
        } else {
            redirectAttributes.addFlashAttribute(ERROR, "Error al eliminar el menú.");
            return REDIRECT_HOME_RESTAURANT;
        }
    }

    @PostMapping("/eliminarItemMenu")
    public String eliminarItemMenu(@RequestParam("platoId") Long platoId, Model model) {
        try {
            Optional<ItemMenu> itemOpt = itemMenuDAO.findById(platoId);
            if (itemOpt.isPresent()) {
                ItemMenu item = itemOpt.get();
                itemMenuDAO.delete(item);
                model.addAttribute(SUCCESS_MESSAGE, "Plato eliminado correctamente.");
            } else {
                model.addAttribute(ERROR_MENSAJE, "El plato no existe.");
            }
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Clave foránea")) {
                model.addAttribute(ERROR_MENSAJE,
                        "No se puede eliminar el plato porque está asociado a uno o más menús.");
            } else {
                model.addAttribute(ERROR_MENSAJE, "No se pudo eliminar el plato.");
            }
        }
        return REDIRECT_HOME_RESTAURANT;
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
            model.addAttribute(ERROR, "Error: Restaurante no encontrado.");
        }
        return REDIRECT_HOME_RESTAURANT;
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
        return REDIRECT_HOME_RESTAURANT;
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
            model.addAttribute(ERROR, "Error: Restaurante no encontrado.");
        }
        return REDIRECT_HOME_RESTAURANT;
    }

    @PostMapping("/editarCartaMenu")
    public String editarCartaMenu(@ModelAttribute CartaMenu cartaMenu, @RequestParam List<Long> itemsIds) {
        Optional<CartaMenu> cartaExistente = cartaMenuDAO.findById(cartaMenu.getId());
        if (cartaExistente.isPresent()) {
            CartaMenu original = cartaExistente.get();
            original.setNombre(cartaMenu.getNombre());
            original.setDescripcion(cartaMenu.getDescripcion());

            // Inicializar la lista de ítems si es null
            if (original.getItems() == null) {
                original.setItems(new ArrayList<>());
            }

            // Convertir la colección a una lista mutable
            List<ItemMenu> itemsActuales = new ArrayList<>(original.getItems());
            List<ItemMenu> itemsNuevos = itemMenuDAO.findAllById(itemsIds);
            itemsActuales.removeIf(item -> !itemsNuevos.contains(item));

            // Asociar los nuevos platos al menú
            original.setItems(itemsNuevos);
            cartaMenuDAO.update(original);
        }
        return REDIRECT_HOME_RESTAURANT;
    }

    @PostMapping("/editarItemMenu")
    public String editarItemMenu(@ModelAttribute ItemMenu itemMenu, RedirectAttributes redirectAttributes) {
        try {
            if (itemMenu.getPrecio() < 0) {
                throw new IllegalArgumentException("El precio no puede ser negativo");
            }

            Optional<ItemMenu> itemExistente = itemMenuDAO.findById(itemMenu.getId());
            if (itemExistente.isPresent()) {
                ItemMenu original = itemExistente.get();
                original.setNombre(itemMenu.getNombre());
                original.setDescripcion(itemMenu.getDescripcion());
                original.setPrecio(itemMenu.getPrecio());
                itemMenuDAO.update(original);
                redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Plato actualizado correctamente.");
            } else {
                redirectAttributes.addFlashAttribute(ERROR_MENSAJE, "El plato no existe.");
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(ERROR_MENSAJE, e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MENSAJE, "Error al actualizar el plato.");
        }
        return REDIRECT_HOME_RESTAURANT;
    }

    @GetMapping("/homeRestaurante")
    public String showHomeRestaurante(Model model, Principal principal) {
        Optional<Usuario> usuarioOpt = usuarioDAO.encontrarUser(principal.getName());
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            Optional<Restaurante> restauranteOpt = restauranteDAO.findByUsuario(usuario);

            if (restauranteOpt.isPresent()) {
                List<CartaMenu> menus = cartaMenuDAO.findAllByRestaurante(restauranteOpt.get().getId());
                List<ItemMenu> items = itemMenuDAO.obtenerItemsPorRestaurante(restauranteOpt.get().getId());
                // Calcular el precio total de cada menú
                for (CartaMenu menu : menus) {
                    double precioTotal = menu.getItems().stream().mapToDouble(ItemMenu::getPrecio).sum();
                    menu.setPrecioTotal(Math.round(precioTotal * 100.0) / 100.0);
                }

                // Obtener los platos no asignados a ningún menú
                List<ItemMenu> platosNoAsignados = items.stream()
                        .filter(plato -> menus.stream().noneMatch(menu -> menu.getItems().contains(plato)))
                        .toList();

                Restaurante restaurante = restauranteOpt.get();
                model.addAttribute("restaurante", restaurante);
                model.addAttribute("isRestauranteRegistrado", true);
                model.addAttribute("items", items);
                model.addAttribute("menus", menus);
                model.addAttribute("platosNoAsignados", platosNoAsignados);
                model.addAttribute("cartaMenu", new CartaMenu());
                model.addAttribute("itemMenu", new ItemMenu());
            } else {
                model.addAttribute("restaurante", new Restaurante());
                model.addAttribute("isRestauranteRegistrado", false);
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
                Restaurante existingRestaurante = restauranteOpt.get();
                existingRestaurante.setDireccion(restaurante.getDireccion());
                existingRestaurante.setNombre(restaurante.getNombre());
                existingRestaurante.setUsuario(usuario);
                restauranteDAO.update(existingRestaurante);
            }
        }
        return REDIRECT_HOME_RESTAURANT;
    }

    @PostMapping("/crearItemMenu")
    public String crearItemMenu(@ModelAttribute ItemMenu itemMenu, Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            if (itemMenu.getPrecio() < 0) {
                throw new IllegalArgumentException("El precio no puede ser negativo");
            }

            Optional<Usuario> usuarioOpt = usuarioDAO.encontrarUser(principal.getName());
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                Optional<Restaurante> restauranteOpt = restauranteDAO.findByUsuario(usuario);
                if (restauranteOpt.isPresent()) {
                    Restaurante restaurante = restauranteOpt.get();
                    itemMenu.setRestaurante(restaurante); // Relacionar el ItemMenu con el restaurante
                    itemMenuDAO.insert(itemMenu);
                    redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Plato creado correctamente.");
                }
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(ERROR_MENSAJE, e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MENSAJE, "Error al crear el plato.");
        }
        return REDIRECT_HOME_RESTAURANT;
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
        return REDIRECT_HOME_RESTAURANT;
    }
}