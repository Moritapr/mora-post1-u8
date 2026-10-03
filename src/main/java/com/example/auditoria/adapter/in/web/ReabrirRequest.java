package com.example.auditoria.adapter.in.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReabrirRequest(@NotBlank @Size(max = 1000) String motivo) {
}
