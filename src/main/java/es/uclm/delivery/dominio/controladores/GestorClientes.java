package es.uclm.delivery.dominio.controladores;

import es.uclm.delivery.dominio.entidades.*;
import es.uclm.delivery.persistencia.ClienteDAO;
import es.uclm.delivery.persistencia.DireccionDAO;
import es.uclm.delivery.persistencia.PedidoDAO;
import es.uclm.delivery.persistencia.RepartidorDAO;
import es.uclm.delivery.presentacion.IUBusqueda;
import es.uclm.delivery.presentacion.IUPedido;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class GestorClientes {
    private static final Logger logger = LoggerFactory.getLogger(GestorClientes.class);
    private static final String REPARTIDOR = "repartidor";
    private static final String VALORACION_REPARTIDOR = "valoracionRepartidor";
    private static final String RESTAURANTE = "restaurante";
    private static final String ESTADO = "estado"; // Definir constante para "estado"

    @Autowired
    private IUBusqueda iuBusqueda;
    @Autowired
    private IUPedido iuPedido;
    @Autowired
    private PedidoDAO pedidoDAO;
    @Autowired
    private RepartidorDAO repartidorDAO;
    @Autowired
    private DireccionDAO direccionDAO;

    @Autowired
    private ClienteDAO clienteDAO;

    @GetMapping("/buscar_restaurantes")
    public List<Restaurante> buscarRestaurantes(@RequestParam("codigoPostal") String codigoPostal) {
        logger.info("Código postal recibido: {}", codigoPostal);
        List<Restaurante> restaurantes = iuBusqueda.buscarRestaurantesPorCodigoPostal(codigoPostal);
        logger.info("Restaurantes encontrados: {}", restaurantes.size());
        return restaurantes;
    }

    @PostMapping("/agregar_favorito")
    public void agregarFavorito(@RequestParam("idRestaurante") Long idRestaurante) {
        if (idRestaurante != null) {
            iuBusqueda.marcarFavorito(idRestaurante);
        } else {
            logger.warn("El id del restaurante es nulo");
        }
    }

    @PostMapping("/eliminar_favorito")
    public void eliminarFavorito(@RequestParam("idRestaurante") Long idRestaurante) {
        if (idRestaurante != null) {
            iuBusqueda.desmarcarFavorito(idRestaurante);
        } else {
            logger.warn("El id del restaurante es nulo");
        }
    }

    @GetMapping("/listar_favoritos")
    public List<Restaurante> listarFavoritos() {
        return iuBusqueda.listarFavoritos();
    }

    @GetMapping("/obtener_restaurante")
    public Restaurante obtenerRestaurante(@RequestParam("restauranteId") Long restauranteId) {
        return iuBusqueda.obtenerRestaurante(restauranteId);
    }

    @GetMapping("/listar_pedidos_curso")
    public ResponseEntity<Object> obtenerPedidosEnCurso() {
        Cliente cliente = iuBusqueda.obtenerClienteActual();
        logger.info("Obteniendo pedidos en curso para el cliente: {}", cliente.getId());
        List<Pedido> pedidosEnCurso = iuPedido.obtenerPedidosEnCurso(cliente.getId());
        if (!pedidosEnCurso.isEmpty()) {
            List<Map<String, Object>> pedidosDetalles = pedidosEnCurso.stream().map(pedido -> {
                Map<String, Object> detalles = new HashMap<>();
                detalles.put("id", pedido.getId());
                detalles.put(RESTAURANTE, pedido.getRestaurante().getNombre());
                detalles.put(ESTADO, pedido.getEstado());
                Optional<ServicioEntrega> servicioEntregaOpt = iuPedido.obtenerServicioEntregaPorPedido(pedido.getId());
                if (servicioEntregaOpt.isPresent()) {
                    ServicioEntrega servicioEntrega = servicioEntregaOpt.get();
                    Repartidor repartidor = servicioEntrega.getRepartidor();
                    if (repartidor != null) {
                        detalles.put(REPARTIDOR, repartidor.getNombre() + " " + repartidor.getApellidos());
                        detalles.put(VALORACION_REPARTIDOR, repartidor.getEficiencia());
                    } else {
                        detalles.put(REPARTIDOR, "Buscando repartidor...");
                        detalles.put(VALORACION_REPARTIDOR, 0);
                    }
                } else {
                    detalles.put(REPARTIDOR, "Buscando repartidor...");
                    detalles.put(VALORACION_REPARTIDOR, 0);
                }
                return detalles;
            }).toList();
            logger.info("Pedidos en curso encontrados: {}", pedidosDetalles.size());
            return ResponseEntity.ok(pedidosDetalles);
        } else {
            logger.info("No hay pedidos en curso");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No hay pedidos en curso");
        }
    }

    @PostMapping("/confirmar_recepcion")
    public ResponseEntity<Object> confirmarRecepcion(@RequestBody Map<String, Long> payload) {
        Long idPedido = payload.get("idPedido");
        logger.info("Confirmando recepción del pedido: {}", idPedido);
        Optional<Pedido> pedidoOpt = iuPedido.obtenerPedidoPorId(idPedido);
        if (pedidoOpt.isPresent()) {
            Pedido pedido = pedidoOpt.get();
            pedido.setEstado(EstadoPedido.ENTREGADO);
            pedidoDAO.update(pedido);
            logger.info("Pedido actualizado a ENTREGADO: {}", idPedido);
            return ResponseEntity.ok("Pedido actualizado a ENTREGADO");
        } else {
            logger.warn("Pedido no encontrado: {}", idPedido);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pedido no encontrado");
        }
    }

    @GetMapping("/listar_pedidos_anteriores")
    public ResponseEntity<Object> obtenerPedidosAnteriores() {
        Cliente cliente = iuBusqueda.obtenerClienteActual();
        logger.info("Obteniendo pedidos anteriores para el cliente: {}", cliente.getId());
        List<Pedido> pedidosAnteriores = iuPedido.obtenerPedidosEntregados(cliente.getId());
        if (!pedidosAnteriores.isEmpty()) {
            List<Map<String, Object>> pedidosDetalles = pedidosAnteriores.stream().map(pedido -> {
                Map<String, Object> detalles = new HashMap<>();
                detalles.put("id", pedido.getId());
                detalles.put(RESTAURANTE, pedido.getRestaurante().getNombre());
                detalles.put(ESTADO, pedido.getEstado());
                detalles.put("fecha", pedido.getFecha()); // Añadir la fecha del pedido
                Optional<ServicioEntrega> servicioEntregaOpt = iuPedido.obtenerServicioEntregaPorPedido(pedido.getId());
                if (servicioEntregaOpt.isPresent()) {
                    ServicioEntrega servicioEntrega = servicioEntregaOpt.get();
                    detalles.put(REPARTIDOR, servicioEntrega.getRepartidor().getNombre() + " "
                            + servicioEntrega.getRepartidor().getApellidos());
                }
                return detalles;
            }).toList();
            logger.info("Pedidos anteriores encontrados: {}", pedidosDetalles.size());
            return ResponseEntity.ok(pedidosDetalles);
        } else {
            logger.info("No hay pedidos anteriores");
            return ResponseEntity.ok(List.of()); // Devolver una lista vacía en lugar de NO_CONTENT
        }
    }

    @PostMapping("/valorar_pedido")
    public ResponseEntity<Object> valorarPedido(@RequestBody Map<String, Object> payload) {
        Long idPedido = Long.valueOf(payload.get("idPedido").toString());
        int valoracion = Integer.parseInt(payload.get("valoracion").toString());
        logger.info("Valorando pedido: {} con valoración: {}", idPedido, valoracion);
        Optional<Pedido> pedidoOpt = iuPedido.obtenerPedidoPorId(idPedido);
        if (pedidoOpt.isPresent()) {
            Optional<ServicioEntrega> servicioEntregaOpt = iuPedido.obtenerServicioEntregaPorPedido(idPedido);
            if (servicioEntregaOpt.isPresent()) {
                ServicioEntrega servicioEntrega = servicioEntregaOpt.get();
                Repartidor repartidor = servicioEntrega.getRepartidor();
                // Calcular la nueva eficiencia del repartidor
                double nuevaEficiencia = (repartidor.getEficiencia() + valoracion) / 2;
                repartidor.setEficiencia(nuevaEficiencia);
                repartidorDAO.update(repartidor);
                logger.info("Repartidor {} valorado con éxito. Nueva eficiencia: {}", repartidor.getId(),
                        nuevaEficiencia);
                return ResponseEntity.ok("Pedido valorado con éxito");
            } else {
                logger.warn("Servicio de entrega no encontrado para el pedido: {}", idPedido);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Servicio de entrega no encontrado");
            }
        } else {
            logger.warn("Pedido no encontrado: {}", idPedido);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pedido no encontrado");
        }
    }

    @PostMapping("/guardar_direccion")
    public ResponseEntity<Object> guardarDireccion(@RequestBody(required = false) Direccion direccion,
            Principal principal) {
        if (direccion == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dirección inválida");
        }

        String username = principal.getName();
        Optional<Cliente> clienteOpt = clienteDAO.findByUsername(username);

        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            direccion.setCliente(cliente);
            direccionDAO.save(direccion);
            return ResponseEntity.ok(direccion);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente no encontrado");
        }
    }

    @GetMapping("/listar_direcciones")
    public ResponseEntity<List<Direccion>> listarDirecciones(Principal principal) {
        String username = principal.getName();
        Optional<Cliente> clienteOpt = clienteDAO.findByUsername(username);

        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            List<Direccion> direcciones = cliente.getDirecciones().stream().toList();
            return ResponseEntity.ok(direcciones);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/listar_pedidos_cancelados")
    public ResponseEntity<Object> listarPedidosCancelados() {
        Cliente cliente = iuBusqueda.obtenerClienteActual();
        logger.info("Obteniendo pedidos cancelados para el cliente: {}", cliente.getId());
        List<Pedido> pedidosCancelados = iuPedido.obtenerPedidosCancelados(cliente.getId());
        if (!pedidosCancelados.isEmpty()) {
            List<Map<String, Object>> pedidosDetalles = pedidosCancelados.stream().map(pedido -> {
                Map<String, Object> detalles = new HashMap<>();
                detalles.put("id", pedido.getId());
                detalles.put(RESTAURANTE, pedido.getRestaurante().getNombre());
                detalles.put(ESTADO, pedido.getEstado());
                detalles.put("fecha", pedido.getFecha()); // Añadir la fecha del pedido
                return detalles;
            }).toList();
            logger.info("Pedidos cancelados encontrados: {}", pedidosDetalles.size());
            return ResponseEntity.ok(pedidosDetalles);
        } else {
            logger.info("No hay pedidos cancelados");
            return ResponseEntity.ok(List.of()); // Devolver una lista vacía en lugar de NO_CONTENT
        }
    }

}