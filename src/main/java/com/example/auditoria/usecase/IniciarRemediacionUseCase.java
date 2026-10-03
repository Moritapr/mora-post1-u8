package com.example.auditoria.usecase;

import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.domain.valueobject.HallazgoId;

import java.time.LocalDate;

public interface IniciarRemediacionUseCase {

    HallazgoAuditoria iniciarRemediacion(Comando comando);

    record Comando(HallazgoId hallazgoId, String descripcion, String responsable,
                   LocalDate fechaCompromiso) {
    }
}
