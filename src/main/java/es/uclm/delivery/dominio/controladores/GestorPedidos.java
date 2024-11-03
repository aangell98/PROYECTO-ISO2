package es.uclm.delivery.dominio.controladores;

import es.uclm.delivery.persistencia.*;
import es.uclm.delivery.presentacion.IUBusqueda;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    private PedidoDAO pedidoDAO;

    @Autowired
    private ServicioEntregaDAO servicioEntregaDAO;

    @Autowired
    private IUBusqueda IUBusqueda;

    @Autowired
    private ItemMenuDAO itemMenuDAO;


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
public ResponseEntity<?> agregarAlCarrito(@ModelAttribute("carrito") Carrito carrito, @RequestBody Long itemId) {
    // Buscar el item por ID
    Optional<ItemMenu> itemOpt = itemMenuDAO.findById(itemId);
    if (itemOpt.isPresent()) {
        // Agregar el item al carrito del cliente
        carrito.agregarItem(itemOpt.get()); // Usar el método agregarItem de Carrito
        return ResponseEntity.ok(carrito); // Devuelve el carrito actualizado al frontend
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item no encontrado");
}


}