package es.uclm.delivery.persistencia;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import es.uclm.delivery.dominio.entidades.Direccion;

import jakarta.persistence.PersistenceContext;

@Repository
public class DireccionDAO extends EntidadDAO<Direccion> {

    public DireccionDAO() {
        super(Direccion.class);
    }


    @Transactional
    public void save(Direccion direccion) {
        entityManager.persist(direccion);
    }

    public Direccion findByPedidoId(Long pedidoId) {
        return entityManager
                .createQuery("SELECT d FROM Direccion d WHERE d.pedidoId = :pedidoId", Direccion.class)
                .setParameter("pedidoId", pedidoId)
                .getSingleResult();
    }
    
}
