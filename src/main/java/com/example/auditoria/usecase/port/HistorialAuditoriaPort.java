package com.example.auditoria.usecase.port;

import com.example.auditoria.domain.valueobject.EstadoHallazgo;
import com.example.auditoria.domain.valueobject.HallazgoId;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Puerto de salida hacia la bitacora append-only de cambios de estado.
 * Solo expone agregar y leer: no existe operacion de modificar ni borrar.
 */
public interface HistorialAuditoriaPort {

    void registrarCambio(HallazgoId hallazgoId, EstadoHallazgo estadoAnterior,
                         EstadoHallazgo estadoNuevo, String detalle, LocalDateTime fecha);

    List<CambioEstadoView> obtenerHistorial(HallazgoId hallazgoId);
}
