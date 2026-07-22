package iTicket.Douglas.Proyectos.Controller;

import iTicket.Douglas.Proyectos.DTO.ProyectoDTO;
import iTicket.Douglas.Proyectos.Service.ProyectoService;
import iTicket.Douglas.Response.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/proyectos")
public class ProyectoController {

    private final ProyectoService service;

    public ProyectoController(ProyectoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProyectoDTO>> nuevoProyecto(@Valid @RequestBody ProyectoDTO json){
        try{
            ProyectoDTO dto = service.crearProyecto(json);
            if (dto != null){
                log.info("Nuevo proyecto creado: " + dto);
                ApiResponse<ProyectoDTO> respuestaExito = new ApiResponse<>(true, "Datos ingresados correctamente", dto);
                return ResponseEntity.status(HttpStatus.CREATED).body(respuestaExito);
            }
            log.warn("Intento de insersión fallido: " + json);
            ApiResponse<ProyectoDTO> respuestaFallida = new ApiResponse<>(false, "Intento fallido de insersión");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaFallida);
        } catch (Exception e) {
            log.error("El proceso presentó fallas inesperadas. Consulta con el administrador");
            e.printStackTrace();
            ApiResponse<ProyectoDTO> respuestaError = new ApiResponse<>(false, "El proceso no se pudo completar", json);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProyectoDTO>>> obtenerDatos(){
        try {
            List<ProyectoDTO> lista = service.obtenerTodo();
            if (lista != null){
                log.info("Datos de proyectos consultados");
                ApiResponse<List<ProyectoDTO>> respuestaExito = new ApiResponse<>(true, "Datos encontrados", lista);
                return ResponseEntity.ok(respuestaExito);
            }
            log.info("Datos no encontrados");
            ApiResponse<List<ProyectoDTO>> respuestaNoEncontrada = new ApiResponse<>(false,"Datos no encontrados");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuestaNoEncontrada);
        }catch (Exception e){
            log.error("El proceso presentó un fallo inesperado. Consulta con el administrador");
            e.printStackTrace();
            ApiResponse<List<ProyectoDTO>> respuestaError = new ApiResponse<>(false, "El proceso no se pudo completar");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProyectoDTO>> obtenerProyectoPorId(@PathVariable Long id){
        try {
            ProyectoDTO dto = service.obtenerPorId(id);
            if (dto != null){
                log.info("Se obtuvieron los datos del proyecto con ID: " + id);
                ApiResponse<ProyectoDTO> respuestaExito = new ApiResponse<>(true, "Se obtuvieron los datos del proyecto con ID: " + id, dto);
                return ResponseEntity.ok(respuestaExito);
            }
            log.info("Datos no encontrados con ID: " + id);
            ApiResponse<ProyectoDTO> respuestaNoEncontrada = new ApiResponse<>(false, "Datos no encontrados con ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        } catch (Exception e){
            log.error("Error crítico al obtener del proyecto con ID: " + id);
            e.printStackTrace();
            ApiResponse<ProyectoDTO> respuestaError = new ApiResponse<>(false, "No se pudo obtener los datos del proyecto");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarProyecto(@PathVariable Long id){
        try {
            boolean respueta = service.eliminarProyecto(id);
            if (respueta){
                log.info("Proyecto con ID: " + id+ ", eliminado");
                ApiResponse<Void> respuestExitosa = new ApiResponse<>(true, "Proyecto con ID: " + id+ ", eliminado");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuestExitosa);
            }
            log.info("Proyecto con ID: " + id+ ", no fue encontrado");
            ApiResponse<Void> respuestaNoEncontrada = new ApiResponse<>(false, "Proyecto con ID: " + id+ ", no fue encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        }catch (Exception e){
            log.error("Error crítico al eliminar el proyecto con ID: " + id);
            e.printStackTrace();
            ApiResponse<Void> respuestaError = new ApiResponse<>(false, "Error crítico al eliminar la data");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProyectoDTO>> actualizarData(@PathVariable Long id, @Valid @RequestBody ProyectoDTO dto){
        try {
            ProyectoDTO data = service.actualizarProyecto(id, dto);
            if (data != null){
                log.info("Proyecto con ID: "+ id + " ha sido actualizado.");
                ApiResponse<ProyectoDTO> respuestaExitosa = new ApiResponse<>(true, "Proyecto con ID: " + id + " ha sido actualizado.", data);
                return ResponseEntity.ok(respuestaExitosa);
            }
            log.warn("No se pudo completar la actualización del proyecto con ID: "+ id);
            ApiResponse<ProyectoDTO> respuestaNoCompletada = new ApiResponse<>(false, "No se pudo completar la actualización del proyecto con ID: "+ id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaNoCompletada);
        }catch (Exception e){
            log.error("Error crítico al actualizar el proyecto con ID: " + id);
            e.printStackTrace();
            ApiResponse<ProyectoDTO> respuestaError = new ApiResponse<>(false, "Error crítico al actualizar el proyecto con ID: " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    //Proceso de búsqueda de proyecto por nombre
    @GetMapping("/nombre")
    public ResponseEntity<ApiResponse<List<ProyectoDTO>>> buscarPorNombre(@RequestParam String nombre){
        try {
            List<ProyectoDTO> lista = service.buscarPorNombre(nombre);
            if (!lista.isEmpty()){
                log.info("Proyectos encontrados con nombre: " + nombre);
                ApiResponse<List<ProyectoDTO>> respuestExitosa = new ApiResponse<>(true, "Proyectos encontrados con nombre: " + nombre, lista);
                return ResponseEntity.ok(respuestExitosa);
            }
            log.warn("Datos no encontrados con nombre: " + nombre);
            ApiResponse<List<ProyectoDTO>> respuestaNoEncontrada = new ApiResponse<>(false, "Proyectos no encontrados con nombre: " + nombre);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        }catch (Exception e){
            log.error("Error crítico al obtener proyectos con nombre: " + nombre);
            e.printStackTrace();
            ApiResponse<List<ProyectoDTO>> respuestaError = new ApiResponse<>(false, "Error crítico al obtener proyectos con nombre: " + nombre);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<ApiResponse<List<ProyectoDTO>>> buscarPorTipo(@PathVariable String tipo){
        try {
            List<ProyectoDTO> data = service.buscarPorTipo(tipo);
            if (data != null){
                log.info("Proyectos encontrados de tipo: " + tipo);
                ApiResponse<List<ProyectoDTO>> respuestaExitosa = new ApiResponse<>(true, "Proyectos encontrados de tipo: "+tipo, data);
                return ResponseEntity.ok(respuestaExitosa);
            }
            log.warn("Datos no encontrados de tipo: " + tipo);
            ApiResponse<List<ProyectoDTO>> respuestaNoEncontrada = new ApiResponse<>(false, "Datos no encontrados de tipo: " + tipo);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaNoEncontrada);
        }catch (Exception e){
            log.error("Error crítico al obtener proyectos de tipo: " + tipo);
            e.printStackTrace();
            ApiResponse<List<ProyectoDTO>> respuestaError = new ApiResponse<>(false, "Error crítico al obtener proyectos de tipo: " + tipo);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
        }
    }
}
