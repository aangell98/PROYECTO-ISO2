package es.uclm.delivery.persistencia;

import es.uclm.delivery.dominio.entidades.ItemMenu;
import org.springframework.stereotype.Repository;

@Repository
public class ItemMenuDAO extends EntidadDAO<ItemMenu> {

    public ItemMenuDAO() {
        super(ItemMenu.class);
    }

    public ItemMenu findById(int id) {
        return entityManager.find(ItemMenu.class, id);
    }
}