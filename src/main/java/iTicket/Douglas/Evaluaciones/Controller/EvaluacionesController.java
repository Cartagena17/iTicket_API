package iTicket.Douglas.Evaluaciones.Controller;

import iTicket.Douglas.Response.ApiResponse;
import iTicket.Douglas.Evaluaciones.DTO.EvaluacionesDTO;
import iTicket.Douglas.Evaluaciones.Service.EvaluacionesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/evaluaciones")
@CrossOrigin
@RequiredArgsConstructor
public class EvaluacionesController {

    private final EvaluacionesService service;

    @PostMapping
    public ResponseEntity<ApiResponse<EvaluacionesDTO>> registrarEvaluacion(@Valid @RequestBody EvaluacionesDTO json) {
        EvaluacionesDTO dto = service.nuevaEvaluacion(json);
        log.info("Nueva evaluación creada: " + dto);
        ApiResponse<EvaluacionesDTO> respuesta = new ApiResponse<>(true, "Evaluación ingresada correctamente", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EvaluacionesDTO>>> obtenerTodas() {
        List<EvaluacionesDTO> lista = service.obtenerTodas();
        log.info("Datos de evaluaciones consultados");
        ApiResponse<List<EvaluacionesDTO>> respuesta = new ApiResponse<>(true, "Evaluaciones encontradas", lista);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EvaluacionesDTO>> obtenerPorId(@PathVariable Long id) {
        EvaluacionesDTO dto = service.buscarPorId(id);
        log.info("Se obtuvieron los datos de la evaluación con ID: " + id);
        ApiResponse<EvaluacionesDTO> respuesta = new ApiResponse<>(true, "Evaluación encontrada", dto);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/ticket/{idTicket}")
    public ResponseEntity<ApiResponse<EvaluacionesDTO>> obtenerPorTicket(@PathVariable Long idTicket) {
        EvaluacionesDTO dto = service.buscarPorTicket(idTicket);
        log.info("Se obtuvo la evaluación para el ticket con ID: " + idTicket);
        ApiResponse<EvaluacionesDTO> respuesta = new ApiResponse<>(true, "Evaluación encontrada para el ticket", dto);
        return ResponseEntity.ok(respuesta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EvaluacionesDTO>> actualizarEvaluacion(@PathVariable Long id, @Valid @RequestBody EvaluacionesDTO dto) {
        EvaluacionesDTO data = service.actualizarEvaluacion(id, dto);
        log.info("Evaluación con ID: " + id + " actualizada.");
        ApiResponse<EvaluacionesDTO> respuesta = new ApiResponse<>(true, "Evaluación actualizada correctamente", data);
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarEvaluacion(@PathVariable Long id) {
        boolean eliminado = service.eliminarEvaluacion(id);
        if (eliminado) {
            log.info("Evaluación con ID: " + id + " eliminada.");
            ApiResponse<Void> respuesta = new ApiResponse<>(true, "Evaluación con ID: " + id + ", eliminada");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuesta);
        }
        ApiResponse<Void> respuesta = new ApiResponse<>(false, "Evaluación con ID: " + id + ", no encontrada");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
    }
}