package es.uclm.delivery.persistencia;

import org.springframework.stereotype.Repository;
import es.uclm.delivery.dominio.entidades.Pedido;

@Repository
public class PedidoDAO extends EntidadDAO<Pedido> {

    public PedidoDAO() {
        super(Pedido.class);
    }
}