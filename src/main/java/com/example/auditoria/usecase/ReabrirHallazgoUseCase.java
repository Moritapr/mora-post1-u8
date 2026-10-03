package com.example.auditoria.usecase;

import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.domain.valueobject.HallazgoId;

public interface ReabrirHallazgoUseCase {

    HallazgoAuditoria reabrir(HallazgoId hallazgoId, String motivo);
}
