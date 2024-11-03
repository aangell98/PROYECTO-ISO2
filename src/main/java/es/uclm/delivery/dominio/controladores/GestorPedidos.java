package es.uclm.delivery.dominio.controladores;

import es.uclm.delivery.persistencia.*;
import es.uclm.delivery.presentacion.IUBusqueda;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import es.uclm.delivery.dominio.entidades.*;

@SessionAttributes("carrito")

@Controller
public class GestorPedidos {

    @ModelAttribute("carrito")
    public Carrito crearCarrito() {
        return new Carrito(); // Crea un nuevo carrito si no existe en la sesión
    }

    @Autowired
    private IUBusqueda IUBusqueda;

    @Autowired
    private ItemMenuDAO itemMenuDAO;

    @Autowired
    private CartaMenuDAO cartaMenuDAO;

    @GetMapping("/realizar_pedido")
    public String realizarPedido(@RequestParam("restauranteId") Long restauranteId, Model model) {
        Restaurante restaurante = IUBusqueda.obtenerRestaurante(restauranteId);

        // Calcular el precio total de cada carta de menú
        restaurante.getCartasMenu().forEach(cartaMenu -> {
            double precioTotal = cartaMenu.getItems().stream()
                    .mapToDouble(plato -> plato.getPrecio())
                    .sum();
            cartaMenu.setPrecioTotal(precioTotal); // Añade un campo `precioTotal` en la clase CartaMenu si no existe
        });

        model.addAttribute("restaurante", restaurante);
        return "realizarPedido";
    }

    @PostMapping("/agregar_al_carrito")
    public ResponseEntity<?> agregarAlCarrito(@ModelAttribute("carrito") Carrito carrito,
            @RequestBody Map<String, Long> requestData) {
        Long menuId = requestData.get("id"); // Obtener el ID del menú completo desde el JSON
        Optional<CartaMenu> menuOpt = cartaMenuDAO.findById(menuId);

        if (menuOpt.isPresent()) {
            // Agregar todos los ítems del menú al carrito
            CartaMenu menu = menuOpt.get();
            menu.getItems().forEach(carrito::agregarItem); // Agrega cada ítem al carrito
            carrito.actualizarPrecioTotal(); // Asegúrate de actualizar el precio total del carrito

            return ResponseEntity.ok(carrito); // Devuelve el carrito actualizado al frontend
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Menú no encontrado");
    }

    @DeleteMapping("/eliminar_del_carrito/{cartaMenuId}")
public ResponseEntity<?> eliminarDelCarrito(@ModelAttribute("carrito") Carrito carrito, @PathVariable Long cartaMenuId) {
    carrito.eliminarItem(cartaMenuId);  // Método que elimina el item por ID en el carrito
    return ResponseEntity.ok(carrito); // Devuelve el carrito actualizado al frontend
}

    @DeleteMapping("/limpiar_carrito")
    public ResponseEntity<?> limpiarCarrito(@ModelAttribute("carrito") Carrito carrito) {
        carrito.vaciar(); // Método que elimina todos los ítems del carrito
        return ResponseEntity.ok(carrito); // Devuelve el carrito vacío
    }

}