package com.example.auditoria.usecase.impl;

import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.usecase.ConsultarHallazgoUseCase;
import com.example.auditoria.usecase.HallazgoNoEncontradoException;
import com.example.auditoria.usecase.port.HallazgoRepositoryPort;

public class ConsultarHallazgoService implements ConsultarHallazgoUseCase {

    private final HallazgoRepositoryPort repositorio;

    public ConsultarHallazgoService(HallazgoRepositoryPort repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public HallazgoAuditoria consultar(HallazgoId hallazgoId) {
        return repositorio.buscarPorId(hallazgoId)
                .orElseThrow(() -> new HallazgoNoEncontradoException(hallazgoId));
    }
}
