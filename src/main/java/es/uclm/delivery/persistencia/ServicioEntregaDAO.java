package es.uclm.delivery.persistencia;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import es.uclm.delivery.dominio.entidades.ServicioEntrega;

@Repository
public class ServicioEntregaDAO extends EntidadDAO<ServicioEntrega> {

    public ServicioEntregaDAO() {
        super(ServicioEntrega.class);
    }

    public Optional<ServicioEntrega> findByPedidoId(Long pedidoId) {
        return entityManager.createQuery(
            "SELECT s FROM ServicioEntrega s WHERE s.pedido.id = :pedidoId", 
            ServicioEntrega.class)
            .setParameter("pedidoId", pedidoId)
            .getResultStream()
            .findFirst();
    }
    

}