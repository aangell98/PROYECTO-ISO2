package es.uclm.delivery.persistencia;

import es.uclm.delivery.dominio.entidades.Usuario;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.PersistenceException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UsuarioDAO extends EntidadDAO<Usuario> {

    public UsuarioDAO() {
        super(Usuario.class);
    }

    public Optional<Usuario> encontrarUser(String username) {
        try {
            Usuario usuario = entityManager.createQuery(
                    "SELECT u FROM Usuario u WHERE u.username = :username", Usuario.class)
                    .setParameter("username", username)
                    .getSingleResult();
            return Optional.of(usuario);
        } catch (NoResultException | NonUniqueResultException e) {
            // No se encontró ningún resultado o más de uno.
            return Optional.empty();
        } catch (PersistenceException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public String getRole(String username) {
        try {
            return entityManager.createQuery(
                    "SELECT u.role FROM Usuario u WHERE u.username = :username", String.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            // Si no se encuentra el usuario, se puede devolver un rol por defecto o manejar la excepción.
            return "ROLE_NOT_FOUND";
        } catch (PersistenceException e) {
            e.printStackTrace();
            return "ERROR_FETCHING_ROLE";
        }
    }
}
