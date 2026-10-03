package com.example.auditoria.adapter.in.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record IniciarRemediacionRequest(
        @NotBlank @Size(max = 2000) String descripcion,
        @NotBlank @Size(max = 120) String responsable,
        @NotNull LocalDate fechaCompromiso) {
}
