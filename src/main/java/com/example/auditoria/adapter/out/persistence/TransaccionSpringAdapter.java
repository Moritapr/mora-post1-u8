package com.example.auditoria.adapter.out.persistence;

import com.example.auditoria.usecase.port.UnidadDeTrabajoPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

/** Implementa la unidad de trabajo con la transaccion de Spring (agregado + bitacora atomicos). */
@Component
public class TransaccionSpringAdapter implements UnidadDeTrabajoPort {

    private final TransactionTemplate transactionTemplate;

    public TransaccionSpringAdapter(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public <T> T ejecutar(Supplier<T> operacion) {
        return transactionTemplate.execute(estado -> operacion.get());
    }
}
