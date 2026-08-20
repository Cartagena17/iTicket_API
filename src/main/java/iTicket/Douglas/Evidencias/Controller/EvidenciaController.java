package iTicket.Douglas.Evidencias.Controller;

import iTicket.Douglas.Evidencias.DTO.EvidenciaDTO;
import iTicket.Douglas.Evidencias.Service.EvidenciaService;
import iTicket.Douglas.Response.ApiResponse;
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
@RequestMapping("/api/evidencias")
@RequiredArgsConstructor
public class EvidenciaController {

    private final EvidenciaService service;

    @PostMapping(value = "/subir", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<EvidenciaDTO>> subirEvidencia(@RequestParam("archivo") MultipartFile archivo, @RequestParam("idTicket") Long idTicket) {
        EvidenciaDTO dto = service.subirEvidencia(archivo, idTicket);
        log.info("Evidencia subida y registrada para el ticket: " + idTicket);
        ApiResponse<EvidenciaDTO> respuesta = new ApiResponse<>(true, "Evidencia subida correctamente", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EvidenciaDTO>>> obtenerDatos() {
        List<EvidenciaDTO> lista = service.obtenerTodo();
        log.info("Datos de evidencias consultados");
        ApiResponse<List<EvidenciaDTO>> respuesta = new ApiResponse<>(true, "Datos encontrados", lista);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EvidenciaDTO>> obtenerEvidenciaPorId(@PathVariable Long id) {
        EvidenciaDTO dto = service.buscarEvidencia(id);
        log.info("Se obtuvieron los datos de la evidencia con ID: " + id);
        ApiResponse<EvidenciaDTO> respuesta = new ApiResponse<>(true, "Se obtuvieron los datos la evidencia con ID: " + id, dto);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/ticket/{idTicket}")
    public ResponseEntity<ApiResponse<List<EvidenciaDTO>>> obtenerEvidenciasPorTicket(@PathVariable Long idTicket) {
        List<EvidenciaDTO> lista = service.obtenerEvidenciasPorTicket(idTicket);
        log.info("Evidencias consultadas para el ticket de ID: " + idTicket);
        ApiResponse<List<EvidenciaDTO>> respuesta = new ApiResponse<>(true, "Evidencias encontradas", lista);
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarEvidencia(@PathVariable Long id) {
        boolean eliminado = service.eliminarData(id);
        if (eliminado) {
            log.info("Evidencia con ID: " + id + " eliminada");
            ApiResponse<Void> respuesta = new ApiResponse<>(true, "Evidencia con ID: " + id + " eliminada");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuesta);
        }
        ApiResponse<Void> respuesta = new ApiResponse<>(false, "Evidencia con ID: " + id + " no fue encontrada");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
    }
}