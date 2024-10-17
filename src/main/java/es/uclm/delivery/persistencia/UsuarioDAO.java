package es.uclm.delivery.persistencia;

import org.springframework.stereotype.Repository;
import es.uclm.delivery.dominio.entidades.Usuario;

@Repository
public class UsuarioDAO extends EntidadDAO<Usuario> {

    public UsuarioDAO() {
        super(Usuario.class);
    }
}