package es.uclm.delivery.persistencia;

import es.uclm.delivery.dominio.entidades.Cliente;

import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public class ClienteDAO extends EntidadDAO<Cliente> {

    public ClienteDAO() {
        super(Cliente.class);
    }

    public Optional<Cliente> findByUsername(String username) {
        return entityManager.createQuery("SELECT c FROM Cliente c WHERE c.usuario.username = :username", Cliente.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }

}