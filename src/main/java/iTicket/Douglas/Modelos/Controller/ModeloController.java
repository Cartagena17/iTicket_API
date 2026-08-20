package iTicket.Douglas.Modelos.Controller;

import iTicket.Douglas.Modelos.DTO.ModeloDTO;
import iTicket.Douglas.Modelos.Service.ModeloService;
import iTicket.Douglas.Response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/modelos")
@RequiredArgsConstructor
@CrossOrigin
public class ModeloController {

    private final ModeloService service;

    @PostMapping
    public ResponseEntity<ApiResponse<ModeloDTO>> nuevoModelo(@Valid @RequestBody ModeloDTO json) {
        ModeloDTO dto = service.nuevoModelo(json);
        log.info("Nuevo modelo registrado " + dto);
        ApiResponse<ModeloDTO> respuesta = new ApiResponse<>(true, "Datos registrados exitosamente", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ModeloDTO>>> obtenerDatos() {
        List<ModeloDTO> lista = service.obtenerTodo();
        ApiResponse<List<ModeloDTO>> respuesta = new ApiResponse<>(true, "Datos encontrados", lista);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ModeloDTO>> obtenerDatosId(@PathVariable Long id) {
        ModeloDTO dto = service.obtenerPorId(id);
        log.info("Se obtuvieron los datos del modelo con id " + id);
        ApiResponse<ModeloDTO> respuesta = new ApiResponse<>(true, "Se obtuvieron los datos del modelo con id " + id, dto);
        return ResponseEntity.ok(respuesta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ModeloDTO>> actualizarModelo(@PathVariable Long id, @Valid @RequestBody ModeloDTO dto) {
        ModeloDTO data = service.actualizarData(id, dto);
        log.info("Se logró actualizar el modelo con id " + id);
        ApiResponse<ModeloDTO> respuesta = new ApiResponse<>(true, "Se logró actualizar el modelo con id " + id, data);
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarDatos(@PathVariable Long id) {
        boolean eliminado = service.eliminarModelo(id);
        if (eliminado) {
            log.info("Se logró eliminar el modelo con id " + id);
            ApiResponse<Void> respuesta = new ApiResponse<>(true, "El modelo con ID: " + id + " ha sido eliminado");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuesta);
        }
        ApiResponse<Void> respuesta = new ApiResponse<>(false, "El modelo con ID: " + id + " no se encontró");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
    }
}