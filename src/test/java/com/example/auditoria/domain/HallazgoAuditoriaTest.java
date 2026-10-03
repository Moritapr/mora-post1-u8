package com.example.auditoria.domain;

import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.domain.valueobject.EstadoHallazgo;
import com.example.auditoria.domain.valueobject.PlanRemediacion;
import com.example.auditoria.domain.valueobject.Severidad;
import com.example.auditoria.domain.valueobject.TransicionInvalidaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Maquina de estados del agregado, en Java puro: sin contexto de Spring Boot. */
class HallazgoAuditoriaTest {

    private static final LocalDateTime AHORA = LocalDateTime.of(2026, 10, 1, 9, 0);

    private HallazgoAuditoria hallazgo;

    @BeforeEach
    void setUp() {
        hallazgo = HallazgoAuditoria.registrar("Accesos sin revisar", "Usuarios inactivos con acceso al ERP",
                "Tecnologia", Severidad.ALTA, AHORA);
    }

    private static PlanRemediacion plan(int diasCompromiso) {
        return new PlanRemediacion("Depurar usuarios inactivos", "Jefe de TI",
                AHORA.toLocalDate().plusDays(diasCompromiso));
    }

    @Test
    @DisplayName("Un hallazgo nuevo nace ABIERTO, con id y sin plan")
    void nuevoHallazgoNaceAbierto() {
        assertEquals(EstadoHallazgo.ABIERTO, hallazgo.getEstado());
        assertNotNull(hallazgo.getId());
        assertNull(hallazgo.getPlanRemediacion());
        assertNull(hallazgo.diasHastaCierre());
    }

    @Test
    @DisplayName("Ciclo completo: ABIERTO -> EN_REMEDIACION -> CERRADO -> REABIERTO -> EN_REMEDIACION")
    void cicloCompleto() {
        hallazgo.iniciarRemediacion(plan(15), AHORA);
        assertEquals(EstadoHallazgo.EN_REMEDIACION, hallazgo.getEstado());

        hallazgo.cerrar(AHORA.plusDays(10));
        assertEquals(EstadoHallazgo.CERRADO, hallazgo.getEstado());
        assertEquals(10L, hallazgo.diasHastaCierre());

        hallazgo.reabrir("El control fallo en la verificacion");
        assertEquals(EstadoHallazgo.REABIERTO, hallazgo.getEstado());
        assertEquals("El control fallo en la verificacion", hallazgo.getMotivoReapertura());
        assertNull(hallazgo.getFechaCierre());

        assertDoesNotThrow(() -> hallazgo.iniciarRemediacion(plan(20), AHORA.plusDays(12)));
        assertEquals(EstadoHallazgo.EN_REMEDIACION, hallazgo.getEstado());
    }

    @Nested
    @DisplayName("Transiciones invalidas")
    class TransicionesInvalidas {

        @Test
        void noSePuedeCerrarDesdeAbierto() {
            assertThrows(TransicionInvalidaException.class, () -> hallazgo.cerrar(AHORA));
            assertEquals(EstadoHallazgo.ABIERTO, hallazgo.getEstado());
        }

        @Test
        void noSePuedeReabrirSinEstarCerrado() {
            assertThrows(TransicionInvalidaException.class, () -> hallazgo.reabrir("motivo"));
        }

        @Test
        void noSePuedeIniciarRemediacionDosVeces() {
            hallazgo.iniciarRemediacion(plan(5), AHORA);
            assertThrows(TransicionInvalidaException.class, () -> hallazgo.iniciarRemediacion(plan(6), AHORA));
        }

        @Test
        void noSePuedeCerrarDosVeces() {
            hallazgo.iniciarRemediacion(plan(5), AHORA);
            hallazgo.cerrar(AHORA);
            assertThrows(TransicionInvalidaException.class, () -> hallazgo.cerrar(AHORA));
        }

        @Test
        void noSePuedeCerrarSinPlanAunqueElEstadoLoPermita() {
            HallazgoAuditoria corrupto = HallazgoAuditoria.reconstituir(hallazgo.getId(), "t", "d", "a",
                    Severidad.BAJA, AHORA, EstadoHallazgo.EN_REMEDIACION, null, null, null);
            assertThrows(TransicionInvalidaException.class, () -> corrupto.cerrar(AHORA));
        }
    }

    @Nested
    @DisplayName("Invariantes")
    class Invariantes {

        @Test
        void reabrirExigeMotivo() {
            hallazgo.iniciarRemediacion(plan(5), AHORA);
            hallazgo.cerrar(AHORA);
            assertThrows(IllegalArgumentException.class, () -> hallazgo.reabrir("  "));
            assertEquals(EstadoHallazgo.CERRADO, hallazgo.getEstado());
        }

        @Test
        void compromisoNoPuedeEstarEnElPasado() {
            assertThrows(IllegalArgumentException.class, () -> hallazgo.iniciarRemediacion(plan(-1), AHORA));
            assertEquals(EstadoHallazgo.ABIERTO, hallazgo.getEstado());
        }

        @Test
        void hallazgoCriticoTienePlazoMaximoDe30Dias() {
            HallazgoAuditoria critico = HallazgoAuditoria.registrar("Fraude", "Pagos duplicados", "Tesoreria",
                    Severidad.CRITICA, AHORA);
            assertThrows(IllegalArgumentException.class, () -> critico.iniciarRemediacion(plan(31), AHORA));
            assertDoesNotThrow(() -> critico.iniciarRemediacion(plan(30), AHORA));
        }

        @Test
        void hallazgoRequiereDatosObligatorios() {
            assertThrows(IllegalArgumentException.class,
                    () -> HallazgoAuditoria.registrar(" ", "d", "a", Severidad.MEDIA, AHORA));
            assertThrows(IllegalArgumentException.class,
                    () -> HallazgoAuditoria.registrar("t", "d", "a", null, AHORA));
        }

        @Test
        void planRemediacionEsValueObjectValidado() {
            assertThrows(IllegalArgumentException.class,
                    () -> new PlanRemediacion("desc", "", AHORA.toLocalDate()));
            assertEquals(plan(3), plan(3));
        }
    }

    @ParameterizedTest(name = "{0} -> {1} = {2}")
    @CsvSource({
            "ABIERTO, EN_REMEDIACION, true",
            "ABIERTO, CERRADO, false",
            "ABIERTO, REABIERTO, false",
            "EN_REMEDIACION, CERRADO, true",
            "EN_REMEDIACION, ABIERTO, false",
            "EN_REMEDIACION, REABIERTO, false",
            "CERRADO, REABIERTO, true",
            "CERRADO, EN_REMEDIACION, false",
            "CERRADO, ABIERTO, false",
            "REABIERTO, EN_REMEDIACION, true",
            "REABIERTO, CERRADO, false",
            "REABIERTO, ABIERTO, false"
    })
    @DisplayName("Tabla de transiciones de EstadoHallazgo")
    void tablaDeTransiciones(EstadoHallazgo origen, EstadoHallazgo destino, boolean esperado) {
        assertEquals(esperado, origen.puedeTransicionarA(destino));
    }
}
