package es.uclm.delivery.persistencia;

import es.uclm.delivery.dominio.entidades.Cliente;
import org.springframework.stereotype.Repository;

@Repository
public class ClienteDAO extends EntidadDAO<Cliente> {

    public ClienteDAO() {
        super(Cliente.class);
    }

}