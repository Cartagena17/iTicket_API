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
        try {
            ArticuloDTO dto = service.nuevoArticulo(json);
            if (dto != null) {
                log.info("Nuevo artículo registrado "+ dto);
                ApiResponse<ArticuloDTO> respuestaExito = new ApiResponse<>(true, "Datos registrados exitosamente", dto);
                return ResponseEntity.status(HttpStatus.CREATED).body(respuestaExito);
            }
            log.warn("Intento de inserción de artículo fallida "+ json);
            ApiResponse<ArticuloDTO> respuestaFallida = new ApiResponse<>(false, "No se pudo registrar el artículo");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaFallida);
        } catch (Exception e) {
            log.error("El proceso presentó un fallo inesperado al registrar el artículo", e);
            ApiResponse<ArticuloDTO> respuestaFallida = new ApiResponse<>(false, "El proceso presentó un fallo inesperado. Contacte con el administrador");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaFallida);
        }
    }


    @GetMapping
    public ResponseEntity<ApiResponse<List<ArticuloDTO>>> obtenerDatos() {

        try {
            List<ArticuloDTO> lista = service.obtenerTodo();
            if (lista != null) {

                log.info("Datos de artículos consultados");
                ApiResponse<List<ArticuloDTO>> respuestaExito = new ApiResponse<>(true, "Datos encontrados", lista);
                return ResponseEntity.status(HttpStatus.OK).body(respuestaExito);
            }
            log.info("No se encontraron datos de artículos");
            ApiResponse<List<ArticuloDTO>> respuestaNoEncontrada = new ApiResponse<>(false, "No se encontraron datos");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        } catch (Exception e) {

            log.error("No se pudieron obtener los datos de los artículos", e);
            ApiResponse<List<ArticuloDTO>> respuestaFallida = new ApiResponse<>(false, "No se pudieron obtener los datos de los artículos");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaFallida);
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ArticuloDTO>> obtenerDatosId(@PathVariable Long id) {
        try {
            ArticuloDTO dto = service.obtenerPorId(id);
            if (dto != null) {
                log.info("Se obtuvieron los datos del artículo con id "+ id);
                ApiResponse<ArticuloDTO> respuestaExitosa = new ApiResponse<>(true, "Se obtuvieron los datos del artículo con id " + id, dto);
                return ResponseEntity.ok(respuestaExitosa);
            }
            log.warn("No se encontraron los datos del artículo con id "+ id);
            ApiResponse<ArticuloDTO> respuestaNoEncontrada = new ApiResponse<>(false, "Artículo no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        } catch (Exception e) {
            log.error("Error crítico al obtener el artículo con id "+ id, e);
            ApiResponse<ArticuloDTO> respuestaError = new ApiResponse<>(false, "No se pudo obtener el artículo");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }


    @GetMapping("/paginado")
    public ResponseEntity<ApiResponse<ArticuloPaginaDTO>> obtenerPaginado(
            @RequestParam(defaultValue = "1") int pagina,
            @RequestParam(defaultValue = "10") int tamano,
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) Long idCategoria,
            @RequestParam(required = false) Long idUbicacion,
            @RequestParam(required = false) Long idMarca) {
        try {
            ArticuloPaginaDTO resultado = service.obtenerPaginado(pagina, tamano, busqueda, idCategoria, idUbicacion, idMarca);
            log.info("Artículos paginados consultados (página " + pagina + ")");
            ApiResponse<ArticuloPaginaDTO> respuesta = new ApiResponse<>(true, "Artículos obtenidos", resultado);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            log.error("Error al obtener los artículos paginados", e);
            ApiResponse<ArticuloPaginaDTO> respuestaError = new ApiResponse<>(false, "No se pudieron obtener los artículos");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<List<ArticuloDTO>>> buscarPorCodigo(@RequestParam String codigo) {
        try {
            List<ArticuloDTO> lista = service.buscarPorCodigoParcial(codigo);
            ApiResponse<List<ArticuloDTO>> respuesta = new ApiResponse<>(true, "Búsqueda completada", lista);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            log.error("Error al buscar artículos por código", e);
            ApiResponse<List<ArticuloDTO>> respuestaError = new ApiResponse<>(false, "No se pudo completar la búsqueda");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ArticuloDTO>> actualizarArticulo(@PathVariable Long id, @Valid @RequestBody ArticuloDTO dto) {
        try {
            ArticuloDTO data = service.actualizarData(id, dto);
            if (data != null) {
                log.info("El artículo con id "+ id+" fue actualizado");
                ApiResponse<ArticuloDTO> respuestaExito = new ApiResponse<>(true, "Artículo actualizado exitosamente", data);
                return ResponseEntity.ok(respuestaExito);
            }
            log.warn("El artículo con id "+id+" no fue actualizado");
            ApiResponse<ArticuloDTO> respuestaNoCompletada = new ApiResponse<>(false, "No se pudo actualizar el artículo");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaNoCompletada);
        } catch (Exception e) {
            log.error("Error crítico en la actualización del artículo con id {}", id, e);
            ApiResponse<ArticuloDTO> respuestaError = new ApiResponse<>(false, "No se pudo actualizar el artículo seleccionado");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarArticulo(@PathVariable Long id) {
        try {
            boolean respuesta = service.eliminarArticulo(id);
            if (respuesta) {
                log.info("El articulo con id " + id+" ha sido eliminado");
                ApiResponse<Void> respuestaExitosa = new ApiResponse<>(true, "El articulo con id " + id + " ha sido eliminado");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuestaExitosa);
            }
            log.warn("No se pudo completar la eliminación del artículo con id "+ id);
            ApiResponse<Void> noEncontrado = new ApiResponse<>(false, "El articulo con id " + id + " no se encontró");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(noEncontrado);
        } catch (Exception e) {
            log.error("Error crítico en la eliminación del artículo con id "+ id);
            ApiResponse<Void> respuestaError = new ApiResponse<>(false, "No se pudo eliminar el artículo seleccionado");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }
}