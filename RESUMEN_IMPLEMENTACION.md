# Resumen de implementación — auditoria-hallazgos-api (Unidad 8)

## Resumen ejecutivo
API Spring Boot 3.2.12 / Java 17 de seguimiento de hallazgos de auditoría con Clean Architecture de 4
círculos. `mvn clean package` → BUILD SUCCESS, 29 pruebas en verde. Prueba de humo con el jar sobre
JDK 17: los 6 endpoints responden con los códigos esperados y la consola H2 responde 200.

## Hallazgos clave
- `domain/` y `usecase/` sin imports de Spring/Jakarta/Hibernate; lo verifica `ArquitecturaTest`.
- Los casos de uso son POJOs registrados como `@Bean` en `config/AuditoriaConfiguration`.
- Se añadió `UnidadDeTrabajoPort` (no estaba en el enunciado) para que agregado + bitácora se guarden
  en la misma transacción sin poner `@Transactional` en el círculo 2. Lo implementa `TransaccionSpringAdapter`.
- Extras no pedidos: `GET /api/hallazgos/{id}` (usa `ConsultarHallazgoUseCase`), regla de plazo máximo
  de 30 días para `CRITICA`, `HallazgoNoEncontradoException` → 404.
- Dashboard: JPQL `GROUP BY` + `AVG(diasCierre)`; `dias_cierre` se desnormaliza al cerrar (portabilidad).
- Bitácora append-only: `@Immutable` + `updatable=false` + `@PreUpdate/@PreRemove` + repositorio sin delete.
- Reabrir limpia `fechaCierre`/`dias_cierre`: el promedio solo cuenta hallazgos cerrados actualmente.
- En esta Mac no hay `java` en el PATH; el jar se ejecutó con `/opt/homebrew/opt/openjdk@17/bin/java`.
  `mvn` usa JDK 26; el pom fija `byte-buddy.version=1.17.8` para que Hibernate/pruebas funcionen ahí.

## Decisiones pendientes
- **Capturas:** `docs/` solo tiene `.gitkeep`. El README ya referencia los 8 PNG
  (`build, run, crear_hallazgo, error_transicion, remediacion, cierre, dashboard, historial`); hay que
  tomarlas y guardarlas con esos nombres.
- Remoto Git: solo hay commit local, no se configuró remoto ni se hizo push.

## Código / rutas relevantes
- Agregado: `src/main/java/com/example/auditoria/domain/entity/HallazgoAuditoria.java`
- Transiciones: `domain/valueobject/EstadoHallazgo.java`
- Consultas agregadas: `adapter/out/persistence/HallazgoJpaRepository.java`
- Wiring: `config/AuditoriaConfiguration.java`
- Pruebas: `src/test/java/com/example/auditoria/{domain/HallazgoAuditoriaTest, ArquitecturaTest, adapter/HallazgoApiIntegrationTest}.java`
