package com.example.auditoria.adapter.out.persistence;

import com.example.auditoria.domain.valueobject.EstadoHallazgo;
import com.example.auditoria.domain.valueobject.Severidad;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Modelo de persistencia. El PlanRemediacion del dominio se aplana en columnas
 * de esta misma tabla: vive y muere con el agregado.
 * dias_cierre se desnormaliza para que el dashboard calcule AVG en SQL portable.
 */
@Entity
@Table(name = "hallazgos")
public class HallazgoJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(nullable = false, length = 2000)
    private String descripcion;

    @Column(nullable = false, length = 120)
    private String area;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Severidad severidad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoHallazgo estado;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "plan_descripcion", length = 2000)
    private String planDescripcion;

    @Column(name = "plan_responsable", length = 120)
    private String planResponsable;

    @Column(name = "plan_fecha_compromiso")
    private LocalDate planFechaCompromiso;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @Column(name = "dias_cierre")
    private Long diasCierre;

    @Column(name = "motivo_reapertura", length = 1000)
    private String motivoReapertura;

    protected HallazgoJpaEntity() {
    }

    public HallazgoJpaEntity(UUID id, String titulo, String descripcion, String area, Severidad severidad,
                             EstadoHallazgo estado, LocalDateTime fechaRegistro, String planDescripcion,
                             String planResponsable, LocalDate planFechaCompromiso,
                             LocalDateTime fechaCierre, Long diasCierre, String motivoReapertura) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.area = area;
        this.severidad = severidad;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
        this.planDescripcion = planDescripcion;
        this.planResponsable = planResponsable;
        this.planFechaCompromiso = planFechaCompromiso;
        this.fechaCierre = fechaCierre;
        this.diasCierre = diasCierre;
        this.motivoReapertura = motivoReapertura;
    }

    public UUID getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String getArea() { return area; }
    public Severidad getSeveridad() { return severidad; }
    public EstadoHallazgo getEstado() { return estado; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public String getPlanDescripcion() { return planDescripcion; }
    public String getPlanResponsable() { return planResponsable; }
    public LocalDate getPlanFechaCompromiso() { return planFechaCompromiso; }
    public LocalDateTime getFechaCierre() { return fechaCierre; }
    public Long getDiasCierre() { return diasCierre; }
    public String getMotivoReapertura() { return motivoReapertura; }
}
