package iTicket.Douglas.Setup.Controller;

import iTicket.Douglas.Response.ApiResponse;
import iTicket.Douglas.Setup.DTO.SetupAdministradorDTO;
import iTicket.Douglas.Setup.DTO.SetupEstadoDTO;
import iTicket.Douglas.Setup.Service.SetupService;
import iTicket.Douglas.Usuarios.DTO.UsuarioDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Las dos rutas que la pantalla de primer usuario necesita sin tener sesion
 */
@RestController
@RequestMapping("/api/setup")
@RequiredArgsConstructor
public class SetupController {

    private final SetupService setupService;

    @GetMapping("/estado")
    public ResponseEntity<ApiResponse<SetupEstadoDTO>> obtenerEstado() {
        SetupEstadoDTO estado = setupService.obtenerEstado();
        return ResponseEntity.ok(new ApiResponse<>(true, "Estado de instalacion obtenido", estado));
    }

    @PostMapping("/administrador")
    public ResponseEntity<ApiResponse<UsuarioDTO>> crearAdministradorInicial(
            @Valid @RequestBody SetupAdministradorDTO dto) {
        UsuarioDTO creado = setupService.crearAdministradorInicial(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Administrador inicial creado", creado));
    }
}
