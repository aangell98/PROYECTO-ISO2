package es.uclm.delivery.dominio.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Column;
import java.util.Collection;

@Entity
@Table(name = "repartidor")
public class Repartidor extends Usuario {

    @OneToMany(mappedBy = "repartidor")
    private Collection<ServicioEntrega> servicios;

    @ManyToMany
    private Collection<CodigoPostal> zonas;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellidos")
    private String apellidos;

    @Column(name = "dni")
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
}