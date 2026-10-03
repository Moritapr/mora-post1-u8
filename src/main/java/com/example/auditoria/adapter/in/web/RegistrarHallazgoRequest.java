package com.example.auditoria.adapter.in.web;

import com.example.auditoria.domain.valueobject.Severidad;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistrarHallazgoRequest(
        @NotBlank @Size(max = 200) String titulo,
        @NotBlank @Size(max = 2000) String descripcion,
        @NotBlank @Size(max = 120) String area,
        @NotNull Severidad severidad) {
}
