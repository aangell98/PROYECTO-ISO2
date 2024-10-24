package es.uclm.delivery.dominio.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "restaurante")
public class Restaurante extends Usuario {

    @OneToMany(mappedBy = "restaurante")
    private Collection<Pedido> pedidos;

    @OneToMany(mappedBy = "restaurante")
    private Collection<CartaMenu> cartasMenu;

    @ManyToOne
    private Direccion direccion;

    @Column(name = "nombre")
    private String nombre;

    // Getters y setters
    public Collection<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(Collection<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    public Collection<CartaMenu> getCartasMenu() {
        return cartasMenu;
    }

    public void setCartasMenu(Collection<CartaMenu> cartasMenu) {
        this.cartasMenu = cartasMenu;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
	 * @param idRestaurante
	 */
	public List<ItemMenu> listarMenu(String idRestaurante) {
		// TODO - implement Restaurante.listarMenu
		throw new UnsupportedOperationException();
	}

}