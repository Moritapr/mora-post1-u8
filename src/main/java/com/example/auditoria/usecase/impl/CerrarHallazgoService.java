package com.example.auditoria.usecase.impl;

import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.domain.valueobject.EstadoHallazgo;
import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.usecase.CerrarHallazgoUseCase;
import com.example.auditoria.usecase.HallazgoNoEncontradoException;
import com.example.auditoria.usecase.port.HallazgoRepositoryPort;
import com.example.auditoria.usecase.port.HistorialAuditoriaPort;
import com.example.auditoria.usecase.port.UnidadDeTrabajoPort;

import java.time.Clock;
import java.time.LocalDateTime;

public class CerrarHallazgoService implements CerrarHallazgoUseCase {

    private final HallazgoRepositoryPort repositorio;
    private final HistorialAuditoriaPort historial;
    private final UnidadDeTrabajoPort unidadDeTrabajo;
    private final Clock reloj;

    public CerrarHallazgoService(HallazgoRepositoryPort repositorio, HistorialAuditoriaPort historial,
                                 UnidadDeTrabajoPort unidadDeTrabajo, Clock reloj) {
        this.repositorio = repositorio;
        this.historial = historial;
        this.unidadDeTrabajo = unidadDeTrabajo;
        this.reloj = reloj;
    }

    @Override
    public HallazgoAuditoria cerrar(HallazgoId hallazgoId) {
        return unidadDeTrabajo.ejecutar(() -> {
            HallazgoAuditoria hallazgo = repositorio.buscarPorId(hallazgoId)
                    .orElseThrow(() -> new HallazgoNoEncontradoException(hallazgoId));
            LocalDateTime ahora = LocalDateTime.now(reloj);
            EstadoHallazgo anterior = hallazgo.getEstado();
            hallazgo.cerrar(ahora);
            HallazgoAuditoria guardado = repositorio.guardar(hallazgo);
            historial.registrarCambio(guardado.getId(), anterior, guardado.getEstado(),
                    "Hallazgo cerrado tras " + guardado.diasHastaCierre() + " dias", ahora);
            return guardado;
        });
    }
}
