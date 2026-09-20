package iTicket.Douglas.Bitacoras.Controller;

import iTicket.Douglas.Bitacoras.DTO.BitacoraDTO;
import iTicket.Douglas.Bitacoras.Service.BitacoraService;
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
@RequestMapping("/api/bitacoras")
@RequiredArgsConstructor
public class BitacoraController {

    private final BitacoraService service;

    @PostMapping
    public ResponseEntity<ApiResponse<BitacoraDTO>> nuevaBitacora(@Valid @RequestBody BitacoraDTO json) {
        BitacoraDTO dto = service.nuevaBitacora(json);
        log.info("Nueva bitácora registrada " + dto);
        ApiResponse<BitacoraDTO> respuesta = new ApiResponse<>(true, "Proceso completado exitosamente", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BitacoraDTO>>> obtenerBitacoras(@RequestParam Long idUsuarioAdmin) {
        List<BitacoraDTO> lista = service.obtenerBitacoras(idUsuarioAdmin);
        log.info("Se obtuvieron con éxito las bitácoras");
        ApiResponse<List<BitacoraDTO>> respuesta = new ApiResponse<>(true, "Se obtuvieron con éxito las bitácoras", lista);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/bitacoraTicket/{idTicket}")
    public ResponseEntity<ApiResponse<List<BitacoraDTO>>> obtenerBitacorasIdTicket(@PathVariable Long idTicket) {
        List<BitacoraDTO> lista = service.obtenerBitacorasIdTicket(idTicket);
        log.info("Se obtuvo la bitácora con ticket: " + idTicket);
        ApiResponse<List<BitacoraDTO>> respuesta = new ApiResponse<>(true, "Se obtuvo la bitácora con ticket: " + idTicket, lista);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/tecnico/resolucion-por-dia")
    public ResponseEntity<ApiResponse<List<Object[]>>> obtenerResolucionPorDiaTecnico(@RequestParam Long idUsuario) {
        List<Object[]> data = service.obtenerResolucionPorDiaSemanaTecnico(idUsuario);
        log.info("Resolución por día consultada para el técnico con ID: " + idUsuario);
        ApiResponse<List<Object[]>> respuesta = new ApiResponse<>(true, "Resolución por día del técnico obtenida con éxito", data);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/usuario/tiempo-promedio")
    public ResponseEntity<ApiResponse<Double>> obtenerTiempoPromedioPorUsuario(@RequestParam Long idUsuario) {
        double promedio = service.obtenerTiempoPromedioResolucionPorUsuario(idUsuario);
        log.info("Tiempo promedio de resolución consultado para el usuario con ID: " + idUsuario);
        ApiResponse<Double> respuesta = new ApiResponse<>(true, "Tiempo promedio obtenido", promedio);
        return ResponseEntity.ok(respuesta);
    }
}