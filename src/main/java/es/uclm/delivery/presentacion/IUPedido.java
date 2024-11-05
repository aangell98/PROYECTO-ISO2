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

    @Autowired
    private PedidoDAO pedidoDAO;

    @Autowired
    private ServicioEntregaDAO servicioEntregaDAO;

    public List<Pedido> obtenerPedidosEnCurso(Long clienteId) {
        return pedidoDAO.findPedidosEnCurso(clienteId);
    }

	public Optional<Pedido> obtenerPedidoPorId(Long idPedido) {
        return pedidoDAO.findById(idPedido);
    }

    public Optional<ServicioEntrega> obtenerServicioEntregaPorPedido(Long pedidoId) {
        return servicioEntregaDAO.findByPedidoId(pedidoId);
    }
}
