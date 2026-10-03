package com.example.auditoria.usecase.impl;

import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.usecase.ConsultarHistorialUseCase;
import com.example.auditoria.usecase.HallazgoNoEncontradoException;
import com.example.auditoria.usecase.port.CambioEstadoView;
import com.example.auditoria.usecase.port.HallazgoRepositoryPort;
import com.example.auditoria.usecase.port.HistorialAuditoriaPort;

import java.util.List;

public class ConsultarHistorialService implements ConsultarHistorialUseCase {

    private final HallazgoRepositoryPort repositorio;
    private final HistorialAuditoriaPort historial;

    public ConsultarHistorialService(HallazgoRepositoryPort repositorio, HistorialAuditoriaPort historial) {
        this.repositorio = repositorio;
        this.historial = historial;
    }

    @Override
    public List<CambioEstadoView> consultarHistorial(HallazgoId hallazgoId) {
        if (repositorio.buscarPorId(hallazgoId).isEmpty()) {
            throw new HallazgoNoEncontradoException(hallazgoId);
        }
        return historial.obtenerHistorial(hallazgoId);
    }
}
