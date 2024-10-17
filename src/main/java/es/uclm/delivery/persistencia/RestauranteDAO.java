package es.uclm.delivery.persistencia;

import org.springframework.stereotype.Repository;
import es.uclm.delivery.dominio.entidades.Restaurante;

@Repository
public class RestauranteDAO extends EntidadDAO<Restaurante> {

    public RestauranteDAO() {
        super(Restaurante.class);
    }
}