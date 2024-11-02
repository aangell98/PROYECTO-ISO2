package es.uclm.delivery.presentacion;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import es.uclm.delivery.dominio.controladores.GestorClientes;
import es.uclm.delivery.dominio.entidades.*;
import es.uclm.delivery.persistencia.RestauranteDAO;

@Service
public class IUBusqueda {
	
    @Autowired
    private RestauranteDAO restauranteDAO;

    public List<Restaurante> buscarRestaurantesPorCodigoPostal(String codigoPostal) {
        return restauranteDAO.findByCodigoPostal(codigoPostal);
    }

	/**
	 * 
	 * @param zona
	 * @param textoBusqueda
	 */
	public List<Restaurante> buscar(CodigoPostal zona, String textoBusqueda) {
		// TODO - implement IUBusqueda.buscar
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param idRestaurante
	 */
	public void marcarFavorito(String idRestaurante) {
		// TODO - implement IUBusqueda.marcarFavorito
		throw new UnsupportedOperationException();
	}

}