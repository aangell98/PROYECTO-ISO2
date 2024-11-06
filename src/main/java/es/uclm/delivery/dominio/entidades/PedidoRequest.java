package es.uclm.delivery.dominio.entidades;

import java.util.List;

public class PedidoRequest {
    private Long restauranteId;
    private List<ItemMenu> items;

    // Getters y setters

    public Long getRestauranteId() {
        return restauranteId;
    }

    public void setRestauranteId(Long restauranteId) {
        this.restauranteId = restauranteId;
    }

    public List<ItemMenu> getItems() {
        return items;
    }

    public void setItems(List<ItemMenu> items) {
        this.items = items;
    }
}