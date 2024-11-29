package es.uclm.delivery.persistencia;


import es.uclm.delivery.dominio.entidades.Pago;

import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public class PagoDAO extends EntidadDAO<Pago> {

    public PagoDAO() {
        super(Pago.class);
    }

}