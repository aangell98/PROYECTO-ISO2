package es.uclm.delivery.dominio.controladores;

import es.uclm.delivery.persistencia.*;
import es.uclm.delivery.presentacion.IUBusqueda;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import org.springframework.web.bind.support.SessionStatus;
import es.uclm.delivery.dominio.entidades.*;

@SessionAttributes("carrito")
@Controller
public class GestorPedidos {
    private static final Logger logger = LoggerFactory.getLogger(GestorPedidos.class);
    private static final String RESTAURANTE = "restaurante";
    private static final String CLIENTE_NO_ENCONTRADO = "Cliente no encontrado";
    private static final String DIRECCION = "direccion";
    private static final String DIRECCION_NO_DISPONIBLE = "Dirección no disponible";

    @ModelAttribute("carrito")
    public Carrito crearCarrito() {
        return new Carrito(); // Crea un nuevo carrito si no existe en la sesión
    }

    @PostMapping("/finalizarPedido")
    public String finalizarPedido(SessionStatus status) {
        // Lógica para finalizar el pedido
        status.setComplete(); // Marca la sesión como completada
        return "redirect:/pedidoCompletado";
    }

    @Autowired
    public IUBusqueda iuBusqueda;
    @Autowired
    public ItemMenuDAO itemMenuDAO;
    @Autowired
    public CartaMenuDAO cartaMenuDAO;
    @Autowired
    public PedidoDAO pedidoDAO;

    @Autowired
    public PagoDAO pagoDAO;
    @Autowired
    public DireccionDAO direccionDAO;
    @Autowired
    public ServicioEntregaDAO servicioEntregaDAO;
    @Autowired
    public RepartidorDAO repartidorDAO;

    @Autowired
    public ClienteDAO clienteDAO;

    @GetMapping("/realizar_pedido")
    public String realizarPedido(@RequestParam("restauranteId") Long restauranteId, Model model,
            @ModelAttribute("carrito") Carrito carrito) {
        Restaurante restaurante = iuBusqueda.obtenerRestaurante(restauranteId);
        // Calcular el precio total de cada carta de menú
        restaurante.getCartasMenu().forEach(cartaMenu -> {
            double precioTotal = cartaMenu.getItems().stream()
                    .mapToDouble(ItemMenu::getPrecio)
                    .sum();
            precioTotal = Math.round(precioTotal * 100.0) / 100.0; // Aproximar a dos decimales
            cartaMenu.setPrecioTotal(precioTotal); // Añade un campo `precioTotal` en la clase CartaMenu si no existe
        });
        carrito.setRestauranteId(restauranteId); // Almacenar el ID del restaurante en el carrito
        model.addAttribute(RESTAURANTE, restaurante);
        return "realizarPedido";
    }

