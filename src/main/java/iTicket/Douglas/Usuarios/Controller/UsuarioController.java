package iTicket.Douglas.Usuarios.Controller;

import iTicket.Douglas.Response.ApiResponse;
import iTicket.Douglas.Usuarios.DTO.UsuarioDTO;
import iTicket.Douglas.Usuarios.DTO.UsuarioPatchDTO;
import iTicket.Douglas.Usuarios.DTO.UsuarioUpdateDTO;
import iTicket.Douglas.Usuarios.Service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;

    @PostMapping
    public ResponseEntity<ApiResponse<UsuarioDTO>> nuevoUsuario(@Valid @RequestBody UsuarioDTO json) {
        try {
            UsuarioDTO dto = service.nuevoUsuario(json);
            if (dto != null) {
                log.info("Nuevo usuario registrado " + dto);
                ApiResponse<UsuarioDTO> respuestaExito = new ApiResponse<>(true, "Datos registrados exitosamente", dto);
                return ResponseEntity.status(HttpStatus.CREATED).body(respuestaExito);
            }
            log.warn("Intento de insercion fallida " + json);
            ApiResponse<UsuarioDTO> respuestaFallida = new ApiResponse<>(false, "Intento de insercion fallida");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaFallida);
        } catch (Exception e) {
            log.error("El proceso presentó un fallo inesperado contacte con el administrador");
            e.printStackTrace();
            ApiResponse<UsuarioDTO> respuestaFallida = new ApiResponse<>(false, "El proceso presentó un fallo inesperado contacte con el administrador");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaFallida);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UsuarioDTO>>> obtenerDatos(){
        try {
            List<UsuarioDTO> lista = service.obtenerTodo();
            if (lista != null) {
                log.info("Datos de usuarios consultados");
                ApiResponse<List<UsuarioDTO>> respuestaExito = new ApiResponse<>(true, "Datos encontrados", lista);
                return ResponseEntity.ok(respuestaExito);
            }
            log.info("Datos no encontrados");
            ApiResponse<List<UsuarioDTO>> respuestaNoEncontrada = new ApiResponse<>(false   , "Datos no encontrados");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        } catch (Exception e) {
            log.error("No se pudieron obtener los datos de los usuarios");
            e.printStackTrace();
            ApiResponse<List<UsuarioDTO>> respuestaFallida = new ApiResponse<>(false, "No se pudieron obtener los datos de los usuarios");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaFallida);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioDTO>> obtenerDatosId(@PathVariable Long id) {
        try {
            UsuarioDTO dto = service.obtenerPorId(id);
            if (dto != null) {
                log.info("Se obtuvieron los datos del usuario con id " + id);
                ApiResponse<UsuarioDTO> respuestaExitosa = new ApiResponse<>(true, "Se obtuvieron los datos del usuario con id " + id, dto);
                return ResponseEntity.ok(respuestaExitosa);
            }
            log.warn("No se encontraron los datos del usuario con id " + id);
            ApiResponse<UsuarioDTO> respuestaNoEncontrada = new ApiResponse<>(false, "Usuario no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        } catch (Exception e) {
            log.error("Error crítico al obtener de usuario con id " + id);
            e.printStackTrace();
            ApiResponse<UsuarioDTO> respuestaError = new ApiResponse<>(false, "No se pudo obtener los datos del usuario");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioDTO>> actualizarUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioUpdateDTO dto) {
        try {
            UsuarioDTO data = service.actualizarData(id, dto);
            if (data != null) {
                log.info("El usuario con id " + id + " fue actualizado");
                ApiResponse<UsuarioDTO> respuestaExito = new ApiResponse<>(true, "Proceso completado", data);
                return ResponseEntity.ok(respuestaExito);
            }
            log.warn("El usuario con id " + id + " no fue actualizado");
            ApiResponse<UsuarioDTO> respuestaNoCompletada = new ApiResponse<>(false, "Proceso no completado");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaNoCompletada);
        } catch (Exception e) {
            log.error("Error crítico en la actualización de usuario con id: " + id);
            e.printStackTrace();
            ApiResponse<UsuarioDTO> respuestaError = new ApiResponse<>(false, "No se pudo actualizar el usuario seleccionado");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioDTO>> actualizarParcial (@PathVariable Long id, @Valid @RequestBody UsuarioPatchDTO dto) {
        try {
            UsuarioDTO data = service.actualizarParcial(id, dto);
            if (data != null){
                log.info("El usuario con id " + id + " fue actualizado");
                ApiResponse<UsuarioDTO> respuestaExito = new ApiResponse<>(true, "Proceso completado", data);
                return ResponseEntity.ok(respuestaExito);
            }
            log.warn("El usuario con id " + id + " no fue actualizado");
            ApiResponse<UsuarioDTO> respuestaNoCompletada = new ApiResponse<>(false, "Proceso no completado");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaNoCompletada);
        }catch (Exception e) {
            log.error("Error al actualizar el usuario con id " + id);
            e.printStackTrace();
            ApiResponse<UsuarioDTO> respuestaError = new ApiResponse<>(false, "No se pudo actualizar el usuario seleccionado");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarDatos(@PathVariable Long id) {
        try {
            boolean respuesta = service.eliminarUsuario(id);
            if (respuesta) {
                log.info("El usuario con ID: " + id + " ha sido eliminado");
                ApiResponse<Void> respuestaExitosa = new ApiResponse<>(true, "El usuario con ID: " + id + " ha sido eliminado");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuestaExitosa);
            }
            log.warn("No se pudo completar el proceso de eliminar, id: " + id);
            ApiResponse<Void> noEncontrado = new ApiResponse<>(false, "El usuario con ID: " + id + " no se encontró");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(noEncontrado);
        } catch (Exception e) {
            log.error("Error crítico en la eliminación de usuario con id: " + id);
            e.printStackTrace();
            ApiResponse<Void> respuestaError = new ApiResponse<>(false, "No se pudo eliminar el usuario seleccionado");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }
}
