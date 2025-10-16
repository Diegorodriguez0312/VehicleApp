package com.garaje.exceptions;

/**
 * Excepción personalizada para violaciones de reglas de negocio.
 *
 * Se utiliza en la capa de fachada (VehiculoFacade) para indicar que
 * una operación no se puede completar porque se incumple una regla
 * de negocio. Esta excepción debe ser capturada en la capa de
 * presentación (Servlet/JSP) para mostrar mensajes amigables al usuario.
 */
public class BusinessException extends Exception {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
