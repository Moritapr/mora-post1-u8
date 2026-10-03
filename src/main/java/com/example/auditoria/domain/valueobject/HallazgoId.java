package com.example.auditoria.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * Identidad del agregado HallazgoAuditoria. Value Object inmutable (Circulo 1).
 */
public record HallazgoId(UUID valor) {

    public HallazgoId {
        Objects.requireNonNull(valor, "El identificador del hallazgo es obligatorio");
    }

    public static HallazgoId nuevo() {
        return new HallazgoId(UUID.randomUUID());
    }

    public static HallazgoId de(String valor) {
        try {
            return new HallazgoId(UUID.fromString(valor));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Identificador de hallazgo invalido: " + valor);
        }
    }

    @Override
    public String toString() {
        return valor.toString();
    }
}
