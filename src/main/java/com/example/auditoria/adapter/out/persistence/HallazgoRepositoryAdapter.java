package com.example.auditoria.adapter.out.persistence;

import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.domain.valueobject.EstadoHallazgo;
import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.domain.valueobject.PlanRemediacion;
import com.example.auditoria.usecase.port.ConteoCategoria;
import com.example.auditoria.usecase.port.HallazgoRepositoryPort;
import com.example.auditoria.usecase.port.PromedioCategoria;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/** Traduce entre el agregado de dominio y el modelo JPA. */
@Component
public class HallazgoRepositoryAdapter implements HallazgoRepositoryPort {

    private final HallazgoJpaRepository jpaRepository;

    public HallazgoRepositoryAdapter(HallazgoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public HallazgoAuditoria guardar(HallazgoAuditoria hallazgo) {
        return aDominio(jpaRepository.save(aEntidad(hallazgo)));
    }

    @Override
    public Optional<HallazgoAuditoria> buscarPorId(HallazgoId id) {
        return jpaRepository.findById(id.valor()).map(HallazgoRepositoryAdapter::aDominio);
    }

    @Override
    public List<ConteoCategoria> contarPorSeveridad() {
        return aConteos(jpaRepository.contarPorSeveridad());
    }

    @Override
    public List<ConteoCategoria> contarPorEstado() {
        return aConteos(jpaRepository.contarPorEstado());
    }

    @Override
    public List<PromedioCategoria> promedioDiasCierrePorSeveridad() {
        return jpaRepository.promedioDiasCierrePorSeveridad(EstadoHallazgo.CERRADO).stream()
                .map(fila -> new PromedioCategoria(fila[0].toString(), ((Number) fila[1]).doubleValue()))
                .toList();
    }

    private static List<ConteoCategoria> aConteos(List<Object[]> filas) {
        return filas.stream()
                .map(fila -> new ConteoCategoria(fila[0].toString(), ((Number) fila[1]).longValue()))
                .toList();
    }

    private static HallazgoJpaEntity aEntidad(HallazgoAuditoria h) {
        PlanRemediacion plan = h.getPlanRemediacion();
        return new HallazgoJpaEntity(h.getId().valor(), h.getTitulo(), h.getDescripcion(), h.getArea(),
                h.getSeveridad(), h.getEstado(), h.getFechaRegistro(),
                plan != null ? plan.descripcion() : null,
                plan != null ? plan.responsable() : null,
                plan != null ? plan.fechaCompromiso() : null,
                h.getFechaCierre(), h.diasHastaCierre(), h.getMotivoReapertura());
    }

    private static HallazgoAuditoria aDominio(HallazgoJpaEntity e) {
        PlanRemediacion plan = e.getPlanDescripcion() != null
                ? new PlanRemediacion(e.getPlanDescripcion(), e.getPlanResponsable(), e.getPlanFechaCompromiso())
                : null;
        return HallazgoAuditoria.reconstituir(new HallazgoId(e.getId()), e.getTitulo(), e.getDescripcion(),
                e.getArea(), e.getSeveridad(), e.getFechaRegistro(), e.getEstado(), plan,
                e.getFechaCierre(), e.getMotivoReapertura());
    }
}
