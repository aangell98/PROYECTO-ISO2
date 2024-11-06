package es.uclm.delivery.dominio.entidades;

import jakarta.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "carta_menu")
public class CartaMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @Column(name = "precio_total", nullable = false)
    private double precioTotal;

    @ManyToOne
    @JoinColumn(name = "restaurante_id")
    private Restaurante restaurante;

    @ManyToMany
    @JoinTable(
        name = "carta_menu_item",
        joinColumns = @JoinColumn(name = "carta_menu_id"),
        inverseJoinColumns = @JoinColumn(name = "item_menu_id")
    )
    private Collection<ItemMenu> items;

    // Getters y setters

    public double getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(double precioTotal) {
        this.precioTotal = precioTotal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Restaurante getRestaurante() {
        return restaurante;
    }

    public void setRestaurante(Restaurante restaurante) {
        this.restaurante = restaurante;
    }

    public Collection<ItemMenu> getItems() {
        return items;
    }

    public void setItems(Collection<ItemMenu> items) {
        this.items = items;
    }
}