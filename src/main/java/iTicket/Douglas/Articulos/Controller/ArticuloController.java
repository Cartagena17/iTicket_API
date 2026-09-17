package iTicket.Douglas.Articulos.Controller;

import iTicket.Douglas.Articulos.DTO.ArticuloDTO;
import iTicket.Douglas.Articulos.DTO.ArticuloPaginaDTO;
import iTicket.Douglas.Articulos.Service.ArticuloService;
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
@RequestMapping("/api/articulos")
@RequiredArgsConstructor
@CrossOrigin
public class ArticuloController {

    private final ArticuloService service;

    @PostMapping
    public ResponseEntity<ApiResponse<ArticuloDTO>> nuevoArticulo(@Valid @RequestBody ArticuloDTO json) {
        ArticuloDTO dto = service.nuevoArticulo(json);
        log.info("Nuevo artículo registrado " + dto);
        ApiResponse<ArticuloDTO> respuesta = new ApiResponse<>(true, "Datos registrados exitosamente", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ArticuloDTO>>> obtenerDatos() {
        List<ArticuloDTO> lista = service.obtenerTodo();
        ApiResponse<List<ArticuloDTO>> respuesta = new ApiResponse<>(true, "Datos encontrados", lista);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ArticuloDTO>> obtenerDatosId(@PathVariable Long id) {
        ArticuloDTO dto = service.obtenerPorId(id);
        log.info("Se obtuvieron los datos del artículo con id " + id);
        ApiResponse<ArticuloDTO> respuesta = new ApiResponse<>(true, "Se obtuvieron los datos del artículo con id " + id, dto);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/paginado")
    public ResponseEntity<ApiResponse<ArticuloPaginaDTO>> obtenerPaginado(
            @RequestParam(defaultValue = "1") int pagina,
            @RequestParam(defaultValue = "10") int tamano,
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) Long idCategoria,
            @RequestParam(required = false) Long idUbicacion,
            @RequestParam(required = false) Long idMarca) {
        ArticuloPaginaDTO resultado = service.obtenerPaginado(pagina, tamano, busqueda, idCategoria, idUbicacion, idMarca);
        log.info("Artículos paginados consultados (página " + pagina + ")");
        ApiResponse<ArticuloPaginaDTO> respuesta = new ApiResponse<>(true, "Artículos obtenidos", resultado);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<List<ArticuloDTO>>> buscarPorCodigo(@RequestParam String codigo) {
        List<ArticuloDTO> lista = service.buscarPorCodigoParcial(codigo);
        ApiResponse<List<ArticuloDTO>> respuesta = new ApiResponse<>(true, "Búsqueda completada", lista);
        return ResponseEntity.ok(respuesta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ArticuloDTO>> actualizarArticulo(@PathVariable Long id, @Valid @RequestBody ArticuloDTO dto) {
        ArticuloDTO data = service.actualizarData(id, dto);
        log.info("El artículo con id " + id + " fue actualizado");
        ApiResponse<ArticuloDTO> respuesta = new ApiResponse<>(true, "Artículo actualizado exitosamente", data);
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarArticulo(@PathVariable Long id) {
        boolean eliminado = service.eliminarArticulo(id);
        if (eliminado) {
            log.info("El artículo con id " + id + " ha sido eliminado");
            ApiResponse<Void> respuesta = new ApiResponse<>(true, "El artículo con id " + id + " ha sido eliminado");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuesta);
        }
        ApiResponse<Void> respuesta = new ApiResponse<>(false, "El artículo con id " + id + " no se encontró");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
    }
}
