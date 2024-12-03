package es.uclm.delivery.dominio.entidades;

import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Carrito {
    private List<ItemMenu> items;
    private double precioTotal;
    private Long restauranteId; // Añadir campo para almacenar el ID del restaurante

    public Carrito() {
        this.items = new ArrayList<>();
        this.precioTotal = 0.0;
    }

    public void agregarItem(ItemMenu item) {
        if (item.getPrecio() > 0) {
            this.items.add(item);
            this.precioTotal += item.getPrecio();
            redondearPrecioTotal();
        }
    }

    public List<ItemMenu> getItems() {
        return items;
    }

    public double getPrecioTotal() {
        return precioTotal;
    }

    public Long getRestauranteId() {
        return restauranteId;
    }

    public void setRestauranteId(Long restauranteId) {
        this.restauranteId = restauranteId;
    }

    public void eliminarItem(Long itemId) {
        items.removeIf(item -> item.getId().equals(itemId));
        actualizarPrecioTotal();
    }

    public void actualizarPrecioTotal() {
        precioTotal = items.stream().mapToDouble(ItemMenu::getPrecio).sum();
        redondearPrecioTotal();
    }

    public void vaciar() {
        items.clear();
        actualizarPrecioTotal();
    }

    private void redondearPrecioTotal() {
        BigDecimal bd = BigDecimal.valueOf(precioTotal);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        precioTotal = bd.doubleValue();
    }
}