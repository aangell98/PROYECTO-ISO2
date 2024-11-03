package es.uclm.delivery.presentacion;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
        // Obtener el cliente actual (esto es solo un ejemplo, debes obtener el cliente autenticado)
        Cliente cliente = clienteDAO.findById(1L).orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        Restaurante restaurante = restauranteDAO.findById(idRestaurante).orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));
        
        ClienteFavoritos favorito = new ClienteFavoritos();
        favorito.setCliente(cliente);
        favorito.setRestaurante(restaurante);
        
        cliente.getFavoritos().add(favorito);
        clienteDAO.update(cliente);
    }

    public void desmarcarFavorito(Long idRestaurante) {
        // Obtener el cliente actual (esto es solo un ejemplo, debes obtener el cliente autenticado)
        Cliente cliente = clienteDAO.findById(1L).orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        ClienteFavoritos favorito = cliente.getFavoritos().stream()
            .filter(f -> f.getRestaurante().getId().equals(idRestaurante))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Favorito no encontrado"));
        
        cliente.getFavoritos().remove(favorito);
        clienteDAO.update(cliente);
    }

    public List<Restaurante> listarFavoritos() {
        // Obtener el cliente actual (esto es solo un ejemplo, debes obtener el cliente autenticado)
        Cliente cliente = clienteDAO.findById(1L).orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        return cliente.getFavoritos().stream().map(ClienteFavoritos::getRestaurante).toList();
    }

    public Restaurante obtenerRestaurante(Long restauranteId) {
        Restaurante restaurante = restauranteDAO.findById(restauranteId)
                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));
        
        // Cargar y asignar las cartas de menú del restaurante
        List<CartaMenu> cartasMenu = restauranteDAO.findCartasMenuByRestauranteId(restauranteId);
        restaurante.setCartasMenu(cartasMenu);  // Asigna las cartas de menú al restaurante
        
        return restaurante;
    }

    public Cliente obtenerClienteActual() {
        return clienteDAO.findById(1L).orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }
}