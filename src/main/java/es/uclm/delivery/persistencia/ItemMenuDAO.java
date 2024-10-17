package es.uclm.delivery.persistencia;

import org.springframework.stereotype.Repository;
import es.uclm.delivery.dominio.entidades.ItemMenu;

@Repository
public class ItemMenuDAO extends EntidadDAO<ItemMenu> {

    public ItemMenuDAO() {
        super(ItemMenu.class);
    }
}