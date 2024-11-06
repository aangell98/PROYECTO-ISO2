package es.uclm.delivery.dominio.entidades;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collection;

@Entity
@Table(name = "restaurante")
public class Restaurante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "direccion", nullable = false)
    private String direccion;

    @OneToMany(mappedBy = "restaurante", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Collection<CartaMenu> cartasMenu;

    @OneToMany(mappedBy = "restaurante", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Collection<ItemMenu> itemMenu;

    @OneToMany(mappedBy = "restaurante", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Collection<ClienteFavoritos> favoritos;

    @OneToOne
    @JoinColumn(name = "username", referencedColumnName = "username")
    @JsonIgnore
    private Usuario usuario;

    // Getters y setters

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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Collection<CartaMenu> getCartasMenu() {
        return cartasMenu;
    }

    public void setCartasMenu(Collection<CartaMenu> cartasMenu) {
        this.cartasMenu = cartasMenu;
    }

    public Collection<ClienteFavoritos> getFavoritos() {
        return favoritos;
    }

    public void setFavoritos(Collection<ClienteFavoritos> favoritos) {
        this.favoritos = favoritos;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Collection<ItemMenu> getItemMenu() {
        return itemMenu;
    }

    public void setItemMenu(Collection<ItemMenu> itemMenu) {
        this.itemMenu = itemMenu;
    }
}