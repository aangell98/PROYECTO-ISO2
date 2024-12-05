package es.uclm.delivery.persistencia;

import es.uclm.delivery.dominio.entidades.CartaMenu;
import es.uclm.delivery.dominio.entidades.Restaurante;
import es.uclm.delivery.dominio.entidades.Usuario;
import org.springframework.stereotype.Repository;

import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.PersistenceException;

import java.util.List;
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
        } catch (NoResultException | NonUniqueResultException e) {
            // Si no hay resultado o hay más de uno, retornamos Optional.empty()
            return Optional.empty();
        } catch (PersistenceException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public List<Restaurante> findByCodigoPostal(String codigoPostal) {
        try {
            return entityManager.createQuery(
                    "SELECT r FROM Restaurante r WHERE r.direccion LIKE :codigoPostal", Restaurante.class)
                    .setParameter("codigoPostal", "%" + codigoPostal + "%")
                    .getResultList();
        } catch (PersistenceException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public List<CartaMenu> findCartasMenuByRestauranteId(Long restauranteId) {
        return entityManager.createQuery(
                "SELECT cm FROM CartaMenu cm WHERE cm.restaurante.id = :restauranteId", CartaMenu.class)
                .setParameter("restauranteId", restauranteId)
                .getResultList();
    }

    public List<Restaurante> obtenerRestaurantesAleatorios(int cantidad) {
        try {
            return entityManager.createQuery(
                    "SELECT r FROM Restaurante r ORDER BY RAND()", Restaurante.class)
                    .setMaxResults(cantidad)
                    .getResultList();
        } catch (PersistenceException e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
