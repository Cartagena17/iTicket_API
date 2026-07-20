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
public class RolController {
    private final RolService service;

    @PostMapping
    public ResponseEntity<ApiResponse<RolDTO>> nuevoRol(@Valid @RequestBody RolDTO json) {
        try {
            RolDTO dto = service.nuevoRol(json);
            if (dto != null) {
                log.info("Nuevo rol registrado " + dto);
                ApiResponse<RolDTO> respuestaExito = new ApiResponse<>(true, "Nuevo rol registrado", dto);
                return ResponseEntity.ok(respuestaExito);
            }
            log.warn("Intento de insercion fallida " + json);
            ApiResponse<RolDTO> respuestaFallida = new ApiResponse<>(false, "Intento de insercion fallida " + json);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaFallida);
        } catch (Exception e) {
            log.error("El proceso presentó un fallo inesperado contacte con el administrador");
            e.printStackTrace();
            ApiResponse<RolDTO> respuestaFallida = new ApiResponse<>(false, "El proceso presentó un fallo inesperado contacte con el administrador");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaFallida);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RolDTO>>> obtenerDatos() {
        try {
            List<RolDTO> lista = service.obtenerTodo();
            if (lista != null) {
                log.info("Datos de roles consultados");
                ApiResponse<List<RolDTO>> respuestaExito = new ApiResponse<>(true, "Datos encontrados", lista);
                return ResponseEntity.ok(respuestaExito);
            }
            log.warn("Datos de roles no encontrados");
            ApiResponse<List<RolDTO>> respuestaNoEncontrada = new ApiResponse<>(false, "Datos de roles no encontrados");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuestaNoEncontrada);
        } catch (Exception e) {
            log.error("No se pudieron obtener los datos de los roles");
            e.printStackTrace();
            ApiResponse<List<RolDTO>> respuestaFallida = new ApiResponse<>(false, "No se pudieron obtener los datos de los roles");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaFallida);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RolDTO>> obtenerDatosId(@PathVariable Long id) {
        try {
            RolDTO dto = service.obtenerPorId(id);
            if (dto != null) {
                log.info("Obtencion correcta de rol con id " + id);
                ApiResponse<RolDTO> respuestaExitosa = new ApiResponse<>(true, "Proceso completado", dto);
                return ResponseEntity.ok(respuestaExitosa);
            }
            log.warn("No se encontraron los datos del rol con id " + id);
            ApiResponse<RolDTO> respuestaNoEncontrada = new ApiResponse<>(false, "No se encontraron los datos del rol con id " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        } catch (Exception e) {
            log.error("Error crítico al obtener los datos del rol con id  " + id);
            e.printStackTrace();
            ApiResponse<RolDTO> respuestaError = new ApiResponse<>(false, "Error crítico al obtener los datos del rol con id  " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RolDTO>> actualizarRol(@PathVariable Long id, @Valid @RequestBody RolDTO dto) {
        try {
            RolDTO data = service.actualizarData(id, dto);
            if (data != null) {
                log.info("Se logró actualizar el rol con id "+id);
                ApiResponse<RolDTO> respuestaExito = new ApiResponse<>(true, "Se logró actualizar el rol con id "+id, data);
                return ResponseEntity.ok(respuestaExito);
            }
            log.warn("No se pudo actualizar el rol con id "+id);
            ApiResponse<RolDTO> respuestaNoCompletada = new ApiResponse<>(false, "No se pudo actualizar el rol con id "+id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaNoCompletada);
        } catch (Exception e) {
            log.error("Error crítico en la actualización de rol con id: " + id);
            e.printStackTrace();
            ApiResponse<RolDTO> respuestaError = new ApiResponse<>(false, "Error crítico en la actualización de rol con id: " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarDatos(@PathVariable Long id) {
        try {
            boolean respuesta = service.eliminarRol(id);
            if (respuesta) {
                log.info("Se logró eliminar el rol con id "+id);
                ApiResponse<Void> respuestaExitosa = new ApiResponse<>(true, "Se logró eliminar el rol con id "+id);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuestaExitosa);
            }
            log.warn("No se logro eliminar el rol con id "+id);
            ApiResponse<Void> noEncontrado = new ApiResponse<>(false, "El rol con ID: " + id + " no se encontró");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(noEncontrado);
        } catch (Exception e) {
            log.error("Error crítico en la eliminación de rol con id " + id);
            e.printStackTrace();
            ApiResponse<Void> respuestaError = new ApiResponse<>(false, "No se pudo eliminar el rol seleccionado");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }
}