    @PostMapping("/agregar_al_carrito")
    public ResponseEntity<Object> agregarAlCarrito(@ModelAttribute("carrito") Carrito carrito,
            @RequestBody Map<String, Long> requestData) {
        Long menuId = requestData.get("id");
        Optional<CartaMenu> menuOpt = cartaMenuDAO.findById(menuId);
        if (menuOpt.isPresent()) {
            CartaMenu menu = menuOpt.get();
            menu.getItems().forEach(carrito::agregarItem);
            carrito.actualizarPrecioTotal();
            return ResponseEntity.ok(carrito);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Menú no encontrado");
    }

    @DeleteMapping("/eliminar_del_carrito/{cartaMenuId}")
    public ResponseEntity<Object> eliminarDelCarrito(@ModelAttribute("carrito") Carrito carrito,
            @PathVariable Long cartaMenuId) {
        carrito.eliminarItem(cartaMenuId); // Método que elimina el item por ID en el carrito
        return ResponseEntity.ok(carrito); // Devuelve el carrito actualizado al frontend
    }

    @DeleteMapping("/limpiar_carrito")
    public ResponseEntity<Object> limpiarCarrito(@ModelAttribute("carrito") Carrito carrito) {
        carrito.vaciar(); // Método que elimina todos los ítems del carrito
        return ResponseEntity.ok(carrito); // Devuelve el carrito vacío
    }

    @GetMapping("/pago")
    public String mostrarPago() {
        return "pago";
    }

    @PostMapping("/confirmar_pedido")
    public ResponseEntity<Object> confirmarPedido(@ModelAttribute("carrito") Carrito carrito,
            @RequestBody Map<String, Object> requestData) {
        Long direccionId;

        try {
            direccionId = Long.parseLong(requestData.get("direccionId").toString());
        } catch (NumberFormatException e) {
            logger.error("Error al convertir direccionId a Long", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID de dirección inválido");
        }

        String metodoPago = (String) requestData.get("metodoPago");
        Map<String, Object> pagoInfo = (Map<String, Object>) requestData.get("pagoInfo");

        if (direccionId == null || metodoPago == null || pagoInfo == null || pagoInfo.isEmpty()) {
            logger.error("Dirección, método de pago y datos de pago son requeridos");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Dirección, método de pago y datos de pago son requeridos");
        }

        try {
            Cliente clienteActual = iuBusqueda.obtenerClienteActual();
            if (clienteActual == null || clienteActual.getUsuario() == null) {
                logger.error(CLIENTE_NO_ENCONTRADO);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CLIENTE_NO_ENCONTRADO);
            }
            String username = clienteActual.getUsuario().getUsername();
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
                    pedido.setEstado(EstadoPedido.PAGADO);
                    pedido.setFecha(new Date());
                    pedidoDAO.insert(pedido);

                    // Registrar el pago
                    Pago pago = new Pago();
                    pago.setPedido(pedido);
                    pago.setTipo(MetodoPago.valueOf(metodoPago));
                    pago.setFechaTransaccion(new Date());
                    pagoDAO.insert(pago);

                    // Crear servicio de entrega
                    ServicioEntrega servicioEntrega = new ServicioEntrega();
                    servicioEntrega.setPedido(pedido);
                    servicioEntrega.setDireccion(direccion);
                    servicioEntrega.setEstado(EstadoPedido.PAGADO);
                    servicioEntregaDAO.insert(servicioEntrega);

                    carrito.vaciar();

                    logger.info("Pedido confirmado con éxito y servicio de entrega creado en estado PAGADO");
                    return ResponseEntity.ok("Pedido confirmado y servicio de entrega en proceso");
                } else {
                    logger.error("Dirección no encontrada");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Dirección no encontrada");
                }
            } else {
                logger.error(CLIENTE_NO_ENCONTRADO);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CLIENTE_NO_ENCONTRADO);
            }
        } catch (Exception e) {
            logger.error("Error al confirmar el pedido: ", e);
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
                detalles.put(RESTAURANTE, nombreRestaurante + " (" + direccionRestaurante + ")");
                Direccion direccion = servicioEntrega.getDireccion();
                if (direccion != null) {
                    detalles.put(DIRECCION,
                            direccion.getCalle() + ", " + direccion.getCiudad() + ", " + direccion.getCodigoPostal());
                } else {
                    detalles.put(DIRECCION, DIRECCION_NO_DISPONIBLE);
                }
            } else {
                detalles.put(DIRECCION, DIRECCION_NO_DISPONIBLE);
            }
            return detalles;
        }).toList();
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
                detalles.put(RESTAURANTE, nombreRestaurante + " (" + direccionRestaurante + ")");
                Direccion direccion = servicioEntrega.getDireccion();
                if (direccion != null) {
                    detalles.put(DIRECCION,
                            direccion.getCalle() + ", " + direccion.getCiudad() + ", " + direccion.getCodigoPostal());
                } else {
                    detalles.put(DIRECCION, DIRECCION_NO_DISPONIBLE);
                }
                return detalles;
            }).toList();
            return ResponseEntity.ok(pedidosDetalles);
        } else {
            return ResponseEntity.status(200).body(null);
        }
    }
}