package com.example.auditoria.adapter;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HallazgoApiIntegrationTest {

    @Autowired
    private MockMvc mvc;

    private String registrar(String severidad) throws Exception {
        String body = mvc.perform(post("/api/hallazgos").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"titulo":"Conciliaciones atrasadas","descripcion":"Bancos sin conciliar 3 meses",
                                 "area":"Contabilidad","severidad":"%s"}""".formatted(severidad)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("ABIERTO"))
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.id");
    }

    @Test
    void flujoCompletoConHistorialYDashboard() throws Exception {
        String id = registrar("ALTA");
        String plan = """
                {"descripcion":"Conciliar y automatizar","responsable":"Contador","fechaCompromiso":"%s"}"""
                .formatted(LocalDate.now().plusDays(10));

        mvc.perform(patch("/api/hallazgos/{id}/cerrar", id))
                .andExpect(status().isConflict());

        mvc.perform(patch("/api/hallazgos/{id}/iniciar-remediacion", id)
                        .contentType(MediaType.APPLICATION_JSON).content(plan))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_REMEDIACION"))
                .andExpect(jsonPath("$.planRemediacion.responsable").value("Contador"));

        mvc.perform(patch("/api/hallazgos/{id}/cerrar", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CERRADO"))
                .andExpect(jsonPath("$.fechaCierre").exists());

        mvc.perform(patch("/api/hallazgos/{id}/reabrir", id)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"motivo\":\"Persisten partidas\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("REABIERTO"));

        mvc.perform(get("/api/hallazgos/{id}/historial", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].estadoNuevo").value("ABIERTO"))
                .andExpect(jsonPath("$[3].estadoAnterior").value("CERRADO"))
                .andExpect(jsonPath("$[3].estadoNuevo").value("REABIERTO"));

        mvc.perform(get("/api/hallazgos/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalHallazgos").isNumber())
                .andExpect(jsonPath("$.porSeveridad").isArray())
                .andExpect(jsonPath("$.porEstado").isArray());
    }

    @Test
    void dashboardPromediaDiasDeCierreDeHallazgosCerrados() throws Exception {
        String id = registrar("BAJA");
        mvc.perform(patch("/api/hallazgos/{id}/iniciar-remediacion", id).contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"descripcion":"Ajuste","responsable":"Analista","fechaCompromiso":"%s"}"""
                        .formatted(LocalDate.now().plusDays(5))));
        mvc.perform(patch("/api/hallazgos/{id}/cerrar", id)).andExpect(status().isOk());

        mvc.perform(get("/api/hallazgos/dashboard"))
                .andExpect(jsonPath("$.promedioDiasCierrePorSeveridad[?(@.categoria=='BAJA')].promedioDias")
                        .value(0.0));
    }

    @Test
    void erroresDeEntradaYRecursoInexistente() throws Exception {
        mvc.perform(post("/api/hallazgos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"\",\"descripcion\":\"d\",\"area\":\"a\",\"severidad\":\"ALTA\"}"))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/api/hallazgos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"t\",\"descripcion\":\"d\",\"area\":\"a\",\"severidad\":\"GRAVE\"}"))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/api/hallazgos/{id}/historial", "00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound());

        mvc.perform(patch("/api/hallazgos/{id}/cerrar", "no-es-uuid"))
                .andExpect(status().isBadRequest());

        String critico = registrar("CRITICA");
        mvc.perform(patch("/api/hallazgos/{id}/iniciar-remediacion", critico)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"descripcion":"x","responsable":"y","fechaCompromiso":"%s"}"""
                                .formatted(LocalDate.now().plusDays(45))))
                .andExpect(status().isBadRequest());
    }
}
