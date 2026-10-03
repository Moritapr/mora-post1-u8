package com.example.auditoria.usecase.impl;

import com.example.auditoria.usecase.ObtenerDashboardAuditoriaUseCase;
import com.example.auditoria.usecase.port.ConteoCategoria;
import com.example.auditoria.usecase.port.DashboardAuditoriaView;
import com.example.auditoria.usecase.port.HallazgoRepositoryPort;

import java.util.List;

/**
 * Lado de lectura de la Extension Liviana: no rehidrata agregados, delega en
 * consultas agregadas (GROUP BY / AVG) resueltas por la base de datos.
 */
public class ObtenerDashboardAuditoriaService implements ObtenerDashboardAuditoriaUseCase {

    private final HallazgoRepositoryPort repositorio;

    public ObtenerDashboardAuditoriaService(HallazgoRepositoryPort repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public DashboardAuditoriaView obtenerDashboard() {
        List<ConteoCategoria> porEstado = repositorio.contarPorEstado();
        long total = porEstado.stream().mapToLong(ConteoCategoria::total).sum();
        return new DashboardAuditoriaView(total, repositorio.contarPorSeveridad(), porEstado,
                repositorio.promedioDiasCierrePorSeveridad());
    }
}
