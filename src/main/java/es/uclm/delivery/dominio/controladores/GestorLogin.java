package es.uclm.delivery.dominio.controladores;

import org.springframework.stereotype.Service;

@Service
public class GestorLogin {

    public boolean autenticar(String id, String password) {
        // Aquí iría la lógica de autenticación real, por ejemplo, consultando una base de datos
        // Por ahora, usaremos una autenticación simple
        return "admin".equals(id) && "admin".equals(password);
    }
}