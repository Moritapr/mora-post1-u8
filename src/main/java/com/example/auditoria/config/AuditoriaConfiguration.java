package com.example.auditoria.config;

import com.example.auditoria.usecase.CerrarHallazgoUseCase;
import com.example.auditoria.usecase.ConsultarHallazgoUseCase;
import com.example.auditoria.usecase.ConsultarHistorialUseCase;
import com.example.auditoria.usecase.IniciarRemediacionUseCase;
import com.example.auditoria.usecase.ObtenerDashboardAuditoriaUseCase;
import com.example.auditoria.usecase.ReabrirHallazgoUseCase;
import com.example.auditoria.usecase.RegistrarHallazgoUseCase;
import com.example.auditoria.usecase.impl.CerrarHallazgoService;
import com.example.auditoria.usecase.impl.ConsultarHallazgoService;
import com.example.auditoria.usecase.impl.ConsultarHistorialService;
import com.example.auditoria.usecase.impl.IniciarRemediacionService;
import com.example.auditoria.usecase.impl.ObtenerDashboardAuditoriaService;
import com.example.auditoria.usecase.impl.ReabrirHallazgoService;
import com.example.auditoria.usecase.impl.RegistrarHallazgoService;
import com.example.auditoria.usecase.port.HallazgoRepositoryPort;
import com.example.auditoria.usecase.port.HistorialAuditoriaPort;
import com.example.auditoria.usecase.port.UnidadDeTrabajoPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Circulo 4 (Frameworks & Drivers): unico lugar donde Spring conoce a los casos de uso.
 * Los servicios del circulo 2 no llevan @Service; se ensamblan aqui con sus puertos.
 */
@Configuration
public class AuditoriaConfiguration {

    @Bean
    public Clock reloj() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public RegistrarHallazgoUseCase registrarHallazgoUseCase(HallazgoRepositoryPort repositorio,
                                                             HistorialAuditoriaPort historial,
                                                             UnidadDeTrabajoPort unidadDeTrabajo, Clock reloj) {
        return new RegistrarHallazgoService(repositorio, historial, unidadDeTrabajo, reloj);
    }

    @Bean
    public IniciarRemediacionUseCase iniciarRemediacionUseCase(HallazgoRepositoryPort repositorio,
                                                               HistorialAuditoriaPort historial,
                                                               UnidadDeTrabajoPort unidadDeTrabajo, Clock reloj) {
        return new IniciarRemediacionService(repositorio, historial, unidadDeTrabajo, reloj);
    }

    @Bean
    public CerrarHallazgoUseCase cerrarHallazgoUseCase(HallazgoRepositoryPort repositorio,
                                                       HistorialAuditoriaPort historial,
                                                       UnidadDeTrabajoPort unidadDeTrabajo, Clock reloj) {
        return new CerrarHallazgoService(repositorio, historial, unidadDeTrabajo, reloj);
    }

    @Bean
    public ReabrirHallazgoUseCase reabrirHallazgoUseCase(HallazgoRepositoryPort repositorio,
                                                         HistorialAuditoriaPort historial,
                                                         UnidadDeTrabajoPort unidadDeTrabajo, Clock reloj) {
        return new ReabrirHallazgoService(repositorio, historial, unidadDeTrabajo, reloj);
    }

    @Bean
    public ConsultarHallazgoUseCase consultarHallazgoUseCase(HallazgoRepositoryPort repositorio) {
        return new ConsultarHallazgoService(repositorio);
    }

    @Bean
    public ObtenerDashboardAuditoriaUseCase obtenerDashboardAuditoriaUseCase(HallazgoRepositoryPort repositorio) {
        return new ObtenerDashboardAuditoriaService(repositorio);
    }

    @Bean
    public ConsultarHistorialUseCase consultarHistorialUseCase(HallazgoRepositoryPort repositorio,
                                                               HistorialAuditoriaPort historial) {
        return new ConsultarHistorialService(repositorio, historial);
    }
}
