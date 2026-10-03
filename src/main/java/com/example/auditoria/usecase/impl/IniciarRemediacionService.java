package com.example.auditoria.usecase.impl;

import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.domain.valueobject.EstadoHallazgo;
import com.example.auditoria.domain.valueobject.PlanRemediacion;
import com.example.auditoria.usecase.HallazgoNoEncontradoException;
import com.example.auditoria.usecase.IniciarRemediacionUseCase;
import com.example.auditoria.usecase.port.HallazgoRepositoryPort;
import com.example.auditoria.usecase.port.HistorialAuditoriaPort;
import com.example.auditoria.usecase.port.UnidadDeTrabajoPort;

import java.time.Clock;
import java.time.LocalDateTime;

public class IniciarRemediacionService implements IniciarRemediacionUseCase {

    private final HallazgoRepositoryPort repositorio;
    private final HistorialAuditoriaPort historial;
    private final UnidadDeTrabajoPort unidadDeTrabajo;
    private final Clock reloj;

    public IniciarRemediacionService(HallazgoRepositoryPort repositorio, HistorialAuditoriaPort historial,
                                     UnidadDeTrabajoPort unidadDeTrabajo, Clock reloj) {
        this.repositorio = repositorio;
        this.historial = historial;
        this.unidadDeTrabajo = unidadDeTrabajo;
        this.reloj = reloj;
    }

    @Override
    public HallazgoAuditoria iniciarRemediacion(Comando comando) {
        return unidadDeTrabajo.ejecutar(() -> {
            HallazgoAuditoria hallazgo = repositorio.buscarPorId(comando.hallazgoId())
                    .orElseThrow(() -> new HallazgoNoEncontradoException(comando.hallazgoId()));
            LocalDateTime ahora = LocalDateTime.now(reloj);
            EstadoHallazgo anterior = hallazgo.getEstado();
            PlanRemediacion plan = new PlanRemediacion(comando.descripcion(), comando.responsable(),
                    comando.fechaCompromiso());
            hallazgo.iniciarRemediacion(plan, ahora);
            HallazgoAuditoria guardado = repositorio.guardar(hallazgo);
            historial.registrarCambio(guardado.getId(), anterior, guardado.getEstado(),
                    "Plan a cargo de " + plan.responsable() + " con compromiso " + plan.fechaCompromiso(),
                    ahora);
            return guardado;
        });
    }
}
