package com.example.auditoria.domain.entity;

import com.example.auditoria.domain.valueobject.EstadoHallazgo;
import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.domain.valueobject.PlanRemediacion;
import com.example.auditoria.domain.valueobject.Severidad;
import com.example.auditoria.domain.valueobject.TransicionInvalidaException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Aggregate Root del Sistema de Seguimiento de Hallazgos de Auditoria Interna.
 * Java puro: sin anotaciones de Spring, JPA ni HTTP.
 *
 * Invariantes:
 * - Todo hallazgo nace ABIERTO.
 * - Las transiciones siguen {@link EstadoHallazgo#puedeTransicionarA(EstadoHallazgo)}.
 * - No se puede cerrar sin un plan de remediacion.
 * - El compromiso del plan no puede estar en el pasado y, para hallazgos CRITICA,
 *   no puede superar {@value #PLAZO_MAXIMO_DIAS_CRITICA} dias.
 * - Reabrir exige un motivo.
 */
public class HallazgoAuditoria {

    public static final int PLAZO_MAXIMO_DIAS_CRITICA = 30;

    private final HallazgoId id;
    private final String titulo;
    private final String descripcion;
    private final String area;
    private final Severidad severidad;
    private final LocalDateTime fechaRegistro;
    private EstadoHallazgo estado;
    private PlanRemediacion planRemediacion;
    private LocalDateTime fechaCierre;
    private String motivoReapertura;

    private HallazgoAuditoria(HallazgoId id, String titulo, String descripcion, String area,
                              Severidad severidad, LocalDateTime fechaRegistro, EstadoHallazgo estado,
                              PlanRemediacion planRemediacion, LocalDateTime fechaCierre,
                              String motivoReapertura) {
        this.id = Objects.requireNonNull(id, "El id es obligatorio");
        this.titulo = requerirTexto(titulo, "El titulo es obligatorio");
        this.descripcion = requerirTexto(descripcion, "La descripcion es obligatoria");
        this.area = requerirTexto(area, "El area auditada es obligatoria");
        this.severidad = requerir(severidad, "La severidad es obligatoria");
        this.fechaRegistro = requerir(fechaRegistro, "La fecha de registro es obligatoria");
        this.estado = requerir(estado, "El estado es obligatorio");
        this.planRemediacion = planRemediacion;
        this.fechaCierre = fechaCierre;
        this.motivoReapertura = motivoReapertura;
    }

    /** Fabrica para hallazgos nuevos: siempre nacen en ABIERTO. */
    public static HallazgoAuditoria registrar(String titulo, String descripcion, String area,
                                              Severidad severidad, LocalDateTime ahora) {
        return new HallazgoAuditoria(HallazgoId.nuevo(), titulo, descripcion, area, severidad,
                ahora, EstadoHallazgo.ABIERTO, null, null, null);
    }

    /** Fabrica para rehidratar desde persistencia (usada por los adaptadores de salida). */
    public static HallazgoAuditoria reconstituir(HallazgoId id, String titulo, String descripcion,
                                                 String area, Severidad severidad,
                                                 LocalDateTime fechaRegistro, EstadoHallazgo estado,
                                                 PlanRemediacion planRemediacion,
                                                 LocalDateTime fechaCierre, String motivoReapertura) {
        return new HallazgoAuditoria(id, titulo, descripcion, area, severidad, fechaRegistro,
                estado, planRemediacion, fechaCierre, motivoReapertura);
    }

    public void iniciarRemediacion(PlanRemediacion plan, LocalDateTime ahora) {
        estado.validarTransicionA(EstadoHallazgo.EN_REMEDIACION);
        Objects.requireNonNull(plan, "El plan de remediacion es obligatorio");
        if (plan.fechaCompromiso().isBefore(ahora.toLocalDate())) {
            throw new IllegalArgumentException("La fecha de compromiso no puede estar en el pasado");
        }
        if (severidad == Severidad.CRITICA
                && plan.fechaCompromiso().isAfter(ahora.toLocalDate().plusDays(PLAZO_MAXIMO_DIAS_CRITICA))) {
            throw new IllegalArgumentException("Un hallazgo CRITICA debe remediarse en maximo "
                    + PLAZO_MAXIMO_DIAS_CRITICA + " dias");
        }
        this.planRemediacion = plan;
        this.estado = EstadoHallazgo.EN_REMEDIACION;
    }

    public void cerrar(LocalDateTime ahora) {
        estado.validarTransicionA(EstadoHallazgo.CERRADO);
        if (planRemediacion == null) {
            throw new TransicionInvalidaException("No se puede cerrar un hallazgo sin plan de remediacion");
        }
        this.fechaCierre = Objects.requireNonNull(ahora, "La fecha de cierre es obligatoria");
        this.estado = EstadoHallazgo.CERRADO;
    }

    public void reabrir(String motivo) {
        estado.validarTransicionA(EstadoHallazgo.REABIERTO);
        this.motivoReapertura = requerirTexto(motivo, "El motivo de reapertura es obligatorio");
        this.fechaCierre = null;
        this.estado = EstadoHallazgo.REABIERTO;
    }

    /** Dias transcurridos entre registro y cierre; null si el hallazgo no esta cerrado. */
    public Long diasHastaCierre() {
        if (fechaCierre == null) {
            return null;
        }
        return ChronoUnit.DAYS.between(fechaRegistro, fechaCierre);
    }

    private static String requerirTexto(String valor, String mensaje) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensaje);
        }
        return valor.trim();
    }

    private static <T> T requerir(T valor, String mensaje) {
        if (valor == null) {
            throw new IllegalArgumentException(mensaje);
        }
        return valor;
    }

    public HallazgoId getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String getArea() { return area; }
    public Severidad getSeveridad() { return severidad; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public EstadoHallazgo getEstado() { return estado; }
    public PlanRemediacion getPlanRemediacion() { return planRemediacion; }
    public LocalDateTime getFechaCierre() { return fechaCierre; }
    public String getMotivoReapertura() { return motivoReapertura; }
}
