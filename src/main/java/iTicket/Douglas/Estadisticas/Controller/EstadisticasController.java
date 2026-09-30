package iTicket.Douglas.Estadisticas.Controller;

import iTicket.Douglas.Articulos.DTO.ReportadosDTO;
import iTicket.Douglas.Estadisticas.Service.EstadisticasService;
import iTicket.Douglas.Response.AlertaInsatisfaccionDTO;
import iTicket.Douglas.Response.ApiResponse;
import iTicket.Douglas.Response.MetricasResponseDTO;
import iTicket.Douglas.Response.PaginatedResponseDTO;
import iTicket.Douglas.Security.SecurityUtils;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/estadisticas")
@Validated
public class EstadisticasController {

    private final EstadisticasService estadisticasService;

    @Autowired
    public EstadisticasController(EstadisticasService estadisticasService) {
        this.estadisticasService = estadisticasService;
    }

    // ================================================================
    // ENDPOINT CONSOLIDADO DE MÉTRICAS (Dashboard principal)
    // GET /api/estadisticas/metricas
    // El id del admin ya no viaja en la URL: sale del usuario autenticado en la cookie.
    // ================================================================
    @GetMapping("/metricas")
    public ResponseEntity<ApiResponse<MetricasResponseDTO>> obtenerMetricas(
            @RequestParam(value = "fechaInicio", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,

            @RequestParam(value = "fechaFin", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,

            @RequestParam(value = "pageAlertas", defaultValue = "0") @Min(value = 0, message = "La página mínima es 0") int pageAlertas,
            @RequestParam(value = "sizeAlertas", defaultValue = "5") @Min(value = 5, message = "El tamaño mínimo de página es 5") @Max(value = 50, message = "El tamaño máximo de página es 50") int sizeAlertas,

            @RequestParam(value = "pageEquipos", defaultValue = "0") @Min(value = 0, message = "La página mínima es 0") int pageEquipos,
            @RequestParam(value = "sizeEquipos", defaultValue = "5") @Min(value = 5, message = "El tamaño mínimo de página es 5") @Max(value = 50, message = "El tamaño máximo de página es 50") int sizeEquipos) {

        Long idUsuarioAdmin = SecurityUtils.idUsuarioActual();
        MetricasResponseDTO metricas = estadisticasService.obtenerMetricas(
                idUsuarioAdmin,
                fechaInicio,
                fechaFin,
                PageRequest.of(pageAlertas, sizeAlertas),
                PageRequest.of(pageEquipos, sizeEquipos)
        );

        return ResponseEntity.ok(new ApiResponse<>(true, "Métricas obtenidas con éxito", metricas));
    }

    // ================================================================
    // TABLA 1: ALERTAS DE INSATISFACCIÓN
    // GET /api/estadisticas/alertas
    // ================================================================
    @GetMapping("/alertas")
    public ResponseEntity<ApiResponse<PaginatedResponseDTO<AlertaInsatisfaccionDTO>>> obtenerAlertasInsatisfaccion(
            @RequestParam(value = "fechaInicio", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,

            @RequestParam(value = "fechaFin", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,

            @RequestParam(value = "umbral", required = false) Integer umbral,

            @RequestParam(value = "page", defaultValue = "0") @Min(value = 0, message = "La página mínima es 0") int page,
            @RequestParam(value = "size", defaultValue = "5")
            @Min(value = 5, message = "El tamaño mínimo de página es 5")
            @Max(value = 50, message = "El tamaño máximo de página es 50") int size) {

        Long idUsuarioAdmin = SecurityUtils.idUsuarioActual();
        LocalDateTime inicio = fechaInicio != null ? fechaInicio.atStartOfDay() : null;
        LocalDateTime fin = fechaFin != null ? fechaFin.atTime(LocalTime.MAX) : null;

        PaginatedResponseDTO<AlertaInsatisfaccionDTO> alertas =
                estadisticasService.obtenerAlertasInsatisfaccion(idUsuarioAdmin, inicio, fin, umbral, PageRequest.of(page, size));

        return ResponseEntity.ok(new ApiResponse<>(true, "Alertas obtenidas con éxito", alertas));
    }

    // ================================================================
    // TABLA 2: EQUIPOS MÁS REPORTADOS
    // GET /api/estadisticas/equipos-reportados
    // ================================================================
    @GetMapping("/equipos-reportados")
    public ResponseEntity<ApiResponse<PaginatedResponseDTO<ReportadosDTO>>> obtenerArticulosMasReportados(
            @RequestParam(value = "fechaInicio", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,

            @RequestParam(value = "fechaFin", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,

            @RequestParam(value = "page", defaultValue = "0") @Min(value = 0, message = "La página mínima es 0") int page,
            @RequestParam(value = "size", defaultValue = "5")
            @Min(value = 5, message = "El tamaño mínimo de página es 5")
            @Max(value = 50, message = "El tamaño máximo de página es 50") int size) {

        Long idUsuarioAdmin = SecurityUtils.idUsuarioActual();
        LocalDateTime inicio = fechaInicio != null ? fechaInicio.atStartOfDay() : null;
        LocalDateTime fin = fechaFin != null ? fechaFin.atTime(LocalTime.MAX) : null;

        PaginatedResponseDTO<ReportadosDTO> equipos =
                estadisticasService.obtenerArticulosMasReportados(idUsuarioAdmin, inicio, fin, PageRequest.of(page, size));

        return ResponseEntity.ok(new ApiResponse<>(true, "Equipos más reportados obtenidos con éxito", equipos));
    }

    // ================================================================
    // GRÁFICA: TIEMPO DE RESOLUCIÓN POR DÍA DE SEMANA
    // GET /api/estadisticas/resolucion-por-dia
    // ================================================================
    @GetMapping("/resolucion-por-dia")
    public ResponseEntity<ApiResponse<List<Object[]>>> obtenerResolucionPorDia(
            @RequestParam(value = "fechaInicio", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,

            @RequestParam(value = "fechaFin", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        Long idUsuarioAdmin = SecurityUtils.idUsuarioActual();
        LocalDateTime inicio = fechaInicio != null ? fechaInicio.atStartOfDay() : null;
        LocalDateTime fin = fechaFin != null ? fechaFin.atTime(LocalTime.MAX) : null;

        List<Object[]> data = estadisticasService.obtenerResolucionPorDiaSemana(idUsuarioAdmin, inicio, fin);

        return ResponseEntity.ok(new ApiResponse<>(true, "Resolución por día obtenida con éxito", data));
    }
}
