package com.example.auditoria.adapter.in.web;

import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.domain.valueobject.PlanRemediacion;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record HallazgoResponse(String id, String titulo, String descripcion, String area,
                               String severidad, String estado, PlanResponse planRemediacion,
                               LocalDateTime fechaRegistro, LocalDateTime fechaCierre,
                               Long diasHastaCierre, String motivoReapertura) {

    public record PlanResponse(String descripcion, String responsable, LocalDate fechaCompromiso) {
    }

    public static HallazgoResponse desde(HallazgoAuditoria h) {
        PlanRemediacion plan = h.getPlanRemediacion();
        return new HallazgoResponse(h.getId().toString(), h.getTitulo(), h.getDescripcion(), h.getArea(),
                h.getSeveridad().name(), h.getEstado().name(),
                plan != null ? new PlanResponse(plan.descripcion(), plan.responsable(), plan.fechaCompromiso()) : null,
                h.getFechaRegistro(), h.getFechaCierre(), h.diasHastaCierre(), h.getMotivoReapertura());
    }
}
