package es.uclm.delivery.persistencia;

import es.uclm.delivery.dominio.entidades.CartaMenu;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CartaMenuDAO extends EntidadDAO<CartaMenu> {

    public CartaMenuDAO() {
        super(CartaMenu.class);
    }

    public List<CartaMenu> findAllByRestaurante(Long restauranteId) {
        return entityManager.createQuery("SELECT cm FROM CartaMenu cm WHERE cm.restaurante.id = :restauranteId", CartaMenu.class)
                            .setParameter("restauranteId", restauranteId)
                            .getResultList();
    }
}
