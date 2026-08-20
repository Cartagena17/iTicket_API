package iTicket.Douglas.Evaluaciones.Controller;

import iTicket.Douglas.Response.ApiResponse;
import iTicket.Douglas.Evaluaciones.DTO.MetricasDTO;
import iTicket.Douglas.Evaluaciones.DTO.EvaluacionesDTO;
import iTicket.Douglas.Evaluaciones.Service.EvaluacionesService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;


@Slf4j
@RestController
@RequestMapping("/api/evaluaciones")
@CrossOrigin
public class EvaluacionesController {

    private final EvaluacionesService service;

    public EvaluacionesController(EvaluacionesService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EvaluacionesDTO>> registrarEvaluacion(@Valid @RequestBody EvaluacionesDTO json) {
        try {
            EvaluacionesDTO dto = service.nuevaEvaluacion(json);
            if (dto != null) {
                log.info("Nueva evaluación creada: " + dto);
                ApiResponse<EvaluacionesDTO> respuestaExito = new ApiResponse<>(true, "Evaluación ingresada correctamente", dto);
                return ResponseEntity.status(HttpStatus.CREATED).body(respuestaExito);
            }
            log.warn("Intento de registro de evaluación fallido: " + json);
            ApiResponse<EvaluacionesDTO> respuestaFallida = new ApiResponse<>(false, "Intento fallido de registro de evaluación");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaFallida);
        } catch (Exception e) {
            log.error("El proceso presentó fallas inesperadas: " + e.getMessage());
            ApiResponse<EvaluacionesDTO> respuestaError = new ApiResponse<>(false, "El proceso no se pudo completar: " + e.getMessage(), json);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EvaluacionesDTO>> obtenerPorId(@PathVariable Long id) {
        try {
            EvaluacionesDTO dto = service.buscarPorId(id);
            if (dto != null) {
                log.info("Se obtuvieron los datos de la evaluación con ID: " + id);
                ApiResponse<EvaluacionesDTO> respuestaExito = new ApiResponse<>(true, "Evaluación encontrada", dto);
                return ResponseEntity.ok(respuestaExito);
            }
            log.info("Evaluación no encontrada con ID: " + id);
            ApiResponse<EvaluacionesDTO> respuestaNoEncontrada = new ApiResponse<>(false, "Evaluación no encontrada con ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        } catch (Exception e) {
            log.error("Error al obtener la evaluación con ID: " + id);
            ApiResponse<EvaluacionesDTO> respuestaError = new ApiResponse<>(false, "No se pudo obtener la evaluación");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @GetMapping("/ticket/{idTicket}")
    public ResponseEntity<ApiResponse<EvaluacionesDTO>> obtenerPorTicket(@PathVariable Long idTicket) {
        try {
            EvaluacionesDTO dto = service.buscarPorTicket(idTicket);
            if (dto != null) {
                log.info("Se obtuvo la evaluación para el ticket con ID: " + idTicket);
                ApiResponse<EvaluacionesDTO> respuestaExito = new ApiResponse<>(true, "Evaluación encontrada para el ticket", dto);
                return ResponseEntity.ok(respuestaExito);
            }
            log.info("Evaluación no encontrada para el ticket con ID: " + idTicket);
            ApiResponse<EvaluacionesDTO> respuestaNoEncontrada = new ApiResponse<>(false, "Evaluación no encontrada para el ticket");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        } catch (Exception e) {
            log.error("Error al obtener la evaluación para el ticket con ID: " + idTicket);
            ApiResponse<EvaluacionesDTO> respuestaError = new ApiResponse<>(false, "No se pudo obtener la evaluación del ticket");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EvaluacionesDTO>> actualizarEvaluacion(@PathVariable Long id, @Valid @RequestBody EvaluacionesDTO dto) {
        try {
            EvaluacionesDTO data = service.actualizarEvaluacion(id, dto);
            if (data != null) {
                log.info("Evaluación con ID: " + id + " actualizada.");
                ApiResponse<EvaluacionesDTO> respuestaExitosa = new ApiResponse<>(true, "Evaluación actualizada correctamente", data);
                return ResponseEntity.ok(respuestaExitosa);
            }
            log.warn("No se pudo completar la actualización de la evaluación con ID: " + id);
            ApiResponse<EvaluacionesDTO> respuestaNoCompletada = new ApiResponse<>(false, "No se pudo completar la actualización");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaNoCompletada);
        } catch (Exception e) {
            log.error("Error al actualizar la evaluación con ID: " + id + " - " + e.getMessage());
            ApiResponse<EvaluacionesDTO> respuestaError = new ApiResponse<>(false, "Error al actualizar la evaluación: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarEvaluacion(@PathVariable Long id) {
        try {
            boolean respuesta = service.eliminarEvaluacion(id);
            if (respuesta) {
                log.info("Evaluación con ID: " + id + " eliminada.");
                ApiResponse<Void> respuestaExitosa = new ApiResponse<>(true, "Evaluación con ID: " + id + ", eliminada");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuestaExitosa);
            }
            log.info("Evaluación con ID: " + id + " no encontrada.");
            ApiResponse<Void> respuestaNoEncontrada = new ApiResponse<>(false, "Evaluación con ID: " + id + ", no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        } catch (Exception e) {
            log.error("Error al eliminar la evaluación con ID: " + id);
            ApiResponse<Void> respuestaError = new ApiResponse<>(false, "Error crítico al eliminar la evaluación");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }
    //Contar el total de evaluaciones
    @GetMapping("/contar_evaluaciones")
    public ResponseEntity<ApiResponse<Long>> obtenerTotalEvaluaciones() {
        try {
            long total = service.contarEvaluaciones();
            log.info("Total de evaluaciones consultado: " + total);
            ApiResponse<Long> respuesta = new ApiResponse<>(true, "Total de evaluaciones", total);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            log.error("Error al obtener el total de evaluaciones: " + e.getMessage());
            ApiResponse<Long> respuestaError = new ApiResponse<>(false, "Error al obtener el total");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }



    // paginacion
    @GetMapping
    public ResponseEntity<ApiResponse<Page<EvaluacionesDTO>>> obtenerTodas(
           // Parametros opcionales de busqueda que vienen en la URL
           @RequestParam(required = false) String busqueda,
           @RequestParam(required = false) Double calificacion,
           @RequestParam(required = false) @DateTimeFormat( iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
           @PageableDefault(page = 0, size = 10) Pageable pageable){
       try {
           Page<EvaluacionesDTO> pagina = service.obtenerEvaluacionesPaginadas(busqueda, calificacion, fecha, pageable);

           log.info("Consulta paginada de evaluaciones realizada exitosamente");
           ApiResponse<Page<EvaluacionesDTO>> respuestaExito = new ApiResponse<>( true, "Evaluaciones consultadas correctamente", pagina);
           return ResponseEntity.ok(respuestaExito);

       } catch (Exception e) {
           log.error("Error al obtener las evaluación, Paginadas: " + e.getMessage());
           ApiResponse<Page<EvaluacionesDTO>> respuestaError = new ApiResponse<>(false, "Error del servidor: " + e.getMessage());
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
       }
    }

    @GetMapping("/metricas")
    public  ResponseEntity<ApiResponse<MetricasDTO>> obtenerMetricas(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) Double calificacion,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha){
        try{
            MetricasDTO metricas = service.obtenerMetricas(busqueda, calificacion, fecha);
            log.info("Consulta de metricas globales realizada exitosamente");
            ApiResponse<MetricasDTO>  respuesExitosa = new ApiResponse<>(true, "Metricas consultadas correctamente", metricas);
            return  ResponseEntity.ok(respuesExitosa);
        } catch (Exception e) {
            log.error("Error al obtener metricas de evaluación: " + e.getMessage());
            ApiResponse<MetricasDTO> respuestaError = new ApiResponse<>(false, "Error del servidor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }



}
