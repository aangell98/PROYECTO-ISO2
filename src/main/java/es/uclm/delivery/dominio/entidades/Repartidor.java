package es.uclm.delivery.dominio.entidades;

import jakarta.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "repartidor")
public class Repartidor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToMany(mappedBy = "repartidor")
    private Collection<ServicioEntrega> servicios;

    @ManyToMany
    private Collection<CodigoPostal> zonas;

    @OneToOne
    @JoinColumn(name = "username", referencedColumnName = "username")
    private Usuario usuario;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "apellidos", nullable = false)
    private String apellidos;

    @Column(name = "dni", nullable = false, unique = true)
    private String dni;

    @Column(name = "eficiencia")
    private double eficiencia;

    // Getters y setters
    public Collection<ServicioEntrega> getServicios() {
        return servicios;
    }

    public void setServicios(Collection<ServicioEntrega> servicios) {
        this.servicios = servicios;
    }

    public Collection<CodigoPostal> getZonas() {
        return zonas;
    }

    public void setZonas(Collection<CodigoPostal> zonas) {
        this.zonas = zonas;
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

    public double getEficiencia() {
        return eficiencia;
    }

    public void setEficiencia(double eficiencia) {
        this.eficiencia = eficiencia;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}