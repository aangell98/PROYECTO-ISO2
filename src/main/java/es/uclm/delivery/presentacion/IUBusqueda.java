package es.uclm.delivery.presentacion;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import es.uclm.delivery.dominio.entidades.*;
import es.uclm.delivery.persistencia.ClienteDAO;
import es.uclm.delivery.persistencia.RestauranteDAO;

@Service
public class IUBusqueda {
    
    @Autowired
    private RestauranteDAO restauranteDAO;

    @Autowired
    private ClienteDAO clienteDAO;

    public List<Restaurante> buscarRestaurantesPorCodigoPostal(String codigoPostal) {
        return restauranteDAO.findByCodigoPostal(codigoPostal);
    }

    public void marcarFavorito(Long idRestaurante) {
        Cliente cliente = clienteDAO.findById(1L).orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        Restaurante restaurante = restauranteDAO.findById(idRestaurante).orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));
        
        ClienteFavoritos favorito = new ClienteFavoritos();
        favorito.setCliente(cliente);
        favorito.setRestaurante(restaurante);
        
        cliente.getFavoritos().add(favorito);
        clienteDAO.update(cliente);
    }

    public void desmarcarFavorito(Long idRestaurante) {
        Cliente cliente = clienteDAO.findById(1L).orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        ClienteFavoritos favorito = cliente.getFavoritos().stream()
            .filter(f -> f.getRestaurante().getId().equals(idRestaurante))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Favorito no encontrado"));
        
        cliente.getFavoritos().remove(favorito);
        clienteDAO.update(cliente);
    }

    public List<Restaurante> listarFavoritos() {
        Cliente cliente = clienteDAO.findById(1L).orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        return cliente.getFavoritos().stream().map(ClienteFavoritos::getRestaurante).toList();
    }

    public Restaurante obtenerRestaurante(Long restauranteId) {
        Restaurante restaurante = restauranteDAO.findById(restauranteId).orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));
        List<CartaMenu> cartasMenu = restauranteDAO.findCartasMenuByRestauranteId(restauranteId);
        if (restaurante.getCartasMenu() == null) {
            restaurante.setCartasMenu(new ArrayList<>()); // Inicializar la colección cartasMenu
        }
        restaurante.getCartasMenu().clear();
        restaurante.getCartasMenu().addAll(cartasMenu);
        return restaurante;
    }

    public List<Restaurante> obtenerRestaurantesDestacados() {
        return restauranteDAO.obtenerRestaurantesAleatorios(4).stream()
                .filter(r -> r.getNombre() != null && r.getDireccion() != null)
                .collect(Collectors.toList());
    }

    public Cliente obtenerClienteActual() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        return clienteDAO.findByUsername(username).orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }
}