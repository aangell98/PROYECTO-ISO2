package es.uclm.delivery.dominio.controladores;

import es.uclm.delivery.persistencia.*;
import es.uclm.delivery.presentacion.IUBusqueda;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(GestorPedidos.class);

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

    @Autowired
    private PedidoDAO pedidoDAO;
    
    @Autowired
    private PagoDAO pagoDAO;

    @GetMapping("/realizar_pedido")
    public String realizarPedido(@RequestParam("restauranteId") Long restauranteId, Model model, @ModelAttribute("carrito") Carrito carrito) {
        Restaurante restaurante = IUBusqueda.obtenerRestaurante(restauranteId);

        // Calcular el precio total de cada carta de menú
        restaurante.getCartasMenu().forEach(cartaMenu -> {
            double precioTotal = cartaMenu.getItems().stream()
                    .mapToDouble(plato -> plato.getPrecio())
                    .sum();
            cartaMenu.setPrecioTotal(precioTotal); // Añade un campo `precioTotal` en la clase CartaMenu si no existe
        });

        carrito.setRestauranteId(restauranteId); // Almacenar el ID del restaurante en el carrito

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

    @GetMapping("/pago")
    public String mostrarPago() {
        return "pago";
    }

    @PostMapping("/confirmar_pedido")
    public ResponseEntity<?> confirmarPedido(@ModelAttribute("carrito") Carrito carrito,
                                             @RequestBody Map<String, Object> requestData) {
        String direccion = (String) requestData.get("direccion");
        String metodoPago = (String) requestData.get("metodoPago");
        Map<String, String> pagoInfo = (Map<String, String>) requestData.get("pagoInfo");

        if (direccion == null || metodoPago == null || pagoInfo == null) {
            logger.error("Dirección, método de pago y datos de pago son requeridos");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dirección, método de pago y datos de pago son requeridos");
        }

        try {
            // Crear y guardar el pedido
            Pedido pedido = new Pedido();
            pedido.setCliente(IUBusqueda.obtenerClienteActual());
            pedido.setRestaurante(IUBusqueda.obtenerRestaurante(carrito.getRestauranteId()));

            // Asegurarse de que los ítems del carrito están gestionados por el EntityManager
            List<ItemMenu> itemsGestionados = new ArrayList<>();
            for (ItemMenu item : carrito.getItems()) {
                itemsGestionados.add(itemMenuDAO.findById(item.getId()).orElseThrow(() -> new RuntimeException("Item no encontrado")));
            }
            pedido.setItems(itemsGestionados);

            pedido.setEstado(EstadoPedido.PEDIDO);
            pedidoDAO.insert(pedido);

            // Crear y guardar el pago
            Pago pago = new Pago();
            pago.setPedido(pedido);
            pago.setTipo(MetodoPago.valueOf(metodoPago));
            pago.setFechaTransaccion(new Date());
            pagoDAO.insert(pago);

            // Limpiar el carrito después de confirmar el pedido
            carrito.vaciar();

            logger.info("Pedido confirmado con éxito");
            return ResponseEntity.ok("Pedido confirmado");
        } catch (Exception e) {
            logger.error("Error al confirmar el pedido", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al confirmar el pedido");
        }
    }
}