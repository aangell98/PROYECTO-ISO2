package es.uclm.delivery.persistencia;

import es.uclm.delivery.dominio.entidades.CartaMenu;
import es.uclm.delivery.dominio.entidades.ItemMenu;
import es.uclm.delivery.dominio.entidades.Restaurante;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class CartaMenuDAO extends EntidadDAO<CartaMenu> {

    public CartaMenuDAO() {
        super(CartaMenu.class);
    }

    public List<CartaMenu> findAllByRestauranteId(Long restauranteId) {
        return entityManager.createQuery("SELECT cm FROM CartaMenu cm JOIN cm.restaurante r WHERE r.id = :restauranteId", CartaMenu.class)
                            .setParameter("restauranteId", restauranteId)
                            .getResultList();
    }
    
    public List<CartaMenu> findAllByItem(Long itemId) {
        return entityManager.createQuery("SELECT cm FROM CartaMenu cm JOIN cm.items i WHERE i.id = :itemId", CartaMenu.class)
                            .setParameter("itemId", itemId)
                            .getResultList();
    }
}
