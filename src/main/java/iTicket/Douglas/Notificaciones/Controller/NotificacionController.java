package iTicket.Douglas.Notificaciones.Controller;

import iTicket.Douglas.Notificaciones.DTO.NotificacionDTO;
import iTicket.Douglas.Notificaciones.DTO.NotificacionPaginaDTO;
import iTicket.Douglas.Notificaciones.Service.NotificacionService;
import iTicket.Douglas.Response.ApiResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
@Validated
public class NotificacionController {

    private final NotificacionService service;

    //Paginado y limitado al ultimo mes (ver NotificacionService), asi el payload no crece indefinidamente
    @GetMapping
    public ResponseEntity<ApiResponse<NotificacionPaginaDTO>> obtenerPorUsuario(
            @RequestParam Long idUsuario,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "La página mínima es 1") int pagina,
            @RequestParam(defaultValue = "10") @Min(value = 5, message = "El tamaño mínimo de página es 5")
            @Max(value = 50, message = "El tamaño máximo de página es 50") int tamano) {
        NotificacionPaginaDTO resultado = service.obtenerPorUsuarioPaginado(idUsuario, pagina, tamano);
        return ResponseEntity.ok(new ApiResponse<>(true, "Notificaciones encontradas", resultado));
    }

    @GetMapping("/no-leidas/contador")
    public ResponseEntity<ApiResponse<Long>> contarNoLeidas(@RequestParam Long idUsuario) {
        long total = service.contarNoLeidas(idUsuario);
        return ResponseEntity.ok(new ApiResponse<>(true, "Conteo obtenido", total));
    }

    @PatchMapping("/{id}/leida")
    public ResponseEntity<ApiResponse<NotificacionDTO>> marcarComoLeida(@PathVariable Long id, @RequestParam Long idUsuario) {
        NotificacionDTO dto = service.marcarComoLeida(id, idUsuario);
        return ResponseEntity.ok(new ApiResponse<>(true, "Notificación marcada como leída", dto));
    }

    @PatchMapping("/leerTodas")
    public ResponseEntity<ApiResponse<Void>> marcarTodasComoLeidas(@RequestParam Long idUsuario) {
        service.marcarTodasComoLeidas(idUsuario);
        return ResponseEntity.ok(new ApiResponse<>(true, "Todas las notificaciones fueron marcadas como leídas"));
    }
}
