package es.uclm.delivery.dominio.controladores;

import es.uclm.delivery.dominio.entidades.ServicioEntrega;
import es.uclm.delivery.persistencia.RepartidorDAO;
import es.uclm.delivery.persistencia.ServicioEntregaDAO;
import es.uclm.delivery.persistencia.PedidoDAO;
import es.uclm.delivery.dominio.entidades.CodigoPostal;
import es.uclm.delivery.dominio.entidades.EstadoPedido;
import es.uclm.delivery.dominio.entidades.Pedido;
import es.uclm.delivery.dominio.entidades.Repartidor;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/repartidor")
public class GestorRepartos {

    @Autowired
    private ServicioEntregaDAO servicioEntregaDAO;

    @Autowired
    private RepartidorDAO repartidorDAO;

    @Autowired
    private PedidoDAO pedidoDAO;


    @PostMapping("/autoasignar/{pedidoId}")
    public ResponseEntity<?> autoasignarPedido(@PathVariable Long pedidoId) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<Repartidor> repartidorOpt = repartidorDAO.findByUsername(username);

            if (repartidorOpt.isPresent()) {
                Repartidor repartidor = repartidorOpt.get();
                Optional<ServicioEntrega> servicioEntregaOpt = servicioEntregaDAO.findByPedidoId(pedidoId);

                if (servicioEntregaOpt.isPresent()) {
                    ServicioEntrega servicioEntrega = servicioEntregaOpt.get();
                    if (servicioEntrega.getEstado() == EstadoPedido.PAGADO) {
                        servicioEntrega.setRepartidor(repartidor);
                        servicioEntrega.setEstado(EstadoPedido.RECOGIDO);
                        servicioEntregaDAO.update(servicioEntrega);

                        Optional<Pedido> pedidoOpt = pedidoDAO.findById(pedidoId);
                        if (pedidoOpt.isPresent() && repartidorOpt.isPresent()) {
                            Pedido pedido = pedidoOpt.get();
                        
                            pedido.setEstado(EstadoPedido.RECOGIDO);
                            pedidoDAO.update(pedido);
                        }
                        
                        return ResponseEntity.ok("Pedido autoasignado con éxito");
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El pedido no está disponible para autoasignación");
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pedido no encontrado");
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Repartidor no encontrado");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al autoasignar el pedido");
        }
    }
}
