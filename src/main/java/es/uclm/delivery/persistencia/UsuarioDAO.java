package es.uclm.delivery.persistencia;

import es.uclm.delivery.dominio.entidades.Usuario;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class UsuarioDAO extends EntidadDAO<Usuario> {

    public UsuarioDAO() {
        super(Usuario.class);
    }

    public Optional<Usuario> encontrarUser(String username) {
        try {
            Usuario usuario = entityManager.createQuery("SELECT u FROM Usuario u WHERE u.username = :username", Usuario.class)
                    .setParameter("username", username)
                    .getSingleResult();
            return Optional.of(usuario);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public String getRole(String username) {
        return entityManager.createQuery("SELECT u.role FROM Usuario u WHERE u.username = :username", String.class)
                .setParameter("username", username)
                .getSingleResult();
    }
}