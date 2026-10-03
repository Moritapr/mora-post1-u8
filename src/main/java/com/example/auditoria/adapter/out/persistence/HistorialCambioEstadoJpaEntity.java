package com.example.auditoria.adapter.out.persistence;

import com.example.auditoria.domain.valueobject.EstadoHallazgo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Bitacora append-only: @Immutable hace que Hibernate ignore cualquier cambio,
 * todas las columnas son updatable=false y los callbacks bloquean UPDATE/DELETE.
 */
@Entity
@Immutable
@Table(name = "historial_cambios_estado",
        indexes = @Index(name = "idx_historial_hallazgo", columnList = "hallazgo_id, fecha"))
public class HistorialCambioEstadoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hallazgo_id", nullable = false, updatable = false)
    private UUID hallazgoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_anterior", length = 20, updatable = false)
    private EstadoHallazgo estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_nuevo", nullable = false, length = 20, updatable = false)
    private EstadoHallazgo estadoNuevo;

    @Column(length = 1000, updatable = false)
    private String detalle;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fecha;

    protected HistorialCambioEstadoJpaEntity() {
    }

    public HistorialCambioEstadoJpaEntity(UUID hallazgoId, EstadoHallazgo estadoAnterior,
                                          EstadoHallazgo estadoNuevo, String detalle, LocalDateTime fecha) {
        this.hallazgoId = hallazgoId;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.detalle = detalle;
        this.fecha = fecha;
    }

    @PreUpdate
    @PreRemove
    void bloquearModificacion() {
        throw new IllegalStateException("La bitacora de auditoria es append-only");
    }

    public Long getId() { return id; }
    public UUID getHallazgoId() { return hallazgoId; }
    public EstadoHallazgo getEstadoAnterior() { return estadoAnterior; }
    public EstadoHallazgo getEstadoNuevo() { return estadoNuevo; }
    public String getDetalle() { return detalle; }
    public LocalDateTime getFecha() { return fecha; }
}
