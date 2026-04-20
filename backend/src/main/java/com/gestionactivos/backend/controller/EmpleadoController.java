package com.gestionactivos.backend.controller;

import com.gestionactivos.backend.dto.ActivoDTO;
import com.gestionactivos.backend.dto.EmpleadoDTO;
import com.gestionactivos.backend.service.EmpleadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.gestionactivos.backend.util.QrUtil;
import org.springframework.http.MediaType;

import java.util.List;

@RestController
@RequestMapping("/api/empleados")
@RequiredArgsConstructor
public class EmpleadoController {

    private final EmpleadoService service;
    private final QrUtil qrUtil;

    @GetMapping
    public ResponseEntity<List<EmpleadoDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/buscar")
    public ResponseEntity<EmpleadoDTO> buscar(@RequestParam String codigo) {
        return ResponseEntity.ok(service.buscarPorCodigo(codigo));
    }

    @GetMapping("/{codigo}/activos")
    public ResponseEntity<List<ActivoDTO>> getActivos(@PathVariable String codigo) {
        return ResponseEntity.ok(service.getActivosDeEmpleado(codigo));
    }

    @GetMapping(value = "/{codigo}/qr", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generarQr(@PathVariable String codigo) {
        try {
            service.buscarPorCodigo(codigo);
            byte[] qr = qrUtil.generarQr("EMP:" + codigo, 300);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(qr);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    //@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<EmpleadoDTO> crear(@RequestBody EmpleadoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PostMapping("/batch")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<EmpleadoDTO>> crearBatch(@RequestBody List<EmpleadoDTO> dtos) {
        List<EmpleadoDTO> creados = service.crearBatch(dtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(creados);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<EmpleadoDTO> actualizar(@PathVariable Long id, @RequestBody EmpleadoDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
