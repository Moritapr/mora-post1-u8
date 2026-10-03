package com.example.auditoria;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/** Regla de dependencia: los circulos 1 y 2 no importan frameworks ni circulos externos. */
class ArquitecturaTest {

    private static final Path RAIZ = Path.of("src/main/java/com/example/auditoria");
    private static final List<String> PROHIBIDOS = List.of(
            "import org.springframework", "import jakarta.", "import javax.persistence", "import org.hibernate",
            "import com.example.auditoria.adapter", "import com.example.auditoria.config");

    @Test
    void dominioNoDependeDeNadaExterno() throws IOException {
        verificar(RAIZ.resolve("domain"), List.of("import com.example.auditoria.usecase"));
    }

    @Test
    void casosDeUsoNoDependenDeFrameworksNiAdaptadores() throws IOException {
        verificar(RAIZ.resolve("usecase"), List.of());
    }

    private static void verificar(Path paquete, List<String> extras) throws IOException {
        try (Stream<Path> archivos = Files.walk(paquete)) {
            List<String> violaciones = archivos.filter(p -> p.toString().endsWith(".java"))
                    .flatMap(p -> lineas(p).filter(l -> Stream.concat(PROHIBIDOS.stream(), extras.stream())
                            .anyMatch(l::startsWith)).map(l -> p.getFileName() + ": " + l))
                    .toList();
            assertTrue(violaciones.isEmpty(), "Violaciones de la regla de dependencia: " + violaciones);
        }
    }

    private static Stream<String> lineas(Path p) {
        try {
            return Files.readAllLines(p).stream().map(String::trim);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
