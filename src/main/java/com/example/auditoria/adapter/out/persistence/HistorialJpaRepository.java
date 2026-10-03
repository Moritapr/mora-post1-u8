package com.example.auditoria.adapter.out.persistence;

import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Extiende Repository (no JpaRepository) para no exponer delete/update:
 * solo se puede agregar y leer.
 */
public interface HistorialJpaRepository extends Repository<HistorialCambioEstadoJpaEntity, Long> {

    HistorialCambioEstadoJpaEntity save(HistorialCambioEstadoJpaEntity entrada);

    List<HistorialCambioEstadoJpaEntity> findByHallazgoIdOrderByFechaAscIdAsc(UUID hallazgoId);
}
