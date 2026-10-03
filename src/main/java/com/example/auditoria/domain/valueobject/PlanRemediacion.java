package com.example.auditoria.domain.valueobject;

import java.time.LocalDate;

/**
 * Value Object inmutable embebido en el agregado. No tiene identidad propia:
 * dos planes con los mismos datos son el mismo plan.
 */
public record PlanRemediacion(String descripcion, String responsable, LocalDate fechaCompromiso) {

    public PlanRemediacion {
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("El plan de remediacion requiere una descripcion");
        }
        if (responsable == null || responsable.isBlank()) {
            throw new IllegalArgumentException("El plan de remediacion requiere un responsable");
        }
        if (fechaCompromiso == null) {
            throw new IllegalArgumentException("El plan de remediacion requiere una fecha de compromiso");
        }
        descripcion = descripcion.trim();
        responsable = responsable.trim();
    }
}
