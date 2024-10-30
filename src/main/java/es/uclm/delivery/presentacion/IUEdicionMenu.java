package es.uclm.delivery.presentacion;

import es.uclm.delivery.dominio.entidades.CartaMenu;
import es.uclm.delivery.dominio.entidades.ItemMenu;
import es.uclm.delivery.dominio.entidades.Restaurante;
import es.uclm.delivery.persistencia.CartaMenuDAO;
import es.uclm.delivery.persistencia.ItemMenuDAO;
import es.uclm.delivery.persistencia.RestauranteDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class IUEdicionMenu {

    private static final Logger log = LoggerFactory.getLogger(IUEdicionMenu.class);

    @Autowired
    private RestauranteDAO restauranteDAO;

    @Autowired
    private CartaMenuDAO cartaMenuDAO;

    @Autowired
    public ItemMenuDAO itemMenuDAO;

    public boolean editarRestaurante(Restaurante restaurante) {
        try {
            Optional<Restaurante> restauranteExistente = restauranteDAO.findById(restaurante.getId());
            if (restauranteExistente.isPresent()) {
                Restaurante original = restauranteExistente.get();
                original.setNombre(restaurante.getNombre());
                original.setDireccion(restaurante.getDireccion());
                restauranteDAO.update(original);
                log.info("Restaurante actualizado exitosamente.");
                return true;
            } else {
                log.error("Error: Restaurante no encontrado.");
                return false;
            }
        } catch (Exception e) {
            log.error("Error al actualizar el restaurante", e);
            return false;
        }
    }

    public boolean editarCartaMenu(CartaMenu cartaMenu) {
        try {
            Optional<CartaMenu> cartaExistente = cartaMenuDAO.findById(cartaMenu.getId());
            if (cartaExistente.isPresent()) {
                CartaMenu original = cartaExistente.get();
                original.setNombre(cartaMenu.getNombre());
                original.setDescripcion(cartaMenu.getDescripcion());
                original.setItems(cartaMenu.getItems());
                cartaMenuDAO.update(original);
                log.info("Carta de menú actualizada exitosamente.");
                return true;
            } else {
                log.error("Error: Carta de menú no encontrada.");
                return false;
            }
        } catch (Exception e) {
            log.error("Error al actualizar la carta de menú", e);
            return false;
        }
    }

    public boolean editarItemMenu(ItemMenu itemMenu) {
        try {
            Optional<ItemMenu> itemExistente = itemMenuDAO.findById(itemMenu.getId());
            if (itemExistente.isPresent()) {
                ItemMenu original = itemExistente.get();
                original.setNombre(itemMenu.getNombre());
                original.setDescripcion(itemMenu.getDescripcion());
                original.setPrecio(itemMenu.getPrecio());
                itemMenuDAO.update(original);
                log.info("Plato actualizado exitosamente.");
                return true;
            } else {
                log.error("Error: Plato no encontrado.");
                return false;
            }
        } catch (Exception e) {
            log.error("Error al actualizar el plato", e);
            return false;
        }
    }
}
