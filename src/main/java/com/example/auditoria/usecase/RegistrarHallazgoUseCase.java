package com.example.auditoria.usecase;

import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.domain.valueobject.Severidad;

public interface RegistrarHallazgoUseCase {

    HallazgoAuditoria registrar(Comando comando);

    record Comando(String titulo, String descripcion, String area, Severidad severidad) {
    }
}
