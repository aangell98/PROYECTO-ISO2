package es.uclm.delivery.persistencia;

import es.uclm.delivery.dominio.entidades.CartaMenu;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    public int eliminarCartaMenuPorId(Long menuId) {
        Optional<CartaMenu> menu = findById(menuId);
        return menu.map(this::delete).orElse(0); // Retorna 1 si es éxito, 0 si falla
    }
}
