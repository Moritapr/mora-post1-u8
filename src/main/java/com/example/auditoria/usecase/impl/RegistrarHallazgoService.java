package com.example.auditoria.usecase.impl;

import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.usecase.RegistrarHallazgoUseCase;
import com.example.auditoria.usecase.port.HallazgoRepositoryPort;
import com.example.auditoria.usecase.port.HistorialAuditoriaPort;
import com.example.auditoria.usecase.port.UnidadDeTrabajoPort;

import java.time.Clock;
import java.time.LocalDateTime;

public class RegistrarHallazgoService implements RegistrarHallazgoUseCase {

    private final HallazgoRepositoryPort repositorio;
    private final HistorialAuditoriaPort historial;
    private final UnidadDeTrabajoPort unidadDeTrabajo;
    private final Clock reloj;

    public RegistrarHallazgoService(HallazgoRepositoryPort repositorio, HistorialAuditoriaPort historial,
                                    UnidadDeTrabajoPort unidadDeTrabajo, Clock reloj) {
        this.repositorio = repositorio;
        this.historial = historial;
        this.unidadDeTrabajo = unidadDeTrabajo;
        this.reloj = reloj;
    }

    @Override
    public HallazgoAuditoria registrar(Comando comando) {
        return unidadDeTrabajo.ejecutar(() -> {
            LocalDateTime ahora = LocalDateTime.now(reloj);
            HallazgoAuditoria hallazgo = HallazgoAuditoria.registrar(comando.titulo(),
                    comando.descripcion(), comando.area(), comando.severidad(), ahora);
            HallazgoAuditoria guardado = repositorio.guardar(hallazgo);
            historial.registrarCambio(guardado.getId(), null, guardado.getEstado(),
                    "Hallazgo registrado con severidad " + guardado.getSeveridad(), ahora);
            return guardado;
        });
    }
}
