package com.example.auditoria.usecase.port;

import java.util.function.Supplier;

/**
 * Puerto de salida para ejecutar un caso de uso de forma atomica
 * (guardar agregado + registrar bitacora) sin acoplar el circulo 2 a Spring.
 */
public interface UnidadDeTrabajoPort {

    <T> T ejecutar(Supplier<T> operacion);
}
