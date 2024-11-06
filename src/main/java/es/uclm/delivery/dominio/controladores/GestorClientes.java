package es.uclm.delivery.dominio.controladores;

import es.uclm.delivery.dominio.entidades.*;
import es.uclm.delivery.persistencia.PedidoDAO;
import es.uclm.delivery.persistencia.RepartidorDAO;
import es.uclm.delivery.presentacion.IUBusqueda;
import es.uclm.delivery.presentacion.IUPedido;
import es.uclm.delivery.dominio.controladores.GestorPedidos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class GestorClientes {

    private static final Logger logger = LoggerFactory.getLogger(GestorLogin.class);

    @Autowired
    private IUBusqueda IUBusqueda;

    @Autowired
    private GestorPedidos gestorPedidos;

    @Autowired
    private IUPedido iuPedido;

    @Autowired
    private PedidoDAO pedidoDAO;

    @Autowired
    private RepartidorDAO repartidorDAO;

    @GetMapping("/buscar_restaurantes")
    public List<Restaurante> buscarRestaurantes(@RequestParam("codigoPostal") String codigoPostal) {
        System.out.println("Código postal recibido: " + codigoPostal);
        List<Restaurante> restaurantes = IUBusqueda.buscarRestaurantesPorCodigoPostal(codigoPostal);
        System.out.println("Restaurantes encontrados: " + restaurantes.size());
        return restaurantes;
    }

    @PostMapping("/agregar_favorito")
    public void agregarFavorito(@RequestParam("idRestaurante") Long idRestaurante) {
        IUBusqueda.marcarFavorito(idRestaurante);
    }

    @PostMapping("/eliminar_favorito")
    public void eliminarFavorito(@RequestParam("idRestaurante") Long idRestaurante) {
        IUBusqueda.desmarcarFavorito(idRestaurante);
    }

    @GetMapping("/listar_favoritos")
    public List<Restaurante> listarFavoritos() {
        return IUBusqueda.listarFavoritos();
    }

    @GetMapping("/obtener_restaurante")
    public Restaurante obtenerRestaurante(@RequestParam("restauranteId") Long restauranteId) {
        return IUBusqueda.obtenerRestaurante(restauranteId);
    }

    @GetMapping("/listar_pedidos_curso")
    public ResponseEntity<?> obtenerPedidosEnCurso() {
        Cliente cliente = IUBusqueda.obtenerClienteActual();
        logger.info("Obteniendo pedidos en curso para el cliente: {}", cliente.getId());
        List<Pedido> pedidosEnCurso = iuPedido.obtenerPedidosEnCurso(cliente.getId());

        if (!pedidosEnCurso.isEmpty()) {
            List<Map<String, Object>> pedidosDetalles = pedidosEnCurso.stream().map(pedido -> {
                Map<String, Object> detalles = new HashMap<>();
                detalles.put("id", pedido.getId());
                detalles.put("restaurante", pedido.getRestaurante().getNombre());
                detalles.put("estado", pedido.getEstado());
                Optional<ServicioEntrega> servicioEntregaOpt = iuPedido.obtenerServicioEntregaPorPedido(pedido.getId());
                if (servicioEntregaOpt.isPresent()) {
                    ServicioEntrega servicioEntrega = servicioEntregaOpt.get();
                    detalles.put("repartidor", servicioEntrega.getRepartidor().getNombre() + " " + servicioEntrega.getRepartidor().getApellidos());
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
    public ResponseEntity<?> confirmarRecepcion(@RequestBody Map<String, Long> payload) {
        Long idPedido = payload.get("idPedido");
        logger.info("Confirmando recepción del pedido: {}", idPedido);
        Optional<Pedido> pedidoOpt = iuPedido.obtenerPedidoPorId(idPedido);
        if (pedidoOpt.isPresent()) {
            Pedido pedido = pedidoOpt.get();
            pedido.setEstado(EstadoPedido.ENTREGADO);
            pedidoDAO.update(pedido);  // Asegúrate de tener un método que actualice el pedido en la base de datos
            logger.info("Pedido actualizado a ENTREGADO: {}", idPedido);
            return ResponseEntity.ok("Pedido actualizado a ENTREGADO");
        } else {
            logger.warn("Pedido no encontrado: {}", idPedido);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pedido no encontrado");
        }
    }

    @GetMapping("/listar_pedidos_anteriores")
    public ResponseEntity<?> obtenerPedidosAnteriores() {
        Cliente cliente = IUBusqueda.obtenerClienteActual();
        logger.info("Obteniendo pedidos anteriores para el cliente: {}", cliente.getId());
        List<Pedido> pedidosAnteriores = iuPedido.obtenerPedidosEntregados(cliente.getId());

        if (!pedidosAnteriores.isEmpty()) {
            List<Map<String, Object>> pedidosDetalles = pedidosAnteriores.stream().map(pedido -> {
                Map<String, Object> detalles = new HashMap<>();
                detalles.put("id", pedido.getId());
                detalles.put("restaurante", pedido.getRestaurante().getNombre());
                detalles.put("estado", pedido.getEstado());
                Optional<ServicioEntrega> servicioEntregaOpt = iuPedido.obtenerServicioEntregaPorPedido(pedido.getId());
                if (servicioEntregaOpt.isPresent()) {
                    ServicioEntrega servicioEntrega = servicioEntregaOpt.get();
                    detalles.put("repartidor", servicioEntrega.getRepartidor().getNombre() + " " + servicioEntrega.getRepartidor().getApellidos());
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
    public ResponseEntity<?> valorarPedido(@RequestBody Map<String, Object> payload) {
        Long idPedido = Long.valueOf(payload.get("idPedido").toString());
        int valoracion = Integer.parseInt(payload.get("valoracion").toString());
        logger.info("Valorando pedido: {} con valoración: {}", idPedido, valoracion);

        Optional<Pedido> pedidoOpt = iuPedido.obtenerPedidoPorId(idPedido);
        if (pedidoOpt.isPresent()) {
            Pedido pedido = pedidoOpt.get();
            Optional<ServicioEntrega> servicioEntregaOpt = iuPedido.obtenerServicioEntregaPorPedido(idPedido);
            if (servicioEntregaOpt.isPresent()) {
                ServicioEntrega servicioEntrega = servicioEntregaOpt.get();
                Repartidor repartidor = servicioEntrega.getRepartidor();

                // Calcular la nueva eficiencia del repartidor
                double nuevaEficiencia = (repartidor.getEficiencia() + valoracion) / 2;

                repartidor.setEficiencia(nuevaEficiencia);
                repartidorDAO.update(repartidor);

                logger.info("Repartidor {} valorado con éxito. Nueva eficiencia: {}", repartidor.getId(), nuevaEficiencia);
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


}
