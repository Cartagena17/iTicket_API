package iTicket.Douglas.Roles.Controller;

import iTicket.Douglas.Response.ApiResponse;
import iTicket.Douglas.Roles.DTO.RolDTO;
import iTicket.Douglas.Roles.Service.RolService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@CrossOrigin
public class RolController {

    private final RolService service;

    @PostMapping
    public ResponseEntity<ApiResponse<RolDTO>> nuevoRol(@Valid @RequestBody RolDTO json) {
        RolDTO dto = service.nuevoRol(json);
        log.info("Nuevo rol registrado " + dto);
        ApiResponse<RolDTO> respuesta = new ApiResponse<>(true, "Nuevo rol registrado", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RolDTO>>> obtenerDatos() {
        List<RolDTO> lista = service.obtenerTodo();
        ApiResponse<List<RolDTO>> respuesta = new ApiResponse<>(true, "Datos encontrados", lista);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RolDTO>> obtenerDatosId(@PathVariable Long id) {
        RolDTO dto = service.obtenerPorId(id);
        ApiResponse<RolDTO> respuesta = new ApiResponse<>(true, "Proceso completado", dto);
        return ResponseEntity.ok(respuesta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RolDTO>> actualizarRol(@PathVariable Long id, @Valid @RequestBody RolDTO dto) {
        RolDTO data = service.actualizarData(id, dto);
        log.info("Se logró actualizar el rol con id " + id);
        ApiResponse<RolDTO> respuesta = new ApiResponse<>(true, "Se logró actualizar el rol con id " + id, data);
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarDatos(@PathVariable Long id) {
        boolean eliminado = service.eliminarRol(id);
        if (eliminado) {
            log.info("Se logró eliminar el rol con id " + id);
            ApiResponse<Void> respuesta = new ApiResponse<>(true, "Se logró eliminar el rol con id " + id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuesta);
        }
        ApiResponse<Void> respuesta = new ApiResponse<>(false, "El rol con ID: " + id + " no se encontró");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
    }
}