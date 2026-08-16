package iTicket.Douglas.MultimediaComentario.Service;

import iTicket.Douglas.Comentarios.Entity.ComentarioEntity;
import iTicket.Douglas.Comentarios.Repository.ComentarioRepository;
import iTicket.Douglas.MultimediaComentario.DTO.MultimediaComentarioDTO;
import iTicket.Douglas.MultimediaComentario.Entity.MultimediaComentarioEntity;
import iTicket.Douglas.MultimediaComentario.Repository.MultimediaComentarioRepository;
import iTicket.Douglas.Utils.CloudinaryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class MultimediaComentarioService {

    private final MultimediaComentarioRepository repo;
    private final ComentarioRepository comentarioRepo; //Para buscar si el comentario existe
    private final CloudinaryService cloudinaryService;

    @Transactional
    public MultimediaComentarioDTO subirMultimedia(MultipartFile archivo, Long idComentario) {
        if (!comentarioRepo.existsById(idComentario)) {
            throw new RuntimeException("El comentario con ID: " + idComentario + " no existe");
        }

        String urlPublica = cloudinaryService.subirImagen(archivo, "iticket/comentarios");

        MultimediaComentarioDTO dto = new MultimediaComentarioDTO();
        dto.setMultimediaUrl(urlPublica);
        dto.setIdComentario(idComentario);

        return nuevaMultimedia(dto);
    }

    @Transactional
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

    @Transactional
    public boolean eliminarData(Long id){

        Optional<MultimediaComentarioEntity> entidad = repo.findById(id);
        if (entidad.isPresent()) {
            String publicId = cloudinaryService.extraerPublicId(entidad.get().getMultimediaUrl(), "iticket/comentarios");
            cloudinaryService.eliminarImagen(publicId);
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
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
