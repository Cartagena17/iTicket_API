package iTicket.Douglas.MultimediaComentario.Controller;

import iTicket.Douglas.MultimediaComentario.DTO.MultimediaComentarioDTO;
import iTicket.Douglas.MultimediaComentario.Service.MultimediaComentarioService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/multimediaComentarios")
public class MultimediaComentarioController {

    private final MultimediaComentarioService service;

    public MultimediaComentarioController(MultimediaComentarioService service) {
        this.service = service;
    }

    @PostMapping
    public MultimediaComentarioDTO guardar(@RequestBody @Valid MultimediaComentarioDTO dto){

        return service.nuevaMultimedia(dto);

    }

    @GetMapping
    public List<MultimediaComentarioDTO> obtenerTodo(){

        return service.obtenerTodo();

    }

    @GetMapping("/{id}")
    public MultimediaComentarioDTO buscarPorId(@PathVariable Long id){

        return service.buscarPorId(id);

    }

    @PutMapping("/{id}")
    public MultimediaComentarioDTO actualizar(@PathVariable Long id,
                                               @RequestBody @Valid MultimediaComentarioDTO dto){

        return service.actualizar(id,dto);

    }

    @DeleteMapping("/{id}")
    public boolean eliminar(@PathVariable Long id){

        return service.eliminarData(id);

    }
}
