package iTicket.Douglas.MultimediaComentario.Service;

import iTicket.Douglas.Comentarios.Entity.ComentarioEntity;
import iTicket.Douglas.MultimediaComentario.DTO.MultimediaComentarioDTO;
import iTicket.Douglas.MultimediaComentario.Entity.MultimediaComentarioEntity;
import iTicket.Douglas.MultimediaComentario.Repository.MultimediaComentarioRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MultimediaComentarioService {

    private final MultimediaComentarioRepository repo;

    public MultimediaComentarioService(MultimediaComentarioRepository repo) {
        this.repo = repo;
    }

    public MultimediaComentarioDTO nuevaMultimedia(@Valid MultimediaComentarioDTO dto){

        try{

            MultimediaComentarioEntity entity = convertirAEntity(dto);

            MultimediaComentarioEntity entitySave = repo.save(entity);

            return convertirADTO(entitySave);

        }catch (Exception e){

            log.error("Error al ingresar la multimedia: " + e.getMessage());

            return null;
        }

    }

    private MultimediaComentarioEntity convertirAEntity(@Valid MultimediaComentarioDTO dto){

        MultimediaComentarioEntity objEntity = new MultimediaComentarioEntity();

        objEntity.setMultimediaUrl(dto.getMultimediaUrl());

        ComentarioEntity comentario = new ComentarioEntity();
        comentario.setId(dto.getIdComentario());

        objEntity.setComentario(comentario);

        return objEntity;

    }

    private MultimediaComentarioDTO convertirADTO(@Valid MultimediaComentarioEntity entity){

        MultimediaComentarioDTO objDTO = new MultimediaComentarioDTO();

        objDTO.setId(entity.getId());
        objDTO.setMultimediaUrl(entity.getMultimediaUrl());
        objDTO.setIdComentario(entity.getComentario().getId());

        return objDTO;

    }

    public List<MultimediaComentarioDTO> obtenerTodo(){

        List<MultimediaComentarioEntity> data = repo.findAll();

        return data.stream().map(this::convertirADTO).collect(Collectors.toList());

    }

    public MultimediaComentarioDTO buscarPorId(Long id){

        Optional<MultimediaComentarioEntity> entidadOpcional = repo.findById(id);

        return entidadOpcional.map(this::convertirADTO).orElse(null);

    }

    public boolean eliminarData(Long id){

        if(repo.existsById(id)){

            repo.deleteById(id);

            return true;

        }

        return false;

    }

    public MultimediaComentarioDTO actualizar(Long id,@Valid MultimediaComentarioDTO dto){

        try{

            Optional<MultimediaComentarioEntity> registroExiste = repo.findById(id);

            if(registroExiste.isPresent()){

                MultimediaComentarioEntity entidad = registroExiste.get();

                entidad.setMultimediaUrl(dto.getMultimediaUrl());

                ComentarioEntity comentario = new ComentarioEntity();
                comentario.setId(dto.getIdComentario());

                entidad.setComentario(comentario);

                MultimediaComentarioEntity datosGuardados = repo.save(entidad);

                return convertirADTO(datosGuardados);

            }

            return null;

        }catch (Exception e){

            log.error("Oops ocurrio un error al procesar la informacion");

            return null;

        }

    }
}
