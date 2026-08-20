package iTicket.Douglas.DetalleFases.Controller;

import iTicket.Douglas.DetalleFases.DTO.DetalleFDTO;
import iTicket.Douglas.DetalleFases.Service.DetalleFService;
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
@RequestMapping("/api/detalleFase")
@RequiredArgsConstructor
public class DetalleFController {

    private final DetalleFService service;

    @PostMapping
    public ResponseEntity<ApiResponse<DetalleFDTO>> nuevoDetalleF(@Valid @RequestBody DetalleFDTO json) {
        DetalleFDTO dto = service.nuevoDetalleF(json);
        log.info("Nuevo detalle de fase ingresado: " + dto);
        ApiResponse<DetalleFDTO> respuesta = new ApiResponse<>(true, "Nuevo detalle de fase ingresado", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DetalleFDTO>>> obtenerDetallesF() {
        List<DetalleFDTO> lista = service.obtenerDetallesF();
        log.info("Datos de detalle de fase consultados");
        ApiResponse<List<DetalleFDTO>> respuesta = new ApiResponse<>(true, "Datos de detalle de fase consultados", lista);
        return ResponseEntity.ok(respuesta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DetalleFDTO>> actualizarDetalleF(@PathVariable Long id, @Valid @RequestBody DetalleFDTO dto) {
        DetalleFDTO data = service.actualizarDetalleF(id, dto);
        log.info("Detalle de fase con ID: " + id + " ha sido actualizado.");
        ApiResponse<DetalleFDTO> respuesta = new ApiResponse<>(true, "Detalle de fase con ID: " + id + " ha sido actualizado.", data);
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarDetalleF(@PathVariable Long id) {
        boolean eliminado = service.eliminarDetalleF(id);
        if (eliminado) {
            log.info("Detalle de fase con ID: " + id + " eliminado");
            ApiResponse<Void> respuesta = new ApiResponse<>(true, "Detalle de fase con ID: " + id + " eliminado");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuesta);
        }
        ApiResponse<Void> respuesta = new ApiResponse<>(false, "Detalle de fase con ID: " + id + " no fue encontrado");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
    }

    @GetMapping("/idFase/{fase}")
    public ResponseEntity<ApiResponse<List<DetalleFDTO>>> obtenerPorIdFase(@PathVariable Long fase) {
        List<DetalleFDTO> lista = service.obtenerPorIdFase(fase);
        log.info("Se obtuvieron los datos del detalle de la fase: " + fase);
        ApiResponse<List<DetalleFDTO>> respuesta = new ApiResponse<>(true, "Se obtuvieron los datos del detalle de la fase: " + fase, lista);
        return ResponseEntity.ok(respuesta);
    }
}