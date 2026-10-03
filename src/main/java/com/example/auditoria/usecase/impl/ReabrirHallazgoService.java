package com.example.auditoria.usecase.impl;

import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.domain.valueobject.EstadoHallazgo;
import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.usecase.HallazgoNoEncontradoException;
import com.example.auditoria.usecase.ReabrirHallazgoUseCase;
import com.example.auditoria.usecase.port.HallazgoRepositoryPort;
import com.example.auditoria.usecase.port.HistorialAuditoriaPort;
import com.example.auditoria.usecase.port.UnidadDeTrabajoPort;

import java.time.Clock;
import java.time.LocalDateTime;

public class ReabrirHallazgoService implements ReabrirHallazgoUseCase {

    private final HallazgoRepositoryPort repositorio;
    private final HistorialAuditoriaPort historial;
    private final UnidadDeTrabajoPort unidadDeTrabajo;
    private final Clock reloj;

    public ReabrirHallazgoService(HallazgoRepositoryPort repositorio, HistorialAuditoriaPort historial,
                                  UnidadDeTrabajoPort unidadDeTrabajo, Clock reloj) {
        this.repositorio = repositorio;
        this.historial = historial;
        this.unidadDeTrabajo = unidadDeTrabajo;
        this.reloj = reloj;
    }

    @Override
    public HallazgoAuditoria reabrir(HallazgoId hallazgoId, String motivo) {
        return unidadDeTrabajo.ejecutar(() -> {
            HallazgoAuditoria hallazgo = repositorio.buscarPorId(hallazgoId)
                    .orElseThrow(() -> new HallazgoNoEncontradoException(hallazgoId));
            EstadoHallazgo anterior = hallazgo.getEstado();
            hallazgo.reabrir(motivo);
            HallazgoAuditoria guardado = repositorio.guardar(hallazgo);
            historial.registrarCambio(guardado.getId(), anterior, guardado.getEstado(),
                    "Motivo: " + guardado.getMotivoReapertura(), LocalDateTime.now(reloj));
            return guardado;
        });
    }
}
