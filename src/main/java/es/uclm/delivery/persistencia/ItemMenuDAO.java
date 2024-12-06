package es.uclm.delivery.persistencia;

import es.uclm.delivery.dominio.entidades.ItemMenu;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ItemMenuDAO extends EntidadDAO<ItemMenu> {

    public ItemMenuDAO() {
        super(ItemMenu.class);
    }

    @Override
    public Optional<ItemMenu> findById(Long id) {
        return Optional.ofNullable(entityManager.find(ItemMenu.class, id));
    }

    public int eliminarItemMenuPorId(Long platoId) {
        Optional<ItemMenu> plato = findById(platoId);
        return plato.map(this::delete).orElse(0); // Retorna 1 si es éxito, 0 si falla
    }

    @Transactional
    public List<ItemMenu> obtenerItemsPorRestaurante(Long restauranteId) {
        String queryStr = "SELECT im FROM ItemMenu im WHERE im.restaurante.id = :restauranteId";
        TypedQuery<ItemMenu> query = entityManager.createQuery(queryStr, ItemMenu.class);
        query.setParameter("restauranteId", restauranteId);
        return query.getResultList();
    }
}