package iTicket.Douglas.Bitacoras.Controller;

import iTicket.Douglas.Bitacoras.DTO.BitacoraDTO;
import iTicket.Douglas.Bitacoras.DTO.BitacoraPaginaDTO;
import iTicket.Douglas.Bitacoras.Service.BitacoraService;
import iTicket.Douglas.Response.ApiResponse;
import iTicket.Douglas.Security.SecurityUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/bitacoras")
@RequiredArgsConstructor
@Validated
public class BitacoraController {

    private final BitacoraService service;

    @PostMapping
    public ResponseEntity<ApiResponse<BitacoraDTO>> nuevaBitacora(@Valid @RequestBody BitacoraDTO json) {
        BitacoraDTO dto = service.nuevaBitacora(json);
        log.info("Nueva bitácora registrada " + dto);
        ApiResponse<BitacoraDTO> respuesta = new ApiResponse<>(true, "Proceso completado exitosamente", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    // Listado completo: panel de Administrador/Tecnico. El id del admin ya no
    // viaja en la URL: sale del usuario autenticado en la cookie.
    @PreAuthorize("hasAnyRole('Administrador', 'Tecnico')")
    @GetMapping
    public ResponseEntity<ApiResponse<BitacoraPaginaDTO>> obtenerBitacoras(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "La página mínima es 1") int pagina,
            @RequestParam(defaultValue = "10") @Min(value = 5, message = "El tamaño mínimo de página es 5")
            @Max(value = 50, message = "El tamaño máximo de página es 50") int tamano,
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) String estado) {
        Long idUsuarioAdmin = SecurityUtils.idUsuarioActual();
        BitacoraPaginaDTO resultado = service.obtenerBitacoras(idUsuarioAdmin, pagina, tamano, busqueda, estado);
        log.info("Se obtuvieron con éxito las bitácoras");
        ApiResponse<BitacoraPaginaDTO> respuesta = new ApiResponse<>(true, "Se obtuvieron con éxito las bitácoras", resultado);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/bitacoraTicket/{idTicket}")
    public ResponseEntity<ApiResponse<List<BitacoraDTO>>> obtenerBitacorasIdTicket(@PathVariable Long idTicket) {
        List<BitacoraDTO> lista = service.obtenerBitacorasIdTicket(idTicket);
        log.info("Se obtuvo la bitácora con ticket: " + idTicket);
        ApiResponse<List<BitacoraDTO>> respuesta = new ApiResponse<>(true, "Se obtuvo la bitácora con ticket: " + idTicket, lista);
        return ResponseEntity.ok(respuesta);
    }

    @PreAuthorize("hasAnyRole('Administrador', 'Tecnico')")
    @GetMapping("/tecnico/resolucion-por-dia")
    public ResponseEntity<ApiResponse<List<Object[]>>> obtenerResolucionPorDiaTecnico() {
        Long idUsuario = SecurityUtils.idUsuarioActual();
        List<Object[]> data = service.obtenerResolucionPorDiaSemanaTecnico(idUsuario);
        log.info("Resolución por día consultada para el técnico con ID: " + idUsuario);
        ApiResponse<List<Object[]>> respuesta = new ApiResponse<>(true, "Resolución por día del técnico obtenida con éxito", data);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/usuario/tiempo-promedio")
    public ResponseEntity<ApiResponse<Double>> obtenerTiempoPromedioPorUsuario() {
        Long idUsuario = SecurityUtils.idUsuarioActual();
        double promedio = service.obtenerTiempoPromedioResolucionPorUsuario(idUsuario);
        log.info("Tiempo promedio de resolución consultado para el usuario con ID: " + idUsuario);
        ApiResponse<Double> respuesta = new ApiResponse<>(true, "Tiempo promedio obtenido", promedio);
        return ResponseEntity.ok(respuesta);
    }
}
