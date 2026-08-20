package iTicket.Douglas.TipoUbicacion.Controller;

import iTicket.Douglas.Response.ApiResponse;
import iTicket.Douglas.TipoUbicacion.DTO.TipoUbicacionDTO;
import iTicket.Douglas.TipoUbicacion.Service.TipoUbicacionService;
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
@RequestMapping("/api/tipoubicacion")
@RequiredArgsConstructor
public class TipoUbicacionController {

    private final TipoUbicacionService service;

    @PostMapping
    public ResponseEntity<ApiResponse<TipoUbicacionDTO>> nuevoTipoUbicacion(@Valid @RequestBody TipoUbicacionDTO json) {
        TipoUbicacionDTO dto = service.nuevoTipoUbicacion(json);
        log.info("Nuevo Tipo Ubicacion: " + dto);
        ApiResponse<TipoUbicacionDTO> respuesta = new ApiResponse<>(true, "Datos ingresados correctamente", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TipoUbicacionDTO>>> obtenerDatos() {
        List<TipoUbicacionDTO> lista = service.obtenerTodo();
        ApiResponse<List<TipoUbicacionDTO>> respuesta = new ApiResponse<>(true, "Datos encontrados", lista);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TipoUbicacionDTO>> obtenerDatosPorId(@PathVariable Long id) {
        TipoUbicacionDTO dto = service.buscarNombreTipoUbicacion(id);
        log.info("Se obtuvieron los datos de Tipo Ubicacion " + dto);
        ApiResponse<TipoUbicacionDTO> respuesta = new ApiResponse<>(true, "Se obtuvieron los datos de Tipo Ubicacion mediante su ID: " + id, dto);
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarTipoUbicacion(@PathVariable Long id) {
        boolean eliminado = service.eliminarData(id);
        if (eliminado) {
            log.info("Tipo Ubicacion con ID: " + id + " ya fue eliminado");
            ApiResponse<Void> respuesta = new ApiResponse<>(true, "Tipo Ubicacion con ID: " + id + " ya fue eliminado");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuesta);
        }
        ApiResponse<Void> respuesta = new ApiResponse<>(false, "Tipo Ubicacion con ID: " + id + " no fue encontrado");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<TipoUbicacionDTO>> actualizarData(@PathVariable Long id, @Valid @RequestBody TipoUbicacionDTO dto) {
        TipoUbicacionDTO data = service.actualizar(id, dto);
        log.info("Tipo Ubicacion con ID: " + id + " ha sido actualizado");
        ApiResponse<TipoUbicacionDTO> respuesta = new ApiResponse<>(true, "Tipo Ubicacion con ID: " + id + " ha sido actualizado", data);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/nombreTipoUbicacion/{nombreTipoUbicacion}")
    public ResponseEntity<ApiResponse<TipoUbicacionDTO>> buscarPorNombreTipoUbicacion(@PathVariable String nombreTipoUbicacion) {
        TipoUbicacionDTO data = service.buscarNombreTipoUbicacion(nombreTipoUbicacion);
        log.info("Tipo de Ubicacion encontrado con nombre: " + nombreTipoUbicacion);
        ApiResponse<TipoUbicacionDTO> respuesta = new ApiResponse<>(true, "Se obtuvieron los datos del Tipo de Ubicacion con nombre: " + nombreTipoUbicacion, data);
        return ResponseEntity.ok(respuesta);
    }
}