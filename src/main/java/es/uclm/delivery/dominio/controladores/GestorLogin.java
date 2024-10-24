package es.uclm.delivery.dominio.controladores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import es.uclm.delivery.dominio.entidades.Usuario;
import es.uclm.delivery.persistencia.UsuarioDAO;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class GestorLogin {

    private static final Logger log = LoggerFactory.getLogger(GestorLogin.class);

    @Autowired
    private UsuarioDAO usuarioDAO;

    private String cifrarPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean autenticar(String username, String password) {
        Usuario usuario = usuarioDAO.select(username).orElse(null);
        if (usuario != null) {
            log.debug("Usuario encontrado: " + usuario.getUsername());
            if (cifrarPassword(password).equals(usuario.getPassword())) {
                log.info("Usuario autenticado: " + username);
                return true;
            } else {
                log.warn("Contraseña incorrecta para el usuario: " + username);
            }
        } else {
            log.warn("Usuario no encontrado: " + username);
        }
        return false;
    }

    public boolean registrar(String username, String password, String role) {
        if (usuarioDAO.select(username).isPresent()) {
            log.warn("Intento de registro fallido. El usuario ya existe: " + username);
            return false; // El usuario ya existe
        }
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(cifrarPassword(password)); // La contraseña se encripta aquí
        usuario.setRole(role); // Asignar el rol adecuado
        usuarioDAO.insert(usuario);
        log.info("Usuario registrado: " + username + " con rol: " + role);
        return true;
    }
}