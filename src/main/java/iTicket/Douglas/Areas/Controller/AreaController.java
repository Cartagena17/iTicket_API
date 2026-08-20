package iTicket.Douglas.Areas.Controller;

import iTicket.Douglas.Areas.DTO.AreaDTO;
import iTicket.Douglas.Areas.Service.AreaService;
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
@RequestMapping("/api/areas")
@RequiredArgsConstructor
@CrossOrigin
public class AreaController {

    private final AreaService service;

    @PostMapping
    public ResponseEntity<ApiResponse<AreaDTO>> nuevaArea(@Valid @RequestBody AreaDTO json) {
        AreaDTO dto = service.nuevaArea(json);
        log.info("Nueva área registrada: " + dto);
        ApiResponse<AreaDTO> respuesta = new ApiResponse<>(true, "Datos registrados exitosamente", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AreaDTO>>> obetenerAreas() {
        List<AreaDTO> lista = service.obtenerTodo();
        ApiResponse<List<AreaDTO>> respuesta = new ApiResponse<>(true, "Datos encontrados", lista);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AreaDTO>> obtenerAreaPorId(@PathVariable Long id) {
        AreaDTO dto = service.obtenerPorId(id);
        ApiResponse<AreaDTO> respuesta = new ApiResponse<>(true, "Área encontrada", dto);
        return ResponseEntity.ok(respuesta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AreaDTO>> editar(@PathVariable Long id, @Valid @RequestBody AreaDTO json) {
        AreaDTO dto = service.editarArea(id, json);
        log.info("Área actualizada: " + dto);
        ApiResponse<AreaDTO> respuesta = new ApiResponse<>(true, "Área actualizada correctamente", dto);
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        boolean eliminado = service.eliminarArea(id);
        if (eliminado) {
            log.info("Área eliminada, id: " + id);
            ApiResponse<Void> respuesta = new ApiResponse<>(true, "Área eliminada correctamente");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuesta);
        }
        ApiResponse<Void> respuesta = new ApiResponse<>(false, "Área no encontrada");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
    }
}