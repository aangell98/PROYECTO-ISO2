package es.uclm.delivery.dominio.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Cliente cliente;

    @ManyToOne
    private Pago pago;

    @OneToMany
    private Collection<ItemMenu> items;

    @ManyToOne
    private Restaurante restaurante;

    @ManyToOne
    private ServicioEntrega entrega;

    @Enumerated(EnumType.STRING)
    private EstadoPedido estado;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Pago getPago() {
        return pago;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }

    public Collection<ItemMenu> getItems() {
        return items;
    }

    public void setItems(Collection<ItemMenu> items) {
        this.items = items;
    }

    public Restaurante getRestaurante() {
        return restaurante;
    }

    public void setRestaurante(Restaurante restaurante) {
        this.restaurante = restaurante;
    }

    public ServicioEntrega getEntrega() {
        return entrega;
    }

    public void setEntrega(ServicioEntrega entrega) {
        this.entrega = entrega;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    // Método add para agregar items al pedido
    public void add(ItemMenu itemMenu) {
        this.items.add(itemMenu);
    }
}