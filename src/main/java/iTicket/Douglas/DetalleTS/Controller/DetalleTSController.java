package iTicket.Douglas.DetalleTS.Controller;

import iTicket.Douglas.DetalleTS.DTO.DetalleTSDTO;
import iTicket.Douglas.DetalleTS.Service.DetalleTSService;
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
@CrossOrigin
@RequestMapping("/api/detalleTS")
@RequiredArgsConstructor
public class DetalleTSController {

    private final DetalleTSService service;

    @PostMapping
    public ResponseEntity<ApiResponse<DetalleTSDTO>> agregarDetalleTS(@Valid @RequestBody DetalleTSDTO json) {
        DetalleTSDTO dto = service.nuevoDetalleTS(json);
        log.info("Nuevo detalle ingresado: " + dto);
        ApiResponse<DetalleTSDTO> respuesta = new ApiResponse<>(true, "Nuevo detalle ingresado", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DetalleTSDTO>>> obtenerDetalleTS() {
        List<DetalleTSDTO> lista = service.obtenerTodos();
        log.info("Se obtuvo con éxito el detalle de ticket");
        ApiResponse<List<DetalleTSDTO>> respuesta = new ApiResponse<>(true, "El proceso de obtención se completó con éxito", lista);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/detalleTSIdTicket/{idTicket}")
    public ResponseEntity<ApiResponse<List<DetalleTSDTO>>> buscarDetalleTSIdTicket(@PathVariable Long idTicket) {
        List<DetalleTSDTO> datos = service.obtenerDetalleTSIdTicket(idTicket);
        log.info("Se obtuvo con éxito el detalle del ticket: " + idTicket);
        ApiResponse<List<DetalleTSDTO>> respuesta = new ApiResponse<>(true, "Se obtuvo con éxito el detalle del ticket: " + idTicket, datos);
        return ResponseEntity.ok(respuesta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DetalleTSDTO>> actualizarDetalleTS(@PathVariable Long id, @Valid @RequestBody DetalleTSDTO dto) {
        DetalleTSDTO datos = service.actualizarDetalleTS(id, dto);
        log.info("Detalle de ticket: " + id + ", actualizado con éxito");
        ApiResponse<DetalleTSDTO> respuesta = new ApiResponse<>(true, "Detalle de ticket: " + id + ", actualizado con éxito", datos);
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarDetalleTS(@PathVariable Long id) {
        boolean eliminado = service.eliminarDetalleTS(id);
        if (eliminado) {
            log.info("Detalle de ticket: " + id + ", eliminado");
            ApiResponse<Void> respuesta = new ApiResponse<>(true, "Detalle de ticket: " + id + ", eliminado");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuesta);
        }
        ApiResponse<Void> respuesta = new ApiResponse<>(false, "Detalle de ticket: " + id + ", no fue encontrado");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
    }
}