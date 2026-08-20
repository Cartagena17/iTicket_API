package iTicket.Douglas.DetalleGeneral.Controller;

import iTicket.Douglas.DetalleGeneral.DTO.DetalleGDTO;
import iTicket.Douglas.DetalleGeneral.Service.DetalleGService;
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
@RequestMapping("/api/detalleG")
@RequiredArgsConstructor
public class DetalleGController {

    private final DetalleGService service;

    @PostMapping
    public ResponseEntity<ApiResponse<DetalleGDTO>> nuevoDetalleG(@Valid @RequestBody DetalleGDTO json) {
        DetalleGDTO dto = service.nuevoDetalleG(json);
        log.info("Nuevo detalle ingresado: " + dto);
        ApiResponse<DetalleGDTO> respuesta = new ApiResponse<>(true, "Datos ingresados exitosamente", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DetalleGDTO>>> obtenerDetallesG() {
        List<DetalleGDTO> lista = service.obtenerDetallesG();
        log.info("Se obtuvieron con éxito los detalles");
        ApiResponse<List<DetalleGDTO>> respuesta = new ApiResponse<>(true, "Detalles encontrados", lista);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/detalleGIdTicket/{idTicket}")
    public ResponseEntity<ApiResponse<DetalleGDTO>> buscarDetalleGIdTicket(@PathVariable Long idTicket) {
        DetalleGDTO datos = service.obtenerDetallesIdTicket(idTicket);
        log.info("Se obtuvo con éxito el detalle del ticket: " + idTicket);
        ApiResponse<DetalleGDTO> respuesta = new ApiResponse<>(true, "Se obtuvo con éxito el detalle del ticket: " + idTicket, datos);
        return ResponseEntity.ok(respuesta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DetalleGDTO>> actualizarDetalleG(@PathVariable Long id, @Valid @RequestBody DetalleGDTO dto) {
        DetalleGDTO datos = service.actualizarDetalleG(id, dto);
        log.info("Detalle de ticket: " + id + ", ha sido actualizado");
        ApiResponse<DetalleGDTO> respuesta = new ApiResponse<>(true, "Detalle de ticket: " + id + ", ha sido actualizado", datos);
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarDetalleG(@PathVariable Long id) {
        boolean eliminado = service.eliminarDetalleG(id);
        if (eliminado) {
            log.info("Detalle de ticket: " + id + ", eliminado");
            ApiResponse<Void> respuesta = new ApiResponse<>(true, "Detalle de ticket: " + id + ", eliminado");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuesta);
        }
        ApiResponse<Void> respuesta = new ApiResponse<>(false, "Detalle de ticket: " + id + ", no fue encontrado");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
    }
}