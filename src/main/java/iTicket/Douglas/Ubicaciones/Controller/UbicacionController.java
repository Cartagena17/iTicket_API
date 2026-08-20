package iTicket.Douglas.Ubicaciones.Controller;

import iTicket.Douglas.Response.ApiResponse;
import iTicket.Douglas.Ubicaciones.DTO.UbicacionDTO;
import iTicket.Douglas.Ubicaciones.Service.UbicacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/api/ubicaciones")
@RequiredArgsConstructor
public class UbicacionController {

    private final UbicacionService service;

    @PostMapping
    public ResponseEntity<ApiResponse<UbicacionDTO>> nuevaUbicacion(@Valid @RequestBody UbicacionDTO json) {
        UbicacionDTO dto = service.nuevaUbicacion(json);
        log.info("Nueva Ubicacion: " + dto);
        ApiResponse<UbicacionDTO> respuesta = new ApiResponse<>(true, "Datos ingresados correctamente", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UbicacionDTO>>> obtenerDatos() {
        List<UbicacionDTO> lista = service.obtenerTodo();
        ApiResponse<List<UbicacionDTO>> respuesta = new ApiResponse<>(true, "Datos encontrados", lista);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UbicacionDTO>> obtenerDatosPorId(@PathVariable Long id) {
        UbicacionDTO dto = service.buscarUbicacionPorId(id);
        log.info("Se obtuvieron los datos de la Ubicacion " + dto);
        ApiResponse<UbicacionDTO> respuesta = new ApiResponse<>(true, "Se obtuvieron los datos de la Ubicacion mediante su ID: " + id, dto);
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarUbicacion(@PathVariable Long id) {
        boolean eliminado = service.eliminarData(id);
        if (eliminado) {
            log.info("La Ubicacion con ID: " + id + " ya fue eliminada");
            ApiResponse<Void> respuesta = new ApiResponse<>(true, "La Ubicacion con ID: " + id + " ya fue eliminada");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuesta);
        }
        ApiResponse<Void> respuesta = new ApiResponse<>(false, "La Ubicacion con ID: " + id + " no fue encontrada");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UbicacionDTO>> actualizarData(@PathVariable Long id, @Valid @RequestBody UbicacionDTO dto) {
        UbicacionDTO data = service.actualizar(id, dto);
        log.info("La Ubicacion con ID: " + id + " ha sido actualizada");
        ApiResponse<UbicacionDTO> respuesta = new ApiResponse<>(true, "La Ubicacion con ID: " + id + " ha sido actualizada", data);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/nombreUbicacion/{nombreUbicacion}")
    public ResponseEntity<ApiResponse<UbicacionDTO>> buscarUbicacionPorNombre(@PathVariable String nombreUbicacion) {
        UbicacionDTO data = service.buscarUbicacionPorNombre(nombreUbicacion);
        log.info("La Ubicacion encontrada con nombre: " + nombreUbicacion);
        ApiResponse<UbicacionDTO> respuesta = new ApiResponse<>(true, "Se obtuvieron los datos de la Ubicacion con nombre: " + nombreUbicacion, data);
        return ResponseEntity.ok(respuesta);
    }
}