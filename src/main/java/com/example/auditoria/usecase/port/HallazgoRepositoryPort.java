package com.example.auditoria.usecase.port;

import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.domain.valueobject.HallazgoId;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para persistir el agregado (lado comando) y para las
 * consultas agregadas del dashboard (lado lectura de la Extension Liviana).
 */
public interface HallazgoRepositoryPort {

    HallazgoAuditoria guardar(HallazgoAuditoria hallazgo);

    Optional<HallazgoAuditoria> buscarPorId(HallazgoId id);

    List<ConteoCategoria> contarPorSeveridad();

    List<ConteoCategoria> contarPorEstado();

    List<PromedioCategoria> promedioDiasCierrePorSeveridad();
}
