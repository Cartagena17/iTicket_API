package iTicket.Douglas.Estadisticas.Controller;

import iTicket.Douglas.Articulos.DTO.ReportadosDTO;
import iTicket.Douglas.Estadisticas.Service.EstadisticasService;
import iTicket.Douglas.Response.AlertaInsatisfaccionDTO;
import iTicket.Douglas.Response.ApiResponse;
import iTicket.Douglas.Response.MetricasResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/estadisticas")
@CrossOrigin(origins = "*")
public class EstadisticasController {

    private final EstadisticasService estadisticasService;

    @Autowired
    public EstadisticasController(EstadisticasService estadisticasService) {
        this.estadisticasService = estadisticasService;
    }

    // ================================================================
    // ENDPOINT CONSOLIDADO DE MÉTRICAS (Dashboard principal)
    // GET /api/estadisticas/metricas
    // ================================================================
    @GetMapping("/metricas")
    public ResponseEntity<ApiResponse<MetricasResponseDTO>> obtenerMetricas(
            @RequestParam(value = "fechaInicio", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,

            @RequestParam(value = "fechaFin", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,

            @RequestParam(value = "pageAlertas", defaultValue = "0") int pageAlertas,
            @RequestParam(value = "sizeAlertas", defaultValue = "5") int sizeAlertas,

            @RequestParam(value = "pageEquipos", defaultValue = "0") int pageEquipos,
            @RequestParam(value = "sizeEquipos", defaultValue = "5") int sizeEquipos) {

        MetricasResponseDTO metricas = estadisticasService.obtenerMetricas(
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
    public ResponseEntity<ApiResponse<List<AlertaInsatisfaccionDTO>>> obtenerAlertasInsatisfaccion(
            @RequestParam(value = "fechaInicio", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,

            @RequestParam(value = "fechaFin", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,

            @RequestParam(value = "umbral", required = false) Integer umbral) {

        LocalDateTime inicio = fechaInicio != null ? fechaInicio.atStartOfDay() : null;
        LocalDateTime fin = fechaFin != null ? fechaFin.atTime(LocalTime.MAX) : null;

        List<AlertaInsatisfaccionDTO> alertas =
                estadisticasService.obtenerAlertasInsatisfaccion(inicio, fin, umbral);

        return ResponseEntity.ok(new ApiResponse<>(true, "Alertas obtenidas con éxito", alertas));
    }

    // ================================================================
    // TABLA 2: EQUIPOS MÁS REPORTADOS
    // GET /api/estadisticas/equipos-reportados
    // ================================================================
    @GetMapping("/equipos-reportados")
    public ResponseEntity<ApiResponse<List<ReportadosDTO>>> obtenerArticulosMasReportados(
            @RequestParam(value = "fechaInicio", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,

            @RequestParam(value = "fechaFin", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        LocalDateTime inicio = fechaInicio != null ? fechaInicio.atStartOfDay() : null;
        LocalDateTime fin = fechaFin != null ? fechaFin.atTime(LocalTime.MAX) : null;

        List<ReportadosDTO> equipos =
                estadisticasService.obtenerArticulosMasReportados(inicio, fin);

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

        LocalDateTime inicio = fechaInicio != null ? fechaInicio.atStartOfDay() : null;
        LocalDateTime fin = fechaFin != null ? fechaFin.atTime(LocalTime.MAX) : null;

        List<Object[]> data = estadisticasService.obtenerResolucionPorDiaSemana(inicio, fin);

        return ResponseEntity.ok(new ApiResponse<>(true, "Resolución por día obtenida con éxito", data));
    }
}