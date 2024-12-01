package es.uclm.delivery.persistencia;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import es.uclm.delivery.dominio.entidades.Direccion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class DireccionDAO extends EntidadDAO<Direccion> {

    public DireccionDAO() {
        super(Direccion.class);
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void save(Direccion direccion) {
        if (direccion == null) {
            throw new IllegalArgumentException("La dirección no puede ser nula");
        }
        entityManager.persist(direccion);
    }


    public Direccion findByPedidoId(Long pedidoId) {
        return entityManager
                .createQuery("SELECT d FROM Direccion d WHERE d.pedidoId = :pedidoId", Direccion.class)
                .setParameter("pedidoId", pedidoId)
                .getSingleResult();
    }
    
}
