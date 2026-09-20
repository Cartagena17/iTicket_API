package iTicket.Douglas.Usuarios.Controller;

import iTicket.Douglas.Response.ApiResponse;
import iTicket.Douglas.Usuarios.DTO.CambioContraseñaDTO;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/usuarios")
@RequiredArgsConstructor
@CrossOrigin
public class UsuarioController {

    private final UsuarioService service;

    @PostMapping
    public ResponseEntity<ApiResponse<UsuarioDTO>> nuevoUsuario(@Valid @RequestBody UsuarioDTO json) {
        UsuarioDTO dto = service.nuevoUsuario(json);
        log.info("Nuevo usuario registrado " + dto);
        ApiResponse<UsuarioDTO> respuesta = new ApiResponse<>(true, "Datos registrados exitosamente", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UsuarioDTO>>> obtenerDatos() {
        List<UsuarioDTO> lista = service.obtenerTodo();
        log.info("Datos de usuarios consultados");
        ApiResponse<List<UsuarioDTO>> respuesta = new ApiResponse<>(true, "Datos encontrados", lista);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioDTO>> obtenerDatosId(@PathVariable Long id) {
        UsuarioDTO dto = service.obtenerPorId(id);
        log.info("Se obtuvieron los datos del usuario con id " + id);
        ApiResponse<UsuarioDTO> respuesta = new ApiResponse<>(true, "Se obtuvieron los datos del usuario con id " + id, dto);
        return ResponseEntity.ok(respuesta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioDTO>> actualizarUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioUpdateDTO dto) {
        UsuarioDTO data = service.actualizarData(id, dto);
        log.info("El usuario con id " + id + " fue actualizado");
        ApiResponse<UsuarioDTO> respuesta = new ApiResponse<>(true, "Proceso completado", data);
        return ResponseEntity.ok(respuesta);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioDTO>> actualizarParcial(@PathVariable Long id, @Valid @RequestBody UsuarioPatchDTO dto) {
        UsuarioDTO data = service.actualizarParcial(id, dto);
        log.info("El usuario con id " + id + " fue actualizado parcialmente");
        ApiResponse<UsuarioDTO> respuesta = new ApiResponse<>(true, "Proceso completado", data);
        return ResponseEntity.ok(respuesta);
    }

    @PatchMapping("/{id}/clave")
    public ResponseEntity<ApiResponse<Void>> cambiarClave(@PathVariable Long id, @Valid @RequestBody CambioContraseñaDTO dto) {
        service.cambiarClave(id, dto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Contraseña actualizada correctamente", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarDatos(@PathVariable Long id) {
        boolean eliminado = service.eliminarUsuario(id);
        if (eliminado) {
            log.info("El usuario con ID: " + id + " ha sido eliminado");
            ApiResponse<Void> respuesta = new ApiResponse<>(true, "El usuario con ID: " + id + " ha sido eliminado");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuesta);
        }
        ApiResponse<Void> respuesta = new ApiResponse<>(false, "El usuario con ID: " + id + " no se encontró");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
    }

    @GetMapping("/tecnicos")
    public ResponseEntity<ApiResponse<List<UsuarioDTO>>> obtenerTecnicosPorDepartamento(@RequestParam Long idDepartamento) {
        List<UsuarioDTO> lista = service.obtenerTecnicosPorDepartamento(idDepartamento);
        ApiResponse<List<UsuarioDTO>> respuesta = new ApiResponse<>(true, "Técnicos encontrados", lista);
        return ResponseEntity.ok(respuesta);
    }

    @PatchMapping(value = "/{id}/imagen", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<UsuarioDTO>> actualizarImagen(@PathVariable Long id, @RequestParam("archivo") MultipartFile archivo) {
        UsuarioDTO dto = service.actualizarImagen(id, archivo);
        log.info("Imagen de perfil actualizada para el usuario con id " + id);
        ApiResponse<UsuarioDTO> respuesta = new ApiResponse<>(true, "Imagen actualizada correctamente", dto);
        return ResponseEntity.ok(respuesta);
    }
}