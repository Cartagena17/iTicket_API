package iTicket.Douglas.MultimediaComentario.Controller;

import iTicket.Douglas.MultimediaComentario.DTO.MultimediaComentarioDTO;
import iTicket.Douglas.MultimediaComentario.Service.MultimediaComentarioService;
import iTicket.Douglas.Response.ApiResponse;
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
@CrossOrigin
@RequestMapping("/api/multimediaComentarios")
@RequiredArgsConstructor
public class MultimediaComentarioController {

    private final MultimediaComentarioService service;

    @PostMapping
    public ResponseEntity<ApiResponse<MultimediaComentarioDTO>> nuevaMultimedia(@Valid @RequestBody MultimediaComentarioDTO json) {
        MultimediaComentarioDTO dto = service.nuevaMultimedia(json);
        log.info("Nueva multimedia: " + dto);
        ApiResponse<MultimediaComentarioDTO> respuesta = new ApiResponse<>(true, "Datos ingresados correctamente", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @PostMapping(value = "/subir", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<MultimediaComentarioDTO>> subirMultimedia(@RequestParam("archivo") MultipartFile archivo, @RequestParam("idComentario") Long idComentario) {
        MultimediaComentarioDTO dto = service.subirMultimedia(archivo, idComentario);
        log.info("Multimedia subida y registrada para el comentario: " + idComentario);
        ApiResponse<MultimediaComentarioDTO> respuesta = new ApiResponse<>(true, "Multimedia subida correctamente", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MultimediaComentarioDTO>>> obtenerDatos() {
        List<MultimediaComentarioDTO> lista = service.obtenerTodo();
        log.info("Datos de multimedia consultados");
        ApiResponse<List<MultimediaComentarioDTO>> respuesta = new ApiResponse<>(true, "Datos encontrados", lista);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MultimediaComentarioDTO>> obtenerDatosPorId(@PathVariable Long id) {
        MultimediaComentarioDTO dto = service.buscarPorId(id);
        log.info("Se obtuvieron los datos de la multimedia del comentario " + dto);
        ApiResponse<MultimediaComentarioDTO> respuesta = new ApiResponse<>(true, "Se obtuvieron los datos de la multimedia mediante su ID: " + id, dto);
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarMultimedia(@PathVariable Long id) {
        boolean eliminado = service.eliminarData(id);
        if (eliminado) {
            log.info("La multimedia con ID: " + id + " ya fue eliminada");
            ApiResponse<Void> respuesta = new ApiResponse<>(true, "La multimedia con ID: " + id + " ya fue eliminada");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuesta);
        }
        ApiResponse<Void> respuesta = new ApiResponse<>(false, "La multimedia con ID: " + id + " no fue encontrada");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<MultimediaComentarioDTO>> actualizarData(@PathVariable Long id, @Valid @RequestBody MultimediaComentarioDTO dto) {
        MultimediaComentarioDTO data = service.actualizar(id, dto);
        log.info("La multimedia con ID: " + id + " ha sido actualizada");
        ApiResponse<MultimediaComentarioDTO> respuesta = new ApiResponse<>(true, "La multimedia con ID: " + id + " ha sido actualizada", data);
        return ResponseEntity.ok(respuesta);
    }
}