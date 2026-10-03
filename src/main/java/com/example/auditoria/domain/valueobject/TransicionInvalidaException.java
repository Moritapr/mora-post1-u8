package com.example.auditoria.domain.valueobject;

/**
 * Se lanza cuando una operacion viola la maquina de estados del hallazgo.
 */
public class TransicionInvalidaException extends RuntimeException {

    public TransicionInvalidaException(EstadoHallazgo origen, EstadoHallazgo destino) {
        super("Transicion invalida: " + origen + " -> " + destino);
    }

    public TransicionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
