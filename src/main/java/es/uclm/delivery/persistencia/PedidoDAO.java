package es.uclm.delivery.persistencia;

import org.springframework.stereotype.Repository;
import es.uclm.delivery.dominio.entidades.Pedido;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class PedidoDAO extends EntidadDAO<Pedido> {

    @PersistenceContext
    private EntityManager entityManager;

    public PedidoDAO() {
        super(Pedido.class);
    }

    @Transactional
    public void save(Pedido pedido) {
        entityManager.persist(pedido);
    }

}