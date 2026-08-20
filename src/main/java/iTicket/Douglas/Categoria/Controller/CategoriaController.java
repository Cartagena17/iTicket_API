package iTicket.Douglas.Categoria.Controller;

import iTicket.Douglas.Categoria.DTO.CategoriaDTO;
import iTicket.Douglas.Categoria.Service.CategoriaSevice;
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
@RequestMapping("/api/categorias")
@CrossOrigin
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaSevice service;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoriaDTO>> nuevaCategoria(@Valid @RequestBody CategoriaDTO json) {
        CategoriaDTO dto = service.nuevaCategoria(json);
        log.info("Nueva categoria registrada: " + dto);
        ApiResponse<CategoriaDTO> respuesta = new ApiResponse<>(true, "Datos ingresados con éxito", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoriaDTO>>> obtenerCategorias() {
        List<CategoriaDTO> lista = service.obtenerCategorias();
        ApiResponse<List<CategoriaDTO>> respuesta = new ApiResponse<>(true, "Se obtuvieron los datos de las categorias con éxito", lista);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/nombreCategoria/{nombreCategoria}")
    public ResponseEntity<ApiResponse<CategoriaDTO>> obtenerNombreCategoria(@PathVariable String nombreCategoria) {
        CategoriaDTO dto = service.obtenerNombreCategoria(nombreCategoria);
        log.info("Se obtuvo la categoria: " + nombreCategoria + " con éxito");
        ApiResponse<CategoriaDTO> respuesta = new ApiResponse<>(true, "Se obtuvo la categoria: " + nombreCategoria + " con éxito", dto);
        return ResponseEntity.ok(respuesta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoriaDTO>> actualizarCategoria(@PathVariable Long id, @Valid @RequestBody CategoriaDTO dto) {
        CategoriaDTO datos = service.actualizarCategoria(id, dto);
        log.info("Categoria: " + id + ", ha sido actualizada");
        ApiResponse<CategoriaDTO> respuesta = new ApiResponse<>(true, "Categoria: " + id + ", ha sido actualizada", datos);
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarCategoria(@PathVariable Long id) {
        boolean eliminado = service.eliminarCategoria(id);
        if (eliminado) {
            log.info("Categoria: " + id + " eliminada");
            ApiResponse<Void> respuesta = new ApiResponse<>(true, "Categoria: " + id + " eliminada");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuesta);
        }
        ApiResponse<Void> respuesta = new ApiResponse<>(false, "Categoria: " + id + ", no fue encontrada");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
    }
}