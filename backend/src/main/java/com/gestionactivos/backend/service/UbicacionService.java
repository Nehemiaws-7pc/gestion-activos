package com.gestionactivos.backend.service;

import com.gestionactivos.backend.dto.ActivoDTO;
import com.gestionactivos.backend.dto.UbicacionDTO;
import com.gestionactivos.backend.dto.UbicacionDetalleDTO;
import com.gestionactivos.backend.model.Empleado;
import com.gestionactivos.backend.model.Ubicacion;
import com.gestionactivos.backend.repository.ActivoRepository;
import com.gestionactivos.backend.repository.EmpleadoRepository;
import com.gestionactivos.backend.repository.UbicacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UbicacionService {

    private final UbicacionRepository ubicacionRepository;
    private final EmpleadoRepository empleadoRepository;
    private final ActivoRepository activoRepository;
    private final ActivoService activoService;

    public List<UbicacionDTO> listarTodas() {
        return ubicacionRepository.findAll().stream().map(this::toDTO).toList();
    }

    public UbicacionDTO crear(UbicacionDTO dto) {
        if (ubicacionRepository.findByCodigo(dto.codigo()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe una ubicación con código: " + dto.codigo());
        }
        Ubicacion u = new Ubicacion();
        u.setCodigo(dto.codigo());
        u.setNombre(dto.nombre());
        u.setDireccion(dto.direccion());
        u.setDescripcion(dto.descripcion());
        return toDTO(ubicacionRepository.save(u));
    }

    public UbicacionDTO actualizar(Long id, UbicacionDTO dto) {
        Ubicacion u = ubicacionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ubicación no encontrada"));

        if (!u.getCodigo().equals(dto.codigo()) &&
                ubicacionRepository.findByCodigo(dto.codigo()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe una ubicación con código: " + dto.codigo());
        }
        u.setCodigo(dto.codigo());
        u.setNombre(dto.nombre());
        u.setDireccion(dto.direccion());
        u.setDescripcion(dto.descripcion());
        return toDTO(ubicacionRepository.save(u));
    }

    public void eliminar(Long id) {
        Ubicacion u = ubicacionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ubicación no encontrada"));

        long empleadosEnUbicacion = empleadoRepository.findByUbicacion(u).size();
        if (empleadosEnUbicacion > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La ubicación tiene " + empleadosEnUbicacion + " empleado(s) asignado(s). Reasígnalos primero.");
        }
        ubicacionRepository.delete(u);
    }

    public UbicacionDetalleDTO obtenerDetalle(Long id) {
        Ubicacion u = ubicacionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ubicación no encontrada"));

        List<Empleado> empleados = empleadoRepository.findByUbicacion(u);

        List<UbicacionDetalleDTO.EmpleadoConActivosDTO> empleadosConActivos = empleados.stream()
                .map(emp -> {
                    List<ActivoDTO> activos = activoRepository.findByEmpleado(emp).stream()
                            .map(activoService::toDTO)
                            .toList();
                    return new UbicacionDetalleDTO.EmpleadoConActivosDTO(
                            emp.getId(), emp.getCodigo(), emp.getNombre(),
                            emp.getApellido(), emp.getDepartamento(), emp.getCargo(),
                            activos
                    );
                })
                .toList();

        return new UbicacionDetalleDTO(
                u.getId(), u.getCodigo(), u.getNombre(),
                u.getDireccion(), u.getDescripcion(), empleadosConActivos
        );
    }

    private UbicacionDTO toDTO(Ubicacion u) {
        return new UbicacionDTO(u.getId(), u.getCodigo(), u.getNombre(),
                u.getDireccion(), u.getDescripcion());
    }
}
