package com.example.auditoria.usecase.port;

import java.util.List;

public record DashboardAuditoriaView(long totalHallazgos,
                                     List<ConteoCategoria> porSeveridad,
                                     List<ConteoCategoria> porEstado,
                                     List<PromedioCategoria> promedioDiasCierrePorSeveridad) {
}
