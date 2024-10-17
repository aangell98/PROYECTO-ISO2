package es.uclm.delivery.dominio.controladores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import es.uclm.delivery.dominio.entidades.Usuario;
import es.uclm.delivery.persistencia.UsuarioDAO;

@Service
public class GestorLogin {

    private static final Logger log = LoggerFactory.getLogger(GestorLogin.class);

    @Autowired
    private UsuarioDAO usuarioDAO;

    public boolean autenticar(String username, String password) {
        Usuario usuario = usuarioDAO.select(username).orElse(null);
        if (usuario != null && usuario.getPassword().equals(password)) {
            log.info("Usuario autenticado: " + username);
            return true;
        }
        log.warn("Fallo de autenticación para el usuario: " + username);
        return false;
    }

    public boolean registrar(String username, String password) {
        if (usuarioDAO.select(username).isPresent()) {
            log.warn("Intento de registro fallido. El usuario ya existe: " + username);
            return false; // El usuario ya existe
        }
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(password);
        usuarioDAO.insert(usuario);
        log.info("Usuario registrado: " + username);
        return true;
    }
}