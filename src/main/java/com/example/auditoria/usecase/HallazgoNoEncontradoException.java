package com.example.auditoria.usecase;

import com.example.auditoria.domain.valueobject.HallazgoId;

public class HallazgoNoEncontradoException extends RuntimeException {

    public HallazgoNoEncontradoException(HallazgoId id) {
        super("No existe el hallazgo " + id);
    }
}
