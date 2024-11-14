package es.uclm.delivery.dominio.controladores;

import es.uclm.delivery.dominio.entidades.EstadoPedido;
import es.uclm.delivery.dominio.entidades.Pedido;
import es.uclm.delivery.dominio.entidades.Repartidor;
import es.uclm.delivery.dominio.entidades.ServicioEntrega;
import es.uclm.delivery.persistencia.PedidoDAO;
import es.uclm.delivery.persistencia.RepartidorDAO;
import es.uclm.delivery.persistencia.ServicioEntregaDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Optional;

@RestController
public class GestorRepartos {

    private static final Logger logger = LoggerFactory.getLogger(GestorRepartos.class);

    @Autowired
    private PedidoDAO pedidoDAO;

    @Autowired
    private RepartidorDAO repartidorDAO;

    @Autowired
    private ServicioEntregaDAO servicioEntregaDAO;

    @PostMapping("/autoasignar_pedido")
    public ResponseEntity<String> autoasignarPedido(@RequestParam Long pedidoId, @RequestParam Long repartidorId) {
        logger.info("Intentando autoasignar pedido con ID: {} al repartidor con ID: {}", pedidoId, repartidorId);
        Optional<Pedido> pedidoOpt = pedidoDAO.findById(pedidoId);
        Optional<Repartidor> repartidorOpt = repartidorDAO.findById(repartidorId);

        if (pedidoOpt.isPresent() && repartidorOpt.isPresent()) {
            Pedido pedido = pedidoOpt.get();
            Repartidor repartidor = repartidorOpt.get();

            if (pedido.getEstado() == EstadoPedido.PAGADO) {
                ServicioEntrega servicioEntrega = new ServicioEntrega();
                servicioEntrega.setPedido(pedido);
                servicioEntrega.setRepartidor(repartidor);
                servicioEntrega.setEstado(EstadoPedido.RECOGIDO);

                servicioEntregaDAO.insert(servicioEntrega);

                pedido.setEstado(EstadoPedido.RECOGIDO);
                pedidoDAO.update(pedido);

                logger.info("Pedido con ID: {} autoasignado al repartidor con ID: {}", pedidoId, repartidorId);
                return ResponseEntity.ok("Pedido autoasignado con éxito");
            } else {
                logger.warn("El pedido con ID: {} no está en estado PAGADO", pedidoId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El pedido no está en estado PAGADO");
            }
        } else {
            logger.warn("Pedido o repartidor no encontrado. Pedido ID: {}, Repartidor ID: {}", pedidoId, repartidorId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pedido o repartidor no encontrado");
        }
    }
}