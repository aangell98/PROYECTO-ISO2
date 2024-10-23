package es.uclm.delivery.dominio.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import java.util.Collection;

@Entity
@Table(name = "cliente")
public class Cliente extends Usuario {

    @ManyToMany
    private Collection<Restaurante> favoritos;

    @OneToMany(mappedBy = "cliente")
    private Collection<Pedido> pedidos;

    @OneToMany
    private Collection<Direccion> direcciones;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellidos")
    private String apellidos;

    @Column(name = "dni")
    private String dni;

    // Getters y setters
    public Collection<Restaurante> getFavoritos() {
        return favoritos;
    }

    public void setFavoritos(Collection<Restaurante> favoritos) {
        this.favoritos = favoritos;
    }

    public Collection<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(Collection<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    public Collection<Direccion> getDirecciones() {
        return direcciones;
    }

    public void setDirecciones(Collection<Direccion> direcciones) {
        this.direcciones = direcciones;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }
}