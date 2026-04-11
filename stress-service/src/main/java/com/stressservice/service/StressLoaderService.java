package com.stressservice.service;

import com.stressservice.dto.EmpleadoDTO;
import com.stressservice.dto.StressResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class StressLoaderService {

    private final BackendClient backendClient;
    private final Random random = new Random();

    private static final String[] NOMBRES = {
            "Carlos", "Ana", "Diego", "Laura", "Marco", "Sofia", "Pedro", "Maria",
            "Jose", "Elena", "Luis", "Carmen", "Miguel", "Rosa", "Fernando",
            "Patricia", "Roberto", "Lucia", "Andres", "Isabel"
    };
    private static final String[] APELLIDOS = {
            "Perez", "Garcia", "Lopez", "Martinez", "Rodriguez", "Hernandez",
            "Gonzalez", "Diaz", "Moreno", "Alvarez"
    };
    private static final String[] DEPARTAMENTOS = {
            "Tecnología", "RRHH", "Contabilidad", "Gerencia",
            "Marketing", "Ventas", "Soporte", "Legal"
    };
    private static final String[] CARGOS = {
            "Desarrollador", "Analista", "Gerente", "Coordinador",
            "Asistente", "Director", "Consultor", "Técnico"
    };

    public StressLoaderService(BackendClient backendClient) {
        this.backendClient = backendClient;
    }

    public EmpleadoDTO cargarNehemias() {
        EmpleadoDTO nehemias = new EmpleadoDTO(
                null, "EMP-NEH-001", "Nehemias", "Perez",
                "Tecnología", "Ingeniero de Software", 1L, null
        );
        return backendClient.crearEmpleado(nehemias);
    }

    public StressResult cargaMasiva(int cantidad) {
        long inicio = System.currentTimeMillis();
        int batchSize = 100;
        int totalCreados = 0;

        for (int i = 0; i < cantidad; i += batchSize) {
            int fin = Math.min(i + batchSize, cantidad);
            List<EmpleadoDTO> batch = new ArrayList<>();

            for (int j = i; j < fin; j++) {
                String codigo = String.format("STRESS-%05d", j + 1);
                Long ubicacionId = random.nextBoolean() ? (long) (random.nextInt(3) + 1) : null;

                batch.add(new EmpleadoDTO(
                        null,
                        codigo,
                        NOMBRES[random.nextInt(NOMBRES.length)],
                        APELLIDOS[random.nextInt(APELLIDOS.length)],
                        DEPARTAMENTOS[random.nextInt(DEPARTAMENTOS.length)],
                        CARGOS[random.nextInt(CARGOS.length)],
                        ubicacionId,
                        null
                ));
            }

            List<EmpleadoDTO> creados = backendClient.crearBatch(batch);
            totalCreados += creados.size();
        }

        long duracion = System.currentTimeMillis() - inicio;
        int omitidos = cantidad - totalCreados;
        double promedio = cantidad > 0 ? (double) duracion / cantidad : 0;

        return new StressResult(cantidad, totalCreados, omitidos, duracion, promedio);
    }
}
