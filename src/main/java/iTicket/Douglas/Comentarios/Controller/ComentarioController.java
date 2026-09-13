package iTicket.Douglas.Comentarios.Controller;

import iTicket.Douglas.Comentarios.DTO.ComentarioDTO;
import iTicket.Douglas.Comentarios.Service.ComentarioService;
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
@RequestMapping("/api/comentarios")
@RequiredArgsConstructor
public class ComentarioController {

    private final ComentarioService service;

    @PostMapping
    public ResponseEntity<ApiResponse<ComentarioDTO>> nuevoComentario(@Valid @RequestBody ComentarioDTO json) {
        ComentarioDTO dto = service.nuevoComentario(json);
        log.info("Nuevo comentario: " + dto);
        ApiResponse<ComentarioDTO> respuesta = new ApiResponse<>(true, "Datos ingresados correctamente", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ComentarioDTO>>> obtenerDatos() {
        List<ComentarioDTO> lista = service.obtenerTodo();
        log.info("Datos de los comentarios consultados");
        ApiResponse<List<ComentarioDTO>> respuesta = new ApiResponse<>(true, "Datos encontrados", lista);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ComentarioDTO>> obtenerDatosPorId(@PathVariable Long id) {
        ComentarioDTO dto = service.buscarComentarioPorId(id);
        log.info("Se obtuvieron los datos del comentario " + dto);
        ApiResponse<ComentarioDTO> respuesta = new ApiResponse<>(true, "Se obtuvieron los datos del comentario mediante su ID: " + id, dto);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/ticket/{idTicket}")
    public ResponseEntity<ApiResponse<List<ComentarioDTO>>> obtenerComentariosPorTicket(@PathVariable Long idTicket) {
        List<ComentarioDTO> lista = service.obtenerComentariosPorTicket(idTicket);
        log.info("Comentarios consultados para el ticket: " + idTicket);
        ApiResponse<List<ComentarioDTO>> respuesta = new ApiResponse<>(true, "Comentarios encontrados", lista);
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarComentario(@PathVariable Long id, @RequestParam Long idUsuario) {
        boolean eliminado = service.eliminarData(id, idUsuario);
        if (eliminado) {
            log.info("El comentario con ID: " + id + " ya fue eliminado");
            ApiResponse<Void> respuesta = new ApiResponse<>(true, "El comentario con ID: " + id + " ya fue eliminado");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuesta);
        }
        ApiResponse<Void> respuesta = new ApiResponse<>(false, "El comentario con ID: " + id + " no fue encontrado");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<ComentarioDTO>> actualizarData(@PathVariable Long id, @Valid @RequestBody ComentarioDTO dto) {
        ComentarioDTO data = service.actualizar(id, dto);
        log.info("El comentario con ID: " + id + " ha sido actualizado");
        ApiResponse<ComentarioDTO> respuesta = new ApiResponse<>(true, "El comentario con ID: " + id + " ha sido actualizado", data);
        return ResponseEntity.ok(respuesta);
    }
}