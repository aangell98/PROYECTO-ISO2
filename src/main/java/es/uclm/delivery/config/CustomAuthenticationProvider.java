package es.uclm.delivery.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import es.uclm.delivery.dominio.controladores.GestorLogin;
import es.uclm.delivery.persistencia.UsuarioDAO;

import java.util.Collections;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private GestorLogin gestorLogin;

    @Autowired
    private UsuarioDAO usuarioDAO; // Para obtener los roles

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        if (gestorLogin.autenticar(username, password)) {
            String role = usuarioDAO.getRole(username);
            UserDetails userDetails = new User(username, password, 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)));

            return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
        } else {
            throw new UsernameNotFoundException("Usuario o contraseña incorrectos");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
