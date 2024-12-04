package es.uclm.delivery.persistencia;

import es.uclm.delivery.dominio.entidades.Repartidor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

@Repository
public class RepartidorDAO extends EntidadDAO<Repartidor> {

    public RepartidorDAO() {
        super(Repartidor.class);
    }

    @PersistenceContext
    private EntityManager repartidorEntityManager;

    public List<Long> findAllIds() {
        String sql = "SELECT id FROM repartidor";
        Query query = repartidorEntityManager.createNativeQuery(sql);
        List<?> resultList = query.getResultList();
        return resultList.stream()
                .filter(Number.class::isInstance)
                .map(obj -> ((Number) obj).longValue())
                .collect(Collectors.toList());
    }

    public Optional<Repartidor> findByUsername(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username cannot be null");
        }
        return repartidorEntityManager.createQuery(
            "SELECT r FROM Repartidor r WHERE r.usuario.username = :username",
            Repartidor.class)
            .setParameter("username", username)
            .getResultList()
            .stream()
            .findFirst();
    }
}