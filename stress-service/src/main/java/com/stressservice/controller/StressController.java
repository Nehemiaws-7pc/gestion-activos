package com.stressservice.controller;

import com.stressservice.dto.EmpleadoDTO;
import com.stressservice.dto.StressResult;
import com.stressservice.service.BackendClient;
import com.stressservice.service.StressLoaderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stress")
public class StressController {

    private final StressLoaderService loaderService;
    private final BackendClient backendClient;

    public StressController(StressLoaderService loaderService, BackendClient backendClient) {
        this.loaderService = loaderService;
        this.backendClient = backendClient;
    }

    /**
     * Inserta a "Nehemias Perez" en la tabla empleados.
     * POST http://localhost:8081/stress/nehemias
     */
    @PostMapping("/nehemias")
    public ResponseEntity<EmpleadoDTO> cargarNehemias() {
        return ResponseEntity.ok(loaderService.cargarNehemias());
    }

    /**
     * Carga masiva de empleados aleatorios.
     * POST http://localhost:8081/stress/carga?cantidad=1000
     */
    @PostMapping("/carga")
    public ResponseEntity<StressResult> cargaMasiva(@RequestParam(defaultValue = "1000") int cantidad) {
        return ResponseEntity.ok(loaderService.cargaMasiva(cantidad));
    }

    /**
     * Ver todos los empleados del backend.
     * GET http://localhost:8081/stress/empleados
     */
    @GetMapping("/empleados")
    public ResponseEntity<List<EmpleadoDTO>> listarEmpleados() {
        return ResponseEntity.ok(backendClient.listarEmpleados());
    }

    /**
     * Health check.
     * GET http://localhost:8081/stress/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "stress-service"));
    }
}
