package com.example.auditoria.domain.valueobject;

/**
 * Enum con comportamiento: cada estado conoce a que estados puede transicionar.
 *
 * <pre>
 *   ABIERTO ──► EN_REMEDIACION ──► CERRADO ──► REABIERTO
 *                     ▲                            │
 *                     └────────────────────────────┘
 * </pre>
 */
public enum EstadoHallazgo {
    ABIERTO,
    EN_REMEDIACION,
    CERRADO,
    REABIERTO;

    public boolean puedeTransicionarA(EstadoHallazgo destino) {
        if (destino == null) {
            return false;
        }
        return switch (this) {
            case ABIERTO, REABIERTO -> destino == EN_REMEDIACION;
            case EN_REMEDIACION -> destino == CERRADO;
            case CERRADO -> destino == REABIERTO;
        };
    }

    public void validarTransicionA(EstadoHallazgo destino) {
        if (!puedeTransicionarA(destino)) {
            throw new TransicionInvalidaException(this, destino);
        }
    }
}
