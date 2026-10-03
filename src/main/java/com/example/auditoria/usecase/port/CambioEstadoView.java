package com.example.auditoria.usecase.port;

import java.time.LocalDateTime;

/** Vista de lectura de una entrada de la bitacora. estadoAnterior es null en el registro inicial. */
public record CambioEstadoView(String estadoAnterior, String estadoNuevo, String detalle,
                               LocalDateTime fecha) {
}
