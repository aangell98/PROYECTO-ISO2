package es.uclm.delivery.presentacion;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.uclm.delivery.dominio.entidades.*;
import es.uclm.delivery.persistencia.PedidoDAO;
import es.uclm.delivery.persistencia.ServicioEntregaDAO;

@Service
public class IUPedido {

    private static final String CLIENTE_ID_NULL_ERROR = "El clienteId no puede ser nulo";

    @Autowired
    private PedidoDAO pedidoDAO;

    @Autowired
    private ServicioEntregaDAO servicioEntregaDAO;

    public List<Pedido> obtenerPedidosEnCurso(Long clienteId) {
        if(clienteId == null) {
            throw new IllegalArgumentException(CLIENTE_ID_NULL_ERROR);
        }
        return pedidoDAO.findPedidosEnCurso(clienteId);
    }

    public Optional<Pedido> obtenerPedidoPorId(Long idPedido) {
        if(idPedido == null) {
            throw new IllegalArgumentException("El idPedido no puede ser nulo");
        }
        return pedidoDAO.buscarporid(idPedido);
    }

    public Optional<ServicioEntrega> obtenerServicioEntregaPorPedido(Long pedidoId) {
        if(pedidoId == null) {
            throw new IllegalArgumentException("El pedidoId no puede ser nulo");
        }
        return servicioEntregaDAO.findByPedidoId(pedidoId);
    }

    public void marcarPedidoComoEntregado(Long pedidoId) {
        Pedido pedido = pedidoDAO.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado: " + pedidoId));
        pedido.setEstado(EstadoPedido.ENTREGADO);
        pedidoDAO.update(pedido);
    }

    public List<Pedido> obtenerPedidosEntregados(Long clienteId) {
        if(clienteId == null) {
            throw new IllegalArgumentException(CLIENTE_ID_NULL_ERROR);
        }
        return pedidoDAO.findPedidosEntregados(clienteId);
    }

    public List<Pedido> obtenerPedidosCancelados(Long clienteId) {
        if (clienteId == null) {
            throw new IllegalArgumentException(CLIENTE_ID_NULL_ERROR);
        }
        return pedidoDAO.findPedidosCancelados(clienteId);
    }
}