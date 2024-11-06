package es.uclm.delivery.dominio.controladores;

import es.uclm.delivery.dominio.entidades.*;
import es.uclm.delivery.persistencia.PedidoDAO;
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

}
