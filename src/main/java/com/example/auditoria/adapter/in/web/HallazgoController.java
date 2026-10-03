package com.example.auditoria.adapter.in.web;

import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.usecase.CerrarHallazgoUseCase;
import com.example.auditoria.usecase.ConsultarHallazgoUseCase;
import com.example.auditoria.usecase.ConsultarHistorialUseCase;
import com.example.auditoria.usecase.IniciarRemediacionUseCase;
import com.example.auditoria.usecase.ObtenerDashboardAuditoriaUseCase;
import com.example.auditoria.usecase.ReabrirHallazgoUseCase;
import com.example.auditoria.usecase.RegistrarHallazgoUseCase;
import com.example.auditoria.usecase.port.CambioEstadoView;
import com.example.auditoria.usecase.port.DashboardAuditoriaView;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Adaptador de entrada: traduce HTTP a comandos de casos de uso. No contiene reglas de negocio. */
@RestController
@RequestMapping("/api/hallazgos")
public class HallazgoController {

    private final RegistrarHallazgoUseCase registrar;
    private final IniciarRemediacionUseCase iniciarRemediacion;
    private final CerrarHallazgoUseCase cerrar;
    private final ReabrirHallazgoUseCase reabrir;
    private final ConsultarHallazgoUseCase consultar;
    private final ObtenerDashboardAuditoriaUseCase dashboard;
    private final ConsultarHistorialUseCase historial;

    public HallazgoController(RegistrarHallazgoUseCase registrar, IniciarRemediacionUseCase iniciarRemediacion,
                              CerrarHallazgoUseCase cerrar, ReabrirHallazgoUseCase reabrir,
                              ConsultarHallazgoUseCase consultar, ObtenerDashboardAuditoriaUseCase dashboard,
                              ConsultarHistorialUseCase historial) {
        this.registrar = registrar;
        this.iniciarRemediacion = iniciarRemediacion;
        this.cerrar = cerrar;
        this.reabrir = reabrir;
        this.consultar = consultar;
        this.dashboard = dashboard;
        this.historial = historial;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HallazgoResponse registrar(@Valid @RequestBody RegistrarHallazgoRequest request) {
        return HallazgoResponse.desde(registrar.registrar(new RegistrarHallazgoUseCase.Comando(
                request.titulo(), request.descripcion(), request.area(), request.severidad())));
    }

    @PatchMapping("/{id}/iniciar-remediacion")
    public HallazgoResponse iniciarRemediacion(@PathVariable String id,
                                               @Valid @RequestBody IniciarRemediacionRequest request) {
        return HallazgoResponse.desde(iniciarRemediacion.iniciarRemediacion(new IniciarRemediacionUseCase.Comando(
                HallazgoId.de(id), request.descripcion(), request.responsable(), request.fechaCompromiso())));
    }

    @PatchMapping("/{id}/cerrar")
    public HallazgoResponse cerrar(@PathVariable String id) {
        return HallazgoResponse.desde(cerrar.cerrar(HallazgoId.de(id)));
    }

    @PatchMapping("/{id}/reabrir")
    public HallazgoResponse reabrir(@PathVariable String id, @Valid @RequestBody ReabrirRequest request) {
        return HallazgoResponse.desde(reabrir.reabrir(HallazgoId.de(id), request.motivo()));
    }

    @GetMapping("/dashboard")
    public DashboardAuditoriaView dashboard() {
        return dashboard.obtenerDashboard();
    }

    @GetMapping("/{id}")
    public HallazgoResponse consultar(@PathVariable String id) {
        return HallazgoResponse.desde(consultar.consultar(HallazgoId.de(id)));
    }

    @GetMapping("/{id}/historial")
    public List<CambioEstadoView> historial(@PathVariable String id) {
        return historial.consultarHistorial(HallazgoId.de(id));
    }
}
