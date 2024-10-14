package es.uclm.delivery.dominio.controladores;

import es.uclm.delivery.dominio.entidades.Direccion;
import es.uclm.delivery.dominio.entidades.ItemMenu;
import es.uclm.delivery.dominio.entidades.Restaurante;
import es.uclm.delivery.dominio.entidades.TipoItemMenu;
import java.util.List;

public class GestorRestaurantes {

	/**
	 * 
	 * @param nombre
	 * @param cif
	 * @param d
	 */
	public Restaurante registrarRestaurante(String nombre, String cif, Direccion d) {
		// TODO - implement GestorRestaurantes.registrarRestaurante
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param nombre
	 * @param items
	 */
	public void editarCarta(String nombre, List<ItemMenu> items) {
		// TODO - implement GestorRestaurantes.editarCarta
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param nombre
	 * @param precio
	 * @param tipo
	 */
	private ItemMenu crearItem(String nombre, double precio, TipoItemMenu tipo) {
		// TODO - implement GestorRestaurantes.crearItem
		throw new UnsupportedOperationException();
	}

}