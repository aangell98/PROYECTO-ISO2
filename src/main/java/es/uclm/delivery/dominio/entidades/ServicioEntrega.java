package es.uclm.delivery.dominio.entidades;

public class ServicioEntrega {

	Pedido pedido;
	Direccion direccion;
	Repartidor repartidor;
	private java.time.LocalDateTime fechaRecepcion;
	private java.time.LocalDateTime fechaEntrega;

}