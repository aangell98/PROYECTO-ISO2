package es.uclm.delivery.persistencia;

import es.uclm.delivery.dominio.entidades.ItemMenu;

import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public class ItemMenuDAO extends EntidadDAO<ItemMenu> {

    public ItemMenuDAO() {
        super(ItemMenu.class);
    }

    public Optional<ItemMenu> findById(Long id) {
        return Optional.ofNullable(entityManager.find(ItemMenu.class, id));
    }

    public int eliminarItemMenuPorId(Long platoId) {
        Optional<ItemMenu> plato = findById(platoId);
        return plato.map(this::delete).orElse(0); // Retorna 1 si es éxito, 0 si falla
    }
}