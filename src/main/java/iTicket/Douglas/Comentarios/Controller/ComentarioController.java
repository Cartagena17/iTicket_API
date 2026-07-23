package iTicket.Douglas.Comentarios.Controller;

import iTicket.Douglas.Comentarios.DTO.ComentarioDTO;
import iTicket.Douglas.Comentarios.Service.ComentarioService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comentarios")
public class ComentarioController {
    private final ComentarioService service;

    public ComentarioController(ComentarioService service) {
        this.service = service;
    }

    @PostMapping
    public ComentarioDTO guardar(@RequestBody @Valid ComentarioDTO dto){
        return service.nuevoComentario(dto);
    }

    @GetMapping
    public List<ComentarioDTO> obtenerTodo(){
        return service.obtenerTodo();
    }

    @GetMapping("/{id}")
    public ComentarioDTO buscarPorId(@PathVariable Long id){
        return service.buscarComentarioPorId(id);
    }

    @PutMapping("/{id}")
    public ComentarioDTO actualizar(@PathVariable Long id,
                                    @RequestBody @Valid ComentarioDTO dto){
        return service.actualizar(id,dto);
    }

    @DeleteMapping("/{id}")
    public boolean eliminar(@PathVariable Long id){
        return service.eliminarData(id);
    }
}
