package es.uclm.delivery.persistencia;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import es.uclm.delivery.dominio.entidades.EstadoPedido;
import es.uclm.delivery.dominio.entidades.Pedido;


import jakarta.transaction.Transactional;

@Repository
public class PedidoDAO extends EntidadDAO<Pedido> {


    public PedidoDAO() {
        super(Pedido.class);
    }

    @Transactional
    public void save(Pedido pedido) {
        entityManager.persist(pedido);
    }

    public List<Pedido> findPedidosEnCurso(Long clienteId) {
        return entityManager.createQuery(
            "SELECT p FROM Pedido p WHERE p.cliente.id = :clienteId AND p.estado != :estadoEntregado",
            Pedido.class)
            .setParameter("clienteId", clienteId)
            .setParameter("estadoEntregado", EstadoPedido.ENTREGADO)
            .getResultList();
    }

    public Optional<Pedido> buscarporid(Long id) {
        Pedido pedido = entityManager.find(Pedido.class, id);
        return Optional.ofNullable(pedido);

    }

    public List<Pedido> findPedidosEntregados(Long clienteId) {
        return entityManager.createQuery(
            "SELECT p FROM Pedido p WHERE p.cliente.id = :clienteId AND p.estado = :estadoEntregado",
            Pedido.class)
            .setParameter("clienteId", clienteId)
            .setParameter("estadoEntregado", EstadoPedido.ENTREGADO)
            .getResultList();
    }

    public List<Pedido> findPedidosPagados() {
        return entityManager.createQuery(
            "SELECT p FROM Pedido p WHERE p.estado = :estadoPagado",
            Pedido.class)
            .setParameter("estadoPagado", EstadoPedido.PAGADO)
            .getResultList();
    }

}