package es.uclm.delivery.persistencia;

import es.uclm.delivery.dominio.entidades.Repartidor;
import org.springframework.stereotype.Repository;

@Repository
public class RepartidorDAO extends EntidadDAO<Repartidor> {

    public RepartidorDAO() {
        super(Repartidor.class);
    }
}