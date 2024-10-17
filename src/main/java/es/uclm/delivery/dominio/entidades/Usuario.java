package es.uclm.delivery.dominio.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Usuario {

    @Id
    private String username;
    private String password;

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}