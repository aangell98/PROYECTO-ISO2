package es.uclm.delivery.presentacion;

import es.uclm.delivery.dominio.entidades.*;

public class IUPedido {

	/**
	 * 
	 * @param pedido
	 * @param item
	 */
	public void añadirItemMenu(Pedido pedido, ItemMenu item) {
		// TODO - implement IUPedido.a�adirItemMenu
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param pedido
	 * @param item
	 */
	public void eliinarItemMenu(Pedido pedido, ItemMenu item) {
		// TODO - implement IUPedido.eliinarItemMenu
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param idRestaurante
	 */
	public Pedido comenzarPedido(String idRestaurante) {
		// TODO - implement IUPedido.comenzarPedido
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param pedido
	 */
	public boolean finalizarMenu(Pedido pedido) {
		// TODO - implement IUPedido.finalizarMenu
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param pedido
	 */
	private boolean realizarPago(Pedido pedido) {
		// TODO - implement IUPedido.realizarPago
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param pedido
	 */
	private ServicioEntrega generarServicioEntrega(Pedido pedido) {
		// TODO - implement IUPedido.generarServicioEntrega
		throw new UnsupportedOperationException();
	}

}