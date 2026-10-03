package com.example.auditoria.adapter.out.persistence;

import com.example.auditoria.domain.valueobject.EstadoHallazgo;
import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.usecase.port.CambioEstadoView;
import com.example.auditoria.usecase.port.HistorialAuditoriaPort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class HistorialAuditoriaAdapter implements HistorialAuditoriaPort {

    private final HistorialJpaRepository jpaRepository;

    public HistorialAuditoriaAdapter(HistorialJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void registrarCambio(HallazgoId hallazgoId, EstadoHallazgo estadoAnterior,
                                EstadoHallazgo estadoNuevo, String detalle, LocalDateTime fecha) {
        jpaRepository.save(new HistorialCambioEstadoJpaEntity(hallazgoId.valor(), estadoAnterior,
                estadoNuevo, detalle, fecha));
    }

    @Override
    public List<CambioEstadoView> obtenerHistorial(HallazgoId hallazgoId) {
        return jpaRepository.findByHallazgoIdOrderByFechaAscIdAsc(hallazgoId.valor()).stream()
                .map(e -> new CambioEstadoView(
                        e.getEstadoAnterior() != null ? e.getEstadoAnterior().name() : null,
                        e.getEstadoNuevo().name(), e.getDetalle(), e.getFecha()))
                .toList();
    }
}
