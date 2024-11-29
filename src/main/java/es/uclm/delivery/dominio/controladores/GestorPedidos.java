package es.uclm.delivery.dominio.controladores;

import es.uclm.delivery.persistencia.*;
import es.uclm.delivery.presentacion.IUBusqueda;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private IUBusqueda iuBusqueda;
    @Autowired
    private ItemMenuDAO itemMenuDAO;
    @Autowired
    private CartaMenuDAO cartaMenuDAO;
    @Autowired
    private PedidoDAO pedidoDAO;

    @Autowired
    private PagoDAO pagoDAO;
    @Autowired
    private DireccionDAO direccionDAO;
    @Autowired
    private ServicioEntregaDAO servicioEntregaDAO;
    @Autowired
    private RepartidorDAO repartidorDAO;

    @Autowired
    private ClienteDAO clienteDAO;

    @GetMapping("/realizar_pedido")
    public String realizarPedido(@RequestParam("restauranteId") Long restauranteId, Model model,
            @ModelAttribute("carrito") Carrito carrito) {
        Restaurante restaurante = iuBusqueda.obtenerRestaurante(restauranteId);
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
    public ResponseEntity<?> eliminarDelCarrito(@ModelAttribute("carrito") Carrito carrito,
            @PathVariable Long cartaMenuId) {
        carrito.eliminarItem(cartaMenuId); // Método que elimina el item por ID en el carrito
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
        Long direccionId;
        try {
            direccionId = Long.parseLong((String) requestData.get("direccionId"));
        } catch (NumberFormatException e) {
            logger.error("Error al convertir direccionId a Long", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID de dirección inválido");
        }
        String metodoPago = (String) requestData.get("metodoPago");
        Map<String, String> pagoInfo = (Map<String, String>) requestData.get("pagoInfo");

        if (direccionId == null || metodoPago == null || pagoInfo == null) {
            logger.error("Dirección, método de pago y datos de pago son requeridos");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Dirección, método de pago y datos de pago son requeridos");
        }

        try {
            String username = iuBusqueda.obtenerClienteActual().getUsuario().getUsername();
            Optional<Cliente> clienteOpt = clienteDAO.findByUsername(username);

            if (clienteOpt.isPresent()) {
                Cliente cliente = clienteOpt.get();
                Optional<Direccion> direccionOpt = direccionDAO.findById(direccionId);

                if (direccionOpt.isPresent()) {
                    Direccion direccion = direccionOpt.get();

                    // Crear el pedido
                    Pedido pedido = new Pedido();
                    pedido.setCliente(cliente);
                    pedido.setRestaurante(iuBusqueda.obtenerRestaurante(carrito.getRestauranteId()));
                    pedido.setEstado(EstadoPedido.PAGADO); // Estado inicial del pedido
                    pedido.setFecha(new Date());
                    pedidoDAO.insert(pedido);

                    // Registrar el pago
                    Pago pago = new Pago();
                    pago.setPedido(pedido);
                    pago.setTipo(MetodoPago.valueOf(metodoPago));
                    pago.setFechaTransaccion(new Date());
                    pagoDAO.insert(pago);

                    // Crear servicio de entrega sin asignar repartidor y con estado "PAGADO"
                    ServicioEntrega servicioEntrega = new ServicioEntrega();
                    servicioEntrega.setPedido(pedido);
                    servicioEntrega.setDireccion(direccion);
                    servicioEntrega.setEstado(EstadoPedido.PAGADO); // Estado inicial del servicio
                    servicioEntregaDAO.insert(servicioEntrega);

                    carrito.vaciar();

                    logger.info("Pedido confirmado con éxito y servicio de entrega creado en estado PAGADO");
                    return ResponseEntity.ok("Pedido confirmado y servicio de entrega en proceso");
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Dirección no encontrada");
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente no encontrado");
            }
        } catch (Exception e) {
            logger.error("Error al confirmar el pedido", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al confirmar el pedido");
        }
    }

    @GetMapping("/pedidos_pagados")
    public ResponseEntity<List<Map<String, Object>>> obtenerPedidosPagados() {
        List<Pedido> pedidosPagados = pedidoDAO.findPedidosPagados();
        List<Map<String, Object>> pedidosDetalles = pedidosPagados.stream().map(pedido -> {
            Map<String, Object> detalles = new HashMap<>();
            detalles.put("id", pedido.getId());
            detalles.put("estado", pedido.getEstado().toString());

            Optional<ServicioEntrega> servicioEntregaOpt = servicioEntregaDAO.findByPedidoId(pedido.getId());
            if (servicioEntregaOpt.isPresent()) {
                ServicioEntrega servicioEntrega = servicioEntregaOpt.get();
                String nombre = servicioEntrega.getPedido().getCliente().getNombre();
                String apellidos = servicioEntrega.getPedido().getCliente().getApellidos();
                detalles.put("cliente", nombre + " " + apellidos);
                String nombreRestaurante = servicioEntrega.getPedido().getRestaurante().getNombre();
                String direccionRestaurante = servicioEntrega.getPedido().getRestaurante().getDireccion();
                detalles.put("restaurante", nombreRestaurante + " (" + direccionRestaurante + ")");
                Direccion direccion = servicioEntrega.getDireccion();
                if (direccion != null) {
                    detalles.put("direccion", direccion.getCalle() + ", " + direccion.getCiudad() + ", " + direccion.getCodigoPostal());
                } else {
                    detalles.put("direccion", "Dirección no disponible");
                }
            } else {
                detalles.put("direccion", "Dirección no disponible");
            }
            return detalles;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(pedidosDetalles);
    }
    
    @GetMapping("/pedidos_asignados")
public ResponseEntity<List<Map<String, Object>>> obtenerPedidosAsignados() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    Optional<Repartidor> repartidorOpt = repartidorDAO.findByUsername(username);

    if (repartidorOpt.isPresent()) {
        Repartidor repartidor = repartidorOpt.get();
        List<ServicioEntrega> serviciosEntrega = servicioEntregaDAO.findByRepartidorId(repartidor.getId());
        List<Map<String, Object>> pedidosDetalles = serviciosEntrega.stream().map(servicioEntrega -> {
            Map<String, Object> detalles = new HashMap<>();
            Pedido pedido = servicioEntrega.getPedido();
            detalles.put("id", pedido.getId());
            detalles.put("estado", pedido.getEstado().toString());
            String nombre = servicioEntrega.getPedido().getCliente().getNombre();
            String apellidos = servicioEntrega.getPedido().getCliente().getApellidos();
            detalles.put("cliente", nombre + " " + apellidos);
            String nombreRestaurante = servicioEntrega.getPedido().getRestaurante().getNombre();
            String direccionRestaurante = servicioEntrega.getPedido().getRestaurante().getDireccion();
            detalles.put("restaurante", nombreRestaurante + " (" + direccionRestaurante + ")");
            Direccion direccion = servicioEntrega.getDireccion();
            if (direccion != null) {
                detalles.put("direccion", direccion.getCalle() + ", " + direccion.getCiudad() + ", " + direccion.getCodigoPostal());
            } else {
                detalles.put("direccion", "Dirección no disponible");
            }
            return detalles;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(pedidosDetalles);
    } else {
        return ResponseEntity.status(404).body(null);
    }
}
}