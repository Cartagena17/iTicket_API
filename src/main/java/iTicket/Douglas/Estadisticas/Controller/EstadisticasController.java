package iTicket.Douglas.Estadisticas.Controller;

import iTicket.Douglas.Estadisticas.Service.EstadisticasService;
import iTicket.Douglas.Response.ApiResponse;
import iTicket.Douglas.Response.MetricasResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/estadisticas")
@CrossOrigin(origins = "*")
public class EstadisticasController {

    private final EstadisticasService estadisticasService;

    @Autowired
    public EstadisticasController(EstadisticasService estadisticasService) {
        this.estadisticasService = estadisticasService;
    }

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

        ApiResponse<MetricasResponseDTO> response = new ApiResponse<>(
                true, "Métricas obtenidas con éxito", metricas);

        return ResponseEntity.ok(response);
    }
}
