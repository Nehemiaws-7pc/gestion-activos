package com.gestionactivos.backend.service;

import com.gestionactivos.backend.dto.ActivoDTO;
import com.gestionactivos.backend.dto.ActivoUpdateDTO;
import com.gestionactivos.backend.model.Activo;
import com.gestionactivos.backend.model.Empleado;
import com.gestionactivos.backend.repository.ActivoRepository;
import com.gestionactivos.backend.repository.EmpleadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivoService {

    private final ActivoRepository repository;
    private final EmpleadoRepository empleadoRepository;

    public ActivoDTO crear(ActivoDTO dto) {
        Activo activo = new Activo();
        activo.setCodigo(dto.codigo());
        activo.setDescripcion(dto.descripcion());
        activo.setValorAdquisicion(dto.valorAdquisicion());
        activo.setFechaAdquisicion(dto.fechaAdquisicion());
        activo.setValorActual(dto.valorAdquisicion());

        if (dto.empleadoCodigo() != null && !dto.empleadoCodigo().isBlank()) {
            Empleado emp = empleadoRepository.findByCodigo(dto.empleadoCodigo())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Empleado no encontrado: " + dto.empleadoCodigo()));
            activo.setEmpleado(emp);
            activo.setEstado("ASIGNADO");
            activo.setUbicacionActual(dto.ubicacionActual() != null ? dto.ubicacionActual() : emp.getDepartamento());
        } else {
            activo.setEstado("DISPONIBLE");
            activo.setUbicacionActual(dto.ubicacionActual() != null ? dto.ubicacionActual() : "Bodega Principal");
        }

        return toDTO(repository.save(activo));
    }

    public List<ActivoDTO> listarTodos() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    public ActivoDTO buscarPorCodigo(String codigo) {
        return repository.findByCodigo(codigo)
                .map(this::toDTO)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Activo no encontrado: " + codigo));
    }

    public ActivoDTO actualizar(Long id, ActivoUpdateDTO dto) {
        Activo activo = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Activo no encontrado"));

        activo.setDescripcion(dto.descripcion());
        activo.setValorActual(dto.valorActual());
        activo.setEstado(dto.estado());
        activo.setUbicacionActual(dto.ubicacionActual());

        if (dto.empleadoCodigo() != null && !dto.empleadoCodigo().isBlank()) {
            Empleado emp = empleadoRepository.findByCodigo(dto.empleadoCodigo())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Empleado no encontrado: " + dto.empleadoCodigo()));
            activo.setEmpleado(emp);
        } else {
            activo.setEmpleado(null);
        }

        return toDTO(repository.save(activo));
    }

    public ActivoDTO toDTO(Activo a) {
        Empleado emp = a.getEmpleado();
        return new ActivoDTO(
                a.getId(),
                a.getCodigo(),
                a.getDescripcion(),
                a.getValorAdquisicion(),
                a.getFechaAdquisicion(),
                a.getValorActual(),
                a.getEstado(),
                a.getUbicacionActual(),
                emp != null ? emp.getId() : null,
                emp != null ? emp.getCodigo() : null,
                emp != null ? emp.getNombre() + " " + emp.getApellido() : null
        );
    }
}
