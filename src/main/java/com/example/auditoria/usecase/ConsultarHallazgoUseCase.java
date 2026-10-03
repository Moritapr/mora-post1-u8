package com.example.auditoria.usecase;

import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.domain.valueobject.HallazgoId;

public interface ConsultarHallazgoUseCase {

    HallazgoAuditoria consultar(HallazgoId hallazgoId);
}
