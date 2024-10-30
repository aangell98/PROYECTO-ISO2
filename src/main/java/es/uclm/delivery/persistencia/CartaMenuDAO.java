package es.uclm.delivery.persistencia;

import es.uclm.delivery.dominio.entidades.CartaMenu;
import org.springframework.stereotype.Repository;

@Repository
public class CartaMenuDAO extends EntidadDAO<CartaMenu> {

    public CartaMenuDAO() {
        super(CartaMenu.class);
    }
}