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
    public ResponseEntity<ApiResponse<List<BitacoraDTO>>> obtenerBitacoras() {
        List<BitacoraDTO> lista = service.obtenerBitacoras();
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
}