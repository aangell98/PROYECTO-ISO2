package es.uclm.delivery.persistencia;

import es.uclm.delivery.dominio.entidades.Restaurante;
import es.uclm.delivery.dominio.entidades.Usuario;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class RestauranteDAO extends EntidadDAO<Restaurante> {

    public RestauranteDAO() {
        super(Restaurante.class);
    }

    public Optional<Restaurante> findByUsuario(Usuario usuario) {
        try {
            return Optional.ofNullable(entityManager.createQuery(
                    "SELECT r FROM Restaurante r WHERE r.usuario = :usuario", Restaurante.class)
                    .setParameter("usuario", usuario)
                    .getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
