package com.example.auditoria.adapter.out.persistence;

import com.example.auditoria.domain.valueobject.EstadoHallazgo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * Extension Liviana de CQRS: las lecturas del dashboard son proyecciones JPQL
 * agregadas sobre la misma tabla del lado comando (sin read model separado).
 */
public interface HallazgoJpaRepository extends JpaRepository<HallazgoJpaEntity, UUID> {

    @Query("SELECT h.severidad, COUNT(h) FROM HallazgoJpaEntity h GROUP BY h.severidad ORDER BY h.severidad")
    List<Object[]> contarPorSeveridad();

    @Query("SELECT h.estado, COUNT(h) FROM HallazgoJpaEntity h GROUP BY h.estado ORDER BY h.estado")
    List<Object[]> contarPorEstado();

    @Query("SELECT h.severidad, AVG(h.diasCierre) FROM HallazgoJpaEntity h "
            + "WHERE h.estado = :estado GROUP BY h.severidad ORDER BY h.severidad")
    List<Object[]> promedioDiasCierrePorSeveridad(@Param("estado") EstadoHallazgo estado);
}
