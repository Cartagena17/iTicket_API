package iTicket.Douglas.Categoria.Controller;


import iTicket.Douglas.Categoria.DTO.CategoriaDTO;
import iTicket.Douglas.Categoria.Service.CategoriaSevice;
import iTicket.Douglas.Response.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping ("/api/categorias")
@CrossOrigin
public class CategoriaController {

    private final CategoriaSevice service;

    public CategoriaController(CategoriaSevice service) {
        this.service = service;
    }

    //Ingresar
    @PostMapping
    public ResponseEntity<ApiResponse<CategoriaDTO>> nuevaCategoria(@Valid @RequestBody CategoriaDTO json){
        try {
            CategoriaDTO dto = service.nuevaCategoria(json);
            if (dto != null){
                log.info("Nueva categoria registrada: " + dto);
                ApiResponse<CategoriaDTO> exito = new ApiResponse<>(true, "Datos ingresados con exitó", dto);
                return ResponseEntity.status(HttpStatus.CREATED).body(exito);
            }
            log.warn("Intento de inserción fallido: " + json);
            ApiResponse<CategoriaDTO> respuesta = new ApiResponse<>(false, "Intento de inserción fallido");
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }
        catch (Exception e){
            log.error("Error en el proceso de inserción");
            e.printStackTrace();
            ApiResponse<CategoriaDTO> error = new ApiResponse<>(false, "Error en el proceso de inserción.", json);
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    //Obtener
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoriaDTO>>> obtenerCategorias(){
        try {
            List<CategoriaDTO> lista = service.obtenerCategorias();
            if (lista != null){
                log.info("Se obtuvieron los datos de las categorias con éxito");
                ApiResponse<List<CategoriaDTO>> exito = new ApiResponse<>(true, "Se obtuvieron los datos de las categorias con éxito", lista);
                return ResponseEntity.ok(exito);
            }
            log.warn("Datos de categoria no encontrados");
            ApiResponse<List<CategoriaDTO>> respuesta = new ApiResponse<>(false, "Datos de categoria no encontrados");
            return  ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuesta);
        }
        catch (Exception e){
            log.error("Error en el proceso de obtención");
            e.printStackTrace();
            ApiResponse<List<CategoriaDTO>> error = new ApiResponse<>(false, "Error en el proceso de obtención");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/nombreCategoria/{nombreCategoria}")
    public  ResponseEntity<ApiResponse<CategoriaDTO>> obtenerNombreCategoria(@PathVariable String nombreCategoria){
        try {
            CategoriaDTO dto = service.obtenerNombreCategoria(nombreCategoria);
            if (dto != null){
                log.info("Se obtuvo la categoria: " + nombreCategoria + "con exito");
                ApiResponse<CategoriaDTO> exito = new ApiResponse<>(true, "Se obtuvo la categoria: " + nombreCategoria + " con exito", dto);
                return  ResponseEntity.ok(exito);
            }
            log.warn("No se encontro la categoria: " + nombreCategoria);
            ApiResponse<CategoriaDTO> respuesta = new ApiResponse<>(false, "no se encontro la categoria: " + nombreCategoria);
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }
        catch (Exception e){
            log.error("Error al obtener la categoria: " + nombreCategoria);
            e.printStackTrace();
            ApiResponse<CategoriaDTO> error = new ApiResponse<>(false, "Error al obtener la categoria: " + nombreCategoria);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    //Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoriaDTO>> actualizarCategoria(@PathVariable Long id, @Valid @RequestBody CategoriaDTO dto){
        try{
            CategoriaDTO datos = service.actualizarCategoria(id, dto);
            if (datos != null){
                log.info("Categoria: " + id + ", ha sido actualizada");
                ApiResponse<CategoriaDTO> exito = new ApiResponse<>(true, "Categoria: " + id + ", ha sido actualizada", dto);
                return ResponseEntity.ok(exito);
            }
            log.warn("No se pudo completar la actualizacion de la categoria: " + id);
            ApiResponse<CategoriaDTO> respuestaNoCompletada = new ApiResponse<>(false, "No se pudo completar la actualizacion de la categoria: " + id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaNoCompletada);
        }
        catch (Exception e){
            log.error("Error al actualizar la categoria: " + id);
            e.printStackTrace();
            ApiResponse<CategoriaDTO> error  = new ApiResponse<>(false, "Error al actualizar la categoria: " + id);
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    //Eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarCategoria(@PathVariable Long id){
        try {
            boolean respuesta = service.eliminarCategoria(id);
            if (respuesta){
                log.info("Categoria: " + id + " eliminada");
                ApiResponse<Void> exito = new ApiResponse<>(true, "Categoria: " + id + " eliminada");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(exito);
            }
            log.warn("Categoria: " + id + ", no fue encontrada");
            ApiResponse<Void> respuestaNoEncontrada = new ApiResponse<>(false, "Categoria: " + id + ", no fue encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        }
        catch (Exception e){
            log.error("Error al eliminar la categoria: " + id);
            e.printStackTrace();
            ApiResponse<Void> error  = new ApiResponse<>(false, "Error al eliminar la categoria: " + id);
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
