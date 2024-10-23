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
        if (usuario != null && usuario.getPassword().equals(cifrarPassword(password))) {
            log.info("Usuario autenticado: " + username);
            return true;
        }
        log.warn("Fallo de autenticación para el usuario: " + username);
        return false;
    }

    public boolean registrar(String username, String password) {
        if (usuarioDAO.select(username).isPresent()) {
            log.warn("Intento de registro fallido. El usuario ya existe: " + username);
            return false;
        }
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(username);
        nuevoUsuario.setPassword(cifrarPassword(password));
        usuarioDAO.insert(nuevoUsuario);
        log.info("Usuario registrado exitosamente: " + username);
        return true;
    }
}