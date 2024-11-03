package es.uclm.delivery.persistencia;

import org.springframework.stereotype.Repository;

import es.uclm.delivery.dominio.entidades.ServicioEntrega;

@Repository
public class ServicioEntregaDAO extends EntidadDAO<ServicioEntrega> {

    public ServicioEntregaDAO() {
        super(ServicioEntrega.class);
    }

}