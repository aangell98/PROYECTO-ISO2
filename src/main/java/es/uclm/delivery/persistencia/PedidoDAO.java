package es.uclm.delivery.persistencia;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import es.uclm.delivery.dominio.entidades.EstadoPedido;
import es.uclm.delivery.dominio.entidades.Pedido;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class PedidoDAO extends EntidadDAO<Pedido> {

    private static final String CLIENTE_ID_PARAM = "clienteId";
    private static final String BASE_QUERY = "SELECT p FROM Pedido p WHERE p.cliente.id = :";

    @PersistenceContext
    private EntityManager pedidoEntityManager;

    public PedidoDAO() {
        super(Pedido.class);
    }

    @Transactional
    public void save(Pedido pedido) {
        if (pedido == null) {
            throw new IllegalArgumentException("Pedido cannot be null");
        }
        pedidoEntityManager.persist(pedido);
    }

    public List<Pedido> findPedidosEnCurso(Long clienteId) {
        return pedidoEntityManager.createQuery(
            BASE_QUERY + CLIENTE_ID_PARAM + " AND p.estado NOT IN (:estadoEntregado, :estadoCancelado)",
            Pedido.class)
            .setParameter(CLIENTE_ID_PARAM, clienteId)
            .setParameter("estadoEntregado", EstadoPedido.ENTREGADO)
            .setParameter("estadoCancelado", EstadoPedido.CANCELADO)
            .getResultList();
    }

    public Optional<Pedido> buscarporid(Long id) {
        Pedido pedido = pedidoEntityManager.find(Pedido.class, id);
        return Optional.ofNullable(pedido);
    }

    public List<Pedido> findPedidosEntregados(Long clienteId) {
        return pedidoEntityManager.createQuery(
            BASE_QUERY + CLIENTE_ID_PARAM + " AND p.estado = :estadoEntregado",
            Pedido.class)
            .setParameter(CLIENTE_ID_PARAM, clienteId)
            .setParameter("estadoEntregado", EstadoPedido.ENTREGADO)
            .getResultList();
    }

    public List<Pedido> findPedidosPagados() {
        return pedidoEntityManager.createQuery(
            "SELECT p FROM Pedido p WHERE p.estado = :estadoPagado",
            Pedido.class)
            .setParameter("estadoPagado", EstadoPedido.PAGADO)
            .getResultList();
    }

    public List<Pedido> findPedidosCancelados(Long clienteId) {
        return pedidoEntityManager.createQuery(
            BASE_QUERY + CLIENTE_ID_PARAM + " AND p.estado = :estadoCancelado",
            Pedido.class)
            .setParameter(CLIENTE_ID_PARAM, clienteId)
            .setParameter("estadoCancelado", EstadoPedido.CANCELADO)
            .getResultList();
    }
}