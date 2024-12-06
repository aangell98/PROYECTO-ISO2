package es.uclm.delivery.dominio.excepciones;

public class CifradoException extends RuntimeException {
    public CifradoException(String message, Throwable cause) {
        super(message, cause);
    }
}