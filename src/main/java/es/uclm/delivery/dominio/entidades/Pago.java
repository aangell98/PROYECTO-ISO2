package es.uclm.delivery.dominio.entidades;

import java.sql.Date;
import java.util.UUID;

public class Pago {

	Pedido pedido;
	MetodoPago tipo;
	private UUID idTransaccion;
	private Date fechaTransaccion;

}