package es.uclm.delivery.persistencia;

import es.uclm.delivery.dominio.entidades.Repartidor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

@Repository
public class RepartidorDAO extends EntidadDAO<Repartidor> {

    public RepartidorDAO() {
        super(Repartidor.class);
    }

     @PersistenceContext
    private EntityManager entityManager;

    public List<Long> findAllIds() {
        String sql = "SELECT id FROM repartidor";
        Query query = entityManager.createNativeQuery(sql);
        return query.getResultList();
    }

    public Optional<Repartidor> findByUsername(String username) {
        return entityManager.createQuery(
            "SELECT r FROM Repartidor r WHERE r.usuario.username = :username",
            Repartidor.class)
            .setParameter("username", username)
            .getResultList()
            .stream()
            .findFirst();
    }
}