package iTicket.Douglas.Evaluaciones.Controller;

import iTicket.Douglas.Response.ApiResponse;
import iTicket.Douglas.Evaluaciones.DTO.MetricasDTO;
import iTicket.Douglas.Evaluaciones.DTO.EvaluacionesDTO;
import iTicket.Douglas.Evaluaciones.Service.EvaluacionesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/evaluaciones")
@CrossOrigin
@RequiredArgsConstructor
public class EvaluacionesController {

    private final EvaluacionesService service;

    @PostMapping
    public ResponseEntity<ApiResponse<EvaluacionesDTO>> registrarEvaluacion(@Valid @RequestBody EvaluacionesDTO json, @RequestParam Long idUsuario) {
        EvaluacionesDTO dto = service.nuevaEvaluacion(json, idUsuario);
        log.info("Nueva evaluación creada: " + dto);
        ApiResponse<EvaluacionesDTO> respuesta = new ApiResponse<>(true, "Evaluación ingresada correctamente", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
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

    @GetMapping("/contar_evaluaciones")
    public ResponseEntity<ApiResponse<Long>> obtenerTotalEvaluaciones() {
        long total = service.contarEvaluaciones();
        log.info("Total de evaluaciones consultado: " + total);
        ApiResponse<Long> respuesta = new ApiResponse<>(true, "Total de evaluaciones", total);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<EvaluacionesDTO>>> obtenerTodas(
            @RequestParam Long idUsuarioAdmin,
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) Double calificacion,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        Page<EvaluacionesDTO> pagina = service.obtenerEvaluacionesPaginadas(idUsuarioAdmin, busqueda, calificacion, fecha, pageable);
        log.info("Consulta paginada de evaluaciones realizada exitosamente");
        ApiResponse<Page<EvaluacionesDTO>> respuesta = new ApiResponse<>(true, "Evaluaciones consultadas correctamente", pagina);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/tecnico/calificaciones")
    public ResponseEntity<ApiResponse<List<Long>>> obtenerCalificacionesPorTecnico(@RequestParam Long idUsuario) {
        List<Long> distribucion = service.obtenerDistribucionCalificacionesPorTecnico(idUsuario);
        log.info("Distribución de calificaciones consultada para el técnico con ID: " + idUsuario);
        ApiResponse<List<Long>> respuesta = new ApiResponse<>(true, "Distribución de calificaciones obtenida", distribucion);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/usuario/calificaciones")
    public ResponseEntity<ApiResponse<List<Long>>> obtenerCalificacionesPorUsuario(@RequestParam Long idUsuario) {
        List<Long> distribucion = service.obtenerDistribucionCalificacionesPorUsuario(idUsuario);
        log.info("Distribución de calificaciones consultada para el usuario con ID: " + idUsuario);
        ApiResponse<List<Long>> respuesta = new ApiResponse<>(true, "Distribución de calificaciones obtenida", distribucion);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/metricas")
    public ResponseEntity<ApiResponse<MetricasDTO>> obtenerMetricas(
            @RequestParam Long idUsuarioAdmin,
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) Double calificacion,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        MetricasDTO metricas = service.obtenerMetricas(idUsuarioAdmin, busqueda, calificacion, fecha);
        log.info("Consulta de métricas globales realizada exitosamente");
        ApiResponse<MetricasDTO> respuesta = new ApiResponse<>(true, "Métricas consultadas correctamente", metricas);
        return ResponseEntity.ok(respuesta);
    }
}