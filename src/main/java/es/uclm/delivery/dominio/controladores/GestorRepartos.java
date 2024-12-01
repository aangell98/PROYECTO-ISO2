package es.uclm.delivery.dominio.controladores;

import es.uclm.delivery.dominio.entidades.ServicioEntrega;
import es.uclm.delivery.persistencia.RepartidorDAO;
import es.uclm.delivery.persistencia.ServicioEntregaDAO;
import es.uclm.delivery.persistencia.PedidoDAO;

import es.uclm.delivery.dominio.entidades.Direccion;
import es.uclm.delivery.dominio.entidades.EstadoPedido;
import es.uclm.delivery.dominio.entidades.Pedido;
import es.uclm.delivery.dominio.entidades.Repartidor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

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
public ResponseEntity<String> autoasignarPedido(@PathVariable Long pedidoId) {
    try {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Repartidor> repartidorOpt = repartidorDAO.findByUsername(username);

        if (repartidorOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Repartidor no encontrado");
        }

        Optional<ServicioEntrega> servicioEntregaOpt = servicioEntregaDAO.findByPedidoId(pedidoId);

        if (servicioEntregaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pedido no encontrado");
        }

        ServicioEntrega servicioEntrega = servicioEntregaOpt.get();

        if (servicioEntrega.getEstado() != EstadoPedido.PAGADO) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El pedido no está disponible para autoasignación");
        }

        Repartidor repartidor = repartidorOpt.get();
        servicioEntrega.setRepartidor(repartidor);
        servicioEntrega.setEstado(EstadoPedido.RECOGIDO);
        servicioEntregaDAO.update(servicioEntrega);

        Optional<Pedido> pedidoOpt = pedidoDAO.findById(pedidoId);
        if (pedidoOpt.isPresent()) {
            Pedido pedido = pedidoOpt.get();
            pedido.setEstado(EstadoPedido.RECOGIDO);
            pedidoDAO.update(pedido);
        }

        return ResponseEntity.ok("Pedido autoasignado con éxito");

    } catch (NullPointerException e) {
        // Captura errores de datos nulos
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error de datos nulos: " + e.getMessage());
    } catch (DataAccessException e) {
        // Captura errores de acceso a la base de datos
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error de acceso a la base de datos: " + e.getMessage());
    } catch (IllegalArgumentException e) {
        // Captura errores de argumentos inválidos, como un pedido que no cumple los requisitos
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error de argumento inválido: " + e.getMessage());
    }
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
                Direccion direccion = servicioEntrega.getDireccion();
                if (direccion != null) {
                    detalles.put("direccion", direccion.getCalle() + ", " + direccion.getCiudad() + ", " + direccion.getCodigoPostal());
                } else {
                    detalles.put("direccion", "Dirección no disponible");
                }
                return detalles;
            }).toList();
            return ResponseEntity.ok(pedidosDetalles);
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }

    @GetMapping("/perfil_repartidor")
    public ResponseEntity<Map<String, Object>> obtenerPerfilRepartidor() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Repartidor> repartidorOpt = repartidorDAO.findByUsername(username);

        if (repartidorOpt.isPresent()) {
            Repartidor repartidor = repartidorOpt.get();
            Map<String, Object> perfil = new HashMap<>();
            perfil.put("nombre", repartidor.getNombre());
            perfil.put("apellidos", repartidor.getApellidos());
            perfil.put("dni", repartidor.getDni());
            perfil.put("valoracionMedia", repartidor.getEficiencia());
            return ResponseEntity.ok(perfil);
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }
    
}
