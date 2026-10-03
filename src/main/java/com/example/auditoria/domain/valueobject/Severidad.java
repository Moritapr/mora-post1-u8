package com.example.auditoria.domain.valueobject;

/**
 * Enum simple: la severidad es solo una clasificacion, no gobierna reglas por si misma.
 * Las reglas que dependen de ella (p. ej. plazo maximo de remediacion) viven en el agregado.
 */
public enum Severidad {
    CRITICA,
    ALTA,
    MEDIA,
    BAJA
}
